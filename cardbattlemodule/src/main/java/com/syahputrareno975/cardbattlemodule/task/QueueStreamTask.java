package com.syahputrareno975.cardbattlemodule.task;

import android.os.AsyncTask;
import cardBattle.CardBattle;
import cardBattle.cardBattleServiceGrpc;
import com.syahputrareno975.cardbattlemodule.interfaces.QueueStreamEvent;
import com.syahputrareno975.cardbattlemodule.interfaces.QueueStreamEventController;
import com.syahputrareno975.cardbattlemodule.interfaces.SimpleUnit;
import com.syahputrareno975.cardbattlemodule.model.NetworkConfig;
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import com.syahputrareno975.cardbattlemodule.model.playerWithCard.PlayerWithCardsModel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;

import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerModelGRPC;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toRoomModel;

public class QueueStreamTask extends AsyncTask<Void,Void,Boolean> {

    private ManagedChannel channel;
    private PlayerWithCardsModel player ;
    private NetworkConfig net;
    private QueueStreamEvent streamEvent;
    private Throwable failed = null;

    private QueueStreamEventController controller;
    private StreamObserver<CardBattle.queueStream> request;
    private StreamObserver<CardBattle.queueStream> response;
    private String error = "";


    public QueueStreamTask(PlayerWithCardsModel player, NetworkConfig net, QueueStreamEvent streamEvent) {
        this.player = player;
        this.net = net;
        this.streamEvent = streamEvent;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.channel = ManagedChannelBuilder.forAddress(this.net.Url, this.net.Port)
                .usePlaintext(true)
                .build();


        response = new StreamObserver<CardBattle.queueStream>() {
            @Override
            public void onNext(CardBattle.queueStream value) {
                holder.event = value;
                publishProgress();
            }

            @Override
            public void onError(Throwable t) {
                failed = t;
                error += t.getMessage();
                holder.stop = true;
            }

            @Override
            public void onCompleted() {
                holder.stop = true;
            }
        };

        controller = new QueueStreamEventController() {
            @Override
            public void leftWaitingRoom(PlayerModel p, SimpleUnit action) {
                holder.actionHolder = action;
                request.onNext(CardBattle.queueStream.newBuilder()
                        .setOnLeftWaitingRoom(toPlayerModelGRPC(player.Owner))
                        .build());
            }
        };
    }



    Holder holder = new Holder();

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            cardBattleServiceGrpc.cardBattleServiceStub stub = cardBattleServiceGrpc.newStub(this.channel);
            request = stub.cardBattleQueueStream(response);

            request.onNext(CardBattle.queueStream.newBuilder()
                    .setOnjoinWaitingRoom(toPlayerModelGRPC(player.Owner))
                    .build());

            while (!holder.stop){
                // infinite loop
            }

            request.onCompleted();


        } catch (Exception e){
            error += e.getMessage();
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        if (holder.event != null){
            switch (holder.event.getEventCase()){
                case ONJOINWAITINGROOM:
                    streamEvent.onEnterQueue(controller);
                    streamEvent.onJoinWaitingRoom();
                    break;

                case ONLEFTWAITINGROOM:
                    holder.left = true;
                    holder.stop = true;
                    break;

                case ONBATTLEFOUND:
                    streamEvent.onBattleFound(toRoomModel(holder.event.getOnBattleFound()));
                    break;

                case ONBATTLENOTFOUND:
                    streamEvent.onBattleNotFound();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        try {
            this.channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }

        if (!error.equals("")) {
            streamEvent.onError(error);
            return;
        }


        if (holder.left) {
            holder.actionHolder.invoke();
        }

        streamEvent.onDisconnected();
    }

    class Holder {
        public SimpleUnit actionHolder = new SimpleUnit() {
            @Override
            public void invoke() {

            }
        };
        public Boolean left = false;
        public Boolean stop = false;
        public CardBattle.queueStream event = null;
    }
}
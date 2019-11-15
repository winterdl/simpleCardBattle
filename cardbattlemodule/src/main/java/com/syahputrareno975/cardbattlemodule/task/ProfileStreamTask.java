package com.syahputrareno975.cardbattlemodule.task;

import android.os.AsyncTask;
import cardBattle.CardBattle;
import cardBattle.cardBattleServiceGrpc;
import com.syahputrareno975.cardbattlemodule.interfaces.ProfileStreamEvent;
import com.syahputrareno975.cardbattlemodule.interfaces.ProfileStreamEventController;
import com.syahputrareno975.cardbattlemodule.interfaces.SimpleUnit;
import com.syahputrareno975.cardbattlemodule.model.ModelCasting;
import com.syahputrareno975.cardbattlemodule.model.NetworkConfig;
import com.syahputrareno975.cardbattlemodule.model.card.CardModel;
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import com.syahputrareno975.cardbattlemodule.model.playerWithCard.PlayerWithCardsModel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;

import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerWithCardsModel;

public class ProfileStreamTask extends AsyncTask<Void,Void,Boolean> {

    private ManagedChannel channel;
    private PlayerWithCardsModel player;
    private NetworkConfig net;
    private ProfileStreamEvent streamEvent;
    private Throwable failed = null;

    private ProfileStreamEventController controller;
    private StreamObserver<CardBattle.profileStream> request;
    private StreamObserver<CardBattle.profileStream> response;
    private String error = "";

    public ProfileStreamTask(PlayerWithCardsModel player, NetworkConfig net, ProfileStreamEvent streamEvent) {
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

        response = new StreamObserver<CardBattle.profileStream>() {
            @Override
            public void onNext(CardBattle.profileStream value) {
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

        controller = new ProfileStreamEventController() {
            @Override
            public void getMyPlayerData(String id) {
                PlayerWithCardsModel p = new PlayerWithCardsModel();
                p.Owner.Id = id;
                request.onNext(CardBattle.profileStream.newBuilder()
                        .setOnePlayerWithCards(ModelCasting.toPlayerWithCardsModelGRPC(p))
                        .build());
            }

            @Override
            public void addCardToDeck(PlayerModel p, CardModel c) {
                request.onNext(CardBattle.profileStream.newBuilder()
                        .setAddCardToDeck(CardBattle.playerAndCard.newBuilder()
                                .setClient(ModelCasting.toPlayerModelGRPC(p))
                                .setCardData(ModelCasting.toCardModelGRPC(c))
                                .build())
                        .build());
            }

            @Override
            public void removeCardFromDeck(PlayerModel p, CardModel c) {
                request.onNext(CardBattle.profileStream.newBuilder()
                        .setRemoveCardFromDeck(CardBattle.playerAndCard.newBuilder()
                                .setClient(ModelCasting.toPlayerModelGRPC(p))
                                .setCardData(ModelCasting.toCardModelGRPC(c))
                                .build())
                        .build());
            }

            @Override
            public void leaveProfile(SimpleUnit action) {
                holder.left = true;
                holder.stop = true;
                holder.actionHolder = action;
            }
        };
    }


    Holder holder = new Holder();
    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            cardBattleServiceGrpc.cardBattleServiceStub stub = cardBattleServiceGrpc.newStub(this.channel);
            request = stub.cardBattleProfileStream(response);

            streamEvent.onConnected(controller);

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
                case ADDCARDTODECK: case REMOVECARDFROMDECK:
                    streamEvent.onPlayerCardUpdated();
                    break;

                case ONEPLAYERWITHCARDS:
                    streamEvent.onGetPlayerData(toPlayerWithCardsModel(holder.event.getOnePlayerWithCards()));
                    break;

                case EXCMESSAGE:
                    streamEvent.onException(holder.event .getExcMessage().getExceptionMessage(), holder.event .getExcMessage().getExceptionFlag(),controller);
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
        public CardBattle.profileStream event = null;
    }
}
package com.syahputrareno975.cardbattlemodule.task;

import android.os.AsyncTask;
import cardBattle.CardBattle;
import cardBattle.cardBattleServiceGrpc;
import com.syahputrareno975.cardbattlemodule.interfaces.RoomStreamEvent;
import com.syahputrareno975.cardbattlemodule.interfaces.RoomStreamEventController;
import com.syahputrareno975.cardbattlemodule.interfaces.SimpleUnit;
import com.syahputrareno975.cardbattlemodule.model.NetworkConfig;
import com.syahputrareno975.cardbattlemodule.model.card.CardModel;
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import com.syahputrareno975.cardbattlemodule.model.playerWithCard.PlayerWithCardsModel;
import com.syahputrareno975.cardbattlemodule.model.room.RoomDataModel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;

import static cardBattle.CardBattle.roomStream.EventCase.EXCMESSAGE;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toAllPlayerBattleResultModelModel;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toCardModelGRPC;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toEndResultModel;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerModelGRPC;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerWithCardsModel;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerWithCardsModelGRPC;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toRoomModel;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toRoomModelGRPC;

public class RoomStreamTask extends AsyncTask<Void,Void,Boolean>{

    private ManagedChannel channel;
    private RoomDataModel roomData ;
    private PlayerWithCardsModel player ;
    private NetworkConfig net;
    private RoomStreamEvent roomStreamEvent;
    private Throwable failed = null;

    private RoomStreamEventController controller;
    private StreamObserver<CardBattle.roomStream> request;
    private StreamObserver<CardBattle.roomStream> response;
    private String error  = "";


    public RoomStreamTask( PlayerWithCardsModel player,RoomDataModel roomData, NetworkConfig net, RoomStreamEvent roomStreamEvent) {
        this.roomData = roomData;
        this.player = player;
        this.net = net;
        this.roomStreamEvent = roomStreamEvent;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.channel = ManagedChannelBuilder.forAddress(this.net.Url, this.net.Port)
                .usePlaintext(true)
                .build();

        response = new StreamObserver<CardBattle.roomStream>() {
            @Override
            public void onNext(CardBattle.roomStream value) {
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

        controller = new RoomStreamEventController() {
            @Override
            public void playerJoin(PlayerModel p) {
                // left this empty
                // because its already send when connected
            }

            @Override
            public void getOneRoom(String id) {
                request.onNext(CardBattle.roomStream.newBuilder()
                        .setIdRoom(roomData.Id)
                        .setGetOneRoom(toRoomModelGRPC(roomData))
                        .build());
            }

            @Override
            public void deployCard(PlayerModel p, CardModel c) {
                request.onNext(CardBattle.roomStream.newBuilder()
                        .setIdRoom(roomData.Id)
                        .setDeployCard(CardBattle.playerAndCard.newBuilder()
                                .setCardData(toCardModelGRPC(c))
                                .setClient(toPlayerModelGRPC(p))
                                .build())
                        .build());
            }

            @Override
            public void pickUpCard(PlayerModel p, CardModel c) {
                request.onNext(CardBattle.roomStream.newBuilder()
                        .setIdRoom(roomData.Id)
                        .setPickupCard(CardBattle.playerAndCard.newBuilder()
                                .setCardData(toCardModelGRPC(c))
                                .setClient(toPlayerModelGRPC(p))
                                .build())
                        .build());
            }

            @Override
            public void leftGame(PlayerModel p, RoomDataModel r, SimpleUnit action) {
                holder.actionHolder = action;
                request.onNext(CardBattle.roomStream.newBuilder()
                        .setIdRoom(roomData.Id)
                        .setPlayerLeft(toPlayerWithCardsModelGRPC(player))
                        .build());
            }
        };
    }

    Holder holder = new Holder();
    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            cardBattleServiceGrpc.cardBattleServiceStub stub = cardBattleServiceGrpc.newStub(this.channel);
            request = stub.cardBattleRoomStream(response);

            request.onNext(CardBattle.roomStream.newBuilder()
                    .setIdRoom(roomData.Id)
                    .setPlayerJoin(toPlayerWithCardsModelGRPC(player))
                    .build());


            roomStreamEvent.onConnected(controller);

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
                case COUNTDOWN :
                    roomStreamEvent.onCountDown(holder.event.getCountDown().getBattleTime(), toRoomModel(holder.event.getCountDown().getUpdatedRoom()));
                    break;

                case PLAYERJOIN:
                    roomStreamEvent.onPlayerJoin(toPlayerWithCardsModel(holder.event.getPlayerJoin()).Owner);
                    break;

                case PLAYERLEFT:
                   PlayerModel p = toPlayerWithCardsModel(holder.event.getPlayerLeft()).Owner;
                    holder.stop = p.Id.equals(player.Owner.Id);
                    holder.left = p.Id.equals(player.Owner.Id);
                    roomStreamEvent.onPlayerLeft(p);
                    break;

                case ONROOMUPDATE:
                    roomStreamEvent.onRoomUpdate(toRoomModel(holder.event.getOnRoomUpdate()));
                    break;

                case BATTLERESULT :
                    roomStreamEvent.onBattleResult(toAllPlayerBattleResultModelModel(holder.event.getBattleResult()));
                    break;

                case RESULT:
                    holder.stop = true;
                    roomStreamEvent.onResult(toEndResultModel(holder.event.getResult()));
                    break;

                case ONDRAW:
                    int flag = holder.event.getOnDraw();
                    holder.stop = flag == 1 || flag == 2 || flag == 3;
                    roomStreamEvent.onDraw(flag);
                    break;

                case DEPLOYCARD:

                    // left this empty
                    break;

                case PICKUPCARD:

                    // left this empty
                    break;

                case GETONEROOM:
                    roomStreamEvent.onGetRoomData(toRoomModel(holder.event.getGetOneRoom()));
                    break;

                case EXCMESSAGE:
                    roomStreamEvent.onException(holder.event .getExcMessage().getExceptionMessage(), holder.event .getExcMessage().getExceptionFlag());
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
            roomStreamEvent.onError(error);
            return;
        }

        if (holder.left) {
            holder.actionHolder.invoke();
        }

        roomStreamEvent.onDisconnected();
    }

    class Holder {
        public SimpleUnit actionHolder = new SimpleUnit() {
            @Override
            public void invoke() {

            }
        };
        public Boolean left = false;
        public Boolean stop = false;
        public CardBattle.roomStream event = null;
    }
}
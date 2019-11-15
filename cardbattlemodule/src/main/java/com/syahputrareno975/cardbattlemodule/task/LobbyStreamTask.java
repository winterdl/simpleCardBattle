package com.syahputrareno975.cardbattlemodule.task;

import android.os.AsyncTask;
import cardBattle.CardBattle;
import cardBattle.cardBattleServiceGrpc;
import com.syahputrareno975.cardbattlemodule.interfaces.LobbyStreamEventController;
import com.syahputrareno975.cardbattlemodule.interfaces.LobbyStreamEvent;
import com.syahputrareno975.cardbattlemodule.interfaces.SimpleUnit;
import com.syahputrareno975.cardbattlemodule.model.ModelCasting;
import com.syahputrareno975.cardbattlemodule.model.NetworkConfig;
import com.syahputrareno975.cardbattlemodule.model.player.AllPlayerModel;
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import com.syahputrareno975.cardbattlemodule.model.playerWithCard.PlayerWithCardsModel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;

import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toAllPlayerModel;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toAllPlayerModelGRPC;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerModel;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerModelGRPC;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerWithCardsModel;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerWithCardsModelGRPC;

public class LobbyStreamTask extends AsyncTask<Void, Void, Boolean> {

    private ManagedChannel channel;
    private PlayerModel player;
    private LobbyStreamEvent lobbyStreamEvent;
    private NetworkConfig  net;
    private Throwable failed = null;

    private LobbyStreamEventController eventController;
    private StreamObserver<CardBattle.lobbyStream> request;
    private StreamObserver<CardBattle.lobbyStream> response;
    private String error = "";

    public LobbyStreamTask(PlayerModel player, NetworkConfig net, LobbyStreamEvent lobbyStreamEvent) {
        this.player = player;
        this.lobbyStreamEvent = lobbyStreamEvent;
        this.net = net;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.channel = ManagedChannelBuilder.forAddress(this.net.Url, this.net.Port)
                .usePlaintext(true)
                .build();

        response = new StreamObserver<CardBattle.lobbyStream>() {
            @Override
            public void onNext(CardBattle.lobbyStream value) {
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

        eventController = new LobbyStreamEventController() {
            @Override
            public void getAllPlayer() {
                request.onNext(CardBattle.lobbyStream.newBuilder()
                        .setGetAllPlayers(toAllPlayerModelGRPC(new AllPlayerModel()))
                        .build());
            }

            @Override
            public void getOnePlayer(String id) {
                PlayerModel p = new PlayerModel();
                p.Id = id;
                request.onNext(CardBattle.lobbyStream.newBuilder()
                        .setGetOneplayer(toPlayerModelGRPC(p))
                        .build());
            }

            @Override
            public void getMyPlayerData(String id) {
                PlayerWithCardsModel p = new PlayerWithCardsModel();
                p.Owner.Id = id;
                request.onNext(CardBattle.lobbyStream.newBuilder()
                        .setOnePlayerWithCards(toPlayerWithCardsModelGRPC(p))
                        .build());
            }

            @Override
            public void leftGame(PlayerModel p, SimpleUnit action) {
                holder.actionHolder = action;
                request.onNext(CardBattle.lobbyStream.newBuilder()
                        .setPlayerLeft(toPlayerModelGRPC(p))
                        .build());
            }

            @Override
            public void leftLobby(SimpleUnit action) {
                holder.actionHolder = action;
                holder.stop = true;
                holder.leftLobby = true;
            }
        };
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        if (holder.event != null) {
            switch (holder.event.getEventCase()) {
                case PLAYERJOIN:
                    lobbyStreamEvent.onPlayerJoin(toPlayerModel(holder.event.getPlayerJoin()));
                    break;

               case PLAYERLEFT:
                    lobbyStreamEvent.onPlayerLeft(toPlayerModel(holder.event.getPlayerLeft()));
                    break;

                case GETALLPLAYERS:
                    lobbyStreamEvent.onGetAllPlayer(toAllPlayerModel(holder.event.getGetAllPlayers()).Players);
                    break;

                case GETONEPLAYER:
                    lobbyStreamEvent.onGetOnePlayer(toPlayerModel(holder.event .getGetOneplayer()));
                    break;

                case PLAYERSUCCESSJOIN:
                    lobbyStreamEvent.onConnected(toPlayerModel(holder.event .getPlayerSuccessJoin()),eventController);
                    break;

                case PLAYERSUCCESSLEFT:
                    holder.stop = true;
                    holder.leftLobby = true;
                    lobbyStreamEvent.onPlayerSuccessLeft();
                    break;

                case ONEPLAYERWITHCARDS:
                    lobbyStreamEvent.onGetPlayerData(toPlayerWithCardsModel(holder.event .getOnePlayerWithCards()));
                break;

                case EXCMESSAGE:
                    lobbyStreamEvent.onException(holder.event .getExcMessage().getExceptionMessage(), holder.event .getExcMessage().getExceptionFlag(), eventController);
                    break;

                default:
                    break;
            }
        }
    }

    private Holder holder = new Holder();

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            cardBattleServiceGrpc.cardBattleServiceStub stub = cardBattleServiceGrpc.newStub(this.channel);
            request = stub.cardBattleLobbyStream(response);

            request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setPlayerJoin(toPlayerModelGRPC(player))
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
            lobbyStreamEvent.onError(error);
            return;
        }

        if (holder.leftLobby){
            holder.actionHolder.invoke();
        }

        lobbyStreamEvent.onDisconnected();
    }

    class Holder {
        public SimpleUnit actionHolder = new SimpleUnit() {
            @Override
            public void invoke() {

            }
        };
        public Boolean leftLobby = false;
        public Boolean stop = false;
        public CardBattle.lobbyStream event = null;
    }
}
package com.syahputrareno975.cardbattlemodule.task;

import android.os.AsyncTask;
import cardBattle.CardBattle;
import cardBattle.cardBattleServiceGrpc;
import com.syahputrareno975.cardbattlemodule.interfaces.ShopStreamEvent;
import com.syahputrareno975.cardbattlemodule.interfaces.ShopStreamEventController;
import com.syahputrareno975.cardbattlemodule.interfaces.SimpleUnit;
import com.syahputrareno975.cardbattlemodule.model.NetworkConfig;
import com.syahputrareno975.cardbattlemodule.model.card.AllCardModel;
import com.syahputrareno975.cardbattlemodule.model.card.CardModel;
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import com.syahputrareno975.cardbattlemodule.model.playerWithCard.PlayerWithCardsModel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;

import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toAllCardModel;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toAllCardModelGRPC;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toCardModelGRPC;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerModelGRPC;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerWithCardsModel;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerWithCardsModelGRPC;

public class ShopStreamTask extends AsyncTask<Void,Void,Boolean> {
    private ManagedChannel channel;
    private PlayerWithCardsModel player;
    private NetworkConfig net;
    private ShopStreamEvent shopStreamEvent;
    private Throwable failed = null;

    private ShopStreamEventController controller;
    private StreamObserver<CardBattle.shopStream> request;
    private StreamObserver<CardBattle.shopStream> response;
    private String error = "";


    public ShopStreamTask(PlayerWithCardsModel player, NetworkConfig net, ShopStreamEvent shopStreamEvent) {
        this.player = player;
        this.net = net;
        this.shopStreamEvent = shopStreamEvent;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.channel = ManagedChannelBuilder.forAddress(this.net.Url, this.net.Port)
                .usePlaintext(true)
                .build();


        response = new StreamObserver<CardBattle.shopStream>() {
            @Override
            public void onNext(CardBattle.shopStream value) {
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

        controller = new ShopStreamEventController() {
            @Override
            public void getMyPlayerData(String id) {
                PlayerWithCardsModel p = new PlayerWithCardsModel();
                p.Owner.Id = id;
                request.onNext(CardBattle.shopStream.newBuilder()
                        .setOnePlayerWithCards(toPlayerWithCardsModelGRPC(p))
                        .build());
            }

            @Override
            public void getAllCardInShop(PlayerModel p) {
                request.onNext(CardBattle.shopStream.newBuilder()
                        .setAllCardInShopping(toAllCardModelGRPC(new AllCardModel()))
                        .build());
            }

            @Override
            public void buyCardFromShop(PlayerModel p, CardModel c) {
                request.onNext(CardBattle.shopStream.newBuilder()
                        .setOnBuyCard(CardBattle.playerAndCard.newBuilder()
                                .setClient(toPlayerModelGRPC(p))
                                .setCardData(toCardModelGRPC(c))
                                .build())
                        .build());
            }

            @Override
            public void sellCardToShop(PlayerModel p, CardModel c) {
                request.onNext(CardBattle.shopStream.newBuilder()
                        .setOnSellCard(CardBattle.playerAndCard.newBuilder()
                                .setClient(toPlayerModelGRPC(p))
                                .setCardData(toCardModelGRPC(c))
                                .build())
                        .build());
            }

            @Override
            public void upgradeCard(PlayerModel p, CardModel c) {
                request.onNext(CardBattle.shopStream.newBuilder()
                        .setOnUpgradeCard(CardBattle.playerAndCard.newBuilder()
                                .setClient(toPlayerModelGRPC(p))
                                .setCardData(toCardModelGRPC(c))
                                .build())
                        .build());
            }

            @Override
            public void addDeckSlot(PlayerModel p, int typeSlot) {
                request.onNext(CardBattle.shopStream.newBuilder()
                        .setOnCardDeckSlot(CardBattle.playerAndSlot.newBuilder()
                                .setOwner(toPlayerModelGRPC(p))
                                .setSlotType(typeSlot)
                                .build())
                        .build());
            }

            @Override
            public void leaveShop(SimpleUnit action) {
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
            request = stub.cardBattleShopStream(response);

            request.onNext(CardBattle.shopStream.newBuilder()
                    .setPlayerJoin(toPlayerModelGRPC(player.Owner))
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
                case PLAYERJOIN:
                    shopStreamEvent.onConnected(controller);
                    break;

                case SHOPREFRESHTIME :
                    shopStreamEvent.onShopCountDown(holder.event.getShopRefreshTime());
                    break;

                case SHOPREFRESH :
                    shopStreamEvent.onShopRefreshed();
                    break;

                case ONEPLAYERWITHCARDS :
                    shopStreamEvent.onGetPlayerData(toPlayerWithCardsModel(holder.event.getOnePlayerWithCards()));
                    break;

                case ALLCARDINSHOPPING :
                    shopStreamEvent.onAllCardInShop(toAllCardModel(holder.event.getAllCardInShopping()).Cards);
                    break;

                case ONCARDBOUGHT :
                    shopStreamEvent.onCardBought(holder.event.getOnCardBought());
                    break;

                case ONCARDSOLD :
                    shopStreamEvent.onCardSold(holder.event.getOnCardSold());
                    break;

                case ONSUCCESSADDSLOT :
                    shopStreamEvent.onAddCardSlot(holder.event.getOnSuccessAddSlot());
                    break;

                case EXCMESSAGE :
                    shopStreamEvent.onException(holder.event.getExcMessage().getExceptionMessage(), holder.event .getExcMessage().getExceptionFlag(),controller);
                    break;

                case ONCARDUPGRADED:
                    shopStreamEvent.onCardUpgraded(holder.event.getOnCardUpgraded());
                    break;

                case ONBUYCARD:
                    break;

                case ONUPGRADECARD:
                    break;

                case ONCARDDECKSLOT:
                    break;

                case ONSELLCARD:
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
            shopStreamEvent.onError(error);
            return;
        }

        if (holder.left) {
            holder.actionHolder.invoke();
        }

        shopStreamEvent.onDisconnected();
    }


    class Holder {
        public SimpleUnit actionHolder = new SimpleUnit() {
            @Override
            public void invoke() {

            }
        };
        public Boolean left = false;
        public Boolean stop = false;
        public CardBattle.shopStream event = null;
    }
}
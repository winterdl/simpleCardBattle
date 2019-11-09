package com.syahputrareno975.simplecardbattle.task

import android.os.AsyncTask
import cardBattle.CardBattle
import cardBattle.cardBattleServiceGrpc
import com.syahputrareno975.simplecardbattle.interfaces.RoomStreamEvent
import com.syahputrareno975.simplecardbattle.interfaces.ShopStreamEvent
import com.syahputrareno975.simplecardbattle.interfaces.ShopStreamEventController
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toAllCardModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toAllCardModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toCardModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerWithCardsModelGRPC
import com.syahputrareno975.simplecardbattle.model.NetworkConfig
import com.syahputrareno975.simplecardbattle.model.card.AllCardModel
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.TimeUnit

class ShopStreamTask : AsyncTask<Void,Void,Boolean> {
    lateinit var channel: ManagedChannel
    var player : PlayerWithCardsModel
    var net: NetworkConfig
    var shopStreamEvent : ShopStreamEvent
    private var failed: Throwable? = null

    lateinit var controller : ShopStreamEventController
    lateinit var request : StreamObserver<CardBattle.shopStream>
    lateinit var response : StreamObserver<CardBattle.shopStream>
    var error: String = ""

    constructor(player : PlayerWithCardsModel,net: NetworkConfig,shopStreamEvent : ShopStreamEvent) : super() {
        this.player = player
        this.shopStreamEvent = shopStreamEvent
        this.net = net
    }

    override fun onPreExecute() {
        super.onPreExecute()
        this.channel = ManagedChannelBuilder.forAddress(this.net.Url, this.net.Port)
            .usePlaintext(true)
            .build()


        response = object : StreamObserver<CardBattle.shopStream> {
            override fun onNext(value: CardBattle.shopStream?) {
                holder.event = value
                publishProgress()
            }

            override fun onError(t: Throwable?) {
                failed = t
                error += t!!.message
                holder.stop = true
            }

            override fun onCompleted() {
                holder.stop = true
            }
        }

        controller = object  : ShopStreamEventController {
            override fun getMyPlayerData(id: String) {
                val p = PlayerWithCardsModel()
                p.Owner.Id = id
                request.onNext(CardBattle.shopStream.newBuilder()
                    .setOnePlayerWithCards(toPlayerWithCardsModelGRPC(p))
                    .build())
            }

            override fun getAllCardInShop(p: PlayerModel) {
                request.onNext(CardBattle.shopStream.newBuilder()
                    .setAllCardInShopping(toAllCardModelGRPC(AllCardModel()))
                    .build())
            }

            override fun buyCardFromShop(p: PlayerModel, c: CardModel) {
                request.onNext(CardBattle.shopStream.newBuilder()
                    .setOnBuyCard(CardBattle.playerAndCard.newBuilder()
                        .setClient(toPlayerModelGRPC(p))
                        .setCardData(toCardModelGRPC(c))
                        .build())
                    .build())
            }

            override fun sellCardToShop(p: PlayerModel, c: CardModel) {
                request.onNext(CardBattle.shopStream.newBuilder()
                    .setOnSellCard(CardBattle.playerAndCard.newBuilder()
                        .setClient(toPlayerModelGRPC(p))
                        .setCardData(toCardModelGRPC(c))
                        .build())
                    .build())
            }

            override fun addDeckSlot(p: PlayerModel, typeSlot: Int) {
                request.onNext(CardBattle.shopStream.newBuilder()
                    .setOnCardDeckSlot(CardBattle.playerAndSlot.newBuilder()
                        .setOwner(toPlayerModelGRPC(p))
                        .setSlotType(typeSlot)
                        .build())
                    .build())
            }

            override fun leaveShop(action: () -> Unit) {
                holder.left = true
                holder.stop = true
                holder.actionHolder = action
            }
        }
    }

    val holder = Holder()

    override fun doInBackground(vararg params: Void?): Boolean {
        try {
            val stub = cardBattleServiceGrpc.newStub(this.channel)
            request = stub.cardBattleShopStream(response)

            request.onNext(CardBattle.shopStream.newBuilder()
                .setPlayerJoin(toPlayerModelGRPC(player.Owner))
                .build())


            while (!holder.stop){
                // infinite loop
            }

            request.onCompleted()


        } catch (e : Exception){
            error += e.message
        }

        return true
    }

    override fun onProgressUpdate(vararg values: Void?) {
        super.onProgressUpdate(*values)
        if (holder.event != null){
            when (holder.event!!.eventCase){
                CardBattle.shopStream.EventCase.PLAYERJOIN -> {
                    shopStreamEvent.onConnected(controller)
                }
                CardBattle.shopStream.EventCase.SHOPREFRESHTIME -> {
                    shopStreamEvent.onShopCountDown(holder.event!!.shopRefreshTime)
                }
                CardBattle.shopStream.EventCase.SHOPREFRESH -> {
                    shopStreamEvent.onShopRefreshed()
                }
                CardBattle.shopStream.EventCase.ONEPLAYERWITHCARDS -> {
                    shopStreamEvent.onGetPlayerData(toPlayerWithCardsModel(holder.event!!.onePlayerWithCards))
                }
                CardBattle.shopStream.EventCase.ALLCARDINSHOPPING -> {
                    shopStreamEvent.onAllCardInShop(toAllCardModel(holder.event!!.allCardInShopping).Cards)
                }
                CardBattle.shopStream.EventCase.ONCARDBOUGHT -> {
                    shopStreamEvent.onCardBought(holder.event!!.onCardBought)
                }
                CardBattle.shopStream.EventCase.ONCARDSOLD -> {
                    shopStreamEvent.onCardSold(holder.event!!.onCardSold)
                }
                CardBattle.shopStream.EventCase.ONSUCCESSADDSLOT -> {
                    shopStreamEvent.onAddCardSlot(holder.event!!.onSuccessAddSlot)
                }
                CardBattle.shopStream.EventCase.EXCMESSAGE -> {
                    shopStreamEvent.onException(holder.event!!.excMessage.exceptionMessage,holder.event!!.excMessage.exceptionFlag,controller)
                }
                CardBattle.shopStream.EventCase.ONBUYCARD -> {

                }
                CardBattle.shopStream.EventCase.ONCARDDECKSLOT -> {

                }
                CardBattle.shopStream.EventCase.ONSELLCARD -> {

                }
                else -> {

                }
            }
        }
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        try {
            this.channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: NullPointerException) {
            throw RuntimeException(e)
        }

        if (error != "") {
            shopStreamEvent.onError(error)
            return
        }

        if (holder.left) {
            holder.actionHolder.invoke()
        }

        shopStreamEvent.onDisconnected()
    }

    class Holder {
        var actionHolder : () -> Unit = {}
        var left = false
        var stop = false
        var event :CardBattle.shopStream? = null
    }
}
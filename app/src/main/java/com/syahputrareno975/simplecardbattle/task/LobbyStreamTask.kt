package com.syahputrareno975.simplecardbattle.task

import android.os.AsyncTask
import cardBattle.CardBattle
import cardBattle.cardBattleServiceGrpc
import com.syahputrareno975.simplecardbattle.interfaces.LobbyStreamController
import com.syahputrareno975.simplecardbattle.interfaces.LobbyStreamEvent
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toAllCardModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toAllCardModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toAllPlayerModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toAllPlayerModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toAllRoomModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toAllRoomModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toCardModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerWithCardsModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toRoomModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toRoomModelGRPC
import com.syahputrareno975.simplecardbattle.model.NetworkConfig
import com.syahputrareno975.simplecardbattle.model.card.AllCardModel
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.AllPlayerModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.room.AllRoomModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.TimeUnit

class LobbyStreamTask : AsyncTask<Void, Void, Boolean> {

    lateinit var channel: ManagedChannel
    var player : PlayerModel
    var lobbyStreamEvent : LobbyStreamEvent
    var net: NetworkConfig
    private var failed: Throwable? = null

    lateinit var controller : LobbyStreamController
    lateinit var request : StreamObserver<CardBattle.lobbyStream>
    lateinit var response : StreamObserver<CardBattle.lobbyStream>
    var error: String = ""

    constructor(player: PlayerModel,net: NetworkConfig,lobbyStreamEvent : LobbyStreamEvent) : super() {
        this.player = player
        this.lobbyStreamEvent = lobbyStreamEvent
        this.net = net
    }

    override fun onPreExecute() {
        super.onPreExecute()
        this.channel = ManagedChannelBuilder.forAddress(this.net.Url, this.net.Port)
            .usePlaintext(true)
            .build()

        response = object : StreamObserver<CardBattle.lobbyStream> {
            override fun onNext(value: CardBattle.lobbyStream?) {
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

        controller = object : LobbyStreamController {
            override fun leftLobbyToBattle() {
                holder.leftLobbyToBattle = true
                holder.stop = true
            }

            override fun leftLobby() {
                holder.leftLobby = true
                holder.stop = true
            }

            override fun buyCardFromShop(p: PlayerModel, c: CardModel) {
                request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setOnBuyCard(CardBattle.playerAndCard.newBuilder()
                        .setClient(toPlayerModelGRPC(p))
                        .setCardData(toCardModelGRPC(c))
                        .build())
                    .build())
            }

            override fun sellCardToShop(p: PlayerModel, c: CardModel) {
                request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setOnSellCard(CardBattle.playerAndCard.newBuilder()
                        .setClient(toPlayerModelGRPC(p))
                        .setCardData(toCardModelGRPC(c))
                        .build())
                    .build())
            }

            override fun addCardToDeck(p: PlayerModel, c: CardModel) {
                request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setAddCardToDeck(CardBattle.playerAndCard.newBuilder()
                        .setClient(toPlayerModelGRPC(p))
                        .setCardData(toCardModelGRPC(c))
                        .build())
                    .build())
            }

            override fun removeCardFromDeck(p: PlayerModel, c: CardModel) {
                request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setRemoveCardFromDeck(CardBattle.playerAndCard.newBuilder()
                        .setClient(toPlayerModelGRPC(p))
                        .setCardData(toCardModelGRPC(c))
                        .build())
                    .build())
            }

            override fun getAllPlayer() {
                request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setGetAllPlayers(toAllPlayerModelGRPC(AllPlayerModel()))
                    .build())
            }

            override fun getAllRoom() {
                request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setGetAllRooms(toAllRoomModelGRPC(AllRoomModel()))
                    .build())
            }

            override fun getOnePlayer(id: String) {
                val p = PlayerModel()
                p.Id = id
                request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setGetOneplayer(toPlayerModelGRPC(p))
                    .build())
            }

            override fun getOneRoom(idRoom: String) {
                val room = RoomDataModel()
                room.Id = idRoom
                request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setGetOneRoom(toRoomModelGRPC(room))
                    .build())
            }

            override fun getAllCardInShop(p: PlayerModel) {
                request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setAllCardInShopping(toAllCardModelGRPC(AllCardModel()))
                    .build())
            }

            override fun leftGame(p: PlayerModel) {
                request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setPlayerLeft(toPlayerModelGRPC(p))
                    .build())
            }

            override fun getMyPlayerData(id: String) {
                val p = PlayerWithCardsModel()
                p.Owner.Id = id
                request.onNext(CardBattle.lobbyStream.newBuilder()
                    .setOnePlayerWithCards(toPlayerWithCardsModelGRPC(p))
                    .build())
            }
        }

    }

    val holder = Holder()

    override fun doInBackground(vararg params: Void?): Boolean {
        try {
            val stub = cardBattleServiceGrpc.newStub(this.channel)
            request = stub.cardBattleLobbyStream(response)

            request.onNext(CardBattle.lobbyStream.newBuilder()
                .setPlayerJoin(toPlayerModelGRPC(player))
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
                CardBattle.lobbyStream.EventCase.PLAYERJOIN -> {
                    lobbyStreamEvent.onPlayerJoin(toPlayerModel(holder.event!!.playerJoin))
                }
                CardBattle.lobbyStream.EventCase.PLAYERLEFT -> {
                    lobbyStreamEvent.onPlayerLeft(toPlayerModel(holder.event!!.playerLeft))
                }
                CardBattle.lobbyStream.EventCase.CREATEROOM -> {
                    lobbyStreamEvent.onRoomCreated(toRoomModel(holder.event!!.createRoom))
                }
                CardBattle.lobbyStream.EventCase.SHOPREFRESHTIME -> {
                    lobbyStreamEvent.onShopCountDown(holder.event!!.shopRefreshTime)
                }
                CardBattle.lobbyStream.EventCase.SHOPREFRESH -> {
                    lobbyStreamEvent.onShopRefreshed()
                }
                CardBattle.lobbyStream.EventCase.GETONEROOM -> {
                    lobbyStreamEvent.onGetOneRoom(toRoomModel(holder.event!!.getOneRoom))
                }
                CardBattle.lobbyStream.EventCase.GETALLROOMS -> {
                    lobbyStreamEvent.onGetAllRoom(toAllRoomModel(holder.event!!.getAllRooms).Rooms)
                }
                CardBattle.lobbyStream.EventCase.GETALLPLAYERS -> {
                    lobbyStreamEvent.onGetAllPlayer(toAllPlayerModel(holder.event!!.getAllPlayers).Players)
                }
                CardBattle.lobbyStream.EventCase.GETONEPLAYER -> {
                    lobbyStreamEvent.onGetOnePlayer(toPlayerModel(holder.event!!.getOneplayer))
                }
                CardBattle.lobbyStream.EventCase.PLAYERSUCCESSJOIN -> {
                    lobbyStreamEvent.onConnected(toPlayerModel(holder.event!!.playerSuccessJoin),controller)
                }
                CardBattle.lobbyStream.EventCase.PLAYERSUCCESSLEFT -> {
                    holder.stop = true
                    lobbyStreamEvent.onPlayerSuccessLeft()
                }
                CardBattle.lobbyStream.EventCase.ONEPLAYERWITHCARDS -> {
                    lobbyStreamEvent.onGetPlayerData(toPlayerWithCardsModel(holder.event!!.onePlayerWithCards))
                }
                CardBattle.lobbyStream.EventCase.ALLCARDINSHOPPING -> {
                    lobbyStreamEvent.onAllCardInShop(toAllCardModel(holder.event!!.allCardInShopping).Cards)
                }
                CardBattle.lobbyStream.EventCase.ONBUYCARD -> {}
                CardBattle.lobbyStream.EventCase.ONCARDBOUGHT -> {
                    lobbyStreamEvent.onCardBought(holder.event!!.onCardBought)
                }
                CardBattle.lobbyStream.EventCase.ONSELLCARD -> {}
                CardBattle.lobbyStream.EventCase.ONCARDSOLD -> {
                    lobbyStreamEvent.onCardSold(holder.event!!.onCardSold)
                }
                CardBattle.lobbyStream.EventCase.ADDCARDTODECK -> {
                    lobbyStreamEvent.onPlayerCardUpdated()
                }
                CardBattle.lobbyStream.EventCase.REMOVECARDFROMDECK -> {
                    lobbyStreamEvent.onPlayerCardUpdated()
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
            lobbyStreamEvent.onError(error)
            return
        }

        if (holder.leftLobbyToBattle){
            lobbyStreamEvent.onLeftLobbyToBattle()
            return
        }

        if (holder.leftLobby){
            lobbyStreamEvent.onLeftLobby()
            return
        }


        lobbyStreamEvent.onDisconnected()
    }

    class Holder {
        var leftLobbyToBattle = false
        var leftLobby = false
        var stop = false
        var event : CardBattle.lobbyStream? = null
    }

}
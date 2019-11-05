package com.syahputrareno975.simplecardbattle.task

import android.os.AsyncTask
import cardBattle.CardBattle
import cardBattle.cardBattleServiceGrpc
import com.syahputrareno975.simplecardbattle.interfaces.LobbyStreamController
import com.syahputrareno975.simplecardbattle.interfaces.RoomStreamEvent
import com.syahputrareno975.simplecardbattle.interfaces.RoomStreamEventController
import com.syahputrareno975.simplecardbattle.model.ModelCasting
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toAllPlayerBattleResultModelModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toAllPlayerBattleResultModelModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toAllPlayerWithCardsModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toCardModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerWithCardsModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toRoomModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toRoomModelGRPC
import com.syahputrareno975.simplecardbattle.model.NetworkConfig
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.TimeUnit

class RoomStreamTask : AsyncTask<Void,Void,Boolean>{

    lateinit var channel: ManagedChannel
    var roomData : RoomDataModel
    var player : PlayerWithCardsModel
    var net: NetworkConfig
    var roomStreamEvent :  RoomStreamEvent
    private var failed: Throwable? = null

    lateinit var controller : RoomStreamEventController
    lateinit var request : StreamObserver<CardBattle.roomStream>
    lateinit var response : StreamObserver<CardBattle.roomStream>
    var error: String = ""

    constructor(player : PlayerWithCardsModel,roomData : RoomDataModel,net: NetworkConfig,roomStreamEvent : RoomStreamEvent) : super() {
        this.player = player
        this.roomData = roomData
        this.roomStreamEvent = roomStreamEvent
        this.net = net
    }

    override fun onPreExecute() {
        super.onPreExecute()
        this.channel = ManagedChannelBuilder.forAddress(this.net.Url, this.net.Port)
            .usePlaintext(true)
            .build()


        response = object : StreamObserver<CardBattle.roomStream> {
            override fun onNext(value: CardBattle.roomStream?) {
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

        controller = object : RoomStreamEventController {
            override fun playerJoin(p: PlayerModel) {
                // left this empty
                // because its already send when connected
            }

            override fun getOneRoom(id: String) {
                request.onNext(CardBattle.roomStream.newBuilder()
                    .setIdRoom(roomData.Id)
                    .setGetOneRoom(toRoomModelGRPC(roomData))
                    .build())
            }

            override fun deployCard(p: PlayerModel, c: CardModel) {
                request.onNext(CardBattle.roomStream.newBuilder()
                    .setIdRoom(roomData.Id)
                    .setDeployCard(CardBattle.playerAndCard.newBuilder()
                        .setCardData(toCardModelGRPC(c))
                        .setClient(toPlayerModelGRPC(p))
                        .build())
                    .build())
            }

            override fun pickUpCard(p: PlayerModel, c: CardModel) {
                request.onNext(CardBattle.roomStream.newBuilder()
                    .setIdRoom(roomData.Id)
                    .setPickupCard(CardBattle.playerAndCard.newBuilder()
                        .setCardData(toCardModelGRPC(c))
                        .setClient(toPlayerModelGRPC(p))
                        .build())
                    .build())
            }

            override fun leftGame(p: PlayerModel, r: RoomDataModel) {
                request.onNext(CardBattle.roomStream.newBuilder()
                    .setIdRoom(roomData.Id)
                    .setPlayerLeft(toPlayerWithCardsModelGRPC(player))
                    .build())
            }
        }
    }

    val holder = Holder()

    override fun doInBackground(vararg params: Void?): Boolean {
        try {
            val stub = cardBattleServiceGrpc.newStub(this.channel)
            request = stub.cardBattleRoomStream(response)

            request.onNext(CardBattle.roomStream.newBuilder()
                .setIdRoom(roomData.Id)
                .setPlayerJoin(toPlayerWithCardsModelGRPC(player))
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
                cardBattle.CardBattle.roomStream.EventCase.COUNTDOWN -> {
                    roomStreamEvent.onCountDown(holder.event!!.countDown)
                }
                cardBattle.CardBattle.roomStream.EventCase.PLAYERJOIN -> {
                    val p = toPlayerWithCardsModel(holder.event!!.playerJoin).Owner
                    if (p.Id == player.Owner.Id) {
                        roomStreamEvent.onConnected(controller)
                    }
                    roomStreamEvent.onPlayerJoin(toPlayerWithCardsModel(holder.event!!.playerJoin).Owner)
                }
                cardBattle.CardBattle.roomStream.EventCase.PLAYERLEFT -> {
                    val p = toPlayerWithCardsModel(holder.event!!.playerLeft).Owner
                    if (p.Id == player.Owner.Id) {
                        holder.stop = true
                        holder.left = true
                    }
                    roomStreamEvent.onPlayerLeft(p)
                }
                cardBattle.CardBattle.roomStream.EventCase.ONROOMUPDATE -> {
                    roomStreamEvent.onRoomUpdate(toRoomModel(holder.event!!.onRoomUpdate))
                }
                cardBattle.CardBattle.roomStream.EventCase.RESULT -> {
                    roomStreamEvent.onResult(toAllPlayerBattleResultModelModel(holder.event!!.result))
                }
                cardBattle.CardBattle.roomStream.EventCase.ONWINNER -> {
                    holder.stop = true
                    roomStreamEvent.onWinner(toPlayerModel(holder.event!!.onWinner))
                }
                cardBattle.CardBattle.roomStream.EventCase.DEPLOYCARD -> {

                    // left this empty

                }
                cardBattle.CardBattle.roomStream.EventCase.PICKUPCARD -> {

                    // left this empty

                }
                cardBattle.CardBattle.roomStream.EventCase.GETONEROOM -> {
                    roomStreamEvent.onGetRoomData(toRoomModel(holder.event!!.getOneRoom))
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
            roomStreamEvent.onError(error)
            return
        }

        if (holder.left) {
            roomStreamEvent.onLeft()
        }

        roomStreamEvent.onDisconnected()

    }

    class Holder {
        var left = false
        var stop = false
        var event : CardBattle.roomStream? = null
    }
}
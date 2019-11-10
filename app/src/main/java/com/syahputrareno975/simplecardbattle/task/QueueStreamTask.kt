package com.syahputrareno975.simplecardbattle.task

import android.os.AsyncTask
import cardBattle.CardBattle
import cardBattle.cardBattleServiceGrpc
import com.syahputrareno975.simplecardbattle.interfaces.QueueStreamEvent
import com.syahputrareno975.simplecardbattle.interfaces.QueueStreamEventController
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerModelGRPC
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toRoomModel
import com.syahputrareno975.simplecardbattle.model.NetworkConfig
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.TimeUnit

class QueueStreamTask : AsyncTask<Void,Void,Boolean> {

    lateinit var channel: ManagedChannel
    var player : PlayerWithCardsModel
    var net: NetworkConfig
    var streamEvent :  QueueStreamEvent
    private var failed: Throwable? = null

    lateinit var controller : QueueStreamEventController
    lateinit var request : StreamObserver<CardBattle.queueStream>
    lateinit var response : StreamObserver<CardBattle.queueStream>
    var error: String = ""

    constructor(player: PlayerWithCardsModel, net: NetworkConfig, streamEvent: QueueStreamEvent) : super() {
        this.player = player
        this.net = net
        this.streamEvent = streamEvent
    }


    override fun onPreExecute() {
        super.onPreExecute()
        this.channel = ManagedChannelBuilder.forAddress(this.net.Url, this.net.Port)
            .usePlaintext(true)
            .build()


        response = object : StreamObserver<CardBattle.queueStream> {
            override fun onNext(value: CardBattle.queueStream?) {
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

        controller = object : QueueStreamEventController {

            override fun leftWaitingRoom(p: PlayerModel,action :()-> Unit) {
                holder.actionHolder = action
                request.onNext(CardBattle.queueStream.newBuilder()
                    .setOnLeftWaitingRoom(toPlayerModelGRPC(player.Owner))
                    .build())
            }
        }
    }


    val holder = Holder()
    override fun doInBackground(vararg params: Void?): Boolean {
        try {
            val stub = cardBattleServiceGrpc.newStub(this.channel)
            request = stub.cardBattleQueueStream(response)

            request.onNext(CardBattle.queueStream.newBuilder()
                .setOnjoinWaitingRoom(toPlayerModelGRPC(player.Owner))
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
                CardBattle.queueStream.EventCase.ONJOINWAITINGROOM -> {
                    streamEvent.onEnterQueue(controller)
                    streamEvent.onJoinWaitingRoom()
                }
                CardBattle.queueStream.EventCase.ONLEFTWAITINGROOM -> {
                    holder.left = true
                    holder.stop = true
                }
                CardBattle.queueStream.EventCase.ONBATTLEFOUND -> {
                    streamEvent.onBattleFound(toRoomModel(holder.event!!.onBattleFound))
                }
                CardBattle.queueStream.EventCase.ONBATTLENOTFOUND -> {
                    streamEvent.onBattleNotFound()
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
            streamEvent.onError(error)
            return
        }


        if (holder.left) {
            holder.actionHolder.invoke()
        }

        streamEvent.onDisconnected()
    }
    class Holder {
        var actionHolder : () -> Unit = {}
        var left = false
        var stop = false
        var event : CardBattle.queueStream? = null
    }
}
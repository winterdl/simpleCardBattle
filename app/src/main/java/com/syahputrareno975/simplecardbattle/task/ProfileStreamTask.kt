package com.syahputrareno975.simplecardbattle.task

import android.os.AsyncTask
import cardBattle.CardBattle
import cardBattle.cardBattleServiceGrpc
import com.syahputrareno975.simplecardbattle.interfaces.ProfileStreamEvent
import com.syahputrareno975.simplecardbattle.interfaces.ProfileStreamEventController
import com.syahputrareno975.simplecardbattle.interfaces.QueueStreamEvent
import com.syahputrareno975.simplecardbattle.interfaces.QueueStreamEventController
import com.syahputrareno975.simplecardbattle.model.ModelCasting
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.NetworkConfig
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.TimeUnit

class ProfileStreamTask  : AsyncTask<Void,Void,Boolean> {

    lateinit var channel: ManagedChannel
    var player : PlayerWithCardsModel
    var net: NetworkConfig
    var streamEvent : ProfileStreamEvent
    private var failed: Throwable? = null

    lateinit var controller : ProfileStreamEventController
    lateinit var request : StreamObserver<CardBattle.profileStream>
    lateinit var response : StreamObserver<CardBattle.profileStream>
    var error: String = ""

    constructor(player: PlayerWithCardsModel, net: NetworkConfig, streamEvent : ProfileStreamEvent) : super() {
        this.player = player
        this.net = net
        this.streamEvent = streamEvent
    }

    override fun onPreExecute() {
        super.onPreExecute()
        this.channel = ManagedChannelBuilder.forAddress(this.net.Url, this.net.Port)
            .usePlaintext(true)
            .build()

        response = object : StreamObserver<CardBattle.profileStream> {
            override fun onNext(value: CardBattle.profileStream?) {
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

        controller = object : ProfileStreamEventController {
            override fun getMyPlayerData(id: String) {
                val p = PlayerWithCardsModel()
                p.Owner.Id = id
                request.onNext(CardBattle.profileStream.newBuilder()
                    .setOnePlayerWithCards(ModelCasting.toPlayerWithCardsModelGRPC(p))
                    .build())
            }

            override fun addCardToDeck(p: PlayerModel, c: CardModel) {
                request.onNext(CardBattle.profileStream.newBuilder()
                    .setAddCardToDeck(CardBattle.playerAndCard.newBuilder()
                        .setClient(ModelCasting.toPlayerModelGRPC(p))
                        .setCardData(ModelCasting.toCardModelGRPC(c))
                        .build())
                    .build())
            }

            override fun removeCardFromDeck(p: PlayerModel, c: CardModel) {
                request.onNext(CardBattle.profileStream.newBuilder()
                    .setRemoveCardFromDeck(CardBattle.playerAndCard.newBuilder()
                        .setClient(ModelCasting.toPlayerModelGRPC(p))
                        .setCardData(ModelCasting.toCardModelGRPC(c))
                        .build())
                    .build())
            }

            override fun leaveProfile(action: () -> Unit) {
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
            request = stub.cardBattleProfileStream(response)

            streamEvent.onConnected(controller)

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
                CardBattle.profileStream.EventCase.ADDCARDTODECK -> {
                    streamEvent.onPlayerCardUpdated()
                }
                CardBattle.profileStream.EventCase.ONEPLAYERWITHCARDS -> {
                    streamEvent.onGetPlayerData(toPlayerWithCardsModel(holder.event!!.onePlayerWithCards))
                }
                CardBattle.profileStream.EventCase.REMOVECARDFROMDECK -> {
                    streamEvent.onPlayerCardUpdated()
                }
                CardBattle.profileStream.EventCase.EXCMESSAGE -> {
                    streamEvent.onException(holder.event!!.excMessage.exceptionMessage,holder.event!!.excMessage.exceptionFlag,controller)
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
        var event : CardBattle.profileStream? = null
    }
}
package com.syahputrareno975.simplecardbattle.task

import android.os.AsyncTask
import cardBattle.CardBattle
import cardBattle.cardBattleServiceGrpc
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toPlayerModelGRPC
import com.syahputrareno975.simplecardbattle.model.NetworkConfig
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.lang.Exception
import java.util.concurrent.TimeUnit

class RegisterTask : AsyncTask<Void,Void, CardBattle.player> {

    lateinit var channel: ManagedChannel
    var player : PlayerModel
    var net: NetworkConfig
    var onLogin : (PlayerModel,String) -> Unit
    var error = ""

    constructor(player: PlayerModel, net: NetworkConfig, onLogin: (PlayerModel, String) -> Unit) : super() {
        this.player = player
        this.net = net
        this.onLogin = onLogin
    }

    override fun onPreExecute() {
        super.onPreExecute()
        this.channel = ManagedChannelBuilder.forAddress(this.net.Url, this.net.Port)
            .usePlaintext(true)
            .build()
    }

    override fun doInBackground(vararg params: Void?): CardBattle.player? {
        var response : CardBattle.player? = null
        try {

            val stub = cardBattleServiceGrpc.newBlockingStub(this.channel)
            response = stub.cardBattleRegister(toPlayerModelGRPC(player))

        }catch (e : Exception){
            error += e.message
        }

        return response
    }


    override fun onPostExecute(result: CardBattle.player?) {
        super.onPostExecute(result)

        try {
            this.channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: NullPointerException) {
            throw RuntimeException(e)
        }

        if (result == null){
            onLogin.invoke(PlayerModel(),error)
            return
        }

        onLogin.invoke(toPlayerModel(result),error)
    }

}
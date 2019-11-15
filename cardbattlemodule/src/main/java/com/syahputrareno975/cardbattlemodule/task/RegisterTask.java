package com.syahputrareno975.cardbattlemodule.task;

import android.os.AsyncTask;
import cardBattle.CardBattle;
import cardBattle.cardBattleServiceGrpc;

import com.syahputrareno975.cardbattlemodule.interfaces.onLoginResponse;
import com.syahputrareno975.cardbattlemodule.model.NetworkConfig;
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.lang.Exception;
import java.util.concurrent.TimeUnit;

import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerModel;
import static com.syahputrareno975.cardbattlemodule.model.ModelCasting.toPlayerModelGRPC;

public class RegisterTask extends AsyncTask<Void,Void, CardBattle.player> {

    private ManagedChannel channel;
    private PlayerModel player;
    private NetworkConfig net;
    private onLoginResponse onLogin;
    private String error = "";

    public RegisterTask(PlayerModel player, NetworkConfig net, onLoginResponse onLogin) {
        this.player = player;
        this.net = net;
        this.onLogin = onLogin;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.channel = ManagedChannelBuilder.forAddress(this.net.Url, this.net.Port)
                .usePlaintext(true)
                .build();
    }

    @Override
    protected CardBattle.player doInBackground(Void... voids) {
        CardBattle.player response = null;
        try {

            cardBattleServiceGrpc.cardBattleServiceBlockingStub stub = cardBattleServiceGrpc.newBlockingStub(this.channel);
            response = stub.cardBattleRegister(toPlayerModelGRPC(player));

        }catch (Exception e){
            error += e.getMessage();
        }

        return response;
    }

    @Override
    protected void onPostExecute(CardBattle.player result) {
        super.onPostExecute(result);
        try {
            this.channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }

        if (result == null){
            onLogin.invoke(new PlayerModel(),error);
            return;
        }

        onLogin.invoke(toPlayerModel(result),error);
    }

}
package com.syahputrareno975.cardbattlemodule.interfaces;

import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import com.syahputrareno975.cardbattlemodule.model.playerWithCard.PlayerWithCardsModel;

import java.util.ArrayList;

public interface LobbyStreamEvent {
    void onConnected(PlayerModel p,LobbyStreamEventController c);
    void onPlayerJoin(PlayerModel p);
    void onGetOnePlayer(PlayerModel p);
    void onGetAllPlayer(ArrayList<PlayerModel> p );
    void onGetPlayerData( PlayerWithCardsModel p);
    void onError(String e);
    void onPlayerLeft(PlayerModel p);
    void onPlayerSuccessLeft();
    void onDisconnected();
    void onException(String e,int flag ,LobbyStreamEventController c);
}
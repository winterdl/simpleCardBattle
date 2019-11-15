package com.syahputrareno975.cardbattlemodule.interfaces;

import com.syahputrareno975.cardbattlemodule.model.playerWithCard.PlayerWithCardsModel;

public interface ProfileStreamEvent {
    void onConnected(ProfileStreamEventController c);
    void onGetPlayerData(PlayerWithCardsModel p);
    void onPlayerCardUpdated();
    void onDisconnected();
    void onError(String e);
    void onException( String e,int flag,ProfileStreamEventController c);
}
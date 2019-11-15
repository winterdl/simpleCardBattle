package com.syahputrareno975.cardbattlemodule.interfaces;

import com.syahputrareno975.cardbattlemodule.model.card.CardModel;
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;

public interface ProfileStreamEventController {
    void getMyPlayerData(String id);
    void addCardToDeck(PlayerModel p,CardModel c);
    void removeCardFromDeck(PlayerModel p, CardModel c);
    void leaveProfile(SimpleUnit action);
}
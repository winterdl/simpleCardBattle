package com.syahputrareno975.cardbattlemodule.interfaces;

import com.syahputrareno975.cardbattlemodule.model.card.CardModel;
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;

public interface ShopStreamEventController {
    void getMyPlayerData(String id);
    void getAllCardInShop(PlayerModel p);
    void buyCardFromShop(PlayerModel p,CardModel c);
    void sellCardToShop(PlayerModel p,CardModel c);
    void upgradeCard(PlayerModel p,CardModel c);
    void addDeckSlot(PlayerModel p,int typeSlot);
    void leaveShop(SimpleUnit action);
}
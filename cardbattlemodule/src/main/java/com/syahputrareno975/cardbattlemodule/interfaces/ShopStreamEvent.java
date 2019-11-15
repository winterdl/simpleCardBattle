package com.syahputrareno975.cardbattlemodule.interfaces;

import com.syahputrareno975.cardbattlemodule.model.card.CardModel;
import com.syahputrareno975.cardbattlemodule.model.playerWithCard.PlayerWithCardsModel;

import java.util.ArrayList;

public interface ShopStreamEvent {
    void onConnected(ShopStreamEventController c);
    void onGetPlayerData(PlayerWithCardsModel p);
    void onAllCardInShop(ArrayList<CardModel> c);
    void onCardBought( Boolean success);
    void onCardSold( Boolean success);
    void onCardUpgraded(Boolean success);
    void onAddCardSlot(Boolean success);
    void onShopCountDown(int i);
    void onShopRefreshed();
    void onDisconnected();
    void onError(String e);
    void onException(String e,int flag,ShopStreamEventController c);
}
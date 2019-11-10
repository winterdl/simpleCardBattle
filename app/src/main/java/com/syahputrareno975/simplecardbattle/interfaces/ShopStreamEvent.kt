package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel

interface ShopStreamEvent {
    fun onConnected(c : ShopStreamEventController)
    fun onGetPlayerData(p : PlayerWithCardsModel)
    fun onAllCardInShop(c : ArrayList<CardModel>)
    fun onCardBought(success : Boolean)
    fun onCardSold(success : Boolean)
    fun onCardUpgraded(success : Boolean)
    fun onAddCardSlot(success : Boolean)
    fun onShopCountDown(i : Int)
    fun onShopRefreshed()
    fun onDisconnected()
    fun onError(e : String)
    fun onException(e : String,flag : Int,c : ShopStreamEventController)
}
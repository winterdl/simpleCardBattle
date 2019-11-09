package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel

interface LobbyStreamEvent {
    fun onConnected(p : PlayerModel,c : LobbyStreamController)
    fun onPlayerJoin(p : PlayerModel)
    fun onShopCountDown(i : Int)
    fun onShopRefreshed()
    fun onGetOnePlayer(p : PlayerModel)
    fun onGetAllPlayer(p : ArrayList<PlayerModel>)
    fun onRoomCreated(r : RoomDataModel)
    fun onGetOneRoom(r : RoomDataModel)
    fun onGetAllRoom(r : ArrayList<RoomDataModel>)
    fun onAllCardInShop(c : ArrayList<CardModel>)
    fun onCardBought(success : Boolean)
    fun onCardSold(success : Boolean)
    fun onAddCardSlot(success : Boolean)
    fun onPlayerCardUpdated()
    fun onGetPlayerData(p : PlayerWithCardsModel)
    fun onError(e : String)
    fun onPlayerLeft(p : PlayerModel)
    fun onPlayerSuccessLeft()
    fun onLeftLobby()
    fun onLeftLobbyToBattle()
    fun onDisconnected()
    fun onForceQuit()
    fun onException(e : String,flag : Int,c : LobbyStreamController)
}
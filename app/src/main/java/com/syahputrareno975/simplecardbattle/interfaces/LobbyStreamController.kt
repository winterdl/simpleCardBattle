package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel

interface LobbyStreamController {
    fun getAllPlayer()
    fun getAllRoom()
    fun getOnePlayer(id : String)
    fun getOneRoom(idRoom : String)
    fun getAllCardInShop(p : PlayerModel)
    fun leftGame(p : PlayerModel)
    fun leftLobby()
    fun leftLobbyToBattle()
    fun getMyPlayerData(id : String)
    fun addCardToDeck(p: PlayerModel,c : CardModel)
    fun removeCardFromDeck(p: PlayerModel,c : CardModel)
    fun buyCardFromShop(p: PlayerModel,c : CardModel)
    fun sellCardToShop(p: PlayerModel,c : CardModel)
    fun joinWaitingRoom(p : PlayerModel)
    fun leftWaitingRoom(p : PlayerModel)
}
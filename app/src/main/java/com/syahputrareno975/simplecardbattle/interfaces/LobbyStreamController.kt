package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel

interface LobbyStreamController {
    fun getAllPlayer()
    fun getOnePlayer(id : String)
    fun getMyPlayerData(id : String)
    fun leftGame(p : PlayerModel,action :() -> Unit)
    fun leftLobby(action : () -> Unit)
    fun addCardToDeck(p: PlayerModel,c : CardModel)
    fun removeCardFromDeck(p: PlayerModel,c : CardModel)
}
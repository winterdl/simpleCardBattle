package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel

interface LobbyStreamEventController {
    fun getAllPlayer()
    fun getOnePlayer(id : String)
    fun getMyPlayerData(id : String)
    fun leftGame(p : PlayerModel,action :() -> Unit)
    fun leftLobby(action : () -> Unit)
}
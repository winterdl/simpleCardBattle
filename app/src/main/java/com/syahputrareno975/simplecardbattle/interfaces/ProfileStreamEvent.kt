package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel

interface ProfileStreamEvent {
    fun onConnected(c : ProfileStreamEventController)
    fun onGetPlayerData(p : PlayerWithCardsModel)
    fun onPlayerCardUpdated()
    fun onDisconnected()
    fun onError(e : String)
    fun onException(e : String,flag : Int,c : ProfileStreamEventController)
}
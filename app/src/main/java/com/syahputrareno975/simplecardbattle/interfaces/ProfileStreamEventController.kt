package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel

interface ProfileStreamEventController {
    fun getMyPlayerData(id : String)
    fun addCardToDeck(p: PlayerModel, c : CardModel)
    fun removeCardFromDeck(p: PlayerModel, c : CardModel)
    fun leaveProfile(action : () -> Unit)
}
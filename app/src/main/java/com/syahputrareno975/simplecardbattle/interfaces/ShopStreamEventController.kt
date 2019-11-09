package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel

interface ShopStreamEventController {
    fun getMyPlayerData(id : String)
    fun getAllCardInShop(p : PlayerModel)
    fun buyCardFromShop(p: PlayerModel,c : CardModel)
    fun sellCardToShop(p: PlayerModel,c : CardModel)
    fun addDeckSlot(p :PlayerModel,typeSlot : Int)
    fun leaveShop(action : () -> Unit)
}
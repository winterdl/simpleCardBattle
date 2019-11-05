package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel

interface RoomStreamEventController {
    fun playerJoin(p : PlayerModel)
    fun getOneRoom(id : String)
    fun deployCard(p : PlayerModel,c : CardModel)
    fun pickUpCard(p : PlayerModel,c : CardModel)
    fun leftGame(p :PlayerModel,r :RoomDataModel)
}
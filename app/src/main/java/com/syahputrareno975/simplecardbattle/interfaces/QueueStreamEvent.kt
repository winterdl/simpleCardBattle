package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel

interface QueueStreamEvent {
    fun onEnterQueue(c : QueueStreamEventController)
    fun onBattleFound(r : RoomDataModel)
    fun onBattleNotFound()
    fun onJoinWaitingRoom()
    fun onLeftWaitingRoom()
    fun toBattle()
    fun onDisconnected()
    fun onError(s : String)
}
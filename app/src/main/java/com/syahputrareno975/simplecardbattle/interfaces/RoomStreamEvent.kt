package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerBattleResult.AllPlayerBattleResultModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel
import com.syahputrareno975.simplecardbattle.model.roomResult.EndResultModel

interface RoomStreamEvent {
    fun onConnected(c : RoomStreamEventController)
    fun onPlayerJoin(p : PlayerModel)
    fun onPlayerLeft(p : PlayerModel)
    fun onRoomUpdate(r : RoomDataModel)
    fun onCountDown(i : Int ,r : RoomDataModel)
    fun onBattleResult(r : AllPlayerBattleResultModel)
    fun onResult(r : EndResultModel)
    fun onDraw(flag : Int)
    fun onGetRoomData(r : RoomDataModel)
    fun onDisconnected()
    fun onError(s : String)
    fun onException(e : String,flag : Int)
}
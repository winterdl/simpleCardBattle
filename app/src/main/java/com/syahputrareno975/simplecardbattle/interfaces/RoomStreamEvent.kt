package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerBattleResult.AllPlayerBattleResultModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel

interface RoomStreamEvent {
    fun onConnected(c : RoomStreamEventController)
    fun onPlayerJoin(p : PlayerModel)
    fun onPlayerLeft(p : PlayerModel)
    fun onRoomUpdate(r : RoomDataModel)
    fun onCountDown(i : Int)
    fun onResult(r : AllPlayerBattleResultModel)
    fun onWinner(p : PlayerModel)
    fun onGetRoomData(r : RoomDataModel)
    fun onLeft()
    fun onDisconnected()
    fun onError(s : String)

}
package com.syahputrareno975.cardbattlemodule.interfaces;

import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import com.syahputrareno975.cardbattlemodule.model.playerBattleResult.AllPlayerBattleResultModel;
import com.syahputrareno975.cardbattlemodule.model.room.RoomDataModel;
import com.syahputrareno975.cardbattlemodule.model.roomResult.EndResultModel;

public interface RoomStreamEvent {
    void onConnected(RoomStreamEventController c);
    void onPlayerJoin(PlayerModel p);
    void onPlayerLeft(PlayerModel p);
    void onRoomUpdate(RoomDataModel r);
    void onCountDown(int i , RoomDataModel r);
    void onBattleResult(AllPlayerBattleResultModel r);
    void onResult(EndResultModel r);
    void onDraw(int flag);
    void onGetRoomData(RoomDataModel r);
    void onDisconnected();
    void onError(String s);
    void onException(String e,int flag);
}
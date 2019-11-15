package com.syahputrareno975.cardbattlemodule.interfaces;

import com.syahputrareno975.cardbattlemodule.model.room.RoomDataModel;

public interface QueueStreamEvent {
    void onEnterQueue(QueueStreamEventController c);
    void onBattleFound(RoomDataModel r);
    void onBattleNotFound();
    void onJoinWaitingRoom();
    void onDisconnected();
    void onError(String s);
}
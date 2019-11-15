package com.syahputrareno975.cardbattlemodule.interfaces;

import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;

public interface QueueStreamEventController {
    void leftWaitingRoom(PlayerModel p ,SimpleUnit action);
}
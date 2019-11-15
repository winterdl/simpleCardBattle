package com.syahputrareno975.cardbattlemodule.interfaces;

import com.syahputrareno975.cardbattlemodule.model.card.CardModel;
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import com.syahputrareno975.cardbattlemodule.model.room.RoomDataModel;

public interface RoomStreamEventController {
    void playerJoin(PlayerModel p);
    void getOneRoom(String id);
    void deployCard(PlayerModel p, CardModel c);
    void pickUpCard(PlayerModel p,CardModel c);
    void leftGame(PlayerModel p,RoomDataModel r,SimpleUnit action);
}
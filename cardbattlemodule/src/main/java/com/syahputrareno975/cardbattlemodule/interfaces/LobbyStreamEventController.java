package com.syahputrareno975.cardbattlemodule.interfaces;

import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;

public interface LobbyStreamEventController {
    void getAllPlayer();
    void getOnePlayer(String id);
    void getMyPlayerData(String id);
    void leftGame(PlayerModel p,SimpleUnit action );
    void leftLobby(SimpleUnit action);
}
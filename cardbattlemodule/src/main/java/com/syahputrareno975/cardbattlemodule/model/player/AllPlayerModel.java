package com.syahputrareno975.cardbattlemodule.model.player;

import java.io.Serializable;
import java.util.ArrayList;

public class AllPlayerModel implements Serializable {

    public ArrayList<PlayerModel> Players = new ArrayList<PlayerModel>();

    public AllPlayerModel() {
    }

    public AllPlayerModel(ArrayList<PlayerModel> players) {
        Players = players;
    }
}
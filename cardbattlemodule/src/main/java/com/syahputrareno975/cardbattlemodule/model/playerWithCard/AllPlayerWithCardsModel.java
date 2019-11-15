package com.syahputrareno975.cardbattlemodule.model.playerWithCard;

import java.io.Serializable;
import java.util.ArrayList;

public class AllPlayerWithCardsModel implements Serializable {
    public ArrayList<PlayerWithCardsModel> Players = new ArrayList<PlayerWithCardsModel>();

    public AllPlayerWithCardsModel() {
    }

    public AllPlayerWithCardsModel(ArrayList<PlayerWithCardsModel> players) {
        Players = players;
    }
}
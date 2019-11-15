package com.syahputrareno975.cardbattlemodule.model.card;

import java.io.Serializable;
import java.util.ArrayList;

public class AllCardModel implements Serializable {

    public ArrayList<CardModel> Cards = new ArrayList<CardModel>();

    public AllCardModel() {
    }

    public AllCardModel(ArrayList<CardModel> cards) {
        Cards = cards;
    }
}
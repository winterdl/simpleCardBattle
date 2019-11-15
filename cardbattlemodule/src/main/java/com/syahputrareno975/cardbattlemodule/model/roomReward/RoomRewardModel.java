package com.syahputrareno975.cardbattlemodule.model.roomReward;

import com.syahputrareno975.cardbattlemodule.model.card.CardModel;
import java.io.Serializable;
import java.util.ArrayList;

public class RoomRewardModel  implements Serializable {
    public ArrayList<CardModel> CardsReward = new ArrayList<CardModel>();
    public Long CashReward = 0L;
    public Long ExpReward  = 0L;

    public RoomRewardModel() {
    }

    public RoomRewardModel(ArrayList<CardModel> cardsReward, Long cashReward, Long expReward) {
        CardsReward = cardsReward;
        CashReward = cashReward;
        ExpReward = expReward;
    }
}
package com.syahputrareno975.cardbattlemodule.model.room;

import com.syahputrareno975.cardbattlemodule.model.playerWithCard.PlayerWithCardsModel;
import com.syahputrareno975.cardbattlemodule.model.roomReward.RoomRewardModel;
import java.io.Serializable;
import java.util.ArrayList;

public class RoomDataModel implements Serializable {

    public String Id = "";
    public String RoomName = "";
    public ArrayList<PlayerWithCardsModel> Players = new ArrayList<PlayerWithCardsModel>();
    public int MaxPlayer = 0;
    public int MaxPlayerDeck = 0;
    public int  MaxCurrentDeployment = 0;
    public int MaxDeploment = 0;
    public Long EachPlayerHealth = 0L;
    public int CoolDownTime = 0;
    public RoomRewardModel Reward = new RoomRewardModel();

    public RoomDataModel() {
    }

    public RoomDataModel(String id, String roomName, ArrayList<PlayerWithCardsModel> players, int maxPlayer, int maxPlayerDeck, int maxCurrentDeployment, int maxDeploment, Long eachPlayerHealth, int coolDownTime, RoomRewardModel reward) {
        Id = id;
        RoomName = roomName;
        Players = players;
        MaxPlayer = maxPlayer;
        MaxPlayerDeck = maxPlayerDeck;
        MaxCurrentDeployment = maxCurrentDeployment;
        MaxDeploment = maxDeploment;
        EachPlayerHealth = eachPlayerHealth;
        CoolDownTime = coolDownTime;
        Reward = reward;
    }
}



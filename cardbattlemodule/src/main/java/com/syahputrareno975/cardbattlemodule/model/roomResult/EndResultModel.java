package com.syahputrareno975.cardbattlemodule.model.roomResult;

import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import com.syahputrareno975.cardbattlemodule.model.playerBattleResult.PlayerBattleResultModel;
import com.syahputrareno975.cardbattlemodule.model.roomReward.RoomRewardModel;
import java.io.Serializable;
import java.util.ArrayList;

public class EndResultModel  implements Serializable {
    public PlayerModel Winner = new PlayerModel();
    public ArrayList<PlayerBattleResultModel> allBattleResult  = new ArrayList<PlayerBattleResultModel>();
    public int FlagResult = 0;
    public RoomRewardModel Reward = new RoomRewardModel();

    public EndResultModel() {
    }

    public EndResultModel(PlayerModel winner, ArrayList<PlayerBattleResultModel> allBattleResult, int flagResult, RoomRewardModel reward) {
        Winner = winner;
        this.allBattleResult = allBattleResult;
        FlagResult = flagResult;
        Reward = reward;
    }
}
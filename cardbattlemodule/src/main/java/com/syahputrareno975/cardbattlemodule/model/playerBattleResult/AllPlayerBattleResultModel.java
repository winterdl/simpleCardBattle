package com.syahputrareno975.cardbattlemodule.model.playerBattleResult;

import java.io.Serializable;
import java.util.ArrayList;

public class AllPlayerBattleResultModel implements Serializable {

    public ArrayList<PlayerBattleResultModel> Results = new ArrayList<PlayerBattleResultModel>();

    public AllPlayerBattleResultModel() {
    }

    public AllPlayerBattleResultModel(ArrayList<PlayerBattleResultModel> results) {
        Results = results;
    }
}

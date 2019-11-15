package com.syahputrareno975.cardbattlemodule.model.playerBattleResult;

import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import java.io.Serializable;

public class PlayerBattleResultModel implements Serializable {

    public PlayerModel Owner = new PlayerModel();
    public Long DamageReceive  = 0L;
    public Long EnemyAtk = 0L;
    public Long OwnerDef = 0L;

    public PlayerBattleResultModel() {
    }

    public PlayerBattleResultModel(PlayerModel owner, Long damageReceive, Long enemyAtk, Long ownerDef) {
        Owner = owner;
        DamageReceive = damageReceive;
        EnemyAtk = enemyAtk;
        OwnerDef = ownerDef;
    }
}
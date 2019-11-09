package com.syahputrareno975.simplecardbattle.model.playerBattleResult

import java.io.Serializable
import cardBattle.CardBattle.player
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel


class PlayerBattleResultModel : Serializable {
    var Owner = PlayerModel()
    var DamageReceive: Long = 0
    var EnemyAtk: Long = 0
    var OwnerDef: Long = 0

    constructor()
    constructor(Owner: PlayerModel, DamageReceive: Long, enemyAtk: Long, ownerDef: Long) {
        this.Owner = Owner
        this.DamageReceive = DamageReceive
        this.EnemyAtk = enemyAtk
        this.OwnerDef = ownerDef
    }


}
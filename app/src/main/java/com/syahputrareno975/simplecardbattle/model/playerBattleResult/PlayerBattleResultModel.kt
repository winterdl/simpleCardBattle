package com.syahputrareno975.simplecardbattle.model.playerBattleResult

import java.io.Serializable
import cardBattle.CardBattle.player
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel


class PlayerBattleResultModel : Serializable {
    var Owner = PlayerModel()
    var DamageReceive = 0

    constructor()

    constructor(Owner: PlayerModel, DamageReceive: Int) {
        this.Owner = Owner
        this.DamageReceive = DamageReceive
    }


}
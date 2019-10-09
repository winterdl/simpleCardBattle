package com.syahputrareno975.simplecardbattle.model.playerBattleResult

import java.io.Serializable

class AllPlayerBattleResultModel : Serializable {

    var Results = ArrayList<PlayerBattleResultModel>()

    constructor()

    constructor(Results: ArrayList<PlayerBattleResultModel>) {
        this.Results = Results
    }
}
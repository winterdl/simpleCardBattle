package com.syahputrareno975.simplecardbattle.model.roomResult

import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerBattleResult.PlayerBattleResultModel
import com.syahputrareno975.simplecardbattle.model.roomReward.RoomRewardModel
import java.io.Serializable

class EndResultModel  : Serializable {
    var Winner : PlayerModel = PlayerModel()
    var allBattleResult : ArrayList<PlayerBattleResultModel> = ArrayList<PlayerBattleResultModel>()
    var FlagResult = 0
    var Reward : RoomRewardModel = RoomRewardModel()

    constructor()

    constructor(Winner: PlayerModel, allBattleResult: ArrayList<PlayerBattleResultModel>, FlagResult: Int, Reward: RoomRewardModel) {
        this.Winner = Winner
        this.allBattleResult = allBattleResult
        this.FlagResult = FlagResult
        this.Reward = Reward
    }
}
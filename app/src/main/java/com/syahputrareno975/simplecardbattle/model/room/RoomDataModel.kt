package com.syahputrareno975.simplecardbattle.model.room

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.roomReward.RoomRewardModel
import java.io.Serializable

class RoomDataModel : Serializable {
    var Id = ""
    var RoomName = ""
    var Players = ArrayList<PlayerWithCardsModel>()
    var MaxPlayer = 0
    var MaxPlayerDeck = 0
    var MaxCurrentDeployment = 0
    var MaxDeploment = 0
    var EachPlayerHealth : Long = 0
    var CoolDownTime = 0
    var Reward : RoomRewardModel = RoomRewardModel()


    constructor()
    constructor(
        id: String,
        RoomName: String,
        Players: ArrayList<PlayerWithCardsModel>,
        MaxPlayer: Int,
        MaxPlayerDeck: Int,
        maxCurrentDeployment : Int,
        MaxDeploment: Int,
        EachPlayerHealth: Long,
        CoolDownTime: Int,
        reward : RoomRewardModel
    ) {
        this.Id = id
        this.RoomName = RoomName
        this.Players = Players
        this.MaxPlayer = MaxPlayer
        this.MaxPlayerDeck = MaxPlayerDeck
        this.MaxDeploment = MaxDeploment
        this.EachPlayerHealth = EachPlayerHealth
        this.CoolDownTime = CoolDownTime
        this.Reward = reward
        this.MaxCurrentDeployment = maxCurrentDeployment
    }

}



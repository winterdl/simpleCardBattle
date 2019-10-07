package com.syahputrareno975.simplecardbattle.model.room

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import java.io.Serializable

class RoomDataModel : Serializable {
    var Id = ""
    var RoomName = ""
    var Players = ArrayList<PlayerWithCardsModel>()
    var MaxPlayer = 5
    var MaxPlayerDeck = 6
    var MaxDeploment = 7
    var EachPlayerHealth = 8
    var CoolDownTime = 9
    var CardReward = ArrayList<CardModel>()
    var CashReward = 11
    var LevelReward = 12


    constructor()
    constructor(
        id: String,
        RoomName: String,
        Players: ArrayList<PlayerWithCardsModel>,
        MaxPlayer: Int,
        MaxPlayerDeck: Int,
        MaxDeploment: Int,
        EachPlayerHealth: Int,
        CoolDownTime: Int,
        CardReward: ArrayList<CardModel>,
        CashReward: Int,
        LevelReward: Int
    ) {
        this.Id = id
        this.RoomName = RoomName
        this.Players = Players
        this.MaxPlayer = MaxPlayer
        this.MaxPlayerDeck = MaxPlayerDeck
        this.MaxDeploment = MaxDeploment
        this.EachPlayerHealth = EachPlayerHealth
        this.CoolDownTime = CoolDownTime
        this.CardReward = CardReward
        this.CashReward = CashReward
        this.LevelReward = LevelReward
    }


}



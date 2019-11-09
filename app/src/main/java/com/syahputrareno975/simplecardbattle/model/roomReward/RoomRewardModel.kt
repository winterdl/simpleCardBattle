package com.syahputrareno975.simplecardbattle.model.roomReward

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import java.io.Serializable

class RoomRewardModel  : Serializable {
    var CardsReward = ArrayList<CardModel>()
    var CashReward : Long = 0
    var ExpReward : Long = 0

    constructor()

    constructor(CardsReward: ArrayList<CardModel>, CashReward: Long, ExpReward: Long) {
        this.CardsReward = CardsReward
        this.CashReward = CashReward
        this.ExpReward = ExpReward
    }


}
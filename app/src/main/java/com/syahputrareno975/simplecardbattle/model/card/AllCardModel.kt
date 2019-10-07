package com.syahputrareno975.simplecardbattle.model.card

import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import java.io.Serializable

class AllCardModel :Serializable {

    var Cards = ArrayList<CardModel>()

    constructor()

    constructor(cards: ArrayList<CardModel>) {
        this.Cards = cards
    }

}
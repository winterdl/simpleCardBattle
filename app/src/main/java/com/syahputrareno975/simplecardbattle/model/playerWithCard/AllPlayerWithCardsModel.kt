package com.syahputrareno975.simplecardbattle.model.playerWithCard

import java.io.Serializable

class AllPlayerWithCardsModel :Serializable {
    var Players = ArrayList<PlayerWithCardsModel>()

    constructor()

    constructor(Players: ArrayList<PlayerWithCardsModel>) {
        this.Players = Players
    }


}
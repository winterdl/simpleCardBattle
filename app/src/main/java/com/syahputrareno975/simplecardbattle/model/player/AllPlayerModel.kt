package com.syahputrareno975.simplecardbattle.model.player

import java.io.Serializable

class AllPlayerModel :Serializable {
    var Players = ArrayList<PlayerModel>()

    constructor()

    constructor(Players: ArrayList<PlayerModel>) {
        this.Players = Players
    }

}
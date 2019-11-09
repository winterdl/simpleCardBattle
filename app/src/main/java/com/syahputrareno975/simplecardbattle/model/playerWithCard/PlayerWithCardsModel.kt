package com.syahputrareno975.simplecardbattle.model.playerWithCard

import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import java.io.Serializable

class PlayerWithCardsModel : Serializable {
    var Owner = PlayerModel()
    var Deck = ArrayList<CardModel>()
    var Reserve = ArrayList<CardModel>()
    var Deployed = ArrayList<CardModel>()
    var Hp : Long = 0

    constructor()

    constructor(
        Owner: PlayerModel,
        Deck: ArrayList<CardModel>,
        Reserve: ArrayList<CardModel>,
        Deployed: ArrayList<CardModel>,
        Hp: Long
    ) {
        this.Owner = Owner
        this.Deck = Deck
        this.Reserve = Reserve
        this.Deployed = Deployed
        this.Hp = Hp
    }


    fun copyFromObject(p : PlayerWithCardsModel) {

        this.Reserve.clear()
        this.Deck.clear()
        this.Deployed.clear()

        this.Owner = p.Owner
        this.Deck.addAll(p.Deck)
        this.Reserve.addAll(p.Reserve)
        this.Deployed.addAll(p.Deployed)

        this.Hp = p.Hp
    }


}
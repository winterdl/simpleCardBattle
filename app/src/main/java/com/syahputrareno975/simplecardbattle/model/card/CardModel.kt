package com.syahputrareno975.simplecardbattle.model.card

import java.io.Serializable

class CardModel : Serializable {
    var Id = ""
    var Image = ""
    var Name = ""
    var Level  = 0
    var Atk  = 0
    var Def  = 0
    var Price = 0
    var Color = 0

    var Flag = 0
    var Message = ""

    constructor()

    constructor(
        Id: String,
        Image: String,
        Name: String,
        Level: Int,
        Atk: Int,
        Def: Int,
        Price: Int,
        Color: Int
    ) {
        this.Id = Id
        this.Image = Image
        this.Name = Name
        this.Level = Level
        this.Atk = Atk
        this.Def = Def
        this.Price = Price
        this.Color = Color
    }


}
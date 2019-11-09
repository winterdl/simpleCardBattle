package com.syahputrareno975.simplecardbattle.model.player

import java.io.Serializable

class PlayerModel : Serializable {
    var Id = ""
    var Name = ""
    var Avatar = ""
    var Level = 0
    var Cash: Long = 0
    var Exp : Long = 0
    var MaxExp : Long = 0
    var MaxReserveSlot  = 0
    var MaxDeckSlot = 0

    constructor()
    constructor(
        Id: String,
        Name: String,
        Avatar: String,
        Level: Int,
        Cash: Long,
        Exp: Long,
        MaxExp: Long,
        MaxReserveSlot: Int,
        MaxDeckSlot: Int
    ) {
        this.Id = Id
        this.Name = Name
        this.Avatar = Avatar
        this.Level = Level
        this.Cash = Cash
        this.Exp = Exp
        this.MaxExp = MaxExp
        this.MaxReserveSlot = MaxReserveSlot
        this.MaxDeckSlot = MaxDeckSlot
    }


}
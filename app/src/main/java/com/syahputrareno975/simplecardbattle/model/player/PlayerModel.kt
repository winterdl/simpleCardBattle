package com.syahputrareno975.simplecardbattle.model.player

import java.io.Serializable

class PlayerModel : Serializable {
    var Id = ""
    var Name = ""
    var Avatar = ""
    var Level = 0
    var Cash = 0

    constructor()

    constructor(Id: String, Name: String, Avatar: String, Level: Int, Cash: Int) {
        this.Id = Id
        this.Name = Name
        this.Avatar = Avatar
        this.Level = Level
        this.Cash = Cash
    }


}
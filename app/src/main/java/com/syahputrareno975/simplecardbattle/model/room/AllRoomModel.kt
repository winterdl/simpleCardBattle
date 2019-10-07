package com.syahputrareno975.simplecardbattle.model.room

import java.io.Serializable

class AllRoomModel : Serializable {
    var Rooms = ArrayList<RoomDataModel>()

    constructor()

    constructor(Rooms: ArrayList<RoomDataModel>) {
        this.Rooms = Rooms
    }
}
package com.syahputrareno975.cardbattlemodule.model.room;

import java.io.Serializable;
import java.util.ArrayList;

public class AllRoomModel implements Serializable {
    public ArrayList<RoomDataModel> Rooms = new ArrayList<RoomDataModel>();

    public AllRoomModel() {
    }

    public AllRoomModel(ArrayList<RoomDataModel> rooms) {
        Rooms = rooms;
    }
}
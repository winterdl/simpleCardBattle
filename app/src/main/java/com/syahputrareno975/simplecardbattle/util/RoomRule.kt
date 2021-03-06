package com.syahputrareno975.simplecardbattle.util

import com.syahputrareno975.cardbattlemodule.model.card.CardModel
import com.syahputrareno975.cardbattlemodule.model.room.RoomDataModel

class RoomRule {
    companion object {
        fun getTotalAtk(c : ArrayList<CardModel>) : Long {
            var total: Long = 0
            for (i in c){
                total += i.Atk
            }
            return total
        }
        fun getTotalDef(c : ArrayList<CardModel>) : Long {
            var total : Long= 0
            for (i in c){
                total += i.Def
            }
            return total
        }

        fun findPlayer(id : String,r : RoomDataModel) : Int {
            var pos = 0
            for (i in r.Players){
                if (i.Owner.Id == id) {
                    return pos
                }
                pos++
            }
            return pos
        }
        fun findEnemy(id : String,r : RoomDataModel) : Int {
            var pos = 0
            for (i in r.Players){
                if (i.Owner.Id != id) {
                    return pos
                }
                pos++
            }
            return pos
        }
    }
}
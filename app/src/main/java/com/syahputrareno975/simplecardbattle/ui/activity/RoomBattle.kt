package com.syahputrareno975.simplecardbattle.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel
import com.syahputrareno975.simplecardbattle.util.SerializableSave

class RoomBattle : AppCompatActivity() {
    lateinit var context: Context
    lateinit var IntentData : Intent

    var player = PlayerWithCardsModel()
    lateinit var room: RoomDataModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_battle)
        initWidget()
    }

    fun initWidget(){

        this.context = this@RoomBattle
        IntentData = intent

        room = IntentData.getSerializableExtra("room") as RoomDataModel

        player = SerializableSave(context, SerializableSave.userDataFileSessionName).load() as PlayerWithCardsModel

    }
}

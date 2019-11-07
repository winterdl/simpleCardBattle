package com.syahputrareno975.simplecardbattle.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.simplecardbattle.interfaces.QueueStreamEvent
import com.syahputrareno975.simplecardbattle.interfaces.QueueStreamEventController
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel
import com.syahputrareno975.simplecardbattle.task.QueueStreamTask
import com.syahputrareno975.simplecardbattle.util.NetDefault.Companion.NetConfigDefault
import com.syahputrareno975.simplecardbattle.util.RoomRule.Companion.findEnemy
import com.syahputrareno975.simplecardbattle.util.RoomRule.Companion.findPlayer
import com.syahputrareno975.simplecardbattle.util.SerializableSave
import kotlinx.android.synthetic.main.activity_queue.*
import java.util.*
import kotlin.concurrent.schedule

class QueueActivity : AppCompatActivity() {

    lateinit var context: Context
    lateinit var IntentData : Intent
    var player = PlayerWithCardsModel()
    var room : RoomDataModel = RoomDataModel()

    lateinit var controller: QueueStreamEventController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)
        initWidget()
    }

    fun initWidget(){
        this.context = this@QueueActivity
        IntentData = intent

        player = SerializableSave(context, SerializableSave.userDataFileSessionName).load() as PlayerWithCardsModel
        player_name.setText("Lvl ${player.Owner.Level} ${player.Owner.Name}")

        searching_bar.visibility = View.VISIBLE
        player_names.visibility = View.GONE

        cancel.setOnClickListener(onCancelClick)

        QueueStreamTask(player,NetConfigDefault,streamEvent).execute()
    }

    val onCancelClick = object : View.OnClickListener {
        override fun onClick(v: View?) {
            if (::controller.isInitialized){
                controller.leftWaitingRoom(player.Owner)
            }
        }
    }

    val streamEvent = object : QueueStreamEvent {


        override fun onEnterQueue(c: QueueStreamEventController) {
            controller = c
        }

        override fun onBattleFound(r: RoomDataModel) {
            room = r

            searching_bar.visibility = View.GONE
            player_names.visibility = View.VISIBLE
            cancel.visibility = View.GONE

            if (::controller.isInitialized){
                controller.goToBattle()
            }

            if (r.Players.size < r.MaxPlayer) {
                return
            }
            val p = r.Players.get(findPlayer(player.Owner.Id,r)).Owner
            player_name.setText("Lvl ${p.Level} ${p.Name}")

            val p2 = r.Players.get(findEnemy(player.Owner.Id,r)).Owner
            enemy_name.setText("Lvl ${p2.Level} ${p2.Name}")
        }

        override fun onBattleNotFound() {

        }

        override fun onJoinWaitingRoom() {

        }

        override fun toBattle() {
            Timer().schedule(2000){
                val i = Intent(context,RoomBattle::class.java)
                i.putExtra("room",room)
                startActivity(i)
                finish()
            }
        }

        override fun onLeftWaitingRoom() {
            val intent = Intent(context,MainLobbyActivity::class.java)
            startActivity(intent)
            finish()
        }

        override fun onDisconnected() {
            val intent = Intent(context,MainLobbyActivity::class.java)
            startActivity(intent)
            finish()
        }

        override fun onError(s: String) {

            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("Cannot connect to server, Reason : ${s}")
                .setPositiveButton("Try Again") { dialog, which ->
                    QueueStreamTask(player,NetConfigDefault,this).execute()
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()
        }

    }
}

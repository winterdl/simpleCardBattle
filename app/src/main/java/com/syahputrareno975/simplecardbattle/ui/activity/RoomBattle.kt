package com.syahputrareno975.simplecardbattle.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.simplecardbattle.interfaces.RoomStreamEvent
import com.syahputrareno975.simplecardbattle.interfaces.RoomStreamEventController
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toRoomModel
import com.syahputrareno975.simplecardbattle.model.ModelCasting.Companion.toRoomModelGRPC
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerBattleResult.AllPlayerBattleResultModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel
import com.syahputrareno975.simplecardbattle.task.RoomStreamTask
import com.syahputrareno975.simplecardbattle.ui.dialog.DialogCardDeck
import com.syahputrareno975.simplecardbattle.util.NetDefault.Companion.NetConfigDefault
import com.syahputrareno975.simplecardbattle.util.RoomRule.Companion.findEnemy
import com.syahputrareno975.simplecardbattle.util.RoomRule.Companion.findPlayer
import com.syahputrareno975.simplecardbattle.util.RoomRule.Companion.getTotalAtk
import com.syahputrareno975.simplecardbattle.util.RoomRule.Companion.getTotalDef
import com.syahputrareno975.simplecardbattle.util.SerializableSave
import com.syahputrareno975.simpleuno.adapter.AdapterCard
import kotlinx.android.synthetic.main.activity_room_battle.*

class RoomBattle : AppCompatActivity() {
    lateinit var context: Context
    lateinit var IntentData : Intent

    var player = PlayerWithCardsModel()
    lateinit var room: RoomDataModel

    lateinit var controler : RoomStreamEventController

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

        deploy_card.setOnClickListener(onChooseCard)

        RoomStreamTask(player,room,NetConfigDefault,onRoomEvent).execute()

        main_game_layout.visibility = View.GONE
        waiting_text.visibility = View.VISIBLE
    }

    //-----------setRoomStatus(..)-------------//

    @SuppressLint("SetTextI18n")
    fun setEnemyStatus(p : PlayerWithCardsModel) {
        enemy_atk.text = "Atk : ${getTotalAtk(p.Deployed).toString()}"
        enemy_hp.text = "Hp : ${p.Hp.toString()}"
        enemy_name.text = p.Owner.Name
        enemy_def.text = "Def : ${getTotalDef(p.Deployed).toString()}"

        val adapter = AdapterCard(context,p.Deployed)
        adapter.setOnCardClick { cardModel, i -> }

        enemy_deploy_deck.adapter = adapter
        enemy_deploy_deck.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
    }

    @SuppressLint("SetTextI18n")
    fun setPlayerStatus(p : PlayerWithCardsModel) {
        player_atk.text = "Atk : ${getTotalAtk(p.Deployed).toString()}"
        player_hp.text = "Hp : ${p.Hp.toString()}"
        player_name.text = p.Owner.Name
        player_def.text = "Def : ${getTotalDef(p.Deployed).toString()}"

        val adapter = AdapterCard(context,p.Deployed)
        adapter.setOnCardClick { cardModel, i -> }

        player_deploy_deck.adapter = adapter
        player_deploy_deck.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
    }


    //-----------openDeck(..)-------------//

    val onChooseCard = object : View.OnClickListener {
        override fun onClick(v: View?) {
            DialogCardDeck(context,room.Players.get(findPlayer(player.Owner.Id,room))) {
                if (::controler.isInitialized){
                    controler.deployCard(player.Owner,it)
                }
            }.dialog()
        }
    }

    //-----------roomEvent(..)-------------//

    val onRoomEvent = object : RoomStreamEvent {

        override fun onConnected(c: RoomStreamEventController) {
            controler = c
        }

        override fun onPlayerJoin(p: PlayerModel) {
            Toast.makeText(context,"${p.Name} is join",Toast.LENGTH_SHORT).show()
        }

        override fun onPlayerLeft(p: PlayerModel) {
            Toast.makeText(context,"${p.Name} is left",Toast.LENGTH_SHORT).show()
        }

        override fun onRoomUpdate(r: RoomDataModel) {
            room = r
            if (r.Players.size >= r.MaxPlayer) {
                setPlayerStatus(r.Players.get(findPlayer(player.Owner.Id, r)))
                setEnemyStatus(r.Players.get(findEnemy(player.Owner.Id, r)))
            }

            main_game_layout.visibility = View.VISIBLE
            waiting_text.visibility = View.GONE
        }

        override fun onCountDown(i: Int) {
            countdown.text = "Time : ${i}"
        }

        override fun onResult(r: AllPlayerBattleResultModel) {

            var msg = ""
            for (i in r.Results){
                if (i.Owner.Id != player.Owner.Id){
                    msg += "Player : ${i.Owner.Name}\n"
                    msg += "Damage Receive : ${i.DamageReceive}\n\n"
                }
                if (i.Owner.Id == player.Owner.Id){
                    msg += "Player : ${i.Owner.Name}\n"
                    msg += "Damage Receive : ${i.DamageReceive}\n\n"
                }
            }

            AlertDialog.Builder(context)
                .setTitle("Result Battle")
                .setMessage(msg)
                .setPositiveButton("ok",null)
                .create()
                .show()

            if (::controler.isInitialized){
                controler.getOneRoom(room.Id)
            }

        }

        override fun onWinner(p: PlayerModel) {

            AlertDialog.Builder(context)
                .setTitle("Winner Of Battle")
                .setMessage("Winner : ${p.Name}")
                .setPositiveButton("ok") { dialog, i ->
                    val intent = Intent(context,MainLobbyActivity::class.java)
                    startActivity(intent)
                    finish()

                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()
        }
        override fun onDraw() {

            AlertDialog.Builder(context)
                .setTitle("Draw")
                .setMessage("All player have no HP left to continue the battle")
                .setPositiveButton("ok") { dialog, i ->
                    val intent = Intent(context,MainLobbyActivity::class.java)
                    startActivity(intent)
                    finish()

                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()
        }

        override fun onGetRoomData(r: RoomDataModel) {
            room = r
            if (r.Players.size >= r.MaxPlayer) {
                setPlayerStatus(r.Players.get(findPlayer(player.Owner.Id, r)))
                setEnemyStatus(r.Players.get(findEnemy(player.Owner.Id, r)))
            }

            main_game_layout.visibility = View.VISIBLE
            waiting_text.visibility = View.GONE
        }

        override fun onLeft() {
            Toast.makeText(context,"you left the game",Toast.LENGTH_SHORT).show()
            val intent = Intent(context,MainLobbyActivity::class.java)
            startActivity(intent)
            finish()
        }

        override fun onDisconnected() {
            Toast.makeText(context,"disconnected",Toast.LENGTH_SHORT).show()
        }

        override fun onError(s: String) {
            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("Cannot connect to server, Reason : ${s}")
                .setPositiveButton("Try Again") { dialog, which ->
                    RoomStreamTask(player,room,NetConfigDefault,this).execute()
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()
        }
    }

    //-----------onKeyDown(..)-------------//

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){

            if (::controler.isInitialized) {
                controler.leftGame(player.Owner,room)
            }

            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}

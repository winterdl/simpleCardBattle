package com.syahputrareno975.simplecardbattle.ui.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.simplecardbattle.interfaces.LobbyStreamEventController
import com.syahputrareno975.simplecardbattle.interfaces.LobbyStreamEvent
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel
import com.syahputrareno975.simplecardbattle.task.LobbyStreamTask
import com.syahputrareno975.simplecardbattle.ui.dialog.DialogCardInfo
import com.syahputrareno975.simplecardbattle.util.NetDefault.Companion.NetConfigDefault
import com.syahputrareno975.simplecardbattle.util.SerializableSave
import com.syahputrareno975.simpleuno.adapter.AdapterCard
import com.syahputrareno975.simpleuno.adapter.AdapterPlayer
import com.syahputrareno975.simpleuno.adapter.AdapterRoom
import kotlinx.android.synthetic.main.activity_lobby.*
import java.text.DecimalFormat


class MainLobbyActivity : AppCompatActivity() {

    lateinit var context: Context
    lateinit var IntentData : Intent

    var player = PlayerWithCardsModel()

    lateinit var eventController: LobbyStreamEventController

    lateinit var waiting : ProgressDialog
    lateinit var leaving : ProgressDialog

    val formater = DecimalFormat("##,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        initWidget()
    }

    fun initWidget(){

        this.context = this@MainLobbyActivity
        IntentData = intent

         player = SerializableSave(context, SerializableSave.userDataFileSessionName).load() as PlayerWithCardsModel

        layout_main_menu.visibility = View.VISIBLE


        waiting = ProgressDialog.show(context,"","Connecting....")

        battle_button.setOnClickListener(onMenuLobbyClick)
        players_button.setOnClickListener(onMenuLobbyClick)
        profile_button.setOnClickListener(onMenuLobbyClick)
        shop_button.setOnClickListener(onMenuLobbyClick)
        logout_button.setOnClickListener(onMenuLobbyClick)



        LobbyStreamTask(player.Owner, NetConfigDefault, LobbyEvent).execute()
        waiting.show()
    }


    //------------onMenuLobbyClick-------------//

    val onMenuLobbyClick = object : View.OnClickListener {
        override fun onClick(v: View?) {

            list_item_lobby.adapter = ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line,ArrayList<String>())
            list_item_lobby.setOnItemClickListener { parent, view, position, id -> }

            when (v) {
                battle_button -> {
                    title_item_lobby.setText("Battle")
                    val battleMenu = ArrayList<RoomDataModel>()

                    val randBattle = RoomDataModel()
                    randBattle.Id = "RANDOM_BATTLE"
                    randBattle.RoomName = "Random Battle"
                    battleMenu.add(randBattle)

                    list_item_lobby.adapter = AdapterRoom(context,R.layout.adapter_room,battleMenu)
                    list_item_lobby.setOnItemClickListener { parent, view, position, id ->
                        if (battleMenu.get(position).Id == "RANDOM_BATTLE"){
                            if (::eventController.isInitialized) {
                                eventController.leftLobby(object : ()->Unit{
                                    override fun invoke() {

                                        // left lobby to battle
                                        val intent = Intent(context,QueueActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                })
                            }
                            return@setOnItemClickListener
                        }

                        if (::eventController.isInitialized){
                            eventController.getOnePlayer(battleMenu.get(position).Id)
                        }
                    }


                }
                players_button -> {
                    title_item_lobby.setText("Players")
                    if (::eventController.isInitialized){
                        eventController.getAllPlayer()
                    }
                }
                profile_button -> {

                    if (::eventController.isInitialized) {
                        eventController.leftLobby(object : ()->Unit {
                            override fun invoke() {

                                val intent = Intent(context,ProfileActivity::class.java)
                                startActivity(intent)
                                finish()

                            }
                        })
                    }

                }
                shop_button -> {

                    title_item_lobby.setText("Shop")
                    if (::eventController.isInitialized) {
                        eventController.leftLobby(object : ()->Unit {
                            override fun invoke() {

                                val intent = Intent(context,ShopActivity::class.java)
                                startActivity(intent)
                                finish()

                            }
                        })
                    }
                }

                logout_button -> {

                    AlertDialog.Builder(context)
                        .setTitle("Quit Game")
                        .setMessage("are you sure want to quit game?")
                        .setPositiveButton("Yes") { dialog, pos ->
                            leaving = ProgressDialog.show(context,"","Leaving....")
                            if (::eventController.isInitialized) {
                                eventController.leftGame(player.Owner, object : () -> Unit{
                                    override fun invoke() {
                                        if (SerializableSave(context,SerializableSave.userDataFileSessionName).delete()){
                                            startActivity(Intent(context,Login::class.java))
                                            finish()
                                        }
                                        leaving.dismiss()
                                    }
                                })
                            }
                            leaving.show()
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, pos ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .create()
                        .show()

                }
            }
        }
    }

    //-----------LobbyEvent-------------//

    val LobbyEvent = object : LobbyStreamEvent {

        override fun onGetPlayerData(p: PlayerWithCardsModel) {
            player.copyFromObject(p)
        }

        override fun onConnected(p : PlayerModel,c: LobbyStreamEventController) {
            if (waiting.isShowing) {
                waiting.dismiss()
            }
            eventController = c
            player.Owner.Id = p.Id
            if (::eventController.isInitialized){
                eventController.getMyPlayerData(player.Owner.Id)
            }
        }

        override fun onPlayerJoin(p: PlayerModel) {
            Toast.makeText(context,"${p.Name} hass join",Toast.LENGTH_SHORT).show()
        }

        override fun onGetOnePlayer(p: PlayerModel) {
            Toast.makeText(context,"Player : ${p.Name}",Toast.LENGTH_SHORT).show()
        }

        override fun onGetAllPlayer(p: ArrayList<PlayerModel>) {

            list_item_lobby.adapter = AdapterPlayer(context,R.layout.adapter_player,p)
            list_item_lobby.setOnItemClickListener { parent, view, position, id ->
                if (::eventController.isInitialized){
                    eventController.getOnePlayer(p.get(position).Id)
                }
            }
        }

        override fun onError(e: String) {
            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("Cannot connect to server, Reason : ${e}")
                .setPositiveButton("Try Again") { dialog, which ->
                    LobbyStreamTask(player.Owner,NetConfigDefault,this).execute()
                    waiting.show()
                }
                .setCancelable(false)
                .create()
                .show()
        }
        override fun onException(e : String,flag : Int,c : LobbyStreamEventController) {

            if (waiting.isShowing) {
                waiting.dismiss()
            }

            AlertDialog.Builder(context)
                .setTitle("Something Wrong Happend")
                .setMessage(e)
                .setPositiveButton("Ok") { dialog, which ->
                    // player dont have session in server
                    if (flag == 1) {
                        leaving = ProgressDialog.show(context,"","Save and Quit....")
                        c.leftLobby(object : ()->Unit {
                            override fun invoke() {

                                // go back to login
                                // to start login again
                                if (SerializableSave(context,SerializableSave.userDataFileSessionName).delete()){
                                    startActivity(Intent(context,Login::class.java))
                                    finish()
                                }
                                leaving.dismiss()
                            }
                        })
                        leaving.show()
                    }
                    dialog.dismiss()
                }
                .create()
                .show()

        }

        override fun onPlayerLeft(p: PlayerModel) {
            Toast.makeText(context,"player : ${p.Name} hass left",Toast.LENGTH_SHORT).show()
        }

        override fun onPlayerSuccessLeft() {
            Toast.makeText(context,"you have left the game",Toast.LENGTH_SHORT).show()
        }

        override fun onDisconnected() {

        }
    }



    //-----------onKeyDown(..)-------------//


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){

            leaving = ProgressDialog.show(context,"","Save and Quit....")
            if (::eventController.isInitialized) {
                eventController.leftLobby(object : ()->Unit {
                    override fun invoke() {
                        if (SerializableSave(context,SerializableSave.userDataFileSessionName).save(player)){
                            finish()
                        }
                        leaving.dismiss()
                    }
                })
            }
            leaving.show()

            return false
        }
        return super.onKeyDown(keyCode, event)
    }

}

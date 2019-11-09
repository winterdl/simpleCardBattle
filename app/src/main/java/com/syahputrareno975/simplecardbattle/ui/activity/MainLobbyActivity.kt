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
import com.syahputrareno975.simplecardbattle.interfaces.LobbyStreamController
import com.syahputrareno975.simplecardbattle.interfaces.LobbyStreamEvent
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel
import com.syahputrareno975.simplecardbattle.task.LobbyStreamTask
import com.syahputrareno975.simplecardbattle.task.RoomStreamTask
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
    lateinit var adapterDeckProfile : AdapterCard
    lateinit var adapterReserveProfile : AdapterCard

    lateinit var controller: LobbyStreamController

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

        layout_profile.visibility = View.GONE
        layout_main_menu.visibility = View.VISIBLE


        waiting = ProgressDialog.show(context,"","Connecting....")

        battle_button.setOnClickListener(onMenuLobbyClick)
        players_button.setOnClickListener(onMenuLobbyClick)
        profile_button.setOnClickListener(onMenuLobbyClick)
        shop_button.setOnClickListener(onMenuLobbyClick)
        logout_button.setOnClickListener(onMenuLobbyClick)

        profile_back_to_main_menu.setOnClickListener(onMenuProfileClick)
        remove_card_from_deck.setOnClickListener(onMenuProfileClick)
        add_card_to_deck.setOnClickListener(onMenuProfileClick)
        card_info_in_profile.setOnClickListener(onMenuProfileClick)


        setAdapterListCardInProfile()

        LobbyStreamTask(player.Owner, NetConfigDefault, LobbyEvent).execute()
        waiting.show()
    }



    //-----------onMenuProfileClick-------------//

    val onMenuProfileClick = object : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v) {
                card_info_in_profile -> {
                    DialogCardInfo(context,{}).dialog()
                }

                profile_back_to_main_menu -> {
                    layout_profile.visibility = View.GONE
                    layout_main_menu.visibility =  View.VISIBLE
                }
                remove_card_from_deck -> {
                    var isSelected = false
                    var card = CardModel()

                    for (i in player.Deck){
                        isSelected = i.Flag == 1
                        if (i.Flag == 1){
                            card = i
                        }
                        if (isSelected){
                            break
                        }
                    }

                    if (!isSelected){
                        Toast.makeText(context,"please select one card in deck!",Toast.LENGTH_SHORT).show()
                        return
                    }

                    if (::controller.isInitialized){
                        controller.removeCardFromDeck(player.Owner,card)
                    }


                }
                add_card_to_deck -> {
                    var isSelected = false
                    var card = CardModel()

                    for (i in player.Reserve){
                        isSelected = i.Flag == 1
                        if (i.Flag == 1){
                            card = i
                        }
                        if (isSelected){
                            break
                        }
                    }

                    if (!isSelected){
                        Toast.makeText(context,"please select one card in reserve card!",Toast.LENGTH_SHORT).show()
                        return
                    }

                    if (::controller.isInitialized){
                        controller.addCardToDeck(player.Owner,card)
                    }
                }
            }
        }
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
                            if (::controller.isInitialized) {
                                controller.leftLobby(object : ()->Unit{
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

                        if (::controller.isInitialized){
                            controller.getOnePlayer(battleMenu.get(position).Id)
                        }
                    }


                }
                players_button -> {
                    title_item_lobby.setText("Players")
                    if (::controller.isInitialized){
                        controller.getAllPlayer()
                    }
                }
                profile_button -> {

                    layout_profile.visibility = View.VISIBLE
                    layout_main_menu.visibility = View.GONE

                    if (::controller.isInitialized){
                        controller.getMyPlayerData(player.Owner.Id)
                    }
                }
                shop_button -> {

                    title_item_lobby.setText("Shop")
                    if (::controller.isInitialized) {
                        controller.leftLobby(object : ()->Unit {
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
                            if (::controller.isInitialized) {
                                controller.leftGame(player.Owner, object : () -> Unit{
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

            player_name.setText("Name : ${player.Owner.Name}")
            player_cash.setText("Cash : ${formater.format(player.Owner.Cash)}")
            player_level.setText("Level : ${formater.format(player.Owner.Level)}")
            player_exp.setText("Exp : ${formater.format(player.Owner.Exp)}/${formater.format(player.Owner.MaxExp)}")
            player_deck.setText("My Deck ${player.Deck.size}/${player.Owner.MaxDeckSlot}")
            player_reserve.setText("My Card ${player.Reserve.size}/${player.Owner.MaxReserveSlot}")

            adapterDeckProfile.notifyDataSetChanged()
            adapterReserveProfile.notifyDataSetChanged()

        }

        override fun onConnected(p : PlayerModel,c: LobbyStreamController) {
            if (waiting.isShowing) {
                waiting.dismiss()
            }
            controller = c
            player.Owner.Id = p.Id
            if (::controller.isInitialized){
                controller.getMyPlayerData(player.Owner.Id)
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
                if (::controller.isInitialized){
                    controller.getOnePlayer(p.get(position).Id)
                }
            }
        }
        override fun onPlayerCardUpdated() {
            if (::controller.isInitialized){
                controller.getMyPlayerData(player.Owner.Id)
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
        override fun onException(e : String,flag : Int,c : LobbyStreamController) {

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
            if (::controller.isInitialized) {
                controller.leftLobby(object : ()->Unit {
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


    //-----------setAdapterListCardInProfile()-------------//


    fun setAdapterListCardInProfile(){

        adapterDeckProfile = AdapterCard(context,player.Deck)
        adapterDeckProfile.setOnCardClick { c,pos ->
            for (i in player.Deck) {
                i.Flag = 0
            }
            player.Deck.get(pos).Flag = 1
            adapterDeckProfile.notifyDataSetChanged()
        }

        list_card_deck.adapter = adapterDeckProfile
        list_card_deck.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        adapterReserveProfile = AdapterCard(context,player.Reserve)
        adapterReserveProfile.setOnCardClick { c,pos ->
            for (i in player.Reserve) {
                i.Flag = 0
            }
            player.Reserve.get(pos).Flag = 1
            adapterReserveProfile.notifyDataSetChanged()
        }

        list_card_reseve.adapter = adapterReserveProfile
        list_card_reseve.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }
}

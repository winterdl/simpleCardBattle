package com.syahputrareno975.simplecardbattle.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.simplecardbattle.interfaces.LobbyStreamEventController
import com.syahputrareno975.simplecardbattle.interfaces.ProfileStreamEvent
import com.syahputrareno975.simplecardbattle.interfaces.ProfileStreamEventController
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.task.ProfileStreamTask
import com.syahputrareno975.simplecardbattle.ui.dialog.DialogCardInfo
import com.syahputrareno975.simplecardbattle.util.NetDefault.Companion.NetConfigDefault
import com.syahputrareno975.simplecardbattle.util.SerializableSave
import com.syahputrareno975.simpleuno.adapter.AdapterCard
import kotlinx.android.synthetic.main.activity_lobby.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.text.DecimalFormat

class ProfileActivity : AppCompatActivity() {

    lateinit var context: Context
    lateinit var IntentData : Intent

    var player = PlayerWithCardsModel()
    lateinit var adapterDeckProfile : AdapterCard
    lateinit var adapterReserveProfile : AdapterCard

    lateinit var eventController: ProfileStreamEventController

    val formater = DecimalFormat("##,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initWidget()
    }

    fun initWidget() {

        context = this@ProfileActivity
        IntentData = intent
        player = SerializableSave(context, SerializableSave.userDataFileSessionName).load() as PlayerWithCardsModel

        profile_back_to_main_menu.setOnClickListener(onMenuProfileClick)
        remove_card_from_deck.setOnClickListener(onMenuProfileClick)
        add_card_to_deck.setOnClickListener(onMenuProfileClick)
        card_info_in_profile.setOnClickListener(onMenuProfileClick)

        setAdapterListCardInProfile()

        ProfileStreamTask(player,NetConfigDefault,profileEvent).execute()

    }

    //-----------onMenuProfileClick-------------//

    val onMenuProfileClick = object : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v) {
                card_info_in_profile -> {
                    DialogCardInfo(context,{}).dialog()
                }

                profile_back_to_main_menu -> {

                    if (::eventController.isInitialized){
                        eventController.leaveProfile {
                            val intent = Intent(context,MainLobbyActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

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
                        Toast.makeText(context,"please select one card in deck!", Toast.LENGTH_SHORT).show()
                        return
                    }

                    if (::eventController.isInitialized){
                        eventController.removeCardFromDeck(player.Owner,card)
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
                        Toast.makeText(context,"please select one card in reserve card!", Toast.LENGTH_SHORT).show()
                        return
                    }

                    if (::eventController.isInitialized){
                        eventController.addCardToDeck(player.Owner,card)
                    }

                }
            }
        }
    }

    val profileEvent = object : ProfileStreamEvent {
        override fun onPlayerCardUpdated() {
            if (::eventController.isInitialized){
                eventController.getMyPlayerData(player.Owner.Id)
            }
        }

        override fun onConnected(c: ProfileStreamEventController) {
            eventController = c
            if (::eventController.isInitialized){
                eventController.getMyPlayerData(player.Owner.Id)
            }
        }

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

        override fun onDisconnected() {

        }

        override fun onError(e: String) {
            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("Cannot connect to server, Reason : ${e}")
                .setPositiveButton("Try Again") { dialog, which ->
                    ProfileStreamTask(player,NetConfigDefault,this).execute()
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()
        }

        override fun onException(e: String, flag: Int, c: ProfileStreamEventController) {
            AlertDialog.Builder(context)
                .setTitle("Something Wrong Happend")
                .setMessage(e)
                .setPositiveButton("Ok") { dialog, which ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

    }

    //-----------onKeyDown(..)-------------//


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){

            if (::eventController.isInitialized){
                eventController.leaveProfile {
                    val intent = Intent(context,MainLobbyActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
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

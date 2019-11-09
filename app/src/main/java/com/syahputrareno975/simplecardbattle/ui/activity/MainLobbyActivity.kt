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


    var cardShop = ArrayList<CardModel>()
    lateinit var adapterShop : AdapterCard
    lateinit var adapterReserve : AdapterCard


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

        layout_shoping.visibility = View.GONE
        layout_profile.visibility = View.GONE
        layout_main_menu.visibility = View.VISIBLE

        slot_tab_shop_layout.visibility = View.GONE
        card_tab_shop_layout.visibility = View.VISIBLE

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

        shop_back_to_main_menu.setOnClickListener(onMenuShopClick)
        buy_card.setOnClickListener(onMenuShopClick)
        sell_card.setOnClickListener(onMenuShopClick)
        card_info_in_shop.setOnClickListener(onMenuShopClick)
        add_deck_slot.setOnClickListener(onMenuShopClick)
        add_reserve_slot.setOnClickListener(onMenuShopClick)
        card_tab_shop.setOnClickListener(onMenuShopClick)
        slot_tab_shop.setOnClickListener(onMenuShopClick)

        setAdapterListCardInShop()
        setAdapterListCardInProfile()

        LobbyStreamTask(player.Owner, NetConfigDefault, LobbyEvent).execute()
        waiting.show()
    }

    //-----------onMenuShopClick-------------//

    val onMenuShopClick = object : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v) {

                add_deck_slot -> {


                    AlertDialog.Builder(context)
                        .setTitle("Add Deck Slot")
                        .setMessage("Are you sure want to buy Deck slot (+1) (Cost : ${120 * player.Owner.MaxDeckSlot})?")
                        .setPositiveButton("Buy") { dialog, pos ->
                            if (::controller.isInitialized) {
                                controller.addDeckSlot(player.Owner,0)
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialog, pos ->
                            dialog.dismiss()
                        }.create()
                        .show()


                }

                add_reserve_slot -> {


                    AlertDialog.Builder(context)
                        .setTitle("Add Deck Slot")
                        .setMessage("Are you sure want to buy Reserve slot (+1) (Cost : ${100 * player.Owner.MaxReserveSlot})?")
                        .setPositiveButton("Buy") { dialog, pos ->
                            if (::controller.isInitialized) {
                                controller.addDeckSlot(player.Owner,1)
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialog, pos ->
                            dialog.dismiss()
                        }.create()
                        .show()


                }

                card_tab_shop -> {

                    slot_tab_shop_layout.visibility = View.GONE
                    card_tab_shop_layout.visibility = View.VISIBLE
                }

                slot_tab_shop -> {

                    slot_tab_shop_layout.visibility = View.VISIBLE
                    card_tab_shop_layout.visibility = View.GONE
                }

                card_info_in_shop -> {
                    DialogCardInfo(context,{}).dialog()
                }

                shop_back_to_main_menu -> {

                    layout_shoping.visibility = View.GONE
                    layout_main_menu.visibility = View.VISIBLE

                }

                buy_card -> {
                    cardShop

                    var isSelected = false
                    var card = CardModel()

                    for (i in cardShop){
                        isSelected = i.Flag == 1
                        if (i.Flag == 1){
                            card = i
                        }
                        if (isSelected){
                            break
                        }
                    }

                    if (!isSelected){
                        Toast.makeText(context,"please select one card to you want to buy!",Toast.LENGTH_SHORT).show()
                        return
                    }

                    AlertDialog.Builder(context)
                        .setTitle(card.Name)
                        .setMessage("Are you sure want to buy card Level ${card.Level} : ${card.Name} (Cost : ${card.Price})?")
                        .setPositiveButton("Buy") { dialog, pos ->
                            if (::controller.isInitialized){
                                controller.buyCardFromShop(player.Owner,card)
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialog, pos ->
                            dialog.dismiss()
                        }.create()
                        .show()


                }

                sell_card -> {

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
                        Toast.makeText(context,"please select one card to sell!",Toast.LENGTH_SHORT).show()
                        return
                    }


                    AlertDialog.Builder(context)
                        .setTitle(card.Name)
                        .setMessage("Are you sure want to sell card Level ${card.Level} : ${card.Name} (For : ${card.Price})?")
                        .setPositiveButton("Sell") { dialog, pos ->
                            if (::controller.isInitialized){
                                controller.sellCardToShop(player.Owner,card)
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialog, pos ->
                            dialog.dismiss()
                        }.create()
                        .show()

                }

            }
        }

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
                    if (::controller.isInitialized) {
                        controller.getAllRoom()
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

                    layout_shoping.visibility = View.VISIBLE
                    layout_main_menu.visibility = View.GONE

                    title_item_lobby.setText("Shop")
                    if (::controller.isInitialized){
                        controller.getAllCardInShop(player.Owner)
                    }
                }

                logout_button -> {

                    AlertDialog.Builder(context)
                        .setTitle("Quit Game")
                        .setMessage("are you sure want to quit game?")
                        .setPositiveButton("Yes") { dialog, pos ->
                            leaving = ProgressDialog.show(context,"","Leaving....")
                            if (::controller.isInitialized) {
                                controller.leftGame(player.Owner)
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

            if (::controller.isInitialized){
                controller.getAllCardInShop(player.Owner)
            }

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

        override fun onShopCountDown(i: Int) {
            shop_button.text = "Shop"
            shop_title_bar.text = "Shop (new item added in ${i})"
        }

        override fun onShopRefreshed() {
            Toast.makeText(context,"Shop items is updated",Toast.LENGTH_SHORT).show()
            if (::controller.isInitialized){
                controller.getAllCardInShop(player.Owner)
            }
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

        override fun onRoomCreated(r: RoomDataModel) {
            Toast.makeText(context,"Room : ${r.RoomName} hass created",Toast.LENGTH_SHORT).show()
        }

        override fun onGetOneRoom(r: RoomDataModel) {
            Toast.makeText(context,"Room info : ${r.RoomName}",Toast.LENGTH_SHORT).show()
        }

        override fun onGetAllRoom(r: ArrayList<RoomDataModel>) {

            val battleMenu = ArrayList<RoomDataModel>()

            val randBattle = RoomDataModel()
            randBattle.Id = "RANDOM_BATTLE"
            randBattle.RoomName = "Random Battle"
            battleMenu.add(randBattle)

            for (i in r){
                battleMenu.add(i)
            }

            list_item_lobby.adapter = AdapterRoom(context,R.layout.adapter_room,battleMenu)
            list_item_lobby.setOnItemClickListener { parent, view, position, id ->
                if (battleMenu.get(position).Id == "RANDOM_BATTLE"){
                    if (::controller.isInitialized) {
                        controller.leftLobbyToBattle()
                    }
                    return@setOnItemClickListener
                }

                if (::controller.isInitialized){
                    controller.getOnePlayer(battleMenu.get(position).Id)
                }
            }
        }

        override fun onAllCardInShop(cards: ArrayList<CardModel>) {

            cardShop.clear()
            cardShop.addAll(cards)
            adapterReserve.notifyDataSetChanged()
            adapterShop.notifyDataSetChanged()

            player_in_shop_title_bar.setText("${player.Owner.Name}'s Cards : (${player.Reserve.size}/${player.Owner.MaxReserveSlot}) & Cash : ${formater.format(player.Owner.Cash)}")

        }

        override fun onCardBought(success : Boolean) {
            if (success){
                if (::controller.isInitialized){
                    controller.getMyPlayerData(player.Owner.Id)
                }
            }
            Toast.makeText(context,if (success) "card succesfuly bought" else "failed buy card, check your level,cash or slot avaliable",Toast.LENGTH_SHORT).show()
        }

        override fun onCardSold(success : Boolean) {
            if (success) {
                Toast.makeText(context, "card succesfuly sold", Toast.LENGTH_SHORT).show()
                if (::controller.isInitialized){
                    controller.getMyPlayerData(player.Owner.Id)
                }
            }
        }

        override fun onAddCardSlot(success: Boolean) {
            if (::controller.isInitialized && success){
                Toast.makeText(context,"Card Hass been Slot Added (+1)",Toast.LENGTH_SHORT).show()
                controller.getMyPlayerData(player.Owner.Id)
            } else {
                Toast.makeText(context,"Failed added Card Slot, check your cash",Toast.LENGTH_SHORT).show()
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
                        c.forceQuit()
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
            if (SerializableSave(context,SerializableSave.userDataFileSessionName).delete()){
                startActivity(Intent(context,Login::class.java))
                finish()
            }
            leaving.dismiss()
        }

        override fun onLeftLobby() {
            if (SerializableSave(context,SerializableSave.userDataFileSessionName).save(player)){
                finish()
            }
            leaving.dismiss()
        }

        override fun onLeftLobbyToBattle() {

            val intent = Intent(context,QueueActivity::class.java)
            startActivity(intent)

            finish()
        }

        override fun onForceQuit() {
            if (SerializableSave(context,SerializableSave.userDataFileSessionName).delete()){
                startActivity(Intent(context,Login::class.java))
                finish()
            }
            leaving.dismiss()
        }
    }



    //-----------onKeyDown(..)-------------//


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){

            leaving = ProgressDialog.show(context,"","Save and Quit....")
            if (::controller.isInitialized) {
                controller.leftLobby()
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

    //-----------setAdapterListCardInShop()-------------//

    fun setAdapterListCardInShop(){

        adapterShop = AdapterCard(context, cardShop)
        adapterShop.setOnCardClick { card ,pos ->
            for (i in  cardShop) {
                i.Flag = 0
            }
            cardShop.get(pos).Flag = 1
            adapterShop.notifyDataSetChanged()
        }

        list_card_shop.adapter = adapterShop
        list_card_shop.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        adapterReserve = AdapterCard(context,player.Reserve)
        adapterReserve.setOnCardClick { card,pos ->
            for (i in player.Reserve) {
                i.Flag = 0
            }
            player.Reserve.get(pos).Flag = 1
            adapterReserve.notifyDataSetChanged()
        }

        list_car_player_reserve_card_in_shop.adapter = adapterReserve
        list_car_player_reserve_card_in_shop.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


    }
}

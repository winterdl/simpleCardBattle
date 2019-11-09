package com.syahputrareno975.simplecardbattle.ui.activity

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
import com.syahputrareno975.simplecardbattle.interfaces.LobbyStreamController
import com.syahputrareno975.simplecardbattle.interfaces.ShopStreamEvent
import com.syahputrareno975.simplecardbattle.interfaces.ShopStreamEventController
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.task.ShopStreamTask
import com.syahputrareno975.simplecardbattle.ui.dialog.DialogCardInfo
import com.syahputrareno975.simplecardbattle.util.NetDefault.Companion.NetConfigDefault
import com.syahputrareno975.simplecardbattle.util.SerializableSave
import com.syahputrareno975.simpleuno.adapter.AdapterCard
import kotlinx.android.synthetic.main.activity_shop.*
import java.text.DecimalFormat

class ShopActivity : AppCompatActivity() {

    lateinit var context: Context
    lateinit var IntentData : Intent

    var player = PlayerWithCardsModel()
    var cardShop = ArrayList<CardModel>()
    lateinit var adapterShop : AdapterCard
    lateinit var adapterReserve : AdapterCard

    lateinit var controller : ShopStreamEventController

    val formater = DecimalFormat("##,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        initWidget()
    }

    fun initWidget(){

        context = this@ShopActivity
        IntentData = intent

        slot_tab_shop_layout.visibility = View.GONE
        card_tab_shop_layout.visibility = View.VISIBLE

        player = SerializableSave(context, SerializableSave.userDataFileSessionName).load() as PlayerWithCardsModel

        shop_back_to_main_menu.setOnClickListener(onMenuShopClick)
        buy_card.setOnClickListener(onMenuShopClick)
        sell_card.setOnClickListener(onMenuShopClick)
        card_info_in_shop.setOnClickListener(onMenuShopClick)
        add_deck_slot.setOnClickListener(onMenuShopClick)
        add_reserve_slot.setOnClickListener(onMenuShopClick)
        card_tab_shop.setOnClickListener(onMenuShopClick)
        slot_tab_shop.setOnClickListener(onMenuShopClick)

        setAdapterListCardInShop()

        ShopStreamTask(player,NetConfigDefault,shopEvent).execute()

    }


    //-----------onMenuShopClick-------------//

    val onMenuShopClick = object : View.OnClickListener {

        override fun onClick(v: View?) {
            when (v) {

                add_deck_slot -> {

                    AlertDialog.Builder(context)
                        .setTitle("Add Deck Slot")
                        .setMessage("Are you sure want to buy Deck slot (+1) (Cost : ${formater.format(120 * player.Owner.MaxDeckSlot)})?")
                        .setPositiveButton("Buy") { dialog, pos ->

                            // buy deck slot
                            // type 0
                            if (::controller.isInitialized){
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
                        .setMessage("Are you sure want to buy Reserve slot (+1) (Cost : ${formater.format(100 * player.Owner.MaxReserveSlot)})?")
                        .setPositiveButton("Buy") { dialog, pos ->

                            // buy reserve slot
                            // type 1
                            if (::controller.isInitialized){
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

                    // left shop
                    if (::controller.isInitialized){
                        controller.leaveShop(object : () -> Unit {
                            override fun invoke() {

                                val intent = Intent(context,MainLobbyActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                        })
                    }
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

                            // buy card
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

                            // sell card
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

    //-----------shopEvent()-------------//

    val shopEvent = object : ShopStreamEvent {

        override fun onConnected(c: ShopStreamEventController) {

            controller = c

            if (::controller.isInitialized){
                controller.getMyPlayerData(player.Owner.Id)
            }

        }

        override fun onGetPlayerData(p: PlayerWithCardsModel) {

            player.copyFromObject(p)

            if (::controller.isInitialized){
                controller.getAllCardInShop(player.Owner)
            }

        }

        override fun onAllCardInShop(c: ArrayList<CardModel>) {
            updateListCard(c)
        }

        override fun onCardBought(success: Boolean) {
            if (success){
                if (::controller.isInitialized){
                    controller.getMyPlayerData(player.Owner.Id)
                }
            }
            Toast.makeText(context, if (success) "card succesfuly bought" else "failed buy card, check your level,cash or slot avaliable",Toast.LENGTH_SHORT).show()

        }

        override fun onCardSold(success: Boolean) {
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

        override fun onShopCountDown(i: Int) {
            shop_title_bar.text = "Shop (new item added in ${i})"
        }

        override fun onShopRefreshed() {
            Toast.makeText(context,"Shop items is updated",Toast.LENGTH_SHORT).show()
            if (::controller.isInitialized){
                controller.getAllCardInShop(player.Owner)
            }
        }


        override fun onError(e: String) {

            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("Cannot connect to server, Reason : ${e}")
                .setPositiveButton("Try Again") { dialog, which ->
                    ShopStreamTask(player,NetConfigDefault,this).execute()
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()

        }

        override fun onException(e: String, flag: Int, c: ShopStreamEventController) {

            AlertDialog.Builder(context)
                .setTitle("Something Wrong Happend")
                .setMessage(e)
                .setPositiveButton("Ok") { dialog, which ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }


        override fun onDisconnected() {

        }

    }

    //-----------updateListCardInShop()-------------//
    fun updateListCard(cards : ArrayList<CardModel>){

        cardShop.clear()
        cardShop.addAll(cards)
        adapterReserve.notifyDataSetChanged()
        adapterShop.notifyDataSetChanged()
        player_in_shop_title_bar.setText("${player.Owner.Name}'s Cards : (${player.Reserve.size}/${player.Owner.MaxReserveSlot}) & Cash : ${formater.format(player.Owner.Cash)}")

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

    //-----------onKeyDown(..)-------------//


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){

            return false
        }
        return super.onKeyDown(keyCode, event)
    }



}

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
import com.syahputrareno975.simplecardbattle.model.roomResult.EndResultModel
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
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.schedule

class RoomBattle : AppCompatActivity() {
    lateinit var context: Context
    lateinit var IntentData : Intent

    var player = PlayerWithCardsModel()
    lateinit var room: RoomDataModel

    lateinit var controler : RoomStreamEventController

    lateinit var resultBattle : AlertDialog

    val formater = DecimalFormat("##,###")

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

        brief_layout.visibility = View.VISIBLE
        main_game_layout.visibility = View.GONE
        victory_layout.visibility = View.GONE

        setRewardBrief(room)
    }

    //-----------setRoomStatus(..)-------------//

    fun setVictoryLayoutValue(r : EndResultModel){
        val adapter = AdapterCard(context,r.Reward.CardsReward)
        adapter.setOnCardClick { cardModel, i -> }

        claim_cards.adapter = adapter
        claim_cards.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        if (r.Reward.CardsReward.isEmpty()){
            no_reward_card.text = "Failed Receive Cards, no slot Avaliable!"
        }

        no_reward_card.visibility = if (r.Reward.CardsReward.isEmpty()) View.VISIBLE else View.GONE
        claim_cards.visibility = if (r.Reward.CardsReward.isEmpty()) View.GONE else View.VISIBLE

        claim_cash.setText("Cash Receive : ${formater.format(r.Reward.CashReward)}")
        claim_exp.setText("Exp Receive : ${formater.format(r.Reward.ExpReward)}")
        victory_title.setText("Victory")
        victory_detail.setText(
            if (r.FlagResult == 0) "Enemy Hp Is Reduce to 0"
            else if (r.FlagResult == 1) "You Have More Hp"
            else if (r.FlagResult == 2) "No card deploy, but You Have More Hp"
            else "Receive Rewards")

        main_menu_button.setOnClickListener(OnMainMenuPress)
    }

    fun setDefeatLayoutValue(r : EndResultModel){

        no_reward_card.visibility = View.VISIBLE
        claim_cards.visibility = View.GONE

        claim_cash.setText("Cash Receive : 0")
        claim_exp.setText("Exp Receive : 0")
        victory_title.setText("Defeated")
        victory_detail.setText(
            if (r.FlagResult == 0) "Your Hp hass been Reduce to 0"
            else if (r.FlagResult == 1) "Enemy Have More Hp"
            else if (r.FlagResult == 2) "No card deploy,but Enemy Have More Hp"
            else "Goodluck next time")

        main_menu_button.setOnClickListener(OnMainMenuPress)
    }

    fun setDrawLayoutValue(flag : Int){

        no_reward_card.visibility = View.VISIBLE
        claim_cards.visibility = View.GONE

        claim_cash.setText("Cash Receive : 0")
        claim_exp.setText("Exp Receive : 0")
        victory_title.setText("Draw")
        victory_detail.setText(if (flag == 2) "All player have same Hp" else "All player have no HP and card left to continue the battle")

        main_menu_button.setOnClickListener(OnMainMenuPress)
    }

    fun setRewardBrief(r : RoomDataModel) {

        room_name.text = "Welcome to Room ${room.RoomName}"

        val adapter = AdapterCard(context,r.Reward.CardsReward,true)
        adapter.setOnCardClick { cardModel, i -> }

        card_rewards.adapter = adapter
        card_rewards.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        cash_rewards.setText("Cash Reward : ${formater.format(r.Reward.CashReward)}")
        exp_rewards.setText("Exp Reward : ${formater.format(r.Reward.ExpReward)}")
    }

    @SuppressLint("SetTextI18n")
    fun setEnemyStatus(p : PlayerWithCardsModel) {
        enemy_atk.text = "Atk : ${formater.format(getTotalAtk(p.Deployed))}"
        enemy_hp.text = "Hp : ${formater.format(p.Hp)}"
        enemy_name.text = p.Owner.Name
        enemy_def.text = "Def : ${formater.format(getTotalDef(p.Deployed))}"

        val adapter = AdapterCard(context,p.Deployed)
        adapter.setOnCardClick { cardModel, i -> }

        enemy_deploy_deck.adapter = adapter
        enemy_deploy_deck.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)

        enemy_deployed_total.text = "(${p.Deployed.size}/${room.MaxCurrentDeployment})"
    }

    @SuppressLint("SetTextI18n")
    fun setPlayerStatus(p : PlayerWithCardsModel) {
        player_atk.text = "Atk : ${formater.format(getTotalAtk(p.Deployed))}"
        player_hp.text = "Hp : ${formater.format(p.Hp)}"
        player_name.text = p.Owner.Name
        player_def.text = "Def : ${formater.format(getTotalDef(p.Deployed))}"

        val adapter = AdapterCard(context,p.Deployed)
        adapter.setOnCardClick { cardModel, i -> }

        player_deploy_deck.adapter = adapter
        player_deploy_deck.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)

        player_deployed_total.text = "(${p.Deployed.size}/${room.MaxCurrentDeployment})"
    }

    fun refreshRoom(r : RoomDataModel){
        room = r
        if (r.Players.size >= r.MaxPlayer) {
            setPlayerStatus(r.Players.get(findPlayer(player.Owner.Id, r)))
            setEnemyStatus(r.Players.get(findEnemy(player.Owner.Id, r)))
        }

    }

    fun dismissDialog() {
        if (::resultBattle.isInitialized && resultBattle.isShowing) {
            resultBattle.dismiss()
        }
    }


    //-----------openDeck(..)-------------//

    val onChooseCard = object : View.OnClickListener {
        override fun onClick(v: View?) {
            val player = room.Players.get(findPlayer(player.Owner.Id,room))
            DialogCardDeck(context,player) {
                if (player.Deployed.size >= room.MaxCurrentDeployment) {
                    Toast.makeText(context,"Cannot deploy more cards!",Toast.LENGTH_SHORT).show()
                    return@DialogCardDeck
                }
                if (::controler.isInitialized){
                    controler.deployCard(player.Owner,it)
                }
            }.dialog()
        }
    }

    //-----------clickClaim(..)-------------//

    val OnMainMenuPress = object : View.OnClickListener {
        override fun onClick(v: View?) {
            val intent = Intent(context,MainLobbyActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //-----------roomEvent(..)-------------//

    val onRoomEvent = object : RoomStreamEvent {

        override fun onConnected(c: RoomStreamEventController) {
            controler = c
        }

        override fun onPlayerJoin(p: PlayerModel) {
            //Toast.makeText(context,"${p.Name} is join",Toast.LENGTH_SHORT).show()
        }

        override fun onPlayerLeft(p: PlayerModel) {
            Toast.makeText(context,"${p.Name} is left",Toast.LENGTH_SHORT).show()
        }

        override fun onRoomUpdate(r: RoomDataModel) {
            refreshRoom(r)
            brief_layout.visibility = View.GONE
            main_game_layout.visibility = View.VISIBLE
        }

        override fun onCountDown(i: Int,r : RoomDataModel) {
            countdown.text = "Battle in ${i}"
            refreshRoom(r)
        }
        override fun onBattleResult(r: AllPlayerBattleResultModel) {

            dismissDialog()

            var msg = ""
            for (i in r.Results){
                if (i.Owner.Id != player.Owner.Id){
                    msg += "Player : ${i.Owner.Name}\n"
                    msg += "Damage Receive : ${formater.format(i.DamageReceive)}\n\n"
                }
                if (i.Owner.Id == player.Owner.Id){
                    msg += "Player : ${i.Owner.Name}\n"
                    msg += "Damage Receive : ${formater.format(i.DamageReceive)}\n\n"
                }
            }

            resultBattle = AlertDialog.Builder(context)
                .setTitle("Result Battle")
                .setMessage(msg)
                .setPositiveButton("ok",null)
                .create()

            resultBattle.show()

            if (::controler.isInitialized){
                controler.getOneRoom(room.Id)
            }
        }

        override fun onResult(r: EndResultModel) {

            dismissDialog()

            if (player.Owner.Id == r.Winner.Id) {
                setVictoryLayoutValue(r)
            } else {
                setDefeatLayoutValue(r)
            }
            brief_layout.visibility = View.GONE
            main_game_layout.visibility = View.GONE
            victory_layout.visibility = View.VISIBLE

        }

        override fun onDraw(flag: Int) {

            dismissDialog()

            if (flag == 0) {

                Toast.makeText(context,"All player have no HP left, heal and continue the battle",Toast.LENGTH_SHORT).show()

                if (::controler.isInitialized){
                    controler.getOneRoom(room.Id)
                }

            } else {

                setDrawLayoutValue(flag)
                brief_layout.visibility = View.GONE
                main_game_layout.visibility = View.GONE
                victory_layout.visibility = View.VISIBLE
            }

        }

        override fun onGetRoomData(r: RoomDataModel) {
            refreshRoom(r)
        }

        override fun onDisconnected() {
            Toast.makeText(context,"match finish",Toast.LENGTH_SHORT).show()
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

        override fun onException(e: String, flag: Int) {
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

            if (::controler.isInitialized) {
                controler.leftGame(player.Owner,room) {
                    Toast.makeText(context,"you left the game",Toast.LENGTH_SHORT).show()
                    val intent = Intent(context,MainLobbyActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}

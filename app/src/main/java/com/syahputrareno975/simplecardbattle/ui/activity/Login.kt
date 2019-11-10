package com.syahputrareno975.simplecardbattle.ui.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.task.RegisterTask
import com.syahputrareno975.simplecardbattle.util.NetDefault.Companion.NetConfigDefault
import com.syahputrareno975.simplecardbattle.util.SerializableSave
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initWidget()
    }

    fun initWidget() {

        this.context = this@Login

        login_button.setOnClickListener(onLoginButtonClick)

        if (SerializableSave(context, SerializableSave.userDataFileSessionName).load() != null){
            val intent = Intent(context,MainLobbyActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    val onLoginButtonClick = object : View.OnClickListener {
        override fun onClick(v: View?) {

            if (player_name.text.toString().isEmpty()){
                Toast.makeText(context,"player name is empty", Toast.LENGTH_SHORT).show()
                return
            }

            val waiting = ProgressDialog.show(context,"","Connecting....")

            RegisterTask(
                PlayerModel("",player_name.text.toString(),"",0,0,0,0,0,0),
                NetConfigDefault
            ) { player, e ->

                waiting.dismiss()

                if (e != ""){
                    AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setMessage("Cannot connect to server, Reason : ${e}")
                        .setPositiveButton("Ok") { dialog, which ->
                           dialog.dismiss()
                        }
                        .setCancelable(false)
                        .create()
                        .show()

                    return@RegisterTask
                }

                val p = PlayerWithCardsModel()
                p.Owner = player

                if (SerializableSave(context, SerializableSave.userDataFileSessionName).save(p)){
                    val intent = Intent(context,MainLobbyActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }.execute()

            waiting.show()
        }
    }

}

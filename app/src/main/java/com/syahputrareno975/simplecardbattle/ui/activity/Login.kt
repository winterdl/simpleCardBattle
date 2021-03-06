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
import com.syahputrareno975.cardbattlemodule.model.NetworkConfig
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel
import com.syahputrareno975.cardbattlemodule.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.cardbattlemodule.task.RegisterTask
import com.syahputrareno975.simplecardbattle.ui.dialog.DialogChooseServer
import com.syahputrareno975.cardbattlemodule.util.NetDefault.NetConfigDefault
import com.syahputrareno975.cardbattlemodule.util.SerializableSave
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    lateinit var context: Context
    lateinit var networkConfig: NetworkConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initWidget()
    }

    fun initWidget() {

        this.context = this@Login

        networkConfig = NetConfigDefault
        if (SerializableSave(context,SerializableSave.serverChoosedFileSessionName).load() != null){
            networkConfig = SerializableSave(context,SerializableSave.serverChoosedFileSessionName).load() as NetworkConfig
        }

        login_button.setOnClickListener(onLoginButtonClick)
        browse_server.setOnClickListener(onBrowseServerClick)

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
                networkConfig
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

    val onBrowseServerClick = object : View.OnClickListener {
        override fun onClick(v: View?) {
            DialogChooseServer(context) {
                if (SerializableSave(context,SerializableSave.serverChoosedFileSessionName).save(it)){
                    Toast.makeText(context,"Server Choosed",Toast.LENGTH_SHORT).show()
                }
                networkConfig.Url = it.Url
                networkConfig.Port = it.Port
            }.dialog()
        }

    }

}

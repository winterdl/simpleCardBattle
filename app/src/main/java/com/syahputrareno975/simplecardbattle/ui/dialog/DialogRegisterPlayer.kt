package com.syahputrareno975.simplecardbattle.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel

class DialogRegisterPlayer {

    var context : Context
    var onRegister : (PlayerModel) -> Unit

    constructor(context: Context, onRegister: (PlayerModel) -> Unit) {
        this.context = context
        this.onRegister = onRegister
    }

    fun dialog(){

        val v = (context as Activity).layoutInflater.inflate(R.layout.dialog_register,null)

        val dialog = AlertDialog.Builder(context).create()

        val name : TextView = v.findViewById(R.id.player_name)
        val register : TextView = v.findViewById(R.id.register_button)

        register.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (name.text.toString().isEmpty()){
                    Toast.makeText(context,"player name is empty", Toast.LENGTH_SHORT).show()
                    return
                }
                onRegister.invoke(PlayerModel("", name.text.toString(), "", 0, 0,0,0,0,0))
                dialog.dismiss()
            }

        })


        dialog.setView(v)
        dialog.setCancelable(false)

        dialog.show()
    }
}
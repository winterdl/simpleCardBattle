package com.syahputrareno975.simplecardbattle.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.cardbattlemodule.model.NetworkConfig
import com.syahputrareno975.cardbattlemodule.util.NetDefault

class DialogChooseServer {
    var context : Context
    var onServerChoosed : (NetworkConfig) -> Unit

    constructor(context: Context, onServerChoosed: (NetworkConfig) -> Unit) {
        this.context = context
        this.onServerChoosed = onServerChoosed
    }

    fun dialog(){

        val v = (context as Activity).layoutInflater.inflate(R.layout.dialog_server_list,null)

        val url : EditText = v.findViewById(R.id.server_url)

        val dialog = AlertDialog.Builder(context)
            .setPositiveButton("Set") { dialog, pos ->
                if (url.text.toString().isEmpty()){
                    Toast.makeText(context,"server url is empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                onServerChoosed.invoke(NetworkConfig(url.text.toString(),NetDefault.NetConfigDefault.Port))
                dialog.dismiss()
            }
            .setNegativeButton("Close") { dialog, pos ->
                dialog.dismiss()
            }.create()


        dialog.setView(v)
        dialog.setCancelable(false)

        dialog.show()
    }

}
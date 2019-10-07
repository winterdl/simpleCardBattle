package com.syahputrareno975.simplecardbattle.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.syahputrareno975.simplecardbattle.R

class DialogCardInfo {

    var context : Context
    var onOk : () -> Unit

    constructor(context: Context, onOk : () -> Unit) {
        this.context = context
        this.onOk = onOk
    }

    fun dialog(){

        val v = (context as Activity).layoutInflater.inflate(R.layout.dialog_card_info,null)
        val dialog = AlertDialog.Builder(context)
            .setPositiveButton("Close") { dialog, pos ->
                onOk.invoke()
                dialog.dismiss()
            }.create()

        dialog.setView(v)
        dialog.setCancelable(false)

        dialog.show()
    }
}
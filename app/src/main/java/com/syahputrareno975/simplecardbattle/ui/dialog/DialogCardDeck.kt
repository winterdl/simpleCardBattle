package com.syahputrareno975.simplecardbattle.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simpleuno.adapter.AdapterCard

class DialogCardDeck {

    var context : Context
    var data : PlayerWithCardsModel
    var onCardChoosed : (CardModel) -> Unit

    constructor(context: Context,data : PlayerWithCardsModel, onCardChoosed : (CardModel) -> Unit) {
        this.context = context
        this.data = data
        this.onCardChoosed = onCardChoosed
    }

    fun dialog(){

        val v = (context as Activity).layoutInflater.inflate(R.layout.dialog_card_deck,null)

        val dialog = AlertDialog.Builder(context)
            .setPositiveButton("Close") { dialog, pos ->
                dialog.dismiss()
            }.create()

        val emptyDeck : TextView = v.findViewById(R.id.empty_deck)
        val cards : RecyclerView = v.findViewById(R.id.player_deck)
        val adapter = AdapterCard(context,data.Deck)
        adapter.setOnCardClick { cardModel, i ->
            onCardChoosed.invoke(cardModel)
            dialog.dismiss()
        }

        cards.adapter = adapter
        cards.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        emptyDeck.visibility = if (data.Deck.isEmpty()) View.VISIBLE else View.GONE
        cards.visibility = if (data.Deck.isEmpty()) View.GONE else View.VISIBLE

        dialog.setView(v)
        dialog.setCancelable(false)

        dialog.show()
    }
}
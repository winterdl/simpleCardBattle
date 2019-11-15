package com.syahputrareno975.simpleuno.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel

class AdapterPlayer : ArrayAdapter<PlayerModel> {
    var contextMenu : Context
    var res : Int
    var objects : List<PlayerModel>

    constructor(context: Context, resource: Int, objects: MutableList<PlayerModel>) : super(context, resource, objects) {
        this.contextMenu = context
        this.res = resource
        this.objects = objects
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        var holder: DataList? = null
        if (row == null) {
            val inflater = (context as Activity).layoutInflater
            row = inflater.inflate(res, parent, false)
            holder = DataList()

            holder.Name = row.findViewById(R.id.playerName)

            row.setTag(holder)
        } else {
            holder = (row.getTag() as DataList)
        }
        val item = getItem(position)

        holder.Name.text = item!!.Name

        return row!!
    }

    class DataList {
        lateinit var Name : TextView
    }
}
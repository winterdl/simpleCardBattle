package com.syahputrareno975.simpleuno.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel

class AdapterRoom : ArrayAdapter<RoomDataModel> {
    var contextMenu : Context
    var res : Int
    var objects : List<RoomDataModel>

    constructor(context: Context, resource: Int, objects: MutableList<RoomDataModel>) : super(context, resource, objects) {
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

            holder.Name = row.findViewById(R.id.RoomName)

            row.setTag(holder)
        } else {
            holder = (row.getTag() as DataList)
        }
        val item = getItem(position)

        holder.Name.text = item!!.RoomName

        return row!!
    }

    class DataList {
        lateinit var Name : TextView
    }
}
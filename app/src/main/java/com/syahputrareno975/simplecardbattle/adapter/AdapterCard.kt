package com.syahputrareno975.simpleuno.adapter

import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.util.NetDefault.Companion.NetConfigDefault
import java.lang.Exception
import java.text.DecimalFormat
import java.text.Format
import java.util.*
import kotlin.collections.ArrayList

class AdapterCard : RecyclerView.Adapter<AdapterCard.ViewHolder> {

    private var context : Context
    private var objects : ArrayList<CardModel>
    private var mInflater: LayoutInflater
    var Hidden : Boolean = false
    private lateinit var onCardClick : (CardModel,Int) -> Unit
    val formater = DecimalFormat("##,###")

   constructor(context: Context, objects : ArrayList<CardModel>) {
       this.context = context
       this.mInflater = LayoutInflater.from(context)
       this.objects  = objects

    }
    constructor(context: Context, objects : ArrayList<CardModel>,hidden : Boolean) {
        this.context = context
        this.mInflater = LayoutInflater.from(context)
        this.objects  = objects
        this.Hidden = hidden

    }

    fun setOnCardClick(onCardClick : (CardModel,Int)  -> Unit){
        this.onCardClick = onCardClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.adapter_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = objects.get(position)


        Picasso.get()
            .load("http://${NetConfigDefault.Url}:8080/${item.Image}")
            .into(holder.CardImage,object : Callback{
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    objects.get(position).Message = e!!.message!!
                }

            })

        holder.CardName.text = "${item.Name}"
        holder.CardDesc.text = if (Hidden) "(Atk : ???)\n(Def : ???)" else "(Atk : ${formater.format(item.Atk)})\n(Def : ${formater.format(item.Def)})"
        holder.CardLevel.text = "Level ${formater.format(item.Level)}"

        holder.CardColorSelected.setOnClickListener {
            onCardClick.invoke(objects.get(position),position)
        }

        when (objects.get(position).Flag) {
            0 -> {
                holder.CardColorSelected.setBackgroundColor(Color.WHITE)
            }
            1 -> {
                holder.CardColorSelected.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
        }
        when (objects.get(position).Color){
            0 -> {
                val color = Color.parseColor("#424242")
                holder.CardColor.setBackgroundColor(color)
            }
            1 -> {
                val color = Color.parseColor("#5D4037")
                holder.CardColor.setBackgroundColor(color)
            }
            2 -> {
                val color = Color.parseColor("#33691E")
                holder.CardColor.setBackgroundColor(color)
            }
            3 -> {
                val color = Color.parseColor("#FF9100")
                holder.CardColor.setBackgroundColor(color)
            }
            4 -> {
                val color = Color.parseColor("#4A148C")
                holder.CardColor.setBackgroundColor(color)
            }
        }
    }

    override fun getItemCount(): Int {
        return objects .size
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var CardColorSelected  :LinearLayout
        var CardColor : LinearLayout
        var CardName : TextView
        var CardDesc : TextView
        var CardImage : ImageView
        var CardLevel : TextView

        constructor(itemView: View) : super(itemView) {
            this.CardColor = itemView.findViewById(R.id.layout_card)
            this.CardName = itemView.findViewById(R.id.card_name)
            this.CardDesc = itemView.findViewById(R.id.card_description)
            this.CardImage = itemView.findViewById(R.id.card_image)
            this.CardColorSelected = itemView.findViewById(R.id.layout_card_selected)
            this.CardLevel = itemView.findViewById(R.id.card_level)
        }
    }
}
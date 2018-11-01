package com.andrewkir.andrewforwork.timem8.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.Models.dailyFrog
import com.andrewkir.andrewforwork.timem8.R

class DailyEditorFrogs(val context: Context,val itemClick: (dailyFrog) -> Unit): RecyclerView.Adapter<DailyEditorFrogs.Holder>() {
    var db = DBdaily(context)
    private var frogs = db.allFrogs()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyEditorFrogs.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_daily_editor, parent,false)
        return Holder(view,itemClick)
    }

    override fun getItemCount(): Int {
        return frogs.size
    }

    override fun onBindViewHolder(holder: DailyEditorFrogs.Holder, position: Int) {
        holder.bindFrog(frogs[position])
    }
    inner class Holder(itemView: View?, val itemClick: (dailyFrog) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val frogName = itemView?.findViewById<TextView>(R.id.rowDailyEditorName)
        private val frogDesc = itemView?.findViewById<TextView>(R.id.rowDailyEditorDesc)
        private val frogTask = itemView?.findViewById<TextView>(R.id.rowDailyEditorTask)

        fun bindFrog(frog: dailyFrog){

            frogName!!.text = frog.name
            frogDesc!!.text = frog.description
            frogTask!!.text = "Задач: ${frog.count}"
            itemView.setOnClickListener { itemClick(frog) }
        }
    }
}
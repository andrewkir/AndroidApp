package com.andrewkir.andrewforwork.timem8.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.Models.dailyFrog
import com.andrewkir.andrewforwork.timem8.R

class DailyCheckBoxAdapter(val context:Context,var frog:dailyFrog):RecyclerView.Adapter<DailyCheckBoxAdapter.Holder>() {
    lateinit var db: DBdaily
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.daily_checkboxes_editor, parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return frog.count
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bindcheckbox(position)
    }

    inner class Holder(itemView:View?): RecyclerView.ViewHolder(itemView){
        val checkBox = itemView?.findViewById<CheckBox>(R.id.dailyRowCheck)

        fun bindcheckbox(position: Int){
            var task = frog.tasks.split(";;;")
            var tasks = ArrayList<String>()
            for (i in 0 until frog.count){
                tasks.add(task[i])
            }
            checkBox!!.text = tasks[position]
        }
    }
}
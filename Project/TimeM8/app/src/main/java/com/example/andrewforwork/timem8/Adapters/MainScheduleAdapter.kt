package com.example.andrewforwork.timem8.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.andrewforwork.timem8.R
import com.example.andrewforwork.timem8.Subject.Sub


class MainScheduleAdapter(val context: Context, val subjects: List<Sub>, val itemClick: (Sub) -> Unit) : RecyclerView.Adapter<MainScheduleAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.subject_list_item, parent,false)
        return Holder(view,itemClick)
    }

    override fun getItemCount(): Int {
        return subjects.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bindSubject(subjects[position],context)
    }


    inner class Holder(itemView: View?, val itemClick: (Sub) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val subjectName = itemView?.findViewById<TextView>(R.id.subjectName)
        val subjectTime = itemView?.findViewById<TextView>(R.id.subjectTime)

        fun bindSubject(subject: Sub, context: Context){
            subjectName?.text = subject.name
            subjectTime?.text = subject.time

            itemView.setOnClickListener { itemClick(subject) }
        }
    }
}
package com.example.andrewforwork.timem8.Adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.andrewforwork.timem8.DataBase.DBdetailinfo
import com.example.andrewforwork.timem8.R
import com.example.andrewforwork.timem8.Subject.Sub


class MainScheduleAdapter(val date: String, val context: Context, val subjects: List<Sub>, val itemClick: (Sub) -> Unit) : RecyclerView.Adapter<MainScheduleAdapter.Holder>() {
    internal lateinit var db: DBdetailinfo
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.subject_list_item, parent,false)
        //view.setBackgroundColor(Color.parseColor("#ff9966"))
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
        val subjectHome = itemView?.findViewById<TextView>(R.id.HomeworkText)
        val subjectType = itemView?.findViewById<TextView>(R.id.subjectType)
        val subjectRoom = itemView?.findViewById<TextView>(R.id.subjectRoom)
        val subjectTeacher = itemView?.findViewById<TextView>(R.id.subjectTeacher)
        fun bindSubject(subject: Sub, context: Context){

            subjectName?.text = subject.name
            subjectTime?.text = subject.timeBegin+"-"+subject.timeEnd
            subjectType?.text = subject.type
            subjectRoom?.text = subject.room
            subjectTeacher?.text = subject.teacher


            db = DBdetailinfo(context)
            try {
                var sub = db.allSubDetailByDay(sub_parent = subject.name,date = date,count = subject.count)[0]
                subjectHome?.text = sub.homework
                itemView.setBackgroundColor(sub.color)
            }
            catch (e: Exception){
                subjectHome?.text = ""
            }

            itemView.setOnClickListener { itemClick(subject) }
        }
    }
}
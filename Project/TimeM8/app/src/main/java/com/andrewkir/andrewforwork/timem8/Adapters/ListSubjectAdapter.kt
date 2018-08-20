package com.andrewkir.andrewforwork.timem8.Adapters

import android.app.Activity
import android.content.Context
import kotlin.reflect.KMutableProperty0
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.andrewkir.andrewforwork.timem8.R
import com.andrewkir.andrewforwork.timem8.Models.Sub
import kotlinx.android.synthetic.main.row_main.view.*

class ListSubjectAdapter(
        internal var activity: Activity,
        internal var lstSub:List<Sub>,
        internal var edit_day_spinner:Spinner,
        internal var edit_name: EditText,
        internal var timeB_btn: Button,
        internal var timeE_btn: Button,
        internal var edit_cnt:EditText,
        internal var edit_type: EditText,
        internal var edit_teacher: EditText,
        internal var edit_room: EditText,
        var isedit: KMutableProperty0<Int>):BaseAdapter() {
    internal var inflater:LayoutInflater

    init {
        inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView: View
        rowView = inflater.inflate(R.layout.row_main,null)

        //rowView.txt_row_id.text = lstSub[position].id.toString() разобраться с днем недели

        rowView.txt_row_count.text = lstSub[position].count.toString()
        rowView.txt_row_name.text = lstSub[position].name
        rowView.txt_row_time.text = lstSub[position].timeBegin+"-"+lstSub[position].timeEnd
        rowView.txt_row_type.text = lstSub[position].type
        rowView.txt_row_day.text = lstSub[position].day.toString()
        rowView.txt_row_type.text = lstSub[position].type
        rowView.txt_row_room.text = lstSub[position].room
        rowView.txt_row_teacher.text = lstSub[position].teacher

        when(lstSub[position].day.toString()) {
            "1" -> rowView.txt_row_day.text = "Пн"
            "2" -> rowView.txt_row_day.text = "Вт"
            "3" -> rowView.txt_row_day.text = "Ср"
            "4" -> rowView.txt_row_day.text = "Чт"
            "5" -> rowView.txt_row_day.text = "Пт"
            "6" -> rowView.txt_row_day.text = "Сб"
            "7" -> rowView.txt_row_day.text = "Вс"
            else -> rowView.txt_row_day.text = lstSub[position].day.toString()
        }

        rowView.setOnClickListener{
            isedit.set(1)
            edit_day_spinner.setSelection(Integer.parseInt(lstSub[position].day.toString())-1)
            edit_cnt.setText(rowView.txt_row_count.text.toString())
            edit_name.setText(rowView.txt_row_name.text.toString())
            timeB_btn.setText(rowView.txt_row_time.text.toString().split("-")[0].toString())
            timeE_btn.setText(rowView.txt_row_time.text.toString().split("-")[1].toString())
            edit_type.setText(rowView.txt_row_type.text.toString())
            edit_room.setText(rowView.txt_row_room.text.toString())
            edit_teacher.setText(rowView.txt_row_teacher.text.toString())

        }
        return rowView
    }

    override fun getItem(position: Int): Any {
        return lstSub[position]
    }

    override fun getItemId(position: Int): Long {
        return lstSub[position].id.toLong()
    }

    override fun getCount(): Int {
        return lstSub.size
    }
}
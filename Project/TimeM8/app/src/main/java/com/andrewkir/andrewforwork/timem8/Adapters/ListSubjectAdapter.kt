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
        private var activity: Activity,
        private var lstSub:List<Sub>,
        private var edit_day_spinner:Spinner,
        private var edit_name: EditText,
        private var timeB_btn: Button,
        private var timeE_btn: Button,
        private var edit_cnt:EditText,
        private var edit_type: EditText,
        private var edit_teacher: EditText,
        private var edit_room: EditText,
        private var isEdit: KMutableProperty0<Int>):BaseAdapter() {
    private var inflater:LayoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView: View = inflater.inflate(R.layout.row_main,null)

        rowView.txtRowCount.text = lstSub[position].count.toString()
        rowView.txtRowName.text = lstSub[position].name
        rowView.txtRowTime.text = lstSub[position].timeBegin+"-"+lstSub[position].timeEnd
        rowView.txtRowType.text = lstSub[position].type
        rowView.txtRowDay.text = lstSub[position].day.toString()
        rowView.txtRowType.text = lstSub[position].type
        rowView.txtRowRoom.text = lstSub[position].room
        rowView.txtRowTeacher.text = lstSub[position].teacher

        when(lstSub[position].day.toString()) {
            "1" -> rowView.txtRowDay.text = "Пн"
            "2" -> rowView.txtRowDay.text = "Вт"
            "3" -> rowView.txtRowDay.text = "Ср"
            "4" -> rowView.txtRowDay.text = "Чт"
            "5" -> rowView.txtRowDay.text = "Пт"
            "6" -> rowView.txtRowDay.text = "Сб"
            "7" -> rowView.txtRowDay.text = "Вс"
            else -> rowView.txtRowDay.text = lstSub[position].day.toString()
        }

        rowView.setOnClickListener{
            isEdit.set(1)
            edit_day_spinner.setSelection(Integer.parseInt(lstSub[position].day.toString())-1)
            edit_cnt.setText(rowView.txtRowCount.text.toString())
            edit_name.setText(rowView.txtRowName.text.toString())
            timeB_btn.text = rowView.txtRowTime.text.toString().split("-")[0]
            timeE_btn.text = rowView.txtRowTime.text.toString().split("-")[1]
            edit_type.setText(rowView.txtRowType.text.toString())
            edit_room.setText(rowView.txtRowRoom.text.toString())
            edit_teacher.setText(rowView.txtRowTeacher.text.toString())
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
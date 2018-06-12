package com.example.andrewforwork.timem8.MainScheduleEditor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.andrewforwork.timem8.DataBase.DBHandler
import com.example.andrewforwork.timem8.MainScheduleAdapter.ListSubjectAdapter
import com.example.andrewforwork.timem8.R
import com.example.andrewforwork.timem8.Subject.Sub
import kotlinx.android.synthetic.main.activity_main_editor.*

class MainEditor : AppCompatActivity() {

    internal lateinit var db: DBHandler
    internal var lstSubs:List<Sub> = ArrayList<Sub>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_editor)

        db = DBHandler(this)

        refreshData()
        btn_insert.setOnClickListener{
            val sub = Sub(
                    Integer.parseInt(day.text.toString()+count.text.toString()),
                    Integer.parseInt(day.text.toString()),
                    Integer.parseInt(count.text.toString()),
                    name.text.toString(),
                    time.text.toString(),
                    0,
                    type.text.toString()
            )
            db.addSub(sub)
            refreshData()
        }
        btn_update.setOnClickListener{
            val sub = Sub(
                    Integer.parseInt(day.text.toString()+count.text.toString()),
                    Integer.parseInt(day.text.toString()),
                    Integer.parseInt(count.text.toString()),
                    name.text.toString(),
                    time.text.toString(),
                    0,
                    type.text.toString()
            )
            db.updateSub(sub)
            refreshData()
        }
        btn_delete.setOnClickListener{
            val sub = Sub(
                    Integer.parseInt(day.text.toString()+count.text.toString()),
                    Integer.parseInt(day.text.toString()),
                    Integer.parseInt(count.text.toString()),
                    name.text.toString(),
                    time.text.toString(),
                    0,
                    type.text.toString()
            )
            db.deleteUser(sub)
            refreshData()
        }
    }
    private fun refreshData(){
        lstSubs = db.allUser
        val adapter = ListSubjectAdapter(this@MainEditor,lstSubs,day,name,time,0,count)
        list_subs.adapter = adapter
    }
}
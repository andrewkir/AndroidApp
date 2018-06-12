package com.example.andrewforwork.timem8

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.andrewforwork.timem8.DataBase.DBHandler
import com.example.andrewforwork.timem8.MainScheduleAdapter.ListSubjectAdapter
import com.example.andrewforwork.timem8.MainScheduleEditor.MainEditor
import com.example.andrewforwork.timem8.Subject.Sub
import kotlinx.android.synthetic.main.activity_main_editor.*
import kotlinx.android.synthetic.main.activity_main_schedule_editable.*

class MainScheduleEditable : AppCompatActivity() {
    internal lateinit var db: DBHandler
    internal var lstSubs:List<Sub> = ArrayList<Sub>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_schedule_editable)

        db = DBHandler(this)

        refreshData()
        Edit_btn.setOnClickListener{
            val Editor = Intent(this,MainEditor::class.java)
            startActivity(Editor)
        }
    }
    private fun refreshData(){
        lstSubs = db.allUser
        val adapter = ListSubjectAdapter(this@MainScheduleEditable,lstSubs,day,name,time,0,count)
        list_sub.adapter = adapter
    }

}

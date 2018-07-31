package com.example.andrewforwork.timem8.Editors

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.andrewforwork.timem8.DataBase.DBdetailinfo
import com.example.andrewforwork.timem8.R
import com.example.andrewforwork.timem8.Subject.SubDetail
import kotlinx.android.synthetic.main.activity_main_detail_editor.*

class MainDetailEditor : AppCompatActivity() {

    var subjectName = ""
    var date = ""
    internal lateinit var db: DBdetailinfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_detail_editor)
        db = DBdetailinfo(this)
        subjectName = intent.getStringExtra("NAME_SUB")
        date = intent.getStringExtra("DATE")
        var count = intent.getIntExtra("COUNT_SUB",0)
        try {
            var subject = db.allSubDetailByDay(subjectName,date)[0]
            //TODO change later
            when(subject.hasimage){
                1 -> switchPhotoAttach.performClick()
            }
            editTextHomework.setText(subject.homework)
        }
        catch(e: Exception) {

        }
        saveBtn.setOnClickListener {
            var dateArr =date.split(".")
            println(dateArr)
            var subdt = SubDetail(
                    id=Integer.parseInt(dateArr[0]+dateArr[1]+dateArr[2]+count.toString()),
                    date = date,
                    parent = subjectName,
                    homework = editTextHomework.text.toString(),
                    hasimage = if(switchPhotoAttach.isChecked) 1 else 0,
                    path = "path" //TODO CHANGE LATER
            )
            try {
                db.addSub_detail(subdt)
                Toast.makeText(this,"добавлено",Toast.LENGTH_SHORT).show()
            } catch (e: Exception){
                Toast.makeText(this,"сохранено",Toast.LENGTH_SHORT).show()
                db.updateSub_detail(subdt)
            }
        }
    }

    //TODO сделать своё menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        getMenuInflater().inflate(R.menu.activity_main_editor_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        db.deleteAllData()
        editTextHomework.setText("")
        return true
    }
}

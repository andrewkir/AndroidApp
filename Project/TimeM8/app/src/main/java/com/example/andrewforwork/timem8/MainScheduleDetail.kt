package com.example.andrewforwork.timem8

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.andrewforwork.timem8.DataBase.DBdetailinfo
import com.example.andrewforwork.timem8.Editors.MainDetailEditor
import kotlinx.android.synthetic.main.activity_main_schedule_detail.*

class MainScheduleDetail : AppCompatActivity() {
    internal lateinit var db: DBdetailinfo
    var date = ""
    var SubName = ""
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_schedule_detail)
        SubName = intent.getStringExtra("NAME_SUB")
        date = intent.getStringExtra("DATE")
        count = intent.getIntExtra("COUNT_SUB",0)
        Toast.makeText(this,SubName+" "+date,Toast.LENGTH_SHORT).show()
        detailTextSubName.text = SubName
        detailTextDate.text = date

        db = DBdetailinfo(this)
        try {
            var currSub = db.allSubDetailByDay(SubName, date)[0]
            detailTextHomework.text = "Домашнее задание: \n"+if(currSub.homework.isEmpty()) "" else currSub.homework
        }
        catch(e: Exception) {
            detailTextHomework.text = "Домашнее задание:\n"
        }
//
//        var detailSub = db.allSubDetailByDay(SubName,date)[0]
//        detailTextHomework.text = detailSub.homework
    }
    fun onDetailImage(view: View){
        Toast.makeText(this,"image",Toast.LENGTH_SHORT).show()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        getMenuInflater().inflate(R.menu.activity_main_schedule_menu,menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        try {
            var currSub = db.allSubDetailByDay(SubName, date)[0]
            detailTextHomework.text = "Домашнее задание: \n"+if(currSub.homework.isEmpty()) "" else currSub.homework
        }
        catch(e: Exception) {
            detailTextHomework.text = "Домашнее задание:\n"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //TODO Перенаправление на редактирование предмета
        val DetailEditor = Intent(this,MainDetailEditor::class.java)
        DetailEditor.putExtra("NAME_SUB",SubName)
        DetailEditor.putExtra("DATE",date)
        DetailEditor.putExtra("COUNT_SUB",count)
        startActivity(DetailEditor)
        return true
    }
}

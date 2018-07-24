package com.example.andrewforwork.timem8

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.andrewforwork.timem8.Adapters.MainScheduleAdapter
import com.example.andrewforwork.timem8.DataBase.DBHandler
import com.example.andrewforwork.timem8.MainScheduleEditor.MainEditor
import com.example.andrewforwork.timem8.Subject.Sub
import kotlinx.android.synthetic.main.activity_main_schedule_editable.*

class MainScheduleEditable : AppCompatActivity() {
    internal lateinit var db: DBHandler
    internal var lstSubs:List<Sub> = ArrayList<Sub>()
    lateinit var adapter: MainScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_schedule_editable)

        db = DBHandler(this)
        adapter = MainScheduleAdapter(this,db.allSub){
            subject ->
            Toast.makeText(this,subject.name,Toast.LENGTH_SHORT).show()
        }
        subjectList.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        subjectList.layoutManager = layoutManager
        subjectList.setHasFixedSize(true)
    }

    override fun onResume() {
        refreshData()
        super.onResume()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        getMenuInflater().inflate(R.menu.activity_main_schedule_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val Editor = Intent(this,MainEditor::class.java)
        startActivity(Editor)
        return true
    }

    private fun refreshData(){
        lstSubs = db.allSub
        val adapter = MainScheduleAdapter(this,db.allSub){
            subject ->
            Toast.makeText(this,subject.name,Toast.LENGTH_SHORT).show()
        }
        subjectList.adapter = adapter
    }
}

package com.andrewkir.andrewforwork.timem8.Editors

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.andrewkir.andrewforwork.timem8.Adapters.DailyCheckBoxAdapter
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.Models.dailyFrog
import com.andrewkir.andrewforwork.timem8.R
import kotlinx.android.synthetic.main.activity_daily_editor.*

class DailyEditor : AppCompatActivity() {
    var name = "test"
    var date = "1.1.1.1"
    lateinit var frog:dailyFrog
    lateinit var db: DBdaily
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_editor)
        db = DBdaily(this)
        //recyclerCheckBox.adapter = DailyCheckBoxAdapter(this,name,date)
        frog = db.allFrogByDayName(date = date,name = name)[0]
        refresh()
        dailyAddBtn.setOnClickListener {
            frog.tasks = frog.tasks+taskEdit.text.toString()+";;;"
            frog.count++
            frog.isDone.add(false)
            println(frog.tasks)
            db.updateFrog(frog)
            refresh()
        }
        dailyDelBtn.setOnClickListener {
            if (frog.count >= 1) {
                var tasks = frog.tasks.split(";;;")
                frog.tasks = ""
                for (i in 0 until frog.count-1) {
                    frog.tasks += tasks[i] + ";;;"
                }
                println(frog.tasks)
                frog.count--
                frog.isDone.removeAt(frog.isDone.lastIndex)
                db.updateFrog(frog)
                refresh()
            }
        }
    }

    fun refresh(){
        recyclerCheckBox.adapter = DailyCheckBoxAdapter(context = this,frog = frog)
        val layoutManager = LinearLayoutManager(this)
        recyclerCheckBox.layoutManager = layoutManager
        recyclerCheckBox.setHasFixedSize(true)
    }
}

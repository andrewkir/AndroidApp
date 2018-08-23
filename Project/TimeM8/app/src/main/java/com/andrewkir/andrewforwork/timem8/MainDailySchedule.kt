package com.andrewkir.andrewforwork.timem8

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.andrewkir.andrewforwork.timem8.Adapters.SwipeToDeleteAdapter
import com.andrewkir.andrewforwork.timem8.Adapters.expandAdapter
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.Editors.DailyEditor
import com.andrewkir.andrewforwork.timem8.Models.dailyFrog
import kotlinx.android.synthetic.main.activity_main_daily_schedule.*
import java.util.*
import kotlin.collections.ArrayList


class MainDailySchedule : AppCompatActivity(){
    var day = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_daily_schedule)
        expandRecycler.addItemDecoration(DividerItemDecoration(this,1))
        expandRecycler.layoutManager = LinearLayoutManager(this)


        val db =  DBdaily(this)
        var calendar = Calendar.getInstance()
        when(calendar.get(Calendar.DAY_OF_WEEK)){
            2 -> day = "Пн"
            3 -> day = "Вт"
            4 -> day = "Ср"
            5 -> day = "Чт"
            6 -> day = "Пт"
            7 -> day = "Сб"
            1 -> day = "Вс"
        }
        expandRecycler.adapter = expandAdapter(data = db.allFrogByDay(day) as ArrayList<dailyFrog>)

        val swipeHandler = object : SwipeToDeleteAdapter(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = expandRecycler.adapter as expandAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                expandRecycler.adapter = expandAdapter(db.allFrogByDay(day) as ArrayList<dailyFrog>)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(expandRecycler)
    }

    override fun onResume() {
        super.onResume()
        val db =  DBdaily(this)
        expandRecycler.adapter = expandAdapter(db.allFrogByDay(day) as ArrayList<dailyFrog>)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_schedule_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.edit_menu) {
            val Editor = Intent(this, DailyEditor::class.java)
            startActivity(Editor)
        } else {
            this.finish()
        }
        return true
    }
}

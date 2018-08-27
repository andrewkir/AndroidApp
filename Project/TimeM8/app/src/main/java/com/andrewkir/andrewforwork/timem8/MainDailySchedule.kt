package com.andrewkir.andrewforwork.timem8

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import com.andrewkir.andrewforwork.timem8.Adapters.SwipeToDeleteAdapter
import com.andrewkir.andrewforwork.timem8.Adapters.expandAdapter
import com.andrewkir.andrewforwork.timem8.Adapters.swipeToDoneAdapter
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.Editors.DailyEditor
import com.andrewkir.andrewforwork.timem8.Models.dailyFrog
import kotlinx.android.synthetic.main.activity_main_daily_schedule.*
import java.util.*
import kotlin.collections.ArrayList
import android.R.id.edit
import android.content.SharedPreferences.Editor
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.os.CountDownTimer
import android.widget.Toast


class MainDailySchedule : AppCompatActivity(){
    var day = ""
    val CHECK_DAY = "day"
    var currentVisiblePosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkDay()
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
                AlertDialog.Builder(this@MainDailySchedule)
                        .setMessage("Вы точно хотите удалить это задание?")
                        .setCancelable(false)
                        .setPositiveButton("Удалить") { _, _ ->
                            val adapter = expandRecycler.adapter as expandAdapter
                            adapter.removeAt(viewHolder.adapterPosition)
                            expandRecycler.adapter = expandAdapter(db.allFrogByDay(day) as ArrayList<dailyFrog>)
                        }
                        .setNegativeButton("Отмена"){ _, _ ->
                            expandRecycler.adapter = expandAdapter(db.allFrogByDay(day) as ArrayList<dailyFrog>)
                        }
                        .show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(expandRecycler)
        val swipeHandlerLeft = object : swipeToDoneAdapter(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                println(viewHolder.adapterPosition)
                val adapter = expandRecycler.adapter as expandAdapter
                adapter.doneAt(viewHolder.adapterPosition)
                expandRecycler.adapter = expandAdapter(db.allFrogByDay(day) as ArrayList<dailyFrog>)
            }
        }
        val itemTouchHelperLeft = ItemTouchHelper(swipeHandlerLeft)
        itemTouchHelperLeft.attachToRecyclerView(expandRecycler)


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

    fun checkDay(){
        var sPref = getPreferences(Context.MODE_PRIVATE)
        var savedDay = sPref.getInt(CHECK_DAY, -1)
        if(savedDay != -1){
            if(savedDay != Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
                val db =  DBdaily(this)
                var frogs = db.allFrogs()
                for(frog in frogs){
                    frog.isDone.fill(false)
                    frog.done = false
                    db.updateFrog(frog)
                }
                savedDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                sPref = getPreferences(Context.MODE_PRIVATE)
                val ed = sPref.edit()
                ed.putInt(CHECK_DAY, savedDay)
                ed.apply()
            }
        } else {
            savedDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            sPref = getPreferences(Context.MODE_PRIVATE)
            val ed = sPref.edit()
            ed.putInt(CHECK_DAY, savedDay)
            ed.apply()
        }
    }
}

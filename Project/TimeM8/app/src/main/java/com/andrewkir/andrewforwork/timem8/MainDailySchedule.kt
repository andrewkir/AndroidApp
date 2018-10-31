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
import android.app.ActivityManager
import android.content.SharedPreferences.Editor
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AlertDialog
import android.os.CountDownTimer
import android.util.TypedValue
import android.widget.Toast


class MainDailySchedule : AppCompatActivity(){
    var day = ""
    val CHECK_DAY = "day"
    var currentVisiblePosition = 0
    lateinit var sPref: SharedPreferences
    var stat: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sPref = getSharedPreferences("ThemePrefs",Context.MODE_PRIVATE)
        stat = sPref.getString("THEME", "ORANGE")
        when (stat) {
            "ORANGE" -> setTheme(R.style.AppTheme)
            "GREEN" -> setTheme(R.style.AppThemeGreen)
            "PURPLE" -> setTheme(R.style.AppThemePurple)
            "BLUE" -> setTheme(R.style.AppThemeBlue)
        }
        checkDay()
        setContentView(R.layout.activity_main_daily_schedule)
        expandRecycler.addItemDecoration(DividerItemDecoration(this,1))
        expandRecycler.layoutManager = LinearLayoutManager(this)

        fabDaily.setOnClickListener {
            val Editor = Intent(this, DailyEditor::class.java)
            startActivity(Editor)
        }
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
        if(!db.allFrogByDay(day).isEmpty()) {
            expandRecycler.adapter = expandAdapter(data = db.allFrogByDay(day) as ArrayList<dailyFrog>)
            hintToAddSmthDaily.text = ""
        } else {
            hintToAddSmthDaily.text = "Вы ещё не добавили задания на этот день"
        }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }
        val db =  DBdaily(this)
        if(!db.allFrogByDay(day).isEmpty()){
            hintToAddSmthDaily.text = ""
            expandRecycler.adapter = expandAdapter(db.allFrogByDay(day) as ArrayList<dailyFrog>)
        } else {
            hintToAddSmthDaily.text = "Вы ещё не добавили задания на этот день"
        }
        showHideWhenScroll()
    }
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.activity_main_schedule_menu, menu)
//        return true
//    }
//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        if (item?.itemId == R.id.edit_menu) {
//            val Editor = Intent(this, DailyEditor::class.java)
//            startActivity(Editor)
//        } else {
//            this.finish()
//        }
//        return true
//    }

    private fun checkDay(){
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
    private fun showHideWhenScroll() {
        expandRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0)
                    fabDaily.hide()
                else
                    fabDaily.show()
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }
}

package com.andrewkir.andrewforwork.timem8

import android.app.ActionBar
import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.ramotion.circlemenu.CircleMenuView
import android.support.annotation.NonNull
import android.support.constraint.Constraints
import android.support.v7.app.AlertDialog
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.andrewkir.andrewforwork.timem8.DataBase.DBHandler
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.DataBase.DBdetailinfo
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {
    lateinit var sPref: SharedPreferences
    var stat: String = ""
    var open = 0
    var themes = arrayOf("ORANGE","GREEN","PURPLE","BLUE")
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
        setContentView(R.layout.activity_settings)

        var tmp= getSharedPreferences("NotifMin", Context.MODE_PRIVATE)
        var tMin = tmp.getInt("MINUTES",-1)
        if(tMin !=-1){
            minutesSettings.setText(tMin.toString())
        }
        circleMenu.visibility = View.GONE
        linearcircle.setOnClickListener {
            if(open == 1){
                circleMenu.close(true)
                open = 0
            } else {
                circleMenu.visibility = View.VISIBLE
                val handler = Handler()
                handler.postDelayed(Runnable {
                    circleMenu.open(true)
                }, 100)
            }
        }
        deleteMainSettBtn.setOnClickListener {
            AlertDialog.Builder(this@SettingsActivity)
                    .setMessage("Вы точно хотите удалить эту информацию?\n(Отменить это действие будет невозможно)")
                    .setCancelable(false)
                    .setPositiveButton("Удалить") { _, _ ->
                        var db = DBHandler(this)
                        db.deleteAllData()
                        Toast.makeText(this,"Основное расписание удалено",Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Отмена"){ _, _ ->

                    }
                    .show()
        }
        deleteMainDetailSettBtn.setOnClickListener {
            AlertDialog.Builder(this@SettingsActivity)
                    .setMessage("Вы точно хотите удалить эту информацию?\n(Отменить это действие будет невозможно)")
                    .setCancelable(false)
                    .setPositiveButton("Удалить") { _, _ ->
                        var db = DBdetailinfo(this)
                        db.deleteAllData()
                        Toast.makeText(this,"Детальная информация удалена",Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Отмена"){ _, _ ->

                    }
                    .show()
        }
        deleteFrogSettBtn.setOnClickListener {
            AlertDialog.Builder(this@SettingsActivity)
                    .setMessage("Вы точно хотите удалить эту информацию?\n(Отменить это действие будет невозможно)")
                    .setCancelable(false)
                    .setPositiveButton("Удалить") { _, _ ->
                        var dbtmp = DBdaily(this)
                        dbtmp.deleteAllData()
                        Toast.makeText(this,"Ежедневное расписание удалено",Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Отмена"){ _, _ ->

                    }
                    .show()
        }
        saveMinutes.setOnClickListener {
            if(minutesSettings.text.toString() != "" && minutesSettings.text.toString() != "0") {
                var pref = getSharedPreferences("NotifMin", Context.MODE_PRIVATE)
                var ed = pref.edit()
                ed.putInt("MINUTES", Integer.parseInt(minutesSettings.text.toString()))
                ed.apply()
                Toast.makeText(this,"Сохранено",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"Пожалуйста, введите корректные данные",Toast.LENGTH_SHORT).show()
            }
        }
        val menu = circleMenu
        menu.eventListener = object : CircleMenuView.EventListener() {
            override fun onMenuOpenAnimationStart(view: CircleMenuView) {

            }

            override fun onMenuOpenAnimationEnd(view: CircleMenuView) {
                Log.d("D", "onMenuOpenAnimationEnd")
                open = 1
            }

            override fun onMenuCloseAnimationStart(view: CircleMenuView) {
                Log.d("D", "onMenuCloseAnimationStart")
            }

            override fun onMenuCloseAnimationEnd(view: CircleMenuView) {
                circleMenu.visibility = View.GONE
                open = 0
            }

            override fun onButtonClickAnimationStart(view: CircleMenuView, index: Int) {
                Log.d("D", "onButtonClickAnimationStart| index: $index")
            }

            override fun onButtonClickAnimationEnd(view: CircleMenuView, index: Int) {
                val ed = sPref.edit()
                ed.putString("THEME", themes[index])
                ed.apply()
                finish()
                startActivity(intent)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }
    }

    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}

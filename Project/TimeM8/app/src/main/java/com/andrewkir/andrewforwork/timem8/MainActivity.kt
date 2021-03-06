package com.andrewkir.andrewforwork.timem8

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.andrewkir.andrewforwork.timem8.ActivityFin.FinancialActivity
import com.andrewkir.andrewforwork.timem8.MainScheduleEdit.MainScheduleEditable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var sPref:SharedPreferences
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
        setContentView(R.layout.activity_main)

        MainScheduleBtn.visibility = View.GONE
        dailyTasksBtn.setOnClickListener{
            val dailyTaskIntent = Intent(this,MainDailySchedule::class.java)
            startActivity(dailyTaskIntent)
        }

        MainScheduleBtn.setOnClickListener{
            val scheduleIntent = Intent(this, MainSchedule::class.java)
            startActivity(scheduleIntent)
        }

        MainScheduleBtnEd.setOnClickListener{
            val scheduleIntentEd = Intent(this, MainScheduleEditable::class.java)
            startActivity(scheduleIntentEd)
        }

        financeButton.setOnClickListener {
            val financialIntent = Intent(this, FinancialActivity::class.java)
            startActivity(financialIntent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_settings, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.settings_menu) {
            //переход в настройки
            val settings = Intent(this, SettingsActivity::class.java)
            startActivity(settings)
        }
        return true
    }


    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }

        sPref = getSharedPreferences("ThemePrefs",Context.MODE_PRIVATE)
        if(stat != sPref.getString("THEME", "ORANGE")){
            finish()
            startActivity(intent)
        }
    }
}

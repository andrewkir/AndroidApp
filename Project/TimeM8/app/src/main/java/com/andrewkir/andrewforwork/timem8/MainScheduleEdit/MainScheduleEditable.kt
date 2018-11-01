package com.andrewkir.andrewforwork.timem8.MainScheduleEdit

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import com.andrewkir.andrewforwork.timem8.Editors.MainEditor
import com.andrewkir.andrewforwork.timem8.R
import kotlinx.android.synthetic.main.activity_main_schedule_editable.*
import java.util.*
import android.graphics.BitmapFactory
import android.os.Build


class MainScheduleEditable : AppCompatActivity() {
    private lateinit var pagerAdapter: PagerAdapter
    var day = 0
    var positionDay = 0
    var count = 0
    lateinit var sPref: SharedPreferences
    var stat: String = ""


    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sPref = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        stat = sPref.getString("THEME", "ORANGE")
        when (stat) {
            "ORANGE" -> setTheme(R.style.AppTheme)
            "GREEN" -> setTheme(R.style.AppThemeGreen)
            "PURPLE" -> setTheme(R.style.AppThemePurple)
            "BLUE" -> setTheme(R.style.AppThemeBlue)
        }
        setContentView(R.layout.activity_main_schedule_editable)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pagerAdapter = PagerAdapter(supportFragmentManager, 0)
        pagerView.adapter = pagerAdapter
        pagerView.currentItem = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        positionDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        applyDay()

        supportActionBar?.title = getMonth()
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, typedValue,true)
        pagerTabStrip.tabIndicatorColor = typedValue.data
        pagerTabStrip.setTextColor(resources.getColor(R.color.Txt))
        pagerView?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                positionDay = position
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR,2018)
                calendar.set(Calendar.MONTH,0)
                calendar.set(Calendar.DAY_OF_YEAR,0)
                calendar.add(Calendar.DATE,position)
                day = calendar.get(Calendar.DAY_OF_WEEK)
                supportActionBar?.title = getMonth()
                applyDay()
            }
        })
    }


    fun applyDay(){
        when (day) {
            2 -> day = 0
            3 -> day = 1
            4 -> day = 2
            5 -> day = 3
            6 -> day = 4
            7 -> day = 5
            1 -> day = 6
        }
    }


    fun getMonth():String{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR,2018)
        calendar.set(Calendar.MONTH,0)
        calendar.set(Calendar.DAY_OF_YEAR,0)
        calendar.add(Calendar.DATE,positionDay)
        when(calendar.get(Calendar.MONTH)){
            0 -> return "Январь"
            1 -> return "Февраль"
            2 -> return "Март"
            3 -> return "Апрель"
            4 -> return "Май"
            5 -> return "Июнь"
            6 -> return "Июль"
            7 -> return "Август"
            8 -> return "Сентябрь"
            9 -> return "Октябрь"
            10 ->return  "Ноябрь"
            11 ->return  "Декабрь"
        }
        return ""
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_schedule_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.edit_menu) {
            val editor = Intent(this, MainEditor::class.java)
            editor.putExtra("DAY_OF_THE_WEEK", day)
            startActivity(editor)
        } else {
            this.finish()
        }
        return true
    }
}

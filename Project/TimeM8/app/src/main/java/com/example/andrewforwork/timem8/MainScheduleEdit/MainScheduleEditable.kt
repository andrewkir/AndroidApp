package com.example.andrewforwork.timem8.MainScheduleEdit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import com.example.andrewforwork.timem8.Editors.MainEditor
import com.example.andrewforwork.timem8.R
import kotlinx.android.synthetic.main.activity_main_schedule_editable.*
import java.util.*

class MainScheduleEditable : AppCompatActivity() {
    private lateinit var pagerAdapter: PagerAdapter
    var day = 0
    var position_day = 0
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_schedule_editable)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pagerAdapter = PagerAdapter(supportFragmentManager, 0)
        pagerView.adapter = pagerAdapter
        pagerView.currentItem = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        position_day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        applyDay()

        supportActionBar?.title = getMonth()
        pagerTabStrip.tabIndicatorColor = resources.getColor(R.color.colorAccent)
        pagerTabStrip.setTextColor(resources.getColor(R.color.Txt))
        pagerView?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                position_day = position
                var calendar = Calendar.getInstance()
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
        var calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR,2018)
        calendar.set(Calendar.MONTH,0)
        calendar.set(Calendar.DAY_OF_YEAR,0)
        calendar.add(Calendar.DATE,position_day)
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
            val Editor = Intent(this, MainEditor::class.java)
            Editor.putExtra("DAY_OF_THE_WEEK", day)
            startActivity(Editor)
        } else {
            this.finish()
        }
        return true
    }
}

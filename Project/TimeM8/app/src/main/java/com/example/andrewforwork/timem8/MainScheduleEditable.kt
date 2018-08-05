package com.example.andrewforwork.timem8

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import com.example.andrewforwork.timem8.Adapters.MainScheduleAdapter
import com.example.andrewforwork.timem8.DataBase.DBHandler
import com.example.andrewforwork.timem8.Editors.MainEditor
import com.example.andrewforwork.timem8.Subject.Sub
import kotlinx.android.synthetic.main.activity_main_schedule_editable.*
import java.util.*

class MainScheduleEditable : AppCompatActivity() {
    internal lateinit var db: DBHandler
    internal var lstSubs:List<Sub> = ArrayList<Sub>()
    lateinit var adapter: MainScheduleAdapter
    var calendar = Calendar.getInstance()
    var count = 0
    var currentDay = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_schedule_editable)
        if (savedInstanceState == null) {
            val fragment = MainScheduleFragment.newInstance()
            val bundle = Bundle()
            count = getDayOfTheWeek()
            bundle.putInt("DAY", getDayOfTheWeek())
            bundle.putString("DATE", calendar.get(Calendar.DATE).toString()+"."+(calendar.get(Calendar.MONTH)).toString()+"."+calendar.get(Calendar.YEAR).toString())
            fragment.arguments = bundle
            replaceFragment(fragment)
            back_btn.setOnClickListener {
                val fragment = MainScheduleFragment.newInstance()
                val bundle = Bundle()
                count-=1
                applyDay(-1)
                bundle.putInt("DAY", if (count>=0) count%7 else (count%7+7)%7)
                bundle.putString("DATE", calendar.get(Calendar.DATE).toString()+"."+(calendar.get(Calendar.MONTH)).toString()+"."+calendar.get(Calendar.YEAR).toString())
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
            forward_btn.setOnClickListener {
                val fragment = MainScheduleFragment.newInstance()
                val bundle = Bundle()
                count+=1
                applyDay(1)
                bundle.putInt("DAY", if (count>=0) count%7 else (count%7+7)%7)
                bundle.putString("DATE", calendar.get(Calendar.DATE).toString()+"."+(calendar.get(Calendar.MONTH)).toString()+"."+calendar.get(Calendar.YEAR).toString())
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null){
            count = savedInstanceState.getInt("CURRENT_DAY",0)
            currentDay = savedInstanceState.getInt("CURRENT_DIFF",0)
            applyDay(0)
            val fragment = MainScheduleFragment.newInstance()
            val bundle = Bundle()
            bundle.putInt("DAY", count)
            bundle.putString("DATE", calendar.get(Calendar.DATE).toString()+"."+(calendar.get(Calendar.MONTH)).toString()+"."+calendar.get(Calendar.YEAR).toString())
            fragment.arguments = bundle
            replaceFragment(fragment)
            back_btn.setOnClickListener {
                val fragment = MainScheduleFragment.newInstance()
                val bundle = Bundle()
                count-=1
                applyDay(-1)
                bundle.putInt("DAY", if (count>=0) count%7 else (count%7+7)%7)
                bundle.putString("DATE", calendar.get(Calendar.DATE).toString()+"."+(calendar.get(Calendar.MONTH)).toString()+"."+calendar.get(Calendar.YEAR).toString())
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
            forward_btn.setOnClickListener {
                val fragment = MainScheduleFragment.newInstance()
                val bundle = Bundle()
                count+=1
                applyDay(1)
                bundle.putInt("DAY", if (count>=0) count%7 else (count%7+7)%7)
                bundle.putString("DATE", calendar.get(Calendar.DATE).toString()+"."+(calendar.get(Calendar.MONTH)).toString()+"."+calendar.get(Calendar.YEAR).toString())
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("CURRENT_DAY",count)
        outState?.putInt("CURRENT_DIFF",currentDay)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        getMenuInflater().inflate(R.menu.activity_main_schedule_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val Editor = Intent(this,MainEditor::class.java)
        Editor.putExtra("DAY_OF_THE_WEEK",if (count>=0) count%7 else (count%7+7)%7)
        startActivity(Editor)
        return true
    }
    private fun applyDay(chr: Int){
        currentDay+=chr
        calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE,currentDay)
        println(calendar.get(Calendar.YEAR).toString()+" "+calendar.get(Calendar.MONTH).toString()+" "+calendar.get(Calendar.DATE).toString())
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH) //if (count<=-7) abs(count)%7 else abs(7+count)%7
        var monthText = ""
        when(month){
            0 -> monthText = "Янверь"
            1 -> monthText = "Февраль"
            2 -> monthText = "Март"
            3 -> monthText = "Апрель"
            4 -> monthText = "Май"
            5 -> monthText = "Июнь"
            6 -> monthText = "Июль"
            7 -> monthText = "Август"
            8 -> monthText = "Сентябрь"
            9 -> monthText = "Октябрь"
            10 -> monthText = "Ноябрь"
            11 -> monthText = "Декабрь"
        }
        when(if (count>=0) count%7 else (count%7+7)%7 ){
            0 -> textDay.text = "$monthText\nПонедельник, $day число"
            1 -> textDay.text = "$monthText\nВторник, $day число"
            2 -> textDay.text = "$monthText\nСреда, $day число"
            3 -> textDay.text = "$monthText\nЧетверг, $day число"
            4 -> textDay.text = "$monthText\nПятница, $day число"
            5 -> textDay.text = "$monthText\nСуббота, $day число"
            6 -> textDay.text = "$monthText\nВоскресенье, $day число"
        }
    }

//    private fun refreshData(){
//        lstSubs = db.allSub
//        val adapter = MainScheduleAdapter(this,db.allSub){
//            subject ->
//            Toast.makeText(this,subject.name,Toast.LENGTH_SHORT).show()
//        }
//        subjectList.adapter = adapter
//    }

    private fun replaceFragment(fragment: Fragment){
        val FragmentTransaction = supportFragmentManager.beginTransaction()
        FragmentTransaction.replace(R.id.fragmentsView,fragment)
        FragmentTransaction.commit()
    }
    private fun getDayOfTheWeek(): Int {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        val month = calendar.get(Calendar.MONTH)
        when(day){
            2 -> count = 0
            3 -> count = 1
            4 -> count = 2
            5 -> count = 3
            6 -> count = 4
            7 -> count = 5
            1 -> count = 6
        }
        applyDay(0)
        when(day){
            2 -> return 0
            3 -> return 1
            4 -> return 2
            5 -> return 3
            6 -> return 4
            7 -> return 5
            1 -> return 6
        }
        return 1
    }
}

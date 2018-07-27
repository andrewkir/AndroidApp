package com.example.andrewforwork.timem8

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.andrewforwork.timem8.Adapters.MainScheduleAdapter
import com.example.andrewforwork.timem8.DataBase.DBHandler
import com.example.andrewforwork.timem8.MainScheduleEditor.MainEditor
import com.example.andrewforwork.timem8.Subject.Sub
import kotlinx.android.synthetic.main.activity_main_editor.*
import kotlinx.android.synthetic.main.activity_main_schedule_editable.*
import java.util.*

class MainScheduleEditable : AppCompatActivity() {
    internal lateinit var db: DBHandler
    internal var lstSubs:List<Sub> = ArrayList<Sub>()
    lateinit var adapter: MainScheduleAdapter
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_schedule_editable)
        //SET DAY TO 1
        if (savedInstanceState == null) {
            val fragment = MainScheduleFragment.newInstance()
            val bundle = Bundle()
            count = getDayOfTheWeek()
            bundle.putInt("DAY", getDayOfTheWeek())
            fragment.arguments = bundle
            replaceFragment(fragment)
            back_btn.setOnClickListener {
                val fragment = MainScheduleFragment.newInstance()
                val bundle = Bundle()
                if (count - 1 >= 1) {
                    count -= 1
                }
                applyDay()
                bundle.putInt("DAY", count)
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
            forward_btn.setOnClickListener {
                val fragment = MainScheduleFragment.newInstance()
                val bundle = Bundle()
                if (count + 1 <= 7) {
                    count += 1
                }
                applyDay()
                bundle.putInt("DAY", count)
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null){
            count = savedInstanceState.getInt("CURRENT_DAY",1)
            applyDay()
            val fragment = MainScheduleFragment.newInstance()
            val bundle = Bundle()
            bundle.putInt("DAY", count)
            fragment.arguments = bundle
            replaceFragment(fragment)
            back_btn.setOnClickListener {
                val fragment = MainScheduleFragment.newInstance()
                val bundle = Bundle()
                if (count - 1 >= 1) {
                    count -= 1
                }
                applyDay()
                bundle.putInt("DAY", count)
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
            forward_btn.setOnClickListener {
                val fragment = MainScheduleFragment.newInstance()
                val bundle = Bundle()
                if (count + 1 <= 7) {
                    count += 1
                }
                applyDay()
                bundle.putInt("DAY", count)
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("CURRENT_DAY",count)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        getMenuInflater().inflate(R.menu.activity_main_schedule_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val Editor = Intent(this,MainEditor::class.java)
        Editor.putExtra("DAY_OF_THE_WEEK",count)
        startActivity(Editor)
        return true
    }
    private fun applyDay(){
        when(count){
            1 -> textDay.text = "Понедельник"
            2 -> textDay.text = "Вторник"
            3 -> textDay.text = "Среда"
            4 -> textDay.text = "Четверг"
            5 -> textDay.text = "Пятница"
            6 -> textDay.text = "Суббота"
            7 -> textDay.text = "Воскресенье"
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
        when(day){
            2 -> textDay.text = "Понедельник"
            3 -> textDay.text = "Вторник"
            4 -> textDay.text = "Среда"
            5 -> textDay.text = "Четверг"
            6 -> textDay.text = "Пятница"
            7 -> textDay.text = "Суббота"
            1 -> textDay.text = "Воскресенье"
        }
        when(day){
            2 -> return 1
            3 -> return 2
            4 -> return 3
            5 -> return 4
            6 -> return 5
            7 -> return 6
            1 -> return 7
        }
        return 1
    }
}

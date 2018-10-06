package com.andrewkir.andrewforwork.timem8.Editors

import android.app.ActivityManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.andrewkir.andrewforwork.timem8.DataBase.DBHandler
import com.andrewkir.andrewforwork.timem8.Adapters.ListSubjectAdapter
import com.andrewkir.andrewforwork.timem8.Notifications.NotificationsHandler
import com.andrewkir.andrewforwork.timem8.R
import com.andrewkir.andrewforwork.timem8.Models.Sub
import kotlinx.android.synthetic.main.activity_main_editor.*
import java.util.*

class MainEditor : AppCompatActivity(), AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener{
    lateinit var sPref: SharedPreferences
    var stat: String = ""
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var CurrentDaySelected = 1
    var editSelection = 0
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        CurrentDaySelected = position+1
        if(editSelection == 0) {
            refreshData()
        } else {
            editSelection = 0
        }
    }

    internal lateinit var db: DBHandler
    internal var lstSubs:List<Sub> = ArrayList<Sub>()
    var list_days = arrayOf("Понедельник","Вторник","Среда","Четверг","Пятница","Суббота","Воскресенье")


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        getMenuInflater().inflate(R.menu.activity_main_editor_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.edit_menu_delete) {
            db.deleteAllData()
            refreshData()
        } else {
            this.finish()
        }
        return true
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
        setContentView(R.layout.activity_main_editor)


        var EditorS = MediaPlayer.create(this, R.raw.activityeditors);
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        timeBeginBtn.setOnClickListener {
            var TimePick = TimePickerDialog(this,{
                view: TimePicker?, hourOfDay: Int, minute: Int ->
                timeBeginBtn.text = (if(hourOfDay.toString().length<2){
                    "0$hourOfDay"
                } else{
                    hourOfDay.toString()
                } +":"+ if(minute.toString().length<2){
                    "0$minute"
                } else{
                    minute.toString()
                })
                var calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
                calendar.set(Calendar.MINUTE,minute)
                calendar.add(Calendar.MINUTE,45)
                timeEndBtn.text = (if(calendar.get(Calendar.HOUR_OF_DAY).toString().length<2){
                    "0${calendar.get(Calendar.HOUR_OF_DAY)}"
                } else{
                    calendar.get(Calendar.HOUR_OF_DAY).toString()
                } +":"+ if(calendar.get(Calendar.MINUTE).toString().length<2){
                    "0${calendar.get(Calendar.MINUTE)}"
                } else{
                    calendar.get(Calendar.MINUTE).toString()
                })
            },Integer.parseInt(timeBeginBtn.text.toString().split(":")[0]),Integer.parseInt(timeBeginBtn.text.toString().split(":")[1]),true)
            TimePick.show()
        }
        timeEndBtn.setOnClickListener {
            var TimePick = TimePickerDialog(this,{
                view: TimePicker?, hourOfDay: Int, minute: Int ->
                timeEndBtn.text = (if(hourOfDay.toString().length<2){
                    "0$hourOfDay"
                } else{
                    hourOfDay.toString()
                } +":"+ if(minute.toString().length<2){
                    "0$minute"
                } else{
                    minute.toString()
                })
                if(Integer.parseInt(timeBeginBtn.text.toString().split(":")[0])*60+Integer.parseInt(timeBeginBtn.text.toString().split(":")[1])>hourOfDay*60+minute){
                    var tmp = timeBeginBtn.text
                    timeBeginBtn.text = timeEndBtn.text
                    timeEndBtn.text = tmp
                }
            },Integer.parseInt(timeEndBtn.text.toString().split(":")[0]),Integer.parseInt(timeEndBtn.text.toString().split(":")[1]),true)
            TimePick.show()
        }

        spinner!!.setOnItemSelectedListener(this)
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_days)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = aa

        CurrentDaySelected = intent.getIntExtra("DAY_OF_THE_WEEK",1)+1
        db = DBHandler(this)
        refreshData()
        btn_insert.setOnClickListener{
            try {
                if(name.text.toString() != ""){
                    if(name.text.toString()=="сясь"){
                        EditorS.start()
                        room.setText("сясь")
                        type.setText("сясь")
                        teacher.setText("сясь")
                        timeBeginBtn.text = "13:37"
                        timeEndBtn.text = "13:37"
                        count.setText("1337")
                        Toast.makeText(this,"Надо научить этих грубиянов уважать рЕп",Toast.LENGTH_LONG).show()
                        supportActionBar?.title = "сясь"
                        btn_insert.text = "сясь"
                        btn_delete.text = "сясь"
                        btn_update.text = "сясь"
                    } else {
                        val sub = Sub(
                                id = Integer.parseInt(CurrentDaySelected.toString() + count.text.toString()),
                                day = Integer.parseInt(CurrentDaySelected.toString()),
                                count = Integer.parseInt(count.text.toString()),
                                name = name.text.toString(),
                                timeBegin = timeBeginBtn.text.toString(),
                                timeEnd = timeEndBtn.text.toString(),
                                type = type.text.toString(),
                                room = room.text.toString(),
                                teacher = teacher.text.toString()
                        )
                        db.addSub(sub)
                        var tmp = count.text.toString()
                        NotificationsHandler(context = this).makeNotification(
                                hour = Integer.parseInt(timeBeginBtn.text.toString().split(":")[0]),
                                minute = Integer.parseInt(timeBeginBtn.text.toString().split(":")[1]),
                                text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                                textTitle = name.text.toString(),
                                id = Integer.parseInt(sub.day.toString() + sub.count.toString()),
                                dayOfweek = sub.day,
                                cancel = false,
                                count = sub.count
                        )
                        refreshData()
                        count.setText((Integer.parseInt(tmp) + 1).toString())
                    }
                } else {
                    Toast.makeText(this,"Пожалуйста, введите корректные данные",Toast.LENGTH_SHORT).show()
                }
            }
            catch(e: Exception) {
                Toast.makeText(this,"Пожалуйста, введите корректные данные",Toast.LENGTH_SHORT).show()
            }

        }
        btn_update.setOnClickListener{
            try {
                val sub = Sub(
                        id =Integer.parseInt(CurrentDaySelected.toString() + count.text.toString()),
                        day =Integer.parseInt(CurrentDaySelected.toString()),
                        count =Integer.parseInt(count.text.toString()),
                        name = name.text.toString(),
                        timeBegin = timeBeginBtn.text.toString(),
                        timeEnd = timeEndBtn.text.toString(),
                        type = type.text.toString(),
                        room = room.text.toString(),
                        teacher = teacher.text.toString()
                )
                NotificationsHandler(context = this).makeNotification(
                        hour = Integer.parseInt(timeBeginBtn.text.toString().split(":")[0]),
                        minute = Integer.parseInt(timeBeginBtn.text.toString().split(":")[1]),
                        text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                        textTitle = name.text.toString(),
                        id = Integer.parseInt(sub.day.toString()+sub.count.toString()),
                        dayOfweek = sub.day,
                        cancel = true,
                        delete = true,
                        count = sub.count
                )
                NotificationsHandler(context = this).makeNotification(
                        hour = Integer.parseInt(timeBeginBtn.text.toString().split(":")[0]),
                        minute = Integer.parseInt(timeBeginBtn.text.toString().split(":")[1]),
                        text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                        textTitle = name.text.toString(),
                        id = Integer.parseInt(sub.day.toString()+sub.count.toString()),
                        dayOfweek = sub.day,
                        delete = false,
                        cancel = false,
                        count = sub.count
                )
                db.updateSub(sub)
                refreshData()
            }
            catch(e: Exception) {
                Toast.makeText(this,"Пожалуйста, введите корректные данные",Toast.LENGTH_SHORT).show()
            }
        }
        btn_delete.setOnClickListener{
            try {
                val sub = Sub(
                        id =Integer.parseInt(CurrentDaySelected.toString() + count.text.toString()),
                        day =Integer.parseInt(CurrentDaySelected.toString()),
                        count =Integer.parseInt(count.text.toString()),
                        name = name.text.toString(),
                        timeBegin = timeBeginBtn.text.toString(),
                        timeEnd = timeEndBtn.text.toString(),
                        type = type.text.toString(),
                        room = room.text.toString(),
                        teacher = teacher.text.toString()
                )
                db.deleteSub(sub)
                NotificationsHandler(context = this).makeNotification(
                        hour = Integer.parseInt(timeBeginBtn.text.toString().split(":")[0]),
                        minute = Integer.parseInt(timeBeginBtn.text.toString().split(":")[1]),
                        text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                        textTitle = name.text.toString(),
                        id = Integer.parseInt(sub.day.toString()+sub.count.toString()),
                        dayOfweek = sub.day,
                        cancel = true,
                        delete = true,
                        count = sub.count
                )
                refreshData()
            }
            catch(e: Exception) {
                Toast.makeText(this,"Пожалуйста, введите корректные данные",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun refreshData(){
        lstSubs = db.allSub
        val adapter = com.andrewkir.andrewforwork.timem8.Adapters.ListSubjectAdapter(this@MainEditor, lstSubs, spinner, name, timeBeginBtn, timeEndBtn, count, type, teacher, room, ::editSelection)
        spinner.setSelection(CurrentDaySelected-1)
        println(CurrentDaySelected)
        name.setText("")
        timeBeginBtn.setText("9:00")
        timeEndBtn.setText("10:00")
        count.setText("")
        type.setText("")
        teacher.setText("")
        room.setText("")
        list_subs.adapter = adapter
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
}
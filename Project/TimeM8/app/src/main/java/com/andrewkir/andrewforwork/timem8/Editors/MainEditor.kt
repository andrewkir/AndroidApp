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
import android.support.v4.content.res.ResourcesCompat
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.andrewkir.andrewforwork.timem8.DataBase.DBHandler
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

    private var currentDaySelected = 1
    private var editSelection = 0
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentDaySelected = position+1
        if(editSelection == 0) {
            refreshData()
        } else {
            editSelection = 0
        }
    }

    internal lateinit var db: DBHandler
    private var lstSubs:List<Sub> = ArrayList()
    var listDays = arrayOf("Понедельник","Вторник","Среда","Четверг","Пятница","Суббота","Воскресенье")


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        this.finish()
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


        val editorS = MediaPlayer.create(this, R.raw.activityeditors)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        timeBeginBtn.setOnClickListener {
            val timePick = TimePickerDialog(this,{
                _: TimePicker?, hourOfDay: Int, minute: Int ->
                timeBeginBtn.text = (if(hourOfDay.toString().length<2){
                    "0$hourOfDay"
                } else{
                    hourOfDay.toString()
                } +":"+ if(minute.toString().length<2){
                    "0$minute"
                } else{
                    minute.toString()
                })
                val calendar = Calendar.getInstance()
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
            timePick.show()
        }

        timeEndBtn.setOnClickListener {
            val timePick = TimePickerDialog(this,{
                _: TimePicker?, hourOfDay: Int, minute: Int ->
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
                    val tmp = timeBeginBtn.text
                    timeBeginBtn.text = timeEndBtn.text
                    timeEndBtn.text = tmp
                }
            },Integer.parseInt(timeEndBtn.text.toString().split(":")[0]),Integer.parseInt(timeEndBtn.text.toString().split(":")[1]),true)
            timePick.show()
        }


        spinner!!.onItemSelectedListener = this
        val adapterSpinner = object : ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, listDays) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)

                val externalFont = ResourcesCompat.getFont(context, R.font.rubik_light)
                (v as TextView).typeface = externalFont

                return v
            }


            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent)
                val externalFont = ResourcesCompat.getFont(context, R.font.rubik_light)
                (v as TextView).typeface = externalFont
                return v
            }
        }
        spinner!!.adapter = adapterSpinner

        currentDaySelected = intent.getIntExtra("DAY_OF_THE_WEEK",1)+1
        db = DBHandler(this)
        refreshData()

        btnInsert.setOnClickListener{
            try {
                if(name.text.toString() != ""){
                    if(name.text.toString()=="сясь"){
                        editorS.start()
                        room.setText("сясь")
                        type.setText("сясь")
                        teacher.setText("сясь")
                        timeBeginBtn.text = "13:37"
                        timeEndBtn.text = "13:37"
                        count.setText("1337")
                        Toast.makeText(this,"Надо научить этих грубиянов уважать рЕп",Toast.LENGTH_LONG).show()
                        supportActionBar?.title = "сясь"
                        btnInsert.text = "сясь"
                        btnDelete.text = "сясь"
                        btnUpdate.text = "сясь"
                    } else {
                        val sub = Sub(
                                id = Integer.parseInt(currentDaySelected.toString() + count.text.toString()),
                                day = Integer.parseInt(currentDaySelected.toString()),
                                count = Integer.parseInt(count.text.toString()),
                                name = name.text.toString(),
                                timeBegin = timeBeginBtn.text.toString(),
                                timeEnd = timeEndBtn.text.toString(),
                                type = type.text.toString(),
                                room = room.text.toString(),
                                teacher = teacher.text.toString()
                        )
                        db.addSub(sub)
                        val tmp = count.text.toString()
                        NotificationsHandler(context = this).makeNotification(
                                hour = Integer.parseInt(timeBeginBtn.text.toString().split(":")[0]),
                                minute = Integer.parseInt(timeBeginBtn.text.toString().split(":")[1]),
                                text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                                textTitle = name.text.toString(),
                                id = Integer.parseInt(sub.day.toString() + sub.count.toString()),
                                dayOfWeek = sub.day,
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

        btnUpdate.setOnClickListener{
            try {
                val sub = Sub(
                        id =Integer.parseInt(currentDaySelected.toString() + count.text.toString()),
                        day =Integer.parseInt(currentDaySelected.toString()),
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
                        dayOfWeek = sub.day,
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
                        dayOfWeek = sub.day,
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

        btnDelete.setOnClickListener{
            try {
                val sub = Sub(
                        id =Integer.parseInt(currentDaySelected.toString() + count.text.toString()),
                        day =Integer.parseInt(currentDaySelected.toString()),
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
                        dayOfWeek = sub.day,
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
        spinner.setSelection(currentDaySelected-1)
        name.setText("")
        timeBeginBtn.text = "9:00"
        timeEndBtn.text = "10:00"
        count.setText("")
        type.setText("")
        teacher.setText("")
        room.setText("")
        list_subs.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }
    }
}
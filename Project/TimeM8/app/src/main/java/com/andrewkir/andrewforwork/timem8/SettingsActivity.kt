package com.andrewkir.andrewforwork.timem8

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
import android.support.v7.app.AlertDialog
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.andrewkir.andrewforwork.timem8.DataBase.DBhandler
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.DataBase.DBdetailinfo
import com.andrewkir.andrewforwork.timem8.Models.Sub
import com.andrewkir.andrewforwork.timem8.Notifications.NotificationsHandler
import com.andrewkir.andrewforwork.timem8.Services.App
import com.andrewkir.andrewforwork.timem8.Services.WebData
import com.google.gson.Gson
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
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val prefNotifications = getSharedPreferences("NotifEnabled",Context.MODE_PRIVATE)
        if(prefNotifications.getBoolean("ENABLED",true)) {
            settingsSwitch.performClick()
            minutesSettings.isEnabled = true
            saveMinutes.isEnabled = true
        } else {
            minutesSettings.isEnabled = false
            saveMinutes.isEnabled = false
        }

        val tmp= getSharedPreferences("NotifMin", Context.MODE_PRIVATE)
        val tMin = tmp.getInt("MINUTES",-1)
        if(tMin !=-1){
            minutesSettings.setText(tMin.toString())
        }

        circleMenu.visibility = View.GONE
        linearcircle.setOnClickListener {
            if(open == 1) {
                circleMenu.close(true)
                open = 0
            } else {
                circleMenu.visibility = View.VISIBLE
                val handler = Handler()
                handler.postDelayed({
                    circleMenu.open(true)
                }, 100)
            }
        }

        deleteMainSettBtn.setOnClickListener {
            AlertDialog.Builder(this@SettingsActivity)
                    .setMessage("Вы точно хотите удалить эту информацию?\n(Отменить это действие будет невозможно)")
                    .setCancelable(false)
                    .setPositiveButton("Удалить") { _, _ ->
                        val db = DBhandler(this)
                        val subs = db.allSub
                        for(sub in subs) {
                            NotificationsHandler(context = this).makeNotification(
                                    hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                                    minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                                    text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                                    textTitle = sub.name,
                                    id = Integer.parseInt(sub.day.toString() + sub.count.toString()),
                                    dayOfWeek = sub.day,
                                    cancel = true,
                                    delete = true,
                                    count = sub.count
                            )
                        }
                        db.deleteAllData()
                        DBdetailinfo(this).deleteAllData()
                        Toast.makeText(this,"Основное расписание удалено",Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Отмена") { _, _ ->
                    }
                    .show()
        }

        deleteMainDetailSettBtn.setOnClickListener {
            AlertDialog.Builder(this@SettingsActivity)
                    .setMessage("Вы точно хотите удалить эту информацию?\n(Отменить это действие будет невозможно)")
                    .setCancelable(false)
                    .setPositiveButton("Удалить") { _, _ ->
                        val db = DBdetailinfo(this)
                        db.deleteAllData()
                        Toast.makeText(this,"Детальная информация удалена",Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Отмена") { _, _ ->
                    }
                    .show()
        }

        deleteFrogSettBtn.setOnClickListener {
            AlertDialog.Builder(this@SettingsActivity)
                    .setMessage("Вы точно хотите удалить эту информацию?\n(Отменить это действие будет невозможно)")
                    .setCancelable(false)
                    .setPositiveButton("Удалить") { _, _ ->
                        val dbTmp = DBdaily(this)
                        dbTmp.deleteAllData()
                        Toast.makeText(this,"Ежедневное расписание удалено",Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Отмена") { _, _ ->
                    }
                    .show()
        }

        saveMinutes.setOnClickListener {
            if(minutesSettings.text.toString() != "" && minutesSettings.text.toString() != "0") {

                val pref = getSharedPreferences("NotifMin", Context.MODE_PRIVATE)
                val ed = pref.edit()
                val min =  Integer.parseInt(minutesSettings.text.toString())
                ed.putInt("MINUTES", min)
                ed.apply()

                val db = DBhandler(this)
                val subs = db.allSub
                for(sub in subs) {
                    NotificationsHandler(context = this).makeNotification(
                            hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                            minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                            text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                            textTitle = sub.name,
                            id = Integer.parseInt(sub.day.toString()+sub.count.toString()),
                            dayOfWeek = sub.day,
                            cancel = true,
                            delete = true,
                            count = sub.count
                    )
                    NotificationsHandler(context = this).makeNotification(
                            hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                            minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                            text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                            textTitle = sub.name,
                            id = Integer.parseInt(sub.day.toString()+sub.count.toString()),
                            dayOfWeek = sub.day,
                            delete = false,
                            cancel = false,
                            count = sub.count
                    )
                }
                Toast.makeText(this,"Сохранено",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"Пожалуйста, введите корректные данные",Toast.LENGTH_SHORT).show()
            }
        }

        settingsSwitch.setOnClickListener {
            if (settingsSwitch.isChecked) {
                val tmpPr = getSharedPreferences("NotifEnabled",Context.MODE_PRIVATE)
                val ed = tmpPr.edit()
                ed.putBoolean("ENABLED",true)
                ed.apply()
                staticNotif.text = "выключить уведомления"
                minutesSettings.isEnabled = true
                saveMinutes.isEnabled = true
                val db = DBhandler(this)
                val subs = db.allSub
                for(sub in subs) {
                    NotificationsHandler(context = this).makeNotification(
                            hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                            minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                            text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                            textTitle = sub.name,
                            id = Integer.parseInt(sub.day.toString()+sub.count.toString()),
                            dayOfWeek = sub.day,
                            delete = false,
                            cancel = false,
                            count = sub.count
                    )
                }
            } else {
                staticNotif.text = "включить уведомления"
                minutesSettings.isEnabled = false
                saveMinutes.isEnabled = false
                val db = DBhandler(this)
                val subs = db.allSub
                for(sub in subs) {
                    NotificationsHandler(context = this).makeNotification(
                            hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                            minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                            text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                            textTitle = sub.name,
                            id = Integer.parseInt(sub.day.toString()+sub.count.toString()),
                            dayOfWeek = sub.day,
                            cancel = true,
                            delete = true,
                            count = sub.count
                    )
                }
                val tmpPr = getSharedPreferences("NotifEnabled",Context.MODE_PRIVATE)
                val ed = tmpPr.edit()
                ed.putBoolean("ENABLED",false)
                ed.apply()
            }
        }

        exportButton.setOnClickListener {
            if (DBhandler(this).allSub.isNotEmpty()) {
                val gson = Gson()
                val json = gson.toJson(DBhandler(this).allSub)
                val input = EditText(this)
                input.height = 50.toPx()
                input.width = 150.toPx()
                input.gravity = Gravity.LEFT
                var scheduleName: String
                AlertDialog.Builder(this@SettingsActivity)
                        .setMessage("Введите уникальное имя")
                        .setCancelable(false)
                        .setView(input)
                        .setPositiveButton("Ок") { _, _ ->
                            try {
                                scheduleName = input.text.toString()
                                try {
                                    val bytes = json.toByteArray()
                                    var encodedData = String(android.util.Base64.encode(bytes, android.util.Base64.DEFAULT))
                                    encodedData = encodedData.trim()
                                    encodedData = encodedData.replace("[\n]".toRegex(), "")
                                    WebData.addSchedule(this, scheduleName, encodedData) { success ->
                                        if (success) {
                                            Toast.makeText(this, "Успешно, вы можете импортировать расписание по этому имени: $scheduleName", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(this, "Неправильное уникальное имя или отсутствует подключение к интернету, расписание скопировано в виде текста в буфер обмена", Toast.LENGTH_LONG).show()
                                            setClipboard(this, json)
                                        }
                                    }
                                } catch (e: Exception) {
                                    errorMsg()
                                }
                            } catch (e: Exception) {
                                errorMsg()
                            }
                        }
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Экспорт основного расписания")
                        .setNegativeButton("Отмена") { _, _ ->
                        }
                        .show()
            } else {
                Toast.makeText(this, "Вы не можете экспортировать пустое расписание", Toast.LENGTH_SHORT).show()
            }
        }

        importButton.setOnClickListener {
            val input = EditText(this)
            input.height = 50.toPx()
            input.width = 150.toPx()
            input.gravity = Gravity.LEFT
            var text: String
            AlertDialog.Builder(this@SettingsActivity)
                    .setMessage("Введите уникальное имя / полученный текст")
                    .setCancelable(false)
                    .setView(input)
                    .setPositiveButton("Ок") { _, _ ->
                        try {
                            text = input.text.toString()
                                try {
                                    WebData.getSchedule(this, input.text.toString()) { success: Boolean ->
                                        if (success) {
                                            val decodedData = String(android.util.Base64.decode(App.prefs.data, android.util.Base64.DEFAULT))
                                            text = decodedData
                                            try {
                                                applyData(text)
                                            } catch (e:Exception) {
                                                errorMsg()
                                            }
                                        } else {
                                            Toast.makeText(this, "Неправильное уникальное имя или отсутствует подключение к интернету, попытка обработать введённый текст", Toast.LENGTH_LONG).show()
                                            try {
                                                applyData(text)
                                            } catch (e:Exception) {
                                                errorMsg()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    try {
                                        applyData(text)
                                    } catch (e:Exception) {
                                        errorMsg()
                                    }
                                }
                        }
                        catch (e:Exception) {
                            errorMsg()
                        }
                    }
                     .setIcon(R.mipmap.ic_launcher)
                    .setTitle("Импорт основного расписания")
                    .setNegativeButton("Отмена"){ _, _ ->
                    }
                    .show()
        }

        //menu handler
        val menu = circleMenu
        menu.eventListener = object : CircleMenuView.EventListener() {
            override fun onMenuCloseAnimationEnd(view: CircleMenuView) {
                circleMenu.visibility = View.GONE
                open = 0
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
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }
    }

    //copy data to clipboard
    private fun setClipboard(context: Context, text: String) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = text
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Copied Text", text)
            clipboard.primaryClip = clip
        }
    }

    //back button
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        this.finish()
        return true
    }


    private fun applyData(text: String) {
        val gson = Gson()
        val obj = gson.fromJson(text, Array<Sub>::class.java)
        val db = DBhandler(this)
        val tmp = db.allSub
        //удаление уведомлений
        for (sub in tmp) {
            NotificationsHandler(context = this).makeNotification(
                    hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                    minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                    text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                    textTitle = sub.name,
                    id = Integer.parseInt(sub.day.toString() + sub.count.toString()),
                    dayOfWeek = sub.day,
                    cancel = true,
                    delete = true,
                    count = sub.count
            )
        }
        db.deleteAllData()
        for (sub in obj) {
            db.addSub(sub)
            NotificationsHandler(context = this).makeNotification(
                    hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                    minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                    text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                    textTitle = sub.name,
                    id = Integer.parseInt(sub.day.toString() + sub.count.toString()),
                    dayOfWeek = sub.day,
                    delete = false,
                    cancel = false,
                    count = sub.count
            )
        }
        DBdetailinfo(this).deleteAllData()
        Toast.makeText(this, "Готово", Toast.LENGTH_SHORT).show()
    }

    //dp to px programmatically
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    //error msg
    private fun errorMsg() {
        Toast.makeText(this,"Что-то пошло нет так :c",Toast.LENGTH_SHORT).show()
    }
}

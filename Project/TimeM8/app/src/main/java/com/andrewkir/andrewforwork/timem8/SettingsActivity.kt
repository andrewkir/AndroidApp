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
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.andrewkir.andrewforwork.timem8.DataBase.DBHandler
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.DataBase.DBdetailinfo
import com.andrewkir.andrewforwork.timem8.Models.Sub
import com.andrewkir.andrewforwork.timem8.Notifications.NotificationsHandler
import com.andrewkir.andrewforwork.timem8.Services.App
import com.andrewkir.andrewforwork.timem8.Services.WebData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.StandardCharsets


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
        var prefNotif = getSharedPreferences("NotifEnabled",Context.MODE_PRIVATE)
        if(prefNotif.getBoolean("ENABLED",true)){
            settings_switch.performClick()
            minutesSettings.isEnabled = true
            saveMinutes.isEnabled = true
        } else {
            minutesSettings.isEnabled = false
            saveMinutes.isEnabled = false
        }

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
                        var db = DBHandler(this)
                        var subs = db.allSub
                        for(sub in subs) {
                            NotificationsHandler(context = this).makeNotification(
                                    hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                                    minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                                    text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                                    textTitle = sub.name,
                                    id = Integer.parseInt(sub.day.toString() + sub.count.toString()),
                                    dayOfweek = sub.day,
                                    cancel = true,
                                    delete = true,
                                    count = sub.count
                            )
                        }
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
                var min =  Integer.parseInt(minutesSettings.text.toString())
                ed.putInt("MINUTES", min)
                ed.apply()

                var db = DBHandler(this)
                var subs = db.allSub
                for(sub in subs){
                    NotificationsHandler(context = this).makeNotification(
                            hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                            minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                            text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                            textTitle = sub.name,
                            id = Integer.parseInt(sub.day.toString()+sub.count.toString()),
                            dayOfweek = sub.day,
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
                            dayOfweek = sub.day,
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
        settings_switch.setOnClickListener {
            if (settings_switch.isChecked){
                val tmpPr = getSharedPreferences("NotifEnabled",Context.MODE_PRIVATE)
                val ed = tmpPr.edit()
                ed.putBoolean("ENABLED",true)
                ed.apply()
                static_notif.text = "выключить уведомления"
                minutesSettings.isEnabled = true
                saveMinutes.isEnabled = true
                val db = DBHandler(this)
                val subs = db.allSub
                for(sub in subs){
                    NotificationsHandler(context = this).makeNotification(
                            hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                            minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                            text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                            textTitle = sub.name,
                            id = Integer.parseInt(sub.day.toString()+sub.count.toString()),
                            dayOfweek = sub.day,
                            delete = false,
                            cancel = false,
                            count = sub.count
                    )
                }
            } else {
                static_notif.text = "включить уведомления"
                minutesSettings.isEnabled = false
                saveMinutes.isEnabled = false
                var db = DBHandler(this)
                var subs = db.allSub
                for(sub in subs){
                    NotificationsHandler(context = this).makeNotification(
                            hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                            minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                            text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                            textTitle = sub.name,
                            id = Integer.parseInt(sub.day.toString()+sub.count.toString()),
                            dayOfweek = sub.day,
                            cancel = true,
                            delete = true,
                            count = sub.count
                    )
                    println(Integer.parseInt(sub.timeBegin.split(":")[0]))
                }
                var tmpPr = getSharedPreferences("NotifEnabled",Context.MODE_PRIVATE)
                var ed = tmpPr.edit()
                ed.putBoolean("ENABLED",false)
                ed.apply()
            }
        }
        exportButton.setOnClickListener {
            val gson = Gson()
            val json = gson.toJson(DBHandler(this).allSub)
            var input = EditText(this)
            input.height = 50.toPx()
            input.width = 150.toPx()
            input.gravity = Gravity.LEFT;
            var text = ""
            AlertDialog.Builder(this@SettingsActivity)
                    .setMessage("Введите уникальное имя")
                    .setCancelable(false)
                    .setView(input)
                    .setPositiveButton("Ок") { _, _ ->
                        try {
                            text = input.text.toString()
                            try {
                                val bytes = json.toByteArray()
                                var encodedData = String(android.util.Base64.encode(bytes, android.util.Base64.DEFAULT))
                                encodedData = encodedData.trim()
                                encodedData = encodedData.replace("[\n]".toRegex(), "")
                                println(encodedData)
                                WebData.AddSchedule(this, text, encodedData) { success ->
                                    if (success) {
                                        Toast.makeText(this, "Успешно, вы можете импортировать расписание по этому имени: $text", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(this, "Неправильное уникальное имя или отсутсвует подключение к интернету, расписание скопировано в виде текста в буфер обмена", Toast.LENGTH_LONG).show()
                                        setClipboard(this,json)
                                    }
                                }
                            } catch (e:Exception){
                                Toast.makeText(this,"Что-то пошло нет так :c",Toast.LENGTH_SHORT).show()
                            }
                        }
                        catch (e:Exception){
                            Toast.makeText(this,"Что-то пошло нет так :c",Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("Экспорт основного расписания")
                    .setNegativeButton("Отмена"){ _, _ ->

                    }
                    .show()
        }
        importButton.setOnClickListener {
            var input = EditText(this)
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
                                            } catch (e:Exception){
                                                Toast.makeText(this,"Что-то пошло нет так :c",Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(this, "Неправильное уникальное имя или отсутсвует подключение к интернету, попытка обработать введённый текст", Toast.LENGTH_LONG).show()
                                            try {
                                                applyData(text)
                                            } catch (e:Exception){
                                                Toast.makeText(this,"Что-то пошло нет так :c",Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    try {
                                        applyData(text)
                                    } catch (e:Exception){
                                        Toast.makeText(this,"Что-то пошло нет так :c",Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        catch (e:Exception){
                            Toast.makeText(this,"Что-то пошло нет так :c",Toast.LENGTH_SHORT).show()
                        }

                    }
                     .setIcon(R.mipmap.ic_launcher)
                    .setTitle("Импорт основного расписания")
                    .setNegativeButton("Отмена"){ _, _ ->

                    }
                    .show()
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
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        this.finish()
        return true
    }
    fun applyData(text: String){
        val gson = Gson()
        val obj = gson.fromJson(text, Array<Sub>::class.java)
        var db = DBHandler(this)
        var tmp = db.allSub
        //удаление уведомлений
        for (sub in tmp) {
            NotificationsHandler(context = this).makeNotification(
                    hour = Integer.parseInt(sub.timeBegin.split(":")[0]),
                    minute = Integer.parseInt(sub.timeBegin.split(":")[1]),
                    text = "${sub.timeBegin};;;${sub.teacher};;;${sub.room}",
                    textTitle = sub.name,
                    id = Integer.parseInt(sub.day.toString() + sub.count.toString()),
                    dayOfweek = sub.day,
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
                    dayOfweek = sub.day,
                    delete = false,
                    cancel = false,
                    count = sub.count
            )
        }
        Toast.makeText(this, "Готово", Toast.LENGTH_SHORT).show()
    }
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}

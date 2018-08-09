package com.example.andrewforwork.timem8

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import com.example.andrewforwork.timem8.DataBase.DBHandler
import com.example.andrewforwork.timem8.DataBase.DBdetailinfo
import com.example.andrewforwork.timem8.Notifications.Notification_reciever
import com.example.andrewforwork.timem8.Notifications.NotificationsHandler
import kotlinx.android.synthetic.main.activity_main_editor.*
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DebugDeleteDetail.setOnClickListener {
            var db = DBdetailinfo(this)
            db.deleteAllData()
        }
        debugDeleteMain.setOnClickListener {
            var db = DBHandler(this)
            db.deleteAllData()
        }
        dailyTasksBtn.setOnClickListener{
            val dailyTaskIntent = Intent(this,MainDailySchedule::class.java)
            startActivity(dailyTaskIntent)
        }
        MainScheduleBtn.setOnClickListener{
            val ScheduleIntent = Intent(this,MainSchedule::class.java)
            startActivity(ScheduleIntent)
        }
        MainScheduleBtnEd.setOnClickListener{
            val ScheduleIntentEd = Intent(this,MainScheduleEditable::class.java)
            startActivity(ScheduleIntentEd)
        }
    }

}

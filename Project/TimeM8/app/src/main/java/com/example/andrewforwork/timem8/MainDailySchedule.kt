package com.example.andrewforwork.timem8

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.andrewforwork.timem8.Notifications.Notification_reciever
import java.util.*


class MainDailySchedule : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_daily_schedule)
    }
}

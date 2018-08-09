package com.example.andrewforwork.timem8.Notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*


class NotificationsHandler(var context:Context) {
    internal var text = ""
    fun makeNotification(hour: Int,minute:Int,text:String,textTitle: String,id:Int,dayOfweek:Int,cancel:Boolean,diff:Boolean=false,dayYear:Int=0) {
            var day = 0
            var calendar = Calendar.getInstance()
            if(diff){
                calendar.set(Calendar.DAY_OF_WEEK,dayYear)
            } else {
                calendar = Calendar.getInstance()
            }
            when (dayOfweek) {
                1 -> day = 2
                2 -> day = 3
                3 -> day =4
                4 -> day =5
                5 -> day =6
                6 -> day =7
                7 -> day =1
            }
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.DAY_OF_WEEK,day)
            calendar.set(Calendar.MINUTE, minute)
            calendar.add(Calendar.MINUTE,-10)
            if(diff){
                calendar.add(Calendar.WEEK_OF_YEAR,1)
            }
            println("curr ${Calendar.getInstance().timeInMillis}")
            println("alarm ${calendar.timeInMillis}")
            var intent = Intent(context, Notification_reciever::class.java)
            intent.putExtra("TEXT",text)
            intent.putExtra("DAY",dayOfweek)
            intent.putExtra("DAYOFYEAR",calendar.get(Calendar.DAY_OF_YEAR))
            intent.putExtra("HOUR",hour)
            intent.putExtra("MINUTE",minute)
            intent.putExtra("TITLE",textTitle)
            intent.putExtra("ID",id)
            var pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if(cancel){
                alarmManager.cancel(pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis , pendingIntent) //week = 604800000
            }
        }

    }
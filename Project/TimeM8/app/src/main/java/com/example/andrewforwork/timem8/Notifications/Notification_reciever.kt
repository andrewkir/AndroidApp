package com.example.andrewforwork.timem8.Notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.example.andrewforwork.timem8.MainActivity
import com.example.andrewforwork.timem8.R

class Notification_reciever: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        var notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val repeating_intent = Intent(context, MainActivity::class.java)
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        var text = intent!!.getStringExtra("TEXT")
        var title = intent!!.getStringExtra("TITLE")
        var dayYear = intent!!.getIntExtra("DAYOFYEAR",0)
        var day = intent!!.getIntExtra("DAY",0)
        var hour = intent!!.getIntExtra("HOUR",0)
        var minute = intent!!.getIntExtra("MINUTE",0)
        var id = intent!!.getIntExtra("ID",0)
        var count = intent!!.getIntExtra("COUNT",0)

        println("make notification")
        val channelId = "channel-"+title+hour.toString()+minute.toString()+count
        val channelName = "Channel "+title+hour.toString()+minute.toString()+count
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                    channelId, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        var pendingIntent = PendingIntent.getActivity(context,id,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT)
        var builder = NotificationCompat.Builder(context,channelId)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat_all_inclusive)
                .setContentTitle(title)
                //.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.InboxStyle()
                        .addLine("Начинается в ${text.split(";;;")[0]}")
                        .addLine("Кабинет: ${text.split(";;;")[2]}")
                        .addLine("Преподаватель: ${text.split(";;;")[1]}"))
        notificationManager.notify(id,builder.build())
        NotificationsHandler(context = context).makeNotification(
                hour = hour,
                minute = minute,
                text =  text,
                textTitle = title,
                id = id,
                dayOfweek = day,
                cancel = false,
                diff = true,
                dayYear = dayYear,
                count = count
        )

    }
}

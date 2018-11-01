package com.andrewkir.andrewforwork.timem8.Notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.andrewkir.andrewforwork.timem8.MainActivity
import com.andrewkir.andrewforwork.timem8.R

class NotificationReciever: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val repeatingIntent = Intent(context, MainActivity::class.java)
        repeatingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val text = intent!!.getStringExtra("TEXT")
        val title = intent.getStringExtra("TITLE")
        val dayYear = intent.getIntExtra("DAYOFYEAR",0)
        val day = intent.getIntExtra("DAY",0)
        val hour = intent.getIntExtra("HOUR",0)
        val minute = intent.getIntExtra("MINUTE",0)
        val id = intent.getIntExtra("ID",0)
        val count = intent.getIntExtra("COUNT",0)

        val channelId = "channel-"+day.toString()+count
        val channelName = "Channel "+title+day.toString()+count
        val importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_DEFAULT
        } else {
            NotificationCompat.PRIORITY_DEFAULT
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                    channelId, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        val pendingIntent = PendingIntent.getActivity(context,id,repeatingIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context,channelId)
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
                dayOfWeek = day,
                cancel = false,
                diff = true,
                dayYear = dayYear,
                count = count
        )
    }
}

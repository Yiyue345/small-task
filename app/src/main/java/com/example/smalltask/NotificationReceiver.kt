package com.example.smalltask

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("normal", "Normal", NotificationManager.
            IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)

            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){

                val intent = Intent(context, MainActivity::class.java)
                val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

                val notification = NotificationCompat.Builder(context, "normal")
                    .setContentTitle(context.getString(R.string.no_learn_today_notice_title))
                    .setContentText(context.getString(R.string.no_learn_today_notice_message))
                    .setContentIntent(pi)
                    .setSmallIcon(R.drawable.book)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.elaina))
                    .setAutoCancel(true)
                    .build()
                manager.notify(1, notification)
            }

        }


    }
}
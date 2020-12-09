package com.haag.superchat.firebaseMessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.EXTRA_NOTIFICATION_CHANNEL_ID
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log.d
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.haag.superchat.MainActivity
import com.haag.superchat.R
import kotlin.random.Random

private const val CHANNEL_ID = "channel"

class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        d(",,", "From: " + message.from)

        // Check if message contains a data payload.
        if (message.data.size > 0) {
            d(",,", "Message data payload: " + message.data)


            var intent = Intent(this, MainActivity::class.java)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationID = Random.nextInt()


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager)
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setSmallIcon(R.drawable.ic_extra)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)
        }

        // Check if message contains a notification payload.
        if (message.notification != null) {
            d(",,", "Message Notification Body: " + message.notification!!.body)
        }


        message.notification?.let {
            d(",,", "Message Notification Body: ${it.body}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(nm: NotificationManager) {
        val channelName = "channelName"
        val channel =
            NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
                description = "SuperChat Channel"
                enableLights(true)
                lightColor = Color.GREEN
            }

        nm.createNotificationChannel(channel)

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        d(",,", "Refreshed token: $token")

    }


}

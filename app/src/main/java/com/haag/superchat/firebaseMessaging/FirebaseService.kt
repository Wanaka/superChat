package com.haag.superchat.firebaseMessaging

import android.app.NotificationChannel
import android.app.NotificationManager
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
private const val CHANNEL_NAME = "channelName"
private const val DESCRIPTION_NAME = "SuperChat Channel"
private const val TITLE = "title"
private const val MESSAGE = "message"

class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {

        //TODO later create a separate notification class
        if (message.data.isNotEmpty()) {

            var intent = Intent(this, MainActivity::class.java)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationID = Random.nextInt()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            checkBuildVersion(notificationManager)

            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data[TITLE])
                .setContentText(message.data[MESSAGE])
                .setSmallIcon(R.drawable.ic_extra)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)
        }
    }


    private fun checkBuildVersion(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH).apply {
                description = DESCRIPTION_NAME
                enableLights(true)
                lightColor = Color.GREEN
            }

        notificationManager.createNotificationChannel(channel)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


}

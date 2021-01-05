package com.haag.superchat.firebaseMessaging

import android.app.*
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
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
private const val USER = "user"
private const val MESSAGE = "message"

class FirebaseService : FirebaseMessagingService() {
    lateinit var sharedPreference: SharedPreferences

    override fun onMessageReceived(message: RemoteMessage) {
        sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)!!

        if (message.data.isNotEmpty()) {
            if (sharedPreference?.getString("currentChatUserId", "") != message.data[USER]) {
                var intent = Intent(this, MainActivity::class.java)
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationID = Random.nextInt()
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                checkBuildVersion(notificationManager)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentText(message.data[MESSAGE])
                    .setSmallIcon(R.drawable.logo_shape)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build()

                notificationManager.notify(notificationID, notification)
            }
        }
    }

    private fun checkBuildVersion(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(notificationManager: NotificationManager) {
        val pattern = longArrayOf(0, 200, 100, 300)
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH).apply {
                description = DESCRIPTION_NAME
                enableLights(true)
                lightColor = Color.GREEN
            }
        channel.enableVibration(true)
        channel.vibrationPattern = pattern

//        channel.setSound(alarmSound)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}

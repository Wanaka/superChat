package com.haag.superchat.model

data class PushNotification(
    val data: NotificationData,
    val to: String
)
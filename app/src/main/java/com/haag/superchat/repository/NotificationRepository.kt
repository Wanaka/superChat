package com.haag.superchat.repository

import com.haag.superchat.model.PushNotification
import com.haag.superchat.retrofit.NotificationApi
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class NotificationRepository @Inject constructor(private val notificationService: NotificationApi) {

    suspend fun post(notification: PushNotification): Response<ResponseBody> {
        return notificationService.postNotification(notification)
    }
}
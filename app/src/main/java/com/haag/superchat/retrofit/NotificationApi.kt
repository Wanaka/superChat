package com.haag.superchat.retrofit

import com.haag.superchat.model.PushNotification
import com.haag.superchat.util.FCMConstants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key=${FCMConstants.SERVER_KEY}", "Content-Type:${FCMConstants.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}
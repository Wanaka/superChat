package com.haag.superchat.util

import com.haag.superchat.BuildConfig


class FCMConstants {

    companion object {
        const val BASE_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = BuildConfig.API_KEY
        const val CONTENT_TYPE = "application/json"
        const val TOPIC = "/topics"
    }
}
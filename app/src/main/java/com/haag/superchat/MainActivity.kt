package com.haag.superchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.google.firebase.messaging.FirebaseMessaging
import com.haag.superchat.ui.chat.ChatViewModel
import com.haag.superchat.util.FCMConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_chat.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val vm: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(activityToolbar as Toolbar)
        FirebaseMessaging.getInstance()
            .subscribeToTopic("${FCMConstants.TOPIC}/${vm.getCurrentUser()?.uid.toString()}")
    }
}
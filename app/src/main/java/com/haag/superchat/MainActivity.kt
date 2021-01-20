package com.haag.superchat

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log.d
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.messaging.FirebaseMessaging
import com.haag.superchat.ui.chat.ChatViewModel
import com.haag.superchat.util.ConnectivityListener
import com.haag.superchat.util.FCMConstants
import com.haag.superchat.util.InternetConnectionBroadCastReceiver
import com.haag.superchat.util.showSnack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_chat.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ConnectivityListener {
    private val vm: ChatViewModel by viewModels()

    lateinit var receiver: InternetConnectionBroadCastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(activityToolbar as Toolbar)

        FirebaseMessaging.getInstance()
            .subscribeToTopic("${FCMConstants.TOPIC}/${vm.getCurrentUser()?.uid.toString()}")

        receiver = InternetConnectionBroadCastReceiver()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        setConnectivityListener(this)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    private fun setConnectivityListener(listener: ConnectivityListener) {
        InternetConnectionBroadCastReceiver.connectivityListener = listener
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showSnack(isConnected, findViewById(R.id.navGraphFragment))
    }
}
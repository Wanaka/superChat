package com.haag.superchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.haag.superchat.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.toolbar.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(activityToolbar as Toolbar)
    }
}
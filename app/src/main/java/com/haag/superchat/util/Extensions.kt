package com.haag.superchat.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.haag.superchat.MainActivity
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


fun Fragment.hideKeyBoard() {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
}

fun getDateTime(): String = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
    .withZone(ZoneOffset.UTC)
    .format(Instant.now())

fun RecyclerView.lineDivider() {
    this.addItemDecoration(
        DividerItemDecoration(
            this.context,
            DividerItemDecoration.VERTICAL
        )
    )
}

fun setupActionToolBar(_title: String, activity: FragmentActivity?) {
    var title: String
    var bool = false

    when (_title) {
        "Chats" -> {
            title = "Chats"
        }
        "SuperChat" -> {
            title = "SuperChat"
        }
        else -> {
            title = _title
            bool = true
        }
    }
    (activity as MainActivity?)?.supportActionBar?.title = title
    (activity)?.supportActionBar?.setDisplayHomeAsUpEnabled(bool)
    (activity)?.supportActionBar?.setDisplayShowHomeEnabled(bool)
}
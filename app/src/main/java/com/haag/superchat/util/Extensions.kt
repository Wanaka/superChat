package com.haag.superchat.util

import android.content.Context
import android.text.TextUtils.replace
import android.util.Log.d
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.haag.superchat.MainActivity
import com.haag.superchat.R
import com.haag.superchat.model.Message
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


fun Fragment.hideKeyBoard() {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
}

fun Fragment.showKeyBoard() {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInputFromWindow(requireView().windowToken, InputMethodManager.SHOW_FORCED, 0)

}

fun messageNumber(number: Int): String {
    var size = number
    return modifyNr(size++)
}

fun modifyNr(number: Int): String {
    return (if ((number / 10).toString().dropLast(1).isEmpty())
        if (number < 10) "00_0${number}_${getDateTime()}"
        else "00_${number}_${getDateTime()}"
    else {
        if ((number / 10).toString().dropLast(1).toInt() < 10) {
            "0${((number / 10)).toString().dropLast(1)}_${number}_${getDateTime()}"
        } else {
            "${((number / 10)).toString().dropLast(1)}_${number}_${getDateTime()}"
        }
    }).toString()
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

fun Context.toaster(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun Context.showSnack(isConnected: Boolean, view: View) {
    var message: String
    var length: Int
    var color: Int

    if (isConnected) {
        length = Snackbar.LENGTH_SHORT
        color = getColor(R.color.opacity)
        message = ""

    } else {
        length = Snackbar.LENGTH_INDEFINITE
        color = getColor(R.color.red)
        message = getString(R.string.snack_internet_not_connected)
    }

    var snack = Snackbar.make(view, message, length)
    snack.view.setBackgroundColor(color)
    snack.show()
}
package com.haag.superchat.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import java.util.*


// Navigation

fun navigateTo(view: View, fragmentId: Int) {
    Navigation.findNavController(view)
        .navigate(fragmentId)
}

fun Fragment.hideKeyBoard() {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
}

fun currentDate() = Date().toString()


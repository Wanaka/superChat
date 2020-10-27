package com.haag.superchat.ui.signIn

import android.util.Log
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.haag.superchat.R
import com.haag.superchat.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(private val repo: LoginRepository) : ViewModel() {

    fun getInstance() = repo.getInstance()

    fun signIn(email: String, password: String, view: View) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val authRes = repo.signIn(email, password)

                if (authRes.user != null) {
                    navigateTo(view, R.id.action_loginFragment_to_chatFragment)
                }
            } catch (e: Exception) {
                Log.d(",,", "Exception: $e")
            }
        }
    }


    // Navigation
    fun navigateTo(view: View, fragmentId: Int) {
        Navigation.findNavController(view)
            .navigate(fragmentId)
    }
}
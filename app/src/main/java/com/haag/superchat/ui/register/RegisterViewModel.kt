package com.haag.superchat.ui.register

import android.util.Log.d
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.haag.superchat.R
import com.haag.superchat.model.Chat
import com.haag.superchat.model.User
import com.haag.superchat.model.UserEmailUid
import com.haag.superchat.repository.RegisterRepository
import com.haag.superchat.util.toaster
import kotlinx.android.synthetic.main.fragment_create_user_login.view.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel @ViewModelInject constructor(private val repo: RegisterRepository) :
    ViewModel() {

    fun getInstance() = repo.getInstance()

    fun getCurrentUser() = repo.getCurrentUser()

    fun createAccount(userName: String, email: String, password: String, view: View) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    view.progressBarRegister.visibility = View.VISIBLE
                }

                val authRes = repo.createAccount(email, password)

                if (authRes.user != null) {
                    try {
                        repo.addToUserList(UserEmailUid(email, getCurrentUser()!!.uid))
                        repo.addToUsers(User(userName, email, getCurrentUser()!!.uid))
                        navigateTo(view, R.id.action_createUserLoginFragment_to_chatFragment)
                    } catch (e: Exception) {
                        d(",,", "Exception: $e")
                    }

                }
            } catch (e: Exception) {
                d(",,", "Exception: $e")

                var exception = e.message
                withContext(Dispatchers.Main) {
                    view.progressBarRegister.visibility = View.GONE
                    view.context.toaster("${exception}")
                }
            }
        }
    }


    // Navigation
    fun navigateTo(view: View, fragmentId: Int) {
        Navigation.findNavController(view)
            .navigate(fragmentId)
    }
}
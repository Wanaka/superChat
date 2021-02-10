package com.haag.superchat.ui.settings

import android.util.Log
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.haag.superchat.R
import com.haag.superchat.repository.ChatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel @ViewModelInject constructor(private val repo: ChatsRepository) : ViewModel() {

    fun getCurrentUser() = repo.getCurrentUser()

    fun signOut(view: View) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.signOut()

                withContext(Dispatchers.Main) {
                    signOutUser(view)
                }
            } catch (e: Exception) {
                Log.d(",,", "Exception: $e")
            }
        }
    }

    // Navigation
    private fun signOutUser(view: View) {
        Navigation.findNavController(view)
            .navigate(R.id.action_chatFragment_to_createUserLoginFragment)
    }


}
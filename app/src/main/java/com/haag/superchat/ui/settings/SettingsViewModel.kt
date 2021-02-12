package com.haag.superchat.ui.settings

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.google.firebase.storage.StorageReference
import com.haag.superchat.R
import com.haag.superchat.model.Chat
import com.haag.superchat.model.User
import com.haag.superchat.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SettingsViewModel @ViewModelInject constructor(private val repo: SettingsRepository) :
    ViewModel() {

    fun uploadImageToFirebaseStorage(img: Uri, context: Context?) {
        repo.uploadImageToFirebaseStorage(img)
    }

    fun getImg(): StorageReference =
        repo.getMyProfilePicture()

    fun updateUserName(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.updateUserName(userName)
            } catch (e: Exception) {
                Log.d(",,", "Exception: ${e.message}")
            }
        }
    }

    fun getUser(): LiveData<User> {
        val user = MutableLiveData<User>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val u = repo.getUser()

                withContext(Dispatchers.Main) {
                    user.postValue(
                        User(
                            u["userName"].toString(),
                            u["email"].toString(),
                            u["id"].toString()
                        )
                    )
                }


            } catch (e: Exception) {
                Log.d(",,", "Exception: ${e.message}")
            }
        }

        return user
    }


    fun signOut(view: View) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.signOut()

                withContext(Dispatchers.Main) {
                    signOutUser(view)
                }
            } catch (e: Exception) {
                d(",,", "Exception: $e")
            }
        }
    }


    // Navigation
    private fun signOutUser(view: View) {
        Navigation.findNavController(view)
            .navigate(R.id.action_settingsFragment_to_createUserLoginFragment)
    }
}
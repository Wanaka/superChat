package com.haag.superchat.ui.settings

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.google.firebase.storage.StorageReference
import com.haag.superchat.R
import com.haag.superchat.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel @ViewModelInject constructor(private val repo: SettingsRepository) :
    ViewModel() {

    fun uploadImageToFirebaseStorage(img: Uri, context: Context?) {
        repo.uploadImageToFirebaseStorage(img)
    }

    fun getImg(): StorageReference  =
        repo.getMyProfilePicture()


//    fun getCurrentUser() = repo.getCurrentUser()
//
//    fun signOut(view: View) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                repo.signOut()
//
//                withContext(Dispatchers.Main) {
//                    signOutUser(view)
//                }
//            } catch (e: Exception) {
//                d(",,", "Exception: $e")
//            }
//        }
//    }



    // Navigation
    private fun signOutUser(view: View) {
        Navigation.findNavController(view)
            .navigate(R.id.action_chatFragment_to_createUserLoginFragment)
    }


}
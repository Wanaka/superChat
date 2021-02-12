package com.haag.superchat.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.haag.superchat.R
import com.haag.superchat.util.displayProfilePicture
import com.haag.superchat.util.hideKeyBoard
import com.haag.superchat.util.setupActionToolBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_user_login.*
import kotlinx.android.synthetic.main.fragment_settings.*

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private val vm: SettingsViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        setupActionToolBar(getString(R.string.settings), activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.getUser().observe(viewLifecycleOwner, Observer {
            userNameTxt.text = it.userName
        })

        vm.getImg().downloadUrl
            .addOnSuccessListener { uri ->
                displayProfilePicture(requireContext(), uri, userImg)
            }

        userImg.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)
            }
        }

        userNameTxt.setOnClickListener {
            userNameIsBeingEdited(true)
        }

        updateUserNameBtn.setOnClickListener {
            userNameIsBeingEdited(false)
        }

        signOutButton.setOnClickListener {
            vm.signOut(view)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            data?.data?.let {
                displayProfilePicture(requireContext(), it, userImg)
                uploadImageToFirebaseStorage(it)
            }
        }
    }

    private fun uploadImageToFirebaseStorage(img: Uri) {
        vm.uploadImageToFirebaseStorage(img, context)
    }

    private fun userNameIsBeingEdited(bool: Boolean) {
        when (bool) {
            true -> {

                editUserNameTxt.post {
                    editUserNameTxt.setText(userNameTxt.text)
                }
                userNameTxt.visibility = View.GONE
                editUserNameTxt.visibility = View.VISIBLE
                updateUserNameBtn.visibility = View.VISIBLE
            }
            false -> {
                hideKeyBoard()
                userNameTxt.text = editUserNameTxt.text
                userNameTxt.visibility = View.VISIBLE
                editUserNameTxt.visibility = View.GONE
                updateUserNameBtn.visibility = View.GONE
                vm.updateUserName(editUserNameTxt.text.toString())
            }
        }
    }


    // Menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
            }
        }
        return true
    }
}
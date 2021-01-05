package com.haag.superchat.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.haag.superchat.R
import com.haag.superchat.util.hideKeyBoard
import com.haag.superchat.util.setupActionToolBar
import com.haag.superchat.util.toaster
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_user_login.*

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val vm: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setupActionToolBar(getString(R.string.superChat), activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_user_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ifUserIsLoggedInGoToChat(view)

        createBtn.setOnClickListener {
            createAccount(view)
            hideKeyBoard()
        }

        goToSignInBtn.setOnClickListener {
            vm.navigateTo(view, R.id.action_createUserLoginFragment_to_loginFragment)
        }
    }

    private fun ifUserIsLoggedInGoToChat(view: View) {
        if (!vm.getCurrentUser()?.uid.isNullOrEmpty()) {
            vm.navigateTo(view, R.id.action_createUserLoginFragment_to_chatFragment)
        }
    }

    private fun createAccount(view: View) {
        if (usernameTxt.text.toString().isEmpty() || emailTxt.text.toString().isEmpty() || passwordTxt.text.toString().isEmpty()) {
            context?.toaster(getString(R.string.toast_valid_email_password_needed))
        } else {
            vm.createAccount(
                usernameTxt.text.toString(),
                emailTxt.text.toString(),
                passwordTxt.text.toString(),
                view
            )
        }
    }

}

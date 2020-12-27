package com.haag.superchat.ui.signIn

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import com.haag.superchat.R
import com.haag.superchat.util.hideKeyBoard
import com.haag.superchat.util.toaster
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_user_login.*
import kotlinx.android.synthetic.main.fragment_login.*

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val vm: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInBtn.setOnClickListener {
            signIn(view)
            hideKeyBoard()
        }

        registerButton.setOnClickListener {
            vm.navigateTo(view, R.id.action_loginFragment_to_createUserLoginFragment)
        }
    }

    private fun signIn(view: View) {
        if (emailLoginTxt.text.toString().isEmpty() || passwordLoginTxt.text.toString().isEmpty()) {
            context?.toaster(getString(R.string.toast_valid_email_password_needed))
        } else {
            vm.signIn(
                emailLoginTxt.text.toString(),
                passwordLoginTxt.text.toString(),
                view
            )
        }
    }
}
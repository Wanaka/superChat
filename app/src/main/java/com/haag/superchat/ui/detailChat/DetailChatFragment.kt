package com.haag.superchat.ui.detailChat

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.haag.superchat.MainActivity
import com.haag.superchat.R
import com.haag.superchat.model.Message
import com.haag.superchat.ui.chat.ChatViewModel
import kotlinx.android.synthetic.main.fragment_detail_chat.*


class DetailChatFragment : Fragment() {
    private val vm: DetailChatViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity?)?.supportActionBar?.title = arguments?.getString("user")
        vm.getInstance() // base fragment?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chatId = arguments?.getString("chatId")
        vm.getChat(chatId).observe(viewLifecycleOwner, Observer {
            //Add adapter
        })


        sendMessageBtn.setOnClickListener {
            vm.sendMessage(chatInput.text.toString(), chatId.toString())
            chatInput.text.clear()
        }
    }

}
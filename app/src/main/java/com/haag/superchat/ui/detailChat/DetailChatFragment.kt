package com.haag.superchat.ui.detailChat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.haag.superchat.MainActivity
import com.haag.superchat.R
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.ui.detailChat.recyclerView.DetailChatAdapter
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
        val friendId = arguments?.getString("friendId")

        getChatId(friendId)
    }

    private fun getChatId(friendId: String?) {
        vm.getChatId(friendId.toString()).observe(viewLifecycleOwner, Observer {
            getChat(it)
            sendMessage(friendId, it)
        })
    }

    private fun getChat(chatId: Chat) {
        vm.getChat(chatId.id).observe(viewLifecycleOwner, Observer {
            chatAdapter(it)
        })
    }

    private fun sendMessage(friendId: String?, chatId: Chat) {
        sendMessageBtn.setOnClickListener {
            if (chatInput.text.isNotBlank()) {
                vm.sendMessage(chatInput.text.toString(), chatId)
                chatInput.text.clear()

                //separate this part into its own function
                vm.getUser(vm.getCurrentUser()?.uid.toString())
                    .observe(viewLifecycleOwner, Observer {
                        vm.addUserToFriendsList(it, chatId, friendId.toString())
                    })
                ///////
            }
        }
    }

    private fun chatAdapter(chatMessages: List<Message>) {
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            (layoutManager as LinearLayoutManager).reverseLayout = true
            adapter = DetailChatAdapter(
                chatMessages.reversed(),
                vm.getCurrentUser()?.uid.toString(),
                context
            )
        }
    }
}
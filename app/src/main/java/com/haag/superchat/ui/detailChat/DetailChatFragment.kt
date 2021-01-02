package com.haag.superchat.ui.detailChat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.haag.superchat.R
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import com.haag.superchat.ui.detailChat.recyclerView.DetailChatAdapter
import com.haag.superchat.util.Constants
import com.haag.superchat.util.hideKeyBoard
import com.haag.superchat.util.setupActionToolBar
import kotlinx.android.synthetic.main.fragment_detail_chat.*


class DetailChatFragment : Fragment() {

    private val vm: DetailChatViewModel by viewModels()
    lateinit var user: User
    private var friendId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        vm.getInstance() // base fragment?
    }

    override fun onResume() {
        super.onResume()
        val user = arguments?.getString(Constants.USER)
        setupActionToolBar(user.toString(), activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendId = arguments?.getString(Constants.FRIEND_ID)
        getChatId(friendId)
        getCurrentUser()
    }

    private fun getChatId(friendId: String?) {
        vm.getChatId(friendId.toString()).observe(viewLifecycleOwner, Observer {
            getChat(it)
        })
    }

    private fun getCurrentUser() {
        vm.getUser(vm.getCurrentUser()?.uid.toString())
            .observe(viewLifecycleOwner, Observer {
                user = it
            })
    }

    private fun getChat(chatId: Chat) {
        vm.getChat(chatId.id).observe(viewLifecycleOwner, Observer {
            chatAdapter(it)
            sendMessage(friendId, chatId, it.size)
        })
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

    private fun sendMessage(friendId: String?, chatId: Chat, msgNmbr: Int) {
        sendMessageBtn.setOnClickListener {
            if (chatInput.text.isNotBlank()) {
                vm.sendMessage(
                    chatInput.text.toString(),
                    chatId,
                    friendId.toString(),
                    user,
                    msgNmbr
                )
                vm.addUserToFriendsList(user, chatId, friendId.toString())
                chatInput.text.clear()
            }
        }
    }


    // Menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                var result = arguments?.getString(Constants.FRIEND_ID)

                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(Constants.RESULT, result)

                findNavController().navigateUp()
                hideKeyBoard()
            }
        }

        return true
    }
}
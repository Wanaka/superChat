package com.haag.superchat.ui.detailChat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.haag.superchat.BuildConfig
import com.haag.superchat.R
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import com.haag.superchat.ui.detailChat.recyclerView.DetailChatAdapter
import com.haag.superchat.util.Constants
import com.haag.superchat.util.hideKeyBoard
import com.haag.superchat.util.put
import com.haag.superchat.util.setupActionToolBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_detail_chat.*

@AndroidEntryPoint
class DetailChatFragment : Fragment() {

    private val vm: DetailChatViewModel by viewModels()
    lateinit var user: User
    private var friendId: String? = ""
    lateinit var sharedPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sharedPreference = context?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)!!
    }

    override fun onResume() {
        super.onResume()
        val user = arguments?.getString(Constants.USER)
        storeCurrentFriendChatIdForNotificationsReference(friendId)
        setupActionToolBar(user.toString(), activity)
    }

    override fun onPause() {
        super.onPause()
        storeCurrentFriendChatIdForNotificationsReference("")
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

    private fun storeCurrentFriendChatIdForNotificationsReference(id: String?) {
        sharedPreference.put("currentChatUserId", id)
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
                Log.d(",,", "BACK CLICK detailchat")

                findNavController().navigateUp()
                hideKeyBoard()
            }
        }

        return true
    }
}
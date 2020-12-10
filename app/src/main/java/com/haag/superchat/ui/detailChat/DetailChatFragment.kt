package com.haag.superchat.ui.detailChat

import android.os.Bundle
import android.util.Log.d
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
import com.haag.superchat.ui.detailChat.recyclerView.DetailChatAdapter
import com.haag.superchat.util.Constants
import com.haag.superchat.util.FCMConstants
import com.haag.superchat.util.setupActionToolBar
import kotlinx.android.synthetic.main.fragment_detail_chat.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream


class DetailChatFragment : Fragment() {

    private val vm: DetailChatViewModel by viewModels()


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
        val friendId = arguments?.getString(Constants.FRIEND_ID)

        getChatId(friendId.toString())
    }

    private fun getChatId(friendId: String) {
        vm.getChatId(friendId).observe(viewLifecycleOwner, Observer {
            getChat(it)
            sendMessage(friendId, it)
        })
    }

    private fun getChat(chatId: Chat) {
        vm.getChat(chatId.id).observe(viewLifecycleOwner, Observer {
            chatAdapter(it)
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

    private fun sendMessage(friendId: String, chatId: Chat) {
        sendMessageBtn.setOnClickListener {
            if (chatInput.text.isNotBlank()) {
                vm.sendMessage(chatInput.text.toString(), chatId, friendId)
                addUserToFriendsList(friendId, chatId)
                chatInput.text.clear()
            }
        }
    }

    // should only happen the first time a message is sent
    private fun addUserToFriendsList(friendId: String, chatId: Chat) {
        vm.getUser(vm.getCurrentUser()?.uid.toString())
            .observe(viewLifecycleOwner, Observer {
                vm.addUserToFriendsList(it, chatId, friendId)
            })
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
            }
        }

        return true
    }
}
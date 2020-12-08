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
import com.google.gson.Gson
import com.haag.superchat.MainActivity
import com.haag.superchat.R
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.NotificationData
import com.haag.superchat.model.PushNotification
import com.haag.superchat.retrofit.RetrofitInstance
import com.haag.superchat.ui.detailChat.recyclerView.DetailChatAdapter
import com.haag.superchat.util.setupActionToolBar
import com.squareup.okhttp.Dispatcher
import kotlinx.android.synthetic.main.fragment_detail_chat.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

const val TOPIC = "/topics/myTopic"

class DetailChatFragment : Fragment() {

    private val vm: DetailChatViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        vm.getInstance() // base fragment?
    }

    override fun onResume() {
        super.onResume()
        val user = arguments?.getString("user")
        setupActionToolBar(user.toString(), activity)
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


    fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)

            if (response.isSuccessful) {
                d(",,", "response: ${Gson().toJson(response)}")
            } else {
                d(",,", "response: ${response.errorBody().toString()}")

            }

        } catch (e: Exception) {
            d(",,", "exeption: ${e.toString()}")
        }
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
            testme()
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

    fun testme(){
//        sendMessageBtn.setOnClickListener {
            d(",,", "push send!!")

            PushNotification(
                NotificationData("Hello World", "I'm a message!"),
                TOPIC
            ).also {
                sendNotification(it)
            }
//        }
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


    // Menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                var result = arguments?.getString("friendId")

                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("result", result)

                findNavController().navigateUp()
            }
        }

        return true
    }
}
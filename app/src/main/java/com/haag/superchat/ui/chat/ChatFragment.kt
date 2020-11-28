package com.haag.superchat.ui.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log.d
import android.view.*
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.haag.superchat.R
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import com.haag.superchat.ui.chat.recyclerView.ChatAdapter
import com.haag.superchat.ui.chat.recyclerView.ChatAdapter.OnItemChatClickListener
import com.haag.superchat.ui.chat.recyclerView.SearchAdapter
import com.haag.superchat.ui.chat.recyclerView.SearchAdapter.OnItemClickListener
import com.haag.superchat.util.hideKeyBoard
import com.haag.superchat.util.lineDivider
import com.haag.superchat.util.setupActionToolBar
import kotlinx.android.synthetic.main.chat_list_item.*
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.*
import kotlin.collections.ArrayList


class ChatFragment : Fragment(), OnItemClickListener, OnItemChatClickListener {

    private val vm: ChatViewModel by viewModels()
    var searchList = ArrayList<User>()

    lateinit var sharedPreference: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    var backStackFromUser = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        vm.getInstance() // base fragment?
        sharedPreference = context?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)!!
        editor = sharedPreference?.edit()
    }

    override fun onResume() {
        super.onResume()
        setupActionToolBar(getString(R.string.chats), activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("result")
            ?.observe(viewLifecycleOwner, Observer {

                backStackFromUser = it
            })

        vm.getFriendsList().observe(viewLifecycleOwner, Observer { it ->
            it.forEach {
                getChat(it.id)
            }
            initChatRv(it)
        })

        vm.userData.observe(viewLifecycleOwner, Observer {
            initSearchRv(it)
        })
    }

    private fun initChatRv(it: List<User>) {
        chatsRv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ChatAdapter(it, context, this@ChatFragment)
        }.lineDivider()
    }

    private fun initSearchRv(it: User) {
        searchList.clear()
        searchList.add(it)
        searchRv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SearchAdapter(searchList, context, this@ChatFragment)
        }.lineDivider()
    }

    private fun getChat(friendId: String) {
        vm.getChatId(friendId).observe(viewLifecycleOwner, Observer {
            getLastMessage(it, friendId)
        })
    }

    private fun getLastMessage(chat: Chat, friendId: String) {
        vm.getLastMessage(chat.id).observe(viewLifecycleOwner, Observer {
            checkForNewMessage(it, friendId)
        })
    }


    private fun checkForNewMessage(message: Message, friendId: String) {

        if (friendId == message.userId) {
            d(",,", "friend")

            if (sharedPreference?.getString(message.userId, "") != message.message) {
                d(",,", "new message")

                if (backStackFromUser.isNotEmpty()) {
                    d(",,", "backstaged")

                    editor.putBoolean("${message.userId}+a", false).commit()
                    editor.putString(message.userId, message.message).commit()
                    backStackFromUser = ""
                } else {
                    editor.putBoolean("${message.userId}+a", true).commit()
                    d(",,", "staying")
                }
            } else {
                d(",,", "same message")

            }
        } else if (vm.getCurrentUser()?.uid == message.userId) {
            d(",,", "My message id")

            editor.putBoolean("${friendId}+a", false).commit()
            editor.putString(friendId, message.message).commit()
            backStackFromUser = ""
        } else {
            editor.putBoolean("${friendId}+a", true).commit()
            d(",,", "someone else")

        }

        chatsRv.adapter?.notifyDataSetChanged()
    }

    override fun onItemClick(context: Context, user: User) {
        searchRv.visibility = View.GONE
        hideKeyBoard()
        vm.addUserToFriendsList(user)
    }

    override fun onItemChatClick(context: Context, user: User) {
        vm.navigateTo(requireView(), R.id.action_chatFragment_to_detailChatFragment, user)
    }


    // ---- Menu ---- //
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)

        var item = menu.findItem(R.id.chatSearch)

        var searchView: SearchView? = item.actionView as? SearchView

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                searchRv.visibility = View.VISIBLE

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                searchRv.visibility = View.GONE
                searchList.clear()
                searchView?.setQuery("", false)
                hideKeyBoard()

                return true
            }
        })

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(userEmail: String): Boolean {
                searchRv.visibility = View.VISIBLE
                searchView?.setQuery("", false)
                vm.searchUserByEmail(userEmail)

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

        })

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.chatSignOut -> {
                vm.signOut(requireView())
            }
        }

        return true
    }
}
package com.haag.superchat.ui.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log.d
import android.view.*
import android.widget.SearchView
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
import com.haag.superchat.ui.chat.recyclerView.SearchAdapter.OnItemSearchClickListener
import com.haag.superchat.util.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlin.collections.ArrayList


class ChatFragment : Fragment(), OnItemSearchClickListener, OnItemChatClickListener {

    private val vm: ChatViewModel by viewModels()
    var searchList = ArrayList<User>()
    lateinit var sharedPreference: SharedPreferences
    var backStackFromUser = ""
    private var userList: List<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        vm.getInstance() // base fragment?
        sharedPreference = context?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)!!
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

        vm.getFriendsList().observe(viewLifecycleOwner, Observer { it ->
            userList = it
            initChatRv(it)
            it.forEach { getChat(it.id) }
        })

        vm.userData.observe(viewLifecycleOwner, Observer {
            initSearchRv(it)
        })

        latestBackStack()
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

    // move logic to VM
    private fun checkForNewMessage(message: Message, friendId: String) {
        if (friendId == message.userId) {
            if (sharedPreference?.getString(message.userId, "") != message.message) {
                if (backStackFromUser == message.userId) {
                    sharedPreference.put(
                        "${message.userId}+${Constants.SHARED_PREF_BOOLEAN}",
                        false
                    )
                    sharedPreference.put(message.userId, message.message)
                    backStackFromUser = ""
                } else {
                    sharedPreference.put("${message.userId}+${Constants.SHARED_PREF_BOOLEAN}", true)
                }
            }
        } else if (vm.getCurrentUser()?.uid == message.userId) {
            sharedPreference.put("${friendId}+${Constants.SHARED_PREF_BOOLEAN}", false)
            sharedPreference.put("${friendId}+${Constants.SHARED_PREF_STRING}", message.message)
            backStackFromUser = ""
        } else {
            sharedPreference.put("${friendId}+${Constants.SHARED_PREF_BOOLEAN}", true)

        }
        sharedPreference.put("${friendId}+${Constants.SHARED_PREF_STRING}", message.message)

        chatsRv.adapter?.notifyDataSetChanged()
    }

    override fun onItemSearchClick(context: Context, user: User) {
        vm.addUserToFriendsList(user, userList, context)
        searchRv.visibility = View.GONE
        chatsRv.visibility = View.VISIBLE
        searchList.clear()
        hideKeyBoard()
    }

    override fun onItemChatClick(context: Context, user: User) {
        vm.navigateTo(requireView(), R.id.action_chatFragment_to_detailChatFragment, user)
    }

    private fun latestBackStack() {
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>(Constants.RESULT)
            ?.observe(viewLifecycleOwner, Observer {
                backStackFromUser = it
            })
    }

    // ---- Menu ---- //
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)

        var item = menu.findItem(R.id.chatSearch)

        var searchView: SearchView? = item.actionView as? SearchView

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                searchRv.visibility = View.VISIBLE
                chatsRv.visibility = View.GONE
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                searchRv.visibility = View.GONE
                chatsRv.visibility = View.VISIBLE

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
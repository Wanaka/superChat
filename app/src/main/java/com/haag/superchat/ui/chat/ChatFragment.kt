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
import androidx.recyclerview.widget.LinearLayoutManager
import com.haag.superchat.R
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import com.haag.superchat.sealedClasses.FriendListResponse
import com.haag.superchat.ui.chat.recyclerView.ChatAdapter
import com.haag.superchat.ui.chat.recyclerView.ChatAdapter.OnItemChatClickListener
import com.haag.superchat.ui.chat.recyclerView.SearchAdapter
import com.haag.superchat.ui.chat.recyclerView.SearchAdapter.OnItemSearchClickListener
import com.haag.superchat.util.*
import com.haag.superchat.util.Constants.Companion.SHARED_PREF_BOOLEAN
import com.haag.superchat.util.Constants.Companion.SHARED_PREF_ENTERED
import com.haag.superchat.util.Constants.Companion.SHARED_PREF_MSG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ChatFragment : Fragment(), OnItemSearchClickListener, OnItemChatClickListener {

    private val vm: ChatViewModel by viewModels()

    var searchList = ArrayList<User>()
    lateinit var sharedPreference: SharedPreferences
    private var userList: List<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        vm.friendList.observe(viewLifecycleOwner, Observer { result ->
            d(",,", "observing friendlist")
            when (result) {
                is FriendListResponse.Success -> {
                    userList = result.list
                    initChatRv(result.list)
                    result.list.forEach { getChat(it.id) }
                }

                is FriendListResponse.Failure -> d(",,", result.message ?: "Unknown error message")
            }
        })

        vm.userData.observe(viewLifecycleOwner, Observer {
            initSearchRv(it)
        })

        vm.getFriendsList()
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

    private fun checkForNewMessage(message: Message, friendId: String) {
        if (sharedPreference.getString(friendId + SHARED_PREF_MSG, "") != message.message) {
            sharedPreference.put(friendId + SHARED_PREF_MSG, message.message)

            if (vm.getCurrentUser()?.uid != message.userId) {
                if (sharedPreference.getBoolean(friendId + SHARED_PREF_ENTERED, false)) {
                    sharedPreference.put(friendId + SHARED_PREF_BOOLEAN, false)
                    sharedPreference.put(friendId + SHARED_PREF_ENTERED, false)
                } else {
                    sharedPreference.put(friendId + SHARED_PREF_BOOLEAN, true)
                }
            } else {
                sharedPreference.put(friendId + SHARED_PREF_ENTERED, false)
            }
        } else {
            sharedPreference.put(friendId + SHARED_PREF_ENTERED, false)
        }

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
        sharedPreference.put(user.id + SHARED_PREF_ENTERED, true)
        sharedPreference.put(user.id + SHARED_PREF_BOOLEAN, false)
    }

    private fun latestBackStack() {
        storeCurrentFriendChatIdForNotificationsReference("")
    }

    private fun storeCurrentFriendChatIdForNotificationsReference(id: String?) {
        sharedPreference.put("currentChatUserId", id)
    }


    // ---- Menu ---- //
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)

        var itemChatSearch = menu.findItem(R.id.chatSearch)
        var itemSettings = menu.findItem(R.id.userSettings)

        var searchView: SearchView? = itemChatSearch.actionView as? SearchView

        searchView?.isFocusable = true
        searchView?.isIconified = false


        itemChatSearch.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                itemSettings.isVisible = false
                searchRv.visibility = View.VISIBLE
                chatsRv.visibility = View.GONE
                searchList.clear()
                showKeyBoard()

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                itemSettings.isVisible = true
                searchRv.visibility = View.GONE
                chatsRv.visibility = View.VISIBLE
                searchView?.setQuery("", false)
                searchList.clear()
                hideKeyBoard()

                return true
            }

        })

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(userEmail: String): Boolean {
                searchRv.visibility = View.VISIBLE
                searchView?.setQuery("", false)
                vm.searchUserByEmail(userEmail.toLowerCase(), activity?.applicationContext)
                searchList.clear()

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
            R.id.userSettings -> {
                vm.navigateToSettings(
                    requireView(), R.id.action_chatFragment_to_settingsFragment,
                    vm.getCurrentUser()?.uid.toString()
                )
            }
        }

        return true
    }
}
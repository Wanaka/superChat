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

        vm.getFriendsList().observe(viewLifecycleOwner, Observer { it ->
            it.forEach {
                d(",,", "getFriendsList}")

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


    // This is run twice, why??
    private fun getChat(friendId: String) {
        vm.getChatId(friendId).observe(viewLifecycleOwner, Observer {
            getLastMessage(it)
            d(",,", "getChat}")
        })
    }

    private fun getLastMessage(chat: Chat) {
        vm.getLastMessage(chat.id).observe(viewLifecycleOwner, Observer {
            d(",,", "getLastMessage}")
            checkForNewMessage(it)
        })
    }

    private fun checkForNewMessage(message: Message) {
        d(",,", "checkForNewMessage id: ${UUID.randomUUID()}")
        if (vm.getCurrentUser()?.uid == message.userId) {
            badge.visibility = View.INVISIBLE
            d(",,", "SAME USERID")

        } else {
            if (sharedPreference?.getString("lastMessage", "") != message.message) {
                d(",,", "Different user and not same message in shared pref")
                badge.visibility = View.VISIBLE
                editor?.remove("lastMessage")
                editor?.putString("lastMessage", message.message)
                editor?.commit()
            } else  {

                /////
                ////    IDEA!!! -> what if every time u come back here from other fragment, in resume u set sharedpref. to null or some value
                /////   and then u check if it's that value like above and then maybe u can decide if badge should be shown or not.
                /////
                d(",,", "same message saved ®")
//                badge.visibility = View.INVISIBLE
            }
        }

//        if (sharedPreference?.getString("lastMessage", "") != message.message) {
//            editor?.remove("lastMessage")
//            editor?.putString("lastMessage", message.message)
//            editor?.commit()
//            badge.visibility = View.VISIBLE
//            d(",,", "saved new message: ${sharedPreference?.getString("lastMessage", "")}")
//
//        } else {
//            badge.visibility = View.INVISIBLE
//            d(",,", "The same: ${sharedPreference?.getString("lastMessage", "")}")
//        }
    }

//    private fun checkForNewMessage(message: Message) {
//        d(",,", "message from db: ${message.message}")
//
//        d(",,", "sharedPreference: ${sharedPreference?.getString("lastMessage", "")}")
//
////        badge.visibility = View.INVISIBLE
//
//        if (sharedPreference?.getString("lastMessage", "") != message.message) {
//            editor?.remove("lastMessage")
//            editor?.putString("lastMessage", message.message)
//            editor?.commit()
//            badge.visibility = View.VISIBLE
//            d(",,", "saved new message: ${sharedPreference?.getString("lastMessage", "")}")
//
//        } else {
//            badge.visibility = View.INVISIBLE
//            d(",,", "The same: ${sharedPreference?.getString("lastMessage", "")}")
//        }
//    }


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
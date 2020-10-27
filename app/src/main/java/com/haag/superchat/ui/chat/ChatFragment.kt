package com.haag.superchat.ui.chat

import android.content.Context
import android.os.Bundle
import android.util.Log.d
import android.util.Log.i
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.haag.superchat.R
import com.haag.superchat.model.User
import com.haag.superchat.ui.chat.recyclerView.ChatAdapter
import com.haag.superchat.ui.chat.recyclerView.ChatAdapter.*
import com.haag.superchat.ui.chat.recyclerView.SearchAdapter
import com.haag.superchat.ui.chat.recyclerView.SearchAdapter.*
import com.haag.superchat.util.hideKeyBoard
import kotlinx.android.synthetic.main.fragment_chat.*


class ChatFragment : Fragment(), OnItemClickListener, OnItemChatClickListener {

    private val vm: ChatViewModel by viewModels()
    var searchList = ArrayList<User>()
    var chatList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        vm.getInstance() // base fragment?
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

        vm.getFriendsList().observe(viewLifecycleOwner, Observer {
            initChatRv(it)
        })

        vm.userData.observe(viewLifecycleOwner, Observer {
            initSearchRv(it)
        })

        signOutBtn.setOnClickListener {
            vm.signOut(view)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initChatRv(it: List<User>) {
        chatsRv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ChatAdapter(it, context, this@ChatFragment)
        }
    }

    private fun initSearchRv(it: User) {
        searchList.clear()
        searchList.add(it)
        searchRv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SearchAdapter(searchList, context, this@ChatFragment)
        }
    }

    override fun onItemClick(context: Context, user: User) {
        searchRv.visibility = View.GONE
        hideKeyBoard()
        vm.addUserToFriendsList(user)
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

    override fun onItemChatClick(context: Context, user: User) {
        d(",,", "chatlist click: ${user.userName}")
    }
}
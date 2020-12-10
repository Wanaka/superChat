package com.haag.superchat.ui.chat.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haag.superchat.R
import com.haag.superchat.model.User
import com.haag.superchat.util.Constants
import com.haag.superchat.util.get
import kotlinx.android.synthetic.main.chat_list_item.view.*

class ChatAdapter(
    private val list: List<User>,
    private val context: Context,
    private val mListener: OnItemChatClickListener?
) :
    RecyclerView.Adapter<ChatViewHolder>() {

    var sharedPreference = context?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)!!

    interface OnItemChatClickListener {
        fun onItemChatClick(context: Context, user: User)
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.chat_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val user: User = list[position]

        holder.bind(
            user,
            sharedPreference.get("${user.id}+${Constants.SHARED_PREF_BOOLEAN}", false),
            sharedPreference.get("${user.id}+${Constants.SHARED_PREF_STRING}", "")
        )
        holder.itemView.setOnClickListener {
            mListener!!.onItemChatClick(context, user)
        }
    }
}


class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var userName = view.userNameChat
    private var lastMessageTxt = view.lastMessageTxt
    private var badge = view.badge

    fun bind(user: User, _badge: Boolean, lastMessage: String) {
        userName.text = user.userName
        lastMessageTxt.text = lastMessage


        if (_badge) {
            badge.visibility = View.VISIBLE
        } else {
            badge.visibility = View.INVISIBLE
        }

    }
}
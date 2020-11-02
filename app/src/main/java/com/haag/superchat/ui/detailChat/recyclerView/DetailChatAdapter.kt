package com.haag.superchat.ui.detailChat.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haag.superchat.R
import com.haag.superchat.model.Message
import kotlinx.android.synthetic.main.chat_item_friend.view.*
import kotlinx.android.synthetic.main.chat_item_user.view.*


class DetailChatAdapter(
    private val chatList: List<Message>,
    private val currentUser: String,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemCount(): Int = chatList.size

    override fun getItemViewType(position: Int): Int {
        return when (chatList[position].userId) {
            currentUser -> 1
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> UserDetailChatViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.chat_item_user,
                    parent,
                    false
                )
            )

            0 -> FriendDetailChatViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.chat_item_friend,
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: Message = chatList[position]

        if (message.userId == currentUser) (holder as UserDetailChatViewHolder).bind(message)
        else (holder as FriendDetailChatViewHolder).bind(message)
    }
}


class UserDetailChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var userMessageTxt = view.userMessgeTxt

    fun bind(message: Message) {
        userMessageTxt.text = message.message
    }
}

class FriendDetailChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var friendMessageTxt = view.friendMessgeTxt

    fun bind(message: Message) {
        friendMessageTxt.text = message.message
    }
}
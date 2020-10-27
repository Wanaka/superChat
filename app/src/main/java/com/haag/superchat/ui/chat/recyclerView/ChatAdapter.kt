package com.haag.superchat.ui.chat.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haag.superchat.R
import com.haag.superchat.model.User
import kotlinx.android.synthetic.main.chat_list_item.view.*
import kotlinx.android.synthetic.main.search_list_item.view.*

class ChatAdapter(
    private val list: List<User>,
    private val context: Context,
    private val mListener: OnItemChatClickListener?
) :
    RecyclerView.Adapter<ChatViewHolder>() {

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
        holder.bind(user)
        holder.itemView.setOnClickListener {
            mListener!!.onItemChatClick(context, user)
        }
    }
}


class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var userName = view.userNameChat

    fun bind(user: User) {
        userName.text = user.userName
    }
}
package com.haag.superchat.ui.chat.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haag.superchat.R
import com.haag.superchat.model.User
import kotlinx.android.synthetic.main.search_list_item.view.*

class SearchAdapter(
    private val list: List<User>,
    private val context: Context,
    private val mListener: OnItemClickListener?
) :
    RecyclerView.Adapter<SearchViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(context: Context, user: User)
    }


    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.search_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user: User = list[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            mListener!!.onItemClick(context, user)
        }
    }
}


class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var userName = view.userNameTxt
    private var email = view.emailTxt

    fun bind(user: User) {
        userName.text = user.userName
        email.text = user.email
    }
}
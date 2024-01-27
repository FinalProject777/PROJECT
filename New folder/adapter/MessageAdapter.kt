package com.example.loginpage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpage.R
import com.example.loginpage.messagedata

// Assuming you have a custom adapter for your RecyclerView
class MessageAdapter(private val messages: List<messagedata>) :
        RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

        // ViewHolder for each message item
        class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message, parent, false)
                return MessageViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
                val message = messages[position]
                holder.messageTextView.text = "${message.sender}: ${message.content}"
        }

        override fun getItemCount(): Int {
                return messages.size
        }
}

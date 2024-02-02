package com.example.loginpage.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpage.R
import com.example.loginpage.messagedata
import com.google.firebase.database.DatabaseReference

class MessageAdapter(
        private var messages: MutableList<messagedata>,
        private val context: Context,

        private val messageReference: DatabaseReference, // Add this parameter
        private val contextMenuListener: ContextMenuListener?// Add this parameter
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

        var selectedPosition: Int = -1
        var messageAdapter: MessageAdapter? = null



        // Initialize messageAdapter in the constructor
        init {
                messageAdapter = this
        }

        interface ContextMenuListener {
                fun onContextMenuClick(view: View, position: Int, menuId: Int)
        }




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

                holder.itemView.setOnLongClickListener {
                        // Handle long-click here
                        showOptionsDialog(position)
                        true
                }
        }

        override fun getItemCount(): Int {
                return messages.size
        }

        // Update messages with List<messagedata>
        fun updateMessages(newMessages:  MutableList<messagedata>) {
                this.messages = newMessages
                notifyDataSetChanged()
        }

        private fun showOptionsDialog(position: Int) {
                val options = arrayOf("Delete", "Copy")

                AlertDialog.Builder(context)
                        .setTitle("Options")
                        .setItems(options) { dialog, which ->
                                when (which) {
                                        0 -> deleteMessage(position)
                                        1 -> copyMessage(position)
                                }
                        }
                        .show()
        }

        private fun deleteMessage(position: Int) {
                if (position != -1) {
                        val selectedMessage = messages[position]
                        val messageKey = selectedMessage.key

                        // Remove the message from Firebase
                        messageReference.child(messageKey).removeValue()
                                .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                                Log.d("DeleteMessage", "Message deleted successfully from Firebase")

                                                // Check if position is within bounds before removing
                                                if (position in 0 until messages.size) {
                                                        // Remove the message from the local list and notify the adapter
                                                        messages.removeAt(position)
                                                        messageAdapter?.notifyItemRemoved(position)

                                                        // Reset selectedPosition after deletion
                                                        selectedPosition = -1
                                                } else {
                                                        Log.e("DeleteMessage", "Invalid position: $position, Messages size: ${messages.size}")
                                                }
                                        } else {
                                                Log.e("DeleteMessage", "Error deleting message from Firebase: ${task.exception?.message}")
                                        }
                                }
                }
        }



        private fun copyMessage(position: Int) {
                // Implement copy logic
                // For example, you might want to copy the message content to the clipboard
                val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData =
                        ClipData.newPlainText("Message", messages[position].content)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(context, "Message copied to clipboard", Toast.LENGTH_SHORT).show()
        }
}

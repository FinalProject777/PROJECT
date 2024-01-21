package com.example.dbuser

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.dbuser.databinding.ActivityMainBinding
import com.google.firebase.database.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var messageReference: DatabaseReference
    private lateinit var binding: ActivityMainBinding
    private lateinit var messages: MutableList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DebugTag", "onCreate is called ------------------------------------------------")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        messageReference = database.reference.child("messages")

        messages = mutableListOf()

        binding.sendButton.setOnClickListener {
            val messageText = binding.messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
            }
        }

        // Set up a listener to display messages
        messageReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for (dataSnapshot in snapshot.children) {
                    val message = dataSnapshot.getValue(Message::class.java)
                    message?.let {
                        it.key = dataSnapshot.key ?: ""
                        messages.add(it)
                    }
                }
                displayMessages(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    private fun sendMessage(content: String) {
        val message = Message("Anonymous", content, System.currentTimeMillis())
        messageReference.push().setValue(message)
        binding.messageEditText.text.clear()
    }

    private fun displayMessages(messages: List<Message>) {
        val messageStringBuilder = StringBuilder()

        for (message in messages) {
            val formattedMessage = "${message.sender}: ${message.content}\n"
            messageStringBuilder.append(formattedMessage)
        }

        // Set the formatted message to the TextView
        binding.messageTextView.text = messageStringBuilder.toString()

        // Set clickable spans for individual messages
        val spannableText = SpannableString(messageStringBuilder.toString())

        for (message in messages) {
            val start = messageStringBuilder.indexOf("${message.sender}: ${message.content}")
            val end = start + "${message.sender}: ${message.content}".length

            if (start != -1) {
                spannableText.setSpan(object : ClickableSpan() {
                    override fun onClick(view: View) {
                        handleClick(message)
                    }
                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        // Make the TextView clickable
        binding.messageTextView.text = spannableText
        binding.messageTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun handleClick(message: Message) {
        deleteMessage(message)
    }

    private fun deleteMessage(message: Message) {
        try {
            Log.d("DeleteMessage", "Deleting message: $message")

            val messageReferenceToDelete = messageReference.child(message.key)
            messageReferenceToDelete.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("DeleteMessage", "Message deleted successfully")
                } else {
                    Log.e("DeleteMessage", "Error deleting message: ${task.exception?.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("DeleteMessage", "Error deleting message", e)
        }
    }
}

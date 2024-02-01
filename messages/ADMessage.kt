package com.example.loginpage

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpage.adapter.MessageAdapter
import com.example.loginpage.databinding.ActivityAdmessageBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ADMessage : AppCompatActivity(), MessageAdapter.ContextMenuListener {
    private lateinit var messages: MutableList<messagedata>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var messageReference: DatabaseReference
    private lateinit var binding: ActivityAdmessageBinding
    private lateinit var recyclerView: RecyclerView

    private var selectedPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdmessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        val imageButton: ImageButton = findViewById(R.id.imageButton)
        recyclerView = findViewById(R.id.chat_recycler_view)
        val database = FirebaseDatabase.getInstance()

        // Initialize messageReference here
        messageReference = database.reference.child("messages")

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Initialize messages here
        messages = mutableListOf()

        // Initialize messageAdapter here
        messageAdapter = MessageAdapter(messages, this, messageReference, this)
        recyclerView.adapter = messageAdapter
        // Register the RecyclerView for context menu
        registerForContextMenu(recyclerView)

        recyclerView.post {
            recyclerView.scrollToPosition(messages.size - 1)
        }

        binding.messageSendBtn.setOnClickListener {
            val messageText = binding.chatMessageInput.text.toString().trim()
            Log.d("ButtonClicked", "Button clicked. Message: $messageText")

            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
            }
        }

        messageReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newMessages = mutableListOf<messagedata>()

                for (dataSnapshot in snapshot.children) {
                    val message = dataSnapshot.getValue(messagedata::class.java)
                    message?.let {
                        it.key = dataSnapshot.key ?: ""
                        newMessages.add(it)
                    }
                }

                messages.clear()
                messages.addAll(newMessages)
                displayMessages(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // Your navigation item handling
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        imageButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun sendMessage(content: String) {
        val message = messagedata("Anonymous", content, System.currentTimeMillis())
        messageReference.push().setValue(message).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("SendMessage", "Message sent successfully")
            } else {
                Log.e("SendMessage", "Error sending message: ${task.exception?.message}")
            }
        }
        binding.chatMessageInput.text.clear()
    }

    private fun displayMessages(messages: MutableList<messagedata>) {
        messageAdapter.updateMessages(messages)
    }

    override fun onCreateContextMenu(contextMenu: ContextMenu?, view: View?, contextMenuInfo: ContextMenu.ContextMenuInfo?) {
        Log.d("MessageAdapter", "onCreateContextMenu called")
        contextMenu?.add(Menu.NONE, R.id.menu_delete, Menu.NONE, "Delete")
        contextMenu?.add(Menu.NONE, R.id.menu_copy, Menu.NONE, "Copy")
    }

    override fun onContextMenuClick(view: View, position: Int, menuId: Int) {
        when (menuId) {
            R.id.menu_delete -> {
                // Implement delete logic
                deleteMessage(position)
            }
            R.id.menu_copy -> {
                // Implement copy logic
                copyMessage(position)
            }
        }
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

                        // Remove the message from the local list and notify the adapter
                        messages.removeAt(position)
                        messageAdapter.notifyItemRemoved(position)

                        // Reset selectedPosition after deletion
                        selectedPosition = -1
                    } else {
                        Log.e("DeleteMessage", "Error deleting message from Firebase: ${task.exception?.message}")
                    }
                }
        }
    }


    private fun copyMessage(position: Int) {
        if (position != -1) {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Message", messages[position].content)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Message copied to clipboard", Toast.LENGTH_SHORT).show()
            selectedPosition = -1
        }
    }
}

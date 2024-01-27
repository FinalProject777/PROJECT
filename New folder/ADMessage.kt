package com.example.loginpage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpage.databinding.ActivityAdmessageBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import android.text.Selection
import com.example.loginpage.adapter.MessageAdapter


class ADMessage : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var messageReference: DatabaseReference
    private lateinit var binding: ActivityAdmessageBinding
    private lateinit var messages: MutableList<messagedata>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdmessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        val imageButton: ImageButton = findViewById(R.id.imageButton)
        val recyclerView: RecyclerView = findViewById(R.id.chat_recycler_view)
        val database = FirebaseDatabase.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(this)
        messageReference = database.reference.child("messages")

        messages = mutableListOf()

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
                R.id.admenu_item_home -> {
                    val intent = Intent(this,ADHomeActivity::class.java)
                    startActivity(intent)
                }
                R.id.admenu_item_attendance -> {
                    val intent = Intent(this, ADAttendance::class.java)
                    startActivity(intent)
                }
                R.id.admenu_item_messages -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.admenu_item_internals -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.admenu_item_logout -> {
                    clearAdminLoginStatus()
                    val intent = Intent(this,Adminmain::class.java)
                    startActivity(intent)
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        imageButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        messageReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<messagedata>()
                for (dataSnapshot in snapshot.children) {
                    val message = dataSnapshot.getValue(messagedata::class.java)
                    message?.let {
                        it.key = dataSnapshot.key ?: ""
                        messages.add(it)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })


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


    private fun displayMessages(messages: List<messagedata>) {
        val messageStringBuilder = StringBuilder()

        for (message in messages) {
            val formattedMessage = "${message.sender}: ${message.content}\n"
            messageStringBuilder.append(formattedMessage)
        }

        val spannableText = SpannableString(messageStringBuilder.toString())

        // Make the entire text clickable
        spannableText.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                // Handle click on the entire text if needed
            }
        }, 0, spannableText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Convert SpannableString to CharSequence
        val charSequence: CharSequence = spannableText

        // Display the messages in the TextView
        val recyclerView: RecyclerView = findViewById(R.id.chat_recycler_view)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val messageAdapter = MessageAdapter(messages)
        recyclerView.adapter = messageAdapter
    }


    private fun handleClick(message: messagedata) {
        deleteMessage(message)
    }

    private fun deleteMessage(message:   messagedata) {
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

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {


        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerLayout.closeDrawer(GravityCompat.START)

        }
        else {
            val intent = Intent(this,ADHomeActivity::class.java)
            startActivity(intent)

        }
    }

    private fun clearAdminLoginStatus() {
        markAdminLoggedIn(false)
    }

    private fun markAdminLoggedIn(isLoggedIn: Boolean) {
        val file = File(filesDir, "login_status_ad.txt")
        file.writeText(isLoggedIn.toString())
    }

}

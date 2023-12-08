package com.example.loginpage

import ChatAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import android.widget.ImageButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

class ADMessage : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var messagingPortalManager: MessagingPortalManager
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admessage)
        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        val imageButton: ImageButton = findViewById(R.id.imageButton)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        chatAdapter = ChatAdapter()

        // Assuming you have a LinearLayoutManager for your RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.admenu_item_home -> {
                    val intent = Intent(this, ADHomeActivity::class.java)
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
                    val intent = Intent(this,adminmain::class.java)
                    startActivity(intent)
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }// Other code...

        imageButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Other code...
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val homeIntent = Intent(this, HomeActivity::class.java)
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(homeIntent)
            finish()
        }
    }

    private fun sendMessage() {
        val editTextMessage: EditText = findViewById(R.id.editTextMessage)
        val message = editTextMessage.text.toString().trim()
        chatAdapter.addMessage(ChatMessage("senderId", message, System.currentTimeMillis()))
    }

    // Call this function when you want to send a message
    private fun setupSendMessageButton() {
        val buttonSendMessage: Button = findViewById(R.id.buttonSendMessage)

        buttonSendMessage.setOnClickListener {
            sendMessage()
        }
    }
}

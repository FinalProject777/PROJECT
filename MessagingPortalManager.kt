package com.example.loginpage

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MessagingPortalManager(private val context: AppCompatActivity) {
    fun openMessagingPortal() {
        Toast.makeText(context, "Open Messaging Portal", Toast.LENGTH_SHORT).show()
    }
}
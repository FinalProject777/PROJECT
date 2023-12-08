package com.example.loginpage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import android.widget.Toast
import androidx.core.view.GravityCompat
import android.widget.ImageButton
import androidx.drawerlayout.widget.DrawerLayout


class Message : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        val imageButton: ImageButton = findViewById(R.id.imageButton)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item clicks here
            when (menuItem.itemId) {
                R.id.menu_item_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                R.id.menu_item_contact_us -> {
                    val intent = Intent(this, ContactActivity::class.java)
                    startActivity(intent)
                }
                R.id.menu_item_attendance -> {
                    val intent = Intent(this, Attendance::class.java)
                    startActivity(intent)
                }
                R.id.menu_item_internals -> {
                    val intent = Intent(this, Internals::class.java)
                    startActivity(intent)
                }
                R.id.menu_item_messages -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.menu_item_logout -> {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
            }
            // Close the drawer after handling the click
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        imageButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    @Deprecated("Deprecated in Java")
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
}

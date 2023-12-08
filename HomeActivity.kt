package com.example.loginpage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import android.widget.ImageButton

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
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

            when (menuItem.itemId) {
                R.id.menu_item_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
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
                R.id.menu_item_contact_us -> {
                    val intent = Intent(this, ContactActivity::class.java)
                    startActivity(intent)
                }
                R.id.menu_item_logout -> {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        imageButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

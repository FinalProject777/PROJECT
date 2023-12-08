package com.example.loginpage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ADHomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adhome)
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
                R.id.admenu_item_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.admenu_item_attendance -> {
                    val intent = Intent(this, ADAttendance::class.java)
                    startActivity(intent)
                }
                R.id.admenu_item_internals -> {
                    val intent = Intent(this, ADInternals::class.java)
                    startActivity(intent)
                }
                R.id.admenu_item_messages -> {
                    val intent = Intent(this, ADMessage::class.java)
                    startActivity(intent)
                }
                R.id.admenu_item_logout -> {
                    val intent = Intent(this, adminmain::class.java)
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

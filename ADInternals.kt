package com.example.loginpage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import android.widget.ImageButton
import androidx.drawerlayout.widget.DrawerLayout

@Suppress("DEPRECATION")
class ADInternals : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adinternals)
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
                    val intent = Intent(this, ADHomeActivity::class.java)
                    startActivity(intent)
                }
                R.id.admenu_item_attendance -> {
                    val intent = Intent(this, ADAttendance::class.java)
                    startActivity(intent)
                }
                R.id.admenu_item_messages -> {
                    val intent = Intent(this, ADMessage::class.java)
                    startActivity(intent)
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
        }
        else {
            val homeIntent = Intent(this, HomeActivity::class.java)
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(homeIntent)
            finish()
        }
    }
}

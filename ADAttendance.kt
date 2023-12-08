package com.example.loginpage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout

class ADAttendance : AppCompatActivity() {

    val PICK_FILE_REQUEST_CODE = 1
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var uploadButton: Button
    private lateinit var uploadStatusTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adattendance)
        drawerLayout = findViewById(R.id.drawerLayout)
        uploadButton = findViewById(R.id.uploadButton)
        uploadStatusTextView = findViewById(R.id.uploadStatusTextView)
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        val imageButton: ImageButton = findViewById(R.id.imageButton)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/vnd.ms-excel*"  // Set the type of files you want to allow (e.g., image/*, audio/*, etc.)
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.admenu_item_home -> {
                    val intent = Intent(this, ADHomeActivity::class.java)
                    startActivity(intent)
                }
                R.id.admenu_item_attendance -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.admenu_item_messages -> {
                    val intent = Intent(this, ADMessage::class.java)
                    startActivity(intent)
                }
                R.id.admenu_item_internals -> {
                    val intent = Intent(this, ADInternals::class.java)
                    startActivity(intent)
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
        uploadButton.setOnClickListener {
            openFileManager()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val homeIntent = Intent(this, HomeActivity::class.java)
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(homeIntent)
            finish()
        }
        super.onBackPressed()
    }
    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/vnd.ms-excel*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                // Handle the selected file URI
                val filePath = getRealPathFromURI(uri)
                // Check if the file is an Excel file
                if (isExcelFile(filePath)) {
                    // Upload the Excel file to Firebase Realtime Database
                    uploadExcelToFirebase(filePath)
                } else {
                    Toast.makeText(this, "Invalid file format. Please select an Excel file.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val filePath = cursor?.getString(columnIndex ?: 0) ?: ""
        cursor?.close()
        return filePath
    }
    private fun isExcelFile(filePath: String): Boolean {
        return filePath.endsWith(".xls") || filePath.endsWith(".xlsx")
    }

    private fun uploadExcelToFirebase(filePath: String) {
        // Implement file upload logic to Firebase Realtime Database here
        // Use Firebase Storage to upload the file, and save the download URL to the Realtime Database
        // You may need to create a Firebase Storage reference and use putFile to upload the file
    }
    private fun showUploadSuccessMessage() {
        uploadStatusTextView.text = "File uploaded successfully!"
        uploadStatusTextView.visibility = View.VISIBLE
    }
}

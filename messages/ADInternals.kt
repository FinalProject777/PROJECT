package com.example.loginpage

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.database.FirebaseDatabase
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale


@Suppress("DEPRECATION")
class ADInternals : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adinternals)
        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        val imageButton: ImageButton = findViewById(R.id.imageButton)
        val uploadButton: Button = findViewById(R.id.upbtn)
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
                    clearAdminLoginStatus()
                    val intent = Intent(this, Adminmain::class.java)
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
            pickExcelFile.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val pickExcelFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                if (isExcelFile(it)) {
                    val inputStream: InputStream? = contentResolver.openInputStream(it)
                    if (inputStream != null) {
                        readAndUploadData(inputStream)
                    } else {
                        showToast("Error opening file")
                    }
                } else {
                    showToast("Selected file is not an Excel file")
                }
            }
        }

    private fun isExcelFile(uri: Uri): Boolean {
        val type = contentResolver.getType(uri)
        return type == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    }

    private fun readAndUploadData(inputStream: InputStream) {
        val dataList = readAttendanceFromExcel(inputStream)
        uploadDataToFirebase(dataList)
    }

    private fun readAttendanceFromExcel(inputStream: InputStream): List<Map<String, Any>> {
        val workbook: Workbook = XSSFWorkbook(inputStream)
        val sheet: Sheet = workbook.getSheetAt(0)

        val dataList = mutableListOf<Map<String, Any>>()

        for (i in 1 until sheet.physicalNumberOfRows) {
            val row: Row = sheet.getRow(i)

            // Check if any required cell is null in the row
            val regNo = getCellValueAsString(row.getCell(0))
            val cat1 = getCellValueAsString(row.getCell(1))
            val cat2 = getCellValueAsString(row.getCell(2))
            val cat = getCellValueAsString(row.getCell(3))
            val model = getCellValueAsString(row.getCell(4))
            val attendance =getCellValueAsString(row.getCell(5))

            if (regNo.isNotEmpty() && cat1.isNotEmpty() && cat2.isNotEmpty() && cat.isNotEmpty() && model.isNotEmpty() && attendance.isNotEmpty()) {
                val dataMap = mapOf(
                    "REGNO" to regNo,
                    "CAT-I" to cat1,
                    "CAT-II" to cat2,
                    "CAT-best" to cat,
                    "MODEL" to model ,
                    "ATTENDANCE" to attendance
                )
                dataList.add(dataMap)
            }
        }
        Log.d(ContentValues.TAG, "readinternalFromExcel: $dataList")
        workbook.close()
        return dataList
    }



    private fun getCellValueAsString(cell: Cell?): String {
        return when (cell?.cellType) {
            CellType.NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    val date = DateUtil.getJavaDate(cell.numericCellValue)
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                } else {
                    cell.numericCellValue.toString()
                }
            }
            CellType.STRING -> cell.stringCellValue
            CellType.BLANK, CellType._NONE -> ""
            else -> "null"
        }
    }

    private fun uploadDataToFirebase(dataList: List<Map<String, Any>>) {
        // Get a reference to the "attendance" node in the Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("internals")

        // Loop through the dataList and push each item to the database
        for (dataMap in dataList) {
            // Get the "regno" from the dataMap
            val regNo = dataMap["REGNO"].toString()

            // Use "regno" as the key under the "attendance" node
            val regNoRef = databaseReference.child(regNo)

            // Set the values under the "regno" key
            regNoRef.setValue(dataMap)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Data uploaded to Firebase successfully!")
                }
                .addOnFailureListener {
                    Log.e(ContentValues.TAG, "Error uploading data to Firebase: $it")
                    showToast("Error uploading data to Firebase")
                }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun clearAdminLoginStatus() {
        markAdminLoggedIn(false)
    }

    private fun markAdminLoggedIn(isLoggedIn: Boolean) {
        val file = File(filesDir, "login_status_ad.txt")
        file.writeText(isLoggedIn.toString())
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val intent = Intent(this, ADHomeActivity::class.java)
            startActivity(intent)
        }
    }
}

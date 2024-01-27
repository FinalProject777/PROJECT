package com.example.loginpage

data class messagedata @JvmOverloads constructor(

    val sender: String = "",
    val content: String = "",
    val timestamp: Long = 0,
    var key: String = ""
)
package com.example.myapplication.data

import java.io.Serializable
import java.util.Date



data class Task(
    val id:Int,
    val title:String,
    val isCompleted:Boolean = false,
    val deadline: Date? = null,
) : Serializable

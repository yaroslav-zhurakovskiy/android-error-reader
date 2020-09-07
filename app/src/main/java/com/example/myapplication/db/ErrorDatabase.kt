package com.example.myapplication.db

interface ErrorDatabase {
    fun findErrorDetailsByCode(errorCode: String): ErrorDetails?
}

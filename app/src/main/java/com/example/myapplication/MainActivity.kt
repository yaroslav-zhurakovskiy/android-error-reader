package com.example.myapplication

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.myapplication.db.ErrorDatabase
import com.example.myapplication.db.ErrorDetails
import com.example.myapplication.db.XmlErrorDatabase
import kotlin.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var errorCodeField: EditText
    private lateinit var infoView: TextView

    private lateinit var storage: ErrorDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        connectUI()

//        storage = InMemoryErrorDatabase()
        storage = XmlErrorDatabase(resources.openRawResource(R.raw.db))
    }

    private fun connectUI() {
        errorCodeField = findViewById(R.id.errorCodeField)
        infoView = findViewById(R.id.infoView)

        findViewById<Button>(R.id.searchButton).setOnClickListener(this::handleSearch)
    }

    private fun handleSearch(sender: View) {
        val errorCodeInput = errorCodeField.text.toString()

        if (errorCodeInput.isBlank()) {
            Toast.makeText(this, "Please, enter error code!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val errorDetails = storage.findErrorDetailsByCode(errorCodeInput)
            infoView.text = if (errorDetails == null)
                "Could not find anything :("
            else
                Html.fromHtml(buildErrorDetailsInfoString(errorDetails), HtmlCompat.FROM_HTML_MODE_LEGACY)
        } catch (error: Exception) {
            infoView.text = error.message
        }
    }

    private fun buildErrorDetailsInfoString(errorDetails: ErrorDetails) = buildString {
        append("<b>Description:</b>")
        append("<p>${errorDetails.description}</p>")
        append("<b>How to Fix:</b>")
        append("<ol>")
        for (step in errorDetails.fixSteps) {
            append("<li>$step</li>")
        }
        append("</ol>")
    }
}
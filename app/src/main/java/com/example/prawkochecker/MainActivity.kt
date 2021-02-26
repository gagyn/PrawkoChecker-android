package com.example.prawkochecker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val subscribingService = SubscribingService()
    private val stateSavingService = StateSavingService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onButtonAddPkkDataClicked(view: View) {
        val name = findViewById<EditText>(R.id.editTextName).text.toString()
        val surname = findViewById<EditText>(R.id.editTextSurname).text.toString()
        val pkkNumber = findViewById<EditText>(R.id.editTextPkkNumber).text.toString()
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val pkkData = PkkData(name, surname, pkkNumber, email)

        hideKeyboard(view)
        GlobalScope.launch(Dispatchers.Main) {
            subscribingService.subscribe(view, pkkData)
            stateSavingService.writePkkData(pkkData)
        }
    }

    fun onButtonUnsubscribedClick(view: View) {
        val pkkNumber = findViewById<EditText>(R.id.editTextPkkNumber).text.toString()

        hideKeyboard(view)
        GlobalScope.launch(Dispatchers.Main) {
            subscribingService.unsubscribe(view, pkkNumber)
            stateSavingService.clearPkkData(pkkNumber)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
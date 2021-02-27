package com.example.prawkochecker

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val subscribingService = SubscribingService()
    private val stateSavingService = StateSavingService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        val context = this
        GlobalScope.launch(Dispatchers.Main) {
            val pkkData = stateSavingService.readPkkData()
            findViewById<EditText>(R.id.editTextName).setText(pkkData.name)
            findViewById<EditText>(R.id.editTextSurname).setText(pkkData.surname)
            findViewById<EditText>(R.id.editTextPkkNumber).setText(pkkData.pkkNumber)
            findViewById<EditText>(R.id.editTextEmail).setText(pkkData.email)

            val status = subscribingService.getCurrentStatus(context, pkkData.pkkNumber)
            findViewById<TextView>(R.id.textViewStatus).text = status
        }
    }

    fun onButtonAddPkkDataClicked(view: View) {
        val name = findViewById<EditText>(R.id.editTextName).text.toString()
        val surname = findViewById<EditText>(R.id.editTextSurname).text.toString()
        val pkkNumber = findViewById<EditText>(R.id.editTextPkkNumber).text.toString()
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val pkkData = PkkData(name, surname, pkkNumber, email)

        hideKeyboard(view)
        GlobalScope.launch(Dispatchers.Main) {
            val wasSuccess = subscribingService.subscribe(view, pkkData)
            if (wasSuccess) {
                stateSavingService.writePkkData(pkkData)
            }
        }
    }

    fun onButtonUnsubscribedClick(view: View) {
        val pkkNumber = findViewById<EditText>(R.id.editTextPkkNumber).text.toString()

        hideKeyboard(view)
        GlobalScope.launch(Dispatchers.Main) {
            val wasSuccess = subscribingService.unsubscribe(view, pkkNumber)
            if (wasSuccess) {
                stateSavingService.clearPkkData()
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
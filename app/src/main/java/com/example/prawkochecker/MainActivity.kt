package com.example.prawkochecker

import android.content.Context
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
    private val resources = ResourceHelper();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onButtonAddPkkDataClicked(view: View) {
        hideKeyboard(view)
        GlobalScope.launch(Dispatchers.Main) { addPkkData(view) }
    }

    fun onButtonUnsubscribedClick(view: View) {
        hideKeyboard(view)
        GlobalScope.launch(Dispatchers.Main) { unsubscribe(view) }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private suspend fun addPkkData(view: View) {
        val name = findViewById<EditText>(R.id.editTextName).text
        val surname = findViewById<EditText>(R.id.editTextSurname).text
        val pkkNumber = findViewById<EditText>(R.id.editTextPkkNumber).text
        val email = findViewById<EditText>(R.id.editTextEmail).text
        val clientId = FirebaseMessaging.getInstance().token.await()

        val urlBuilder = getDefaultUrlBuilder()
        urlBuilder.append("?pkk=${pkkNumber}")
        urlBuilder.append("&name=${name}")
        urlBuilder.append("&surname=${surname}")
        urlBuilder.append("&androidClientId=${clientId}")
        if (!email.isNullOrBlank()) {
            urlBuilder.append("&email=${email}")
        }
        val result = httpGet(urlBuilder.toString())
        val text = if (result) "Pomyślnie dodano numer PKK" else "Wystąpił problem lub dane są niepoprawne"
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
    }

    private suspend fun unsubscribe(view: View) {
        val pkkNumber = findViewById<EditText>(R.id.editTextPkkNumber).text

        val urlBuilder = getDefaultUrlBuilder()
        urlBuilder.append("?pkk=${pkkNumber}")

        val result = httpGet(urlBuilder.toString())
        val text = if (result) "Pomyślnie usunięto numer PKK" else "Wystąpił problem lub numer PKK jest niepoprawny"
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
    }

    private fun getDefaultUrlBuilder(): StringBuilder {
        val apiUrl = resources.getConfigValue(this, "api_url")
        return StringBuilder("${apiUrl}/checking/subscribe")
    }

    private suspend fun httpGet(url: String): Boolean {
        return withContext(Dispatchers.IO) {
            val urlObj: URL = URL(url)

            val conn: HttpURLConnection = urlObj.openConnection() as HttpURLConnection
            conn.connect()
            conn.responseCode == 200
        }
    }
}
package com.example.prawkochecker

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class SubscribingService {
    private val resources = ResourceHelper()

    suspend fun subscribe(view: View, name: String, surname: String, pkkNumber: String, email: String) {
        val clientId = FirebaseMessaging.getInstance().token.await()

        val urlBuilder = getDefaultUrlBuilder(view.context)
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

    suspend fun unsubscribe(view: View, pkkNumber: String) {
        val urlBuilder = getDefaultUrlBuilder(view.context)
        urlBuilder.append("?pkk=${pkkNumber}")

        val result = httpGet(urlBuilder.toString())
        val text = if (result) "Pomyślnie usunięto numer PKK" else "Wystąpił problem lub numer PKK jest niepoprawny"
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
    }

    private fun getDefaultUrlBuilder(context: Context): StringBuilder {
        val apiUrl = resources.getConfigValue(context, "api_url")
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
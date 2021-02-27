package com.example.prawkochecker

import android.content.Context
import android.view.View
import com.beust.klaxon.Klaxon
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class StatusResponse(val value: String, val description: String, val name: String) {
}

class SubscribingService {
    private val resources = ResourceHelper()

    suspend fun subscribe(view: View, pkkData: PkkData): Boolean {
        val clientId = FirebaseMessaging.getInstance().token.await()

        val urlBuilder = getDefaultUrlBuilder(view.context)
            .append("subscribe")
            .append("?pkk=${pkkData.pkkNumber}")
            .append("&name=${pkkData.name}")
            .append("&surname=${pkkData.surname}")
            .append("&androidClientId=${clientId}")

        if (pkkData.email.isNotBlank()) {
            urlBuilder.append("&email=${pkkData.email}")
        }
        val wasSuccess = makeHttpGetRequest(urlBuilder.toString()).first
        val text = if (wasSuccess) "Pomyślnie dodano numer PKK" else "Wystąpił problem lub dane są niepoprawne"
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
        return wasSuccess
    }

    suspend fun unsubscribe(view: View, pkkNumber: String): Boolean {
        val urlBuilder = getDefaultUrlBuilder(view.context)
            .append("unsubscribe")
            .append("?pkk=${pkkNumber}")

        val wasSuccess = makeHttpGetRequest(urlBuilder.toString()).first
        val text = if (wasSuccess) "Pomyślnie usunięto numer PKK" else "Wystąpił problem lub numer PKK jest niepoprawny"
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
        return wasSuccess
    }

    suspend fun getCurrentStatus(context: Context, pkkNumber: String): String {
        if (pkkNumber.isNullOrBlank()) {
            return ""
        }
        val urlBuilder = getDefaultUrlBuilder(context)
            .append("current")
            .append("?pkk=${pkkNumber}")

        val result = makeHttpGetRequest(urlBuilder.toString())
        if (!result.first) {
            return ""
        }
        return result.second ?: ""
    }

    private fun getDefaultUrlBuilder(context: Context): StringBuilder {
        val apiUrl = resources.getConfigValue(context, "api_url")
        return StringBuilder("${apiUrl}/checking/")
    }

    private suspend fun makeHttpGetRequest(url: String): Pair<Boolean, String?> {
        return withContext(Dispatchers.IO) {
            val urlObj: URL = URL(url);
            val conn: HttpURLConnection = urlObj.openConnection() as HttpURLConnection;
            conn.connect();
            val responseText = urlObj.readText();
            val json = Klaxon()
                .parse<StatusResponse>(responseText);
            Pair(conn.responseCode == 200, json?.description);
        }
    }
}

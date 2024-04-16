package com.example.qrcodes.api

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import com.example.qrcodes.entity.MarkStudentOnPairRequest
import com.example.qrcodes.entity.MarkedPairsResponse
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException


class StudentService {

    fun tryMarkStudent(request: MarkStudentOnPairRequest): Boolean {
        return runCatching {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val client = OkHttpClient()

            var marked = false

            val url = BACKEND_URL + "api/v1/note"
            val body =
                RequestBody.create(MediaType.parse("application/json"), Gson().toJson(request))

            val httpRequest = Request.Builder()
                .url(url)
                .patch(body)
                .build()

            client.newCall(httpRequest).execute().use { response ->
                if (response.isSuccessful)
                    marked = true
            }
            marked
        }.getOrNull() ?: false
    }

    fun getAllMarkedPairs(studentId: String): List<String> {
        return runCatching {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val client = OkHttpClient()

            val url = BACKEND_URL + "api/v1/note/student/${studentId}"

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).execute().let { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                mapObjectFromGson(response.body()?.string())?.pairs
            }
        }.getOrNull() ?: emptyList()
    }

    private fun mapObjectFromGson(source: String?): MarkedPairsResponse? {
        if (source == null) return null
        return Gson().fromJson(source, MarkedPairsResponse::class.java)
    }
}



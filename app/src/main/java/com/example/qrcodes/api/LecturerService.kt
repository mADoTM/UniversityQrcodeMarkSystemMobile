package com.example.qrcodes.api

import android.os.StrictMode
import com.example.qrcodes.entity.AllPairsByLecturerIdResponse
import com.example.qrcodes.entity.CreatePairRequest
import com.example.qrcodes.entity.CreatePairResponse
import com.example.qrcodes.entity.MarkedPairsResponse
import com.example.qrcodes.entity.MarkedStudentsResponse
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LecturerService {

    fun createPair(request: CreatePairRequest): String? {
        return runCatching {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val client = OkHttpClient()
            var createResponse: CreatePairResponse? = null

            val url = BACKEND_URL + "api/v1/pair"
            val body =
                RequestBody.create(MediaType.parse("application/json"), Gson().toJson(request))

            val httpRequest = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(httpRequest).execute().use { response ->
                if (response.isSuccessful) {
                    createResponse =
                        Gson().fromJson(response.body()?.string(), CreatePairResponse::class.java)
                }
            }

            if (createResponse?.pairId == null) {
                return null
            }

            "${BACKEND_URL}api/v1/qr/${request.lecturerId}/${createResponse!!.pairId}"
        }.getOrNull()
    }

    fun getAllPairs(lecturerId: String): List<String?> {
        return runCatching {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val client = OkHttpClient()

            val url = BACKEND_URL + "api/v1/pair/${lecturerId}"

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).execute().let { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                Gson()
                    .fromJson(
                        response.body()!!.string(),
                        AllPairsByLecturerIdResponse::class.java
                    )?.pairs
            }
        }.getOrNull() ?: emptyList()
    }

    fun getAllStudents(pairId: String): List<String?> {
        return runCatching {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val client = OkHttpClient()

            val url = BACKEND_URL + "api/v1/note/students/${pairId}"

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).execute().let { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                mapObjectFromGson(response.body()!!.string())?.students
            }
        }.getOrNull() ?: emptyList()
    }

    private fun mapObjectFromGson(source: String?): MarkedStudentsResponse? {
        if (source == null) return null
        return Gson().fromJson(source, MarkedStudentsResponse::class.java)
    }
}
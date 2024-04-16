package com.example.qrcodes

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodes.api.StudentService
import com.example.qrcodes.entity.MarkStudentOnPairRequest
import com.example.qrcodes.entity.QrCodePairResponse
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator


class StudentActivity : AppCompatActivity() {

    private var studentService = StudentService()
    private var btnMarkAttendance: Button? = null

    private var studentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.qrcodes.R.layout.activity_student)

        btnMarkAttendance = findViewById(com.example.qrcodes.R.id.btnMarkAttendance)
        studentId = intent.getStringExtra("name")

        updateMarkedPairs()

        btnMarkAttendance?.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setDesiredBarcodeFormats(listOf(IntentIntegrator.QR_CODE))
            intentIntegrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        if (result != null) {
            val response = mapObjectFromGson(result.contents)
            val request = MarkStudentOnPairRequest(
                studentId = studentId,
                pairId = response.pairId,
                secret = response.secret
            )
            val marked = studentService.tryMarkStudent(request)

            if (marked) {
                Toast.makeText(this, "Вы успешно отметились на занятии", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Не удалось отметить ваше присутствие на паре", Toast.LENGTH_SHORT).show()
            }
            updateMarkedPairs()
        }
    }

    private fun updateMarkedPairs() {
        val pairs = studentService.getAllMarkedPairs(studentId!!)
        val listView = findViewById<ListView>(com.example.qrcodes.R.id.listView)
        val adapter = ArrayAdapter(
            this,
            R.layout.simple_list_item_1, pairs
        )
        listView.adapter = adapter
    }

    private fun mapObjectFromGson(source: String) =
        Gson().fromJson(source, QrCodePairResponse::class.java)
}
package com.example.qrcodes

import android.R
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.example.qrcodes.api.LecturerService
import com.example.qrcodes.entity.CreatePairRequest
import java.time.LocalDateTime


class LecturerActivity : Activity() {

    private var startTimeEditText: EditText? = null
    private var endTimeEditText: EditText? = null
    private var pairNameEditText: EditText? = null
    private var createButton: Button? = null

    private val lecturerService = LecturerService()

    private var lecturerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.qrcodes.R.layout.activity_lecturer)

        startTimeEditText = findViewById(com.example.qrcodes.R.id.startTimeEditText)
        endTimeEditText = findViewById(com.example.qrcodes.R.id.endTimeEditText)
        pairNameEditText = findViewById(com.example.qrcodes.R.id.pairNameEditText)
        createButton = findViewById(com.example.qrcodes.R.id.createButton)

        lecturerId = intent.getStringExtra("name")

        updateCreatePairs()
        createButton?.setOnClickListener {
            runCatching {
                val pairName = pairNameEditText?.text.toString()

                if (pairName.isEmpty()) {
                    throw IllegalArgumentException("Не введено имя пары")
                }

                val startTime = LocalDateTime.parse(
                    startTimeEditText?.text.toString()
                )
                val endTime = LocalDateTime.parse(
                    endTimeEditText?.text.toString()
                )

                CreatePairRequest(
                    lecturerId,
                    startTimeEditText?.text.toString(),
                    endTimeEditText?.text.toString(),
                    pairName
                )
            }.onFailure {
                val message = if (it is java.lang.IllegalArgumentException) "Не введено имя пары"
                else "Некорретный формат даты"
                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }.onSuccess {
                val url = lecturerService.createPair(it)

                if (url != null) {
                    copyToClipboard(this, url)
                    Toast.makeText(
                        this,
                        "Пара успешно создана. Ссылка на QR-код добавлена в буфер обмена",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateCreatePairs()
                }
            }

        }
    }

    private fun updateCreatePairs() {
        val pairs = lecturerService.getAllPairs(lecturerId!!)
        val listView = findViewById<ListView>(com.example.qrcodes.R.id.listView)
        val adapter = ArrayAdapter(
            this,
            R.layout.simple_list_item_1, pairs
        )
        listView.adapter = adapter
        listView.onItemClickListener =
            OnItemClickListener { _, _, arg2, _ ->
                val pairId = adapter.getItem(arg2)
                pairId?.let {
                    val students = lecturerService.getAllStudents(it)
                    copyToClipboard(this, students.toString())
                    Toast.makeText(
                        this,
                        "Имена студентов успешно добавлены в буфер обмена",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun copyToClipboard(context: Context, textToCopy: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clipData = ClipData.newPlainText("text", textToCopy)

        clipboardManager.setPrimaryClip(clipData)
    }
}
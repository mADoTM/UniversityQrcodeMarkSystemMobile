package com.example.qrcodes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var etId: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etId = findViewById(R.id.etId)

        val btnGoToStudentActivity: Button = findViewById(R.id.btnGoToStudentActivity)
        val btnGoToLecturerActivity: Button = findViewById(R.id.btnGoToLecturerActivity)

        btnGoToStudentActivity.setOnClickListener {
            if (isNameNotEmpty()) {
                val intent = Intent(this, StudentActivity::class.java)
                intent.putExtra("name", etId?.text.toString())
                this.startActivity(intent)
            }
        }

        btnGoToLecturerActivity.setOnClickListener {
            if (isNameNotEmpty()) {
                val intent = Intent(this, LecturerActivity::class.java)
                intent.putExtra("name", etId?.text.toString())
                this.startActivity(intent)
            }
        }
    }

    private fun putNameOnExtra() {
        if (isNameNotEmpty()) {
            intent.putExtra("name", etId?.text.toString())
        }
    }

    private fun isNameNotEmpty() : Boolean {
        if (etId?.text.toString().isBlank()) {
            Toast.makeText(this, "Имя не может быть пустым", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}
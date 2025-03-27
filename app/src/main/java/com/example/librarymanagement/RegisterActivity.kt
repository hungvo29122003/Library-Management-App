package com.example.librarymanagement

import RegisterViewModel
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val edtUsername = findViewById<EditText>(R.id.edt_Username_txt)
        val edtPassword = findViewById<EditText>(R.id.edt_password_txt)
        val edtConfirmPassword = findViewById<EditText>(R.id.edt_confirm_password_text)
        val edtEmail = findViewById<EditText>(R.id.edt_email_text)
        val spinnerRole = findViewById<Spinner>(R.id.spinner_role)
        val checkboxTerms = findViewById<CheckBox>(R.id.checkbox_terms_conditions)
        val btnRegister = findViewById<AppCompatButton>(R.id.btn_register)

        val roles = listOf("Quản lý", "Thủ thư", "Đọc giả")
        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        spinnerRole.adapter = roleAdapter

        fun getRoleValue(selectedRole: String) = when (selectedRole) {
            "Quản lý" -> "QuanLy"
            "Thủ thư" -> "ThuThu"
            "Đọc giả" -> "DocGia"
            else -> ""
        }

        btnRegister.setOnClickListener {
            val role = getRoleValue(spinnerRole.selectedItem.toString())

            registerViewModel.registerUser(edtUsername.text.toString(), edtPassword.text.toString(), edtEmail.text.toString(), role) { success, message ->
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    if (success) finish()
                }
            }
        }
    }

}
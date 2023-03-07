package com.example.rasq

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SignUpActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val txtName: EditText = findViewById(R.id.editName)
        val txtEmail: EditText = findViewById(R.id.editEmail)
        val txtPassword: EditText = findViewById(R.id.editPassword)
        val txtConfirmPassword: EditText = findViewById(R.id.editConfirmPassword)
        val txtPhoneNumber: EditText = findViewById(R.id.editPhoneNumber)
        val txtAddress: EditText = findViewById(R.id.editAddress)
        val btnSignUp: Button = findViewById(R.id.btnSignup)
        listen(btnSignUp)
    }

    private fun listen(pBtnSignUp : Button)
    {
        pBtnSignUp.setOnClickListener{
            Toast.makeText(baseContext, "Se presiono el boton", Toast.LENGTH_LONG).show()
        }
    }
}
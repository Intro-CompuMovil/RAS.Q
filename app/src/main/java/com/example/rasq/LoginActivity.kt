package com.example.rasq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val txtEmailIn: EditText = findViewById(R.id.editEmail)
        val txtPasswordIn: EditText = findViewById(R.id.editPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val txtBtnSignUp: TextView = findViewById(R.id.txtSignup)
        listen(btnLogin, txtBtnSignUp)

    }

    private fun listen(pBtnLogin : Button, pTxtBtnSignUp : TextView)
    {
        pBtnLogin.setOnClickListener{
            Toast.makeText(baseContext, "Se presiono el boton", Toast.LENGTH_LONG).show()
        }

        pTxtBtnSignUp.setOnClickListener {
            val intentSignUp = Intent(this, SignUpActivity::class.java)
            startActivity(intentSignUp)
        }
    }
}
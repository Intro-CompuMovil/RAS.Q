package com.example.rasq.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.rasq.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth


        val txtEmailIn: EditText = findViewById(R.id.editEmail)
        val txtPasswordIn: EditText = findViewById(R.id.editPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val txtBtnSignUp: TextView = findViewById(R.id.txtSignup)
        listen(txtEmailIn, txtPasswordIn, btnLogin, txtBtnSignUp)
        /*if(intent.hasExtra("RasQ"))
        {
            rasq = intent.getSerializableExtra("RasQ") as RasQ
        }
        else
        {
            rasq = RasQ
            rasq.load(this)
        }*/
    }

    private fun listen(pTxtEmailIn: EditText, pTxtPasswordIn: EditText, pBtnLogin : Button, pTxtBtnSignUp : TextView)
    {
        pBtnLogin.setOnClickListener{

            signIn(pTxtEmailIn, pTxtPasswordIn)

            /* if(rasq.logIn(pTxtEmailIn.text.toString(), pTxtPasswordIn.text.toString()))
             {
                 val intentHomePage = Intent(this, ServicesActivity::class.java)
                 intentHomePage.putExtra("RasQ", rasq)
                 startActivity(intentHomePage)
             }
             else
             {
                 Toast.makeText(baseContext, "Credenciales Invalidas", Toast.LENGTH_LONG).show()
             }*/

        }

        pTxtBtnSignUp.setOnClickListener {
            val intentSignUp = Intent(this, SignUpActivity::class.java)
            //intentSignUp.putExtra("RasQ", rasq)
            startActivity(intentSignUp)
        }
    }

    private fun signIn (txtEmailIn : EditText, txtPasswordIn : EditText) {
        if (validateForm(txtEmailIn, txtPasswordIn)) {
            val cEmail = txtEmailIn.text.toString()
            val cPassword = txtPasswordIn.text.toString()

            auth.signInWithEmailAndPassword(cEmail, cPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(
                            baseContext,
                            "Bienvenido!",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val intentHomePage = Intent(this, ServicesActivity::class.java)
                        startActivity(intentHomePage)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "No se pudo loggear! Revise sus datos",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(pTxtEmailIn: EditText, pTxtPasswordIn: EditText): Boolean {
        var valid = true
        val email = pTxtEmailIn.text.toString()
        if(TextUtils.isEmpty(email))
        {
            pTxtEmailIn.error = "Requerido"
            valid = false
        }
        else
        {
            if(isEmailValid(email))
            {
                pTxtEmailIn.error = null
            }
            else
            {
                pTxtEmailIn.error = "Email Invalido"
                valid = false
            }

        }
        val pass = pTxtPasswordIn.text.toString()
        if(TextUtils.isEmpty(pass))
        {
            pTxtPasswordIn.error = "Requerido"
            valid = false
        }
        else
        {
            pTxtPasswordIn.error = null
        }
        return valid
    }

    private fun isEmailValid(pEmail : String): Boolean {

        if(!pEmail.contains("@")||!pEmail.contains(".")||pEmail.length < 5)
        {
            return false
        }
        return true
    }

    override fun onBackPressed() {
        // do nothing
    }

}


package com.example.rasq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.imageview.ShapeableImageView;

class ProfileActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val imgProfilePic: ShapeableImageView = findViewById(R.id.imgProfilePic)
        val txtName: TextView = findViewById(R.id.txtName)
        val txtEmail: TextView = findViewById(R.id.txtEmail)
        val layoutSettingsBtn: LinearLayout = findViewById(R.id.layoutBtnSettings)
        val layoutPaymentBtn: LinearLayout = findViewById(R.id.layoutBtnPayment)
        val layoutMessagesBtn: LinearLayout = findViewById(R.id.layoutBtnMessages)
        val txtPhoneNum: TextView = findViewById(R.id.txtPhoneNumber)
        val txtVerificationStatus: TextView = findViewById(R.id.txtVerificationStatus)
        val txtAddress: TextView = findViewById((R.id.txtAddress))
        val btnLogout: Button = findViewById(R.id.btnLogout)
        listen(layoutSettingsBtn, layoutPaymentBtn, layoutMessagesBtn, btnLogout)

    }

    private fun listen(pLayoutSettings : LinearLayout, pLayoutPayment : LinearLayout, pLayoutMessages : LinearLayout, pBtnLogout : Button)
    {
        pLayoutSettings.setOnClickListener{
            Toast.makeText(baseContext, "Se presiono el boton", Toast.LENGTH_LONG).show()
        }
        pLayoutPayment.setOnClickListener{
            Toast.makeText(baseContext, "Se presiono el boton", Toast.LENGTH_LONG).show()
        }
        pLayoutMessages.setOnClickListener{
            Toast.makeText(baseContext, "Se presiono el boton", Toast.LENGTH_LONG).show()
        }
        pBtnLogout.setOnClickListener{
            val intentLogout = Intent(this, LoginActivity::class.java)
            startActivity(intentLogout)
        }

    }
}
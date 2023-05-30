package com.example.rasq.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.rasq.R
import User
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide

class ProfileActivity : AppCompatActivity() {
    //private lateinit var rasq: RasQ
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        myRef = database.reference
        storageReference = FirebaseStorage.getInstance().reference

        setContentView(R.layout.activity_profile)
        val imgProfilePic: ShapeableImageView = findViewById(R.id.imgProfilePic)
        val txtName: TextView = findViewById(R.id.txtName)
        val txtEmail: TextView = findViewById(R.id.txtEmail)
        val layoutSettingsBtn: LinearLayout = findViewById(R.id.layoutBtnSettings)
        val layoutPaymentBtn: LinearLayout = findViewById(R.id.layoutBtnPayment)
        val layoutMessagesBtn: LinearLayout = findViewById(R.id.layoutBtnMessages)
        val txtPhoneNum: TextView = findViewById(R.id.editTxtRequestorEmail)
        val txtVerificationStatus: TextView = findViewById(R.id.editTxtTimeDate)
        val txtAddress: TextView = findViewById((R.id.editTxtAddress))
        val btnLogout: Button = findViewById(R.id.btnUpdate)

        val currentUser = auth.currentUser

        currentUser?.let { user ->
            myRef.child("users").child(user.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(User::class.java)
                    txtName.text = userData?.name
                    txtEmail.text = userData?.email
                    txtPhoneNum.text = userData?.cellphone
                    txtAddress.text = userData?.address

                    if (userData?.verified == true) {
                        txtVerificationStatus.text = "Verificado"
                    } else {
                        txtVerificationStatus.text = "Sin Verificar"
                    }

                    // Fetch the image URL and load it into the ImageView
                    val imageRef = storageReference.child("images/profile/"+ auth.currentUser!!.uid +"/image.jpg")
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Load the image using the URI into your ImageView
                        Glide.with(this@ProfileActivity)
                            .load(uri)
                            .into(imgProfilePic)
                    }.addOnFailureListener { exception ->
                        // Handle any errors while loading the image
                        Toast.makeText(this@ProfileActivity, "Failed to load image: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
        }

        listen(layoutSettingsBtn, layoutPaymentBtn, layoutMessagesBtn, btnLogout)
    }

    private fun listen(
        pLayoutSettings: LinearLayout,
        pLayoutPayment: LinearLayout,
        pLayoutMessages: LinearLayout,
        pBtnLogout: Button
    ) {
        pLayoutSettings.setOnClickListener {
            val intentSettings = Intent(this, ConfigActivity::class.java)
            //intentSettings.putExtra("RasQ", rasq)
            startActivity(intentSettings)
        }
        pLayoutPayment.setOnClickListener {
            Toast.makeText(baseContext, "Se presiono el boton", Toast.LENGTH_LONG).show()
        }
        pLayoutMessages.setOnClickListener {
            Toast.makeText(baseContext, "Se presiono el boton", Toast.LENGTH_LONG).show()
        }
        pBtnLogout.setOnClickListener {
            auth.signOut()
            val intentLogout = Intent(this, LoginActivity::class.java)
            startActivity(intentLogout)
        }
    }
}

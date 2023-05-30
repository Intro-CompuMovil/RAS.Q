package com.example.rasq.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.example.rasq.R
import User
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ConfigActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        auth = Firebase.auth
        myRef = database.reference
        storageReference = FirebaseStorage.getInstance().reference

        val imgProfilePic: ShapeableImageView = findViewById(R.id.imgProfilePic)
        val txtName: TextView = findViewById(R.id.txtName)
        val txtEmail: TextView = findViewById(R.id.txtEmail)
        val txtCurrentAddress: TextView = findViewById(R.id.editTxtRequestorEmail)
        val editTxtAddress: EditText = findViewById(R.id.editTxtAddress)
        val btnUpdate: Button = findViewById(R.id.btnUpdate)

        val currentUser = auth.currentUser

        currentUser?.let { user ->
            myRef.child("users").child(user.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(User::class.java)
                    txtName.text = userData?.name
                    txtEmail.text = userData?.email

                    // Fetch the image URL and load it into the ImageView
                    val imageRef =
                        storageReference.child("images/profile/" + auth.currentUser!!.uid + "/image.jpg")
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Load the image using the URI into your ImageView
                        Glide.with(this@ConfigActivity)
                            .load(uri)
                            .into(imgProfilePic)
                    }.addOnFailureListener { exception ->
                        // Handle any errors while loading the image
                        Toast.makeText(
                            this@ConfigActivity,
                            "Failed to load image: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
        }

        listen(editTxtAddress, btnUpdate)
    }

    private fun listen(pEditTxtAddress: EditText, pBtnUpdate: Button) {
        pBtnUpdate.setOnClickListener {
            if (pEditTxtAddress.text.toString().isNotEmpty()) {
                val currentUser = auth.currentUser
                val userRef = myRef.child("users").child(currentUser?.uid ?: "")
                userRef.child("address").setValue(pEditTxtAddress.text.toString())
                userRef.child("verified").setValue(true)
                val intentHome = Intent(this, ProfileActivity::class.java)
                startActivity(intentHome)
            } else {
                Toast.makeText(baseContext, "Ingrese una dirección válida", Toast.LENGTH_LONG).show()
            }
        }
    }
}

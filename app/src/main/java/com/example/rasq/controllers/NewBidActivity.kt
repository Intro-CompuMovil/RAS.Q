package com.example.rasq.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.rasq.R
import com.example.rasq.entities.Bid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NewBidActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var eventRef: DatabaseReference
    private var selectedEventId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_bid)

        auth = FirebaseAuth.getInstance()


        selectedEventId = intent.getStringExtra("selectedEventId") ?: ""

        val txtPrice: EditText = findViewById(R.id.editPrice)
        val btnBid: Button = findViewById(R.id.btnBid)

        eventRef = database.getReference(PATH_EVENTS).child(selectedEventId)

        btnBid.setOnClickListener {
            val price = txtPrice.text.toString().toInt()

            val bid = Bid(price, auth.currentUser?.email.toString())
            eventRef.child("selectedBid").setValue(bid)

            val homeIntent = Intent(this, ServicesActivity::class.java)
            startActivity(homeIntent)
        }
    }
}
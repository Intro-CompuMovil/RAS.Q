package com.example.rasq.controllers

import User
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.rasq.R
import com.example.rasq.entities.Bid
import com.example.rasq.entities.Event
import com.example.rasq.model.RasQ
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ViewEventRequestActivity : AppCompatActivity() {

    private lateinit var eventRequest: Event
    private lateinit var auth: FirebaseAuth
    private lateinit var eventRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event_request)

        auth = Firebase.auth

        val selectedEventId = intent.getStringExtra("selectedEventId")
        if (selectedEventId != null) {
            eventRef = FirebaseDatabase.getInstance().reference.child(PATH_EVENTS).child(selectedEventId)

            val txtRequestTitle: TextView = findViewById(R.id.txtRequestTitle)
            val txtRequesterEmail: TextView = findViewById(R.id.editTxtRequestorEmail)
            val txtTimeDate: TextView = findViewById(R.id.editTxtTimeDate)
            val txtAddress: TextView = findViewById(R.id.editTxtAddress)
            val txtDescription: TextView = findViewById(R.id.editTxtDescription)
            val bidList: ListView = findViewById(R.id.bid_list)
            val btnNewBid: ImageButton = findViewById(R.id.btn_new_bid)

            val adapter = ArrayAdapter<String>(
                this,
                R.layout.list_item_layout,
                R.id.priceTextView
            )
            bidList.adapter = adapter

            eventRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    eventRequest = dataSnapshot.getValue(Event::class.java) ?: return

                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val user = User()
                        user.email = currentUser.email.toString()
                        txtRequesterEmail.text = user.email
                    }

                    txtRequestTitle.text = eventRequest.title
                    //txtRequesterEmail.text = eventRequest.selectedBid.bidderEmail
                    txtTimeDate.text = formatDateAndTime(eventRequest.date, eventRequest.time)
                    txtAddress.text = eventRequest.address
                    txtDescription.text = eventRequest.description

                    adapter.clear()

                    if (eventRequest.selectedBid.price != 0 ) {
                        val bidDetails = "Costo Oferta: $${eventRequest.selectedBid.price}\n" +
                                "Correo del ofertador: ${eventRequest.selectedBid.bidderEmail}\n"
                        adapter.add(bidDetails)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

            btnNewBid.setOnClickListener {
                val newBidIntent = Intent(this, NewBidActivity::class.java)
                newBidIntent.putExtra("selectedEventId", selectedEventId)
                startActivity(newBidIntent)
            }
        } else {
            //Toast.makeText(this, "NO SE PUDO!", Toast.LENGTH_LONG).show()
        }
    }

    private fun formatDateAndTime(date: String?, time: String?): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val formattedDate = LocalDate.parse(date).format(dateFormatter)
        val formattedTime = LocalTime.parse(time).format(timeFormatter)

        return "$formattedDate $formattedTime"
    }


    /*private fun createOnSelectListener(pBidList : ListView)
    {
        pBidList.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                rasq.events.add(Event(rasq.eventRequests[eventRequestIndex].requestorIndex, rasq.eventRequests[eventRequestIndex].title, rasq.eventRequests[eventRequestIndex].date, rasq.eventRequests[eventRequestIndex].time, rasq.eventRequests[eventRequestIndex].address, rasq.eventRequests[eventRequestIndex].description, rasq.eventRequests[eventRequestIndex].bids[position]))
                rasq.eventRequests.removeAt(eventRequestIndex)
                rasq.save(this)
                val intentSelectBid = Intent(this, ServicesActivity::class.java)
                intentSelectBid.putExtra("RasQ", rasq)
                startActivity(intentSelectBid)
            }
    }*/
}


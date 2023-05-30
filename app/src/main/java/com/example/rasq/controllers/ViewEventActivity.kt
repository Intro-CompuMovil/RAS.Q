package com.example.rasq.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.rasq.R
import com.example.rasq.model.RasQ
import java.time.format.DateTimeFormatter

class ViewEventActivity : AppCompatActivity()
{
    private lateinit var rasq: RasQ
    var eventIndex: Int = -1
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        val bundle = intent.getBundleExtra("sendBundle")
        rasq = bundle?.getSerializable("RasQ") as RasQ
        eventIndex = bundle.getInt("selectedIndex")

        val txtRequestTitle: TextView = findViewById(R.id.txtRequestTitle)
        val txtRequesterEmail: TextView = findViewById(R.id.editTxtRequestorEmail)
        val txtTimeDate: TextView = findViewById(R.id.editTxtTimeDate)
        val txtAddress: TextView = findViewById(R.id.editTxtAddress)
        val txtDescription: TextView = findViewById(R.id.editTxtDescription)
        val txtBid: TextView = findViewById((R.id.editTxtBid))

        txtRequestTitle.text = rasq.events[eventIndex].title.toString()
        txtRequesterEmail.text =
            rasq.users[rasq.events[eventIndex].requestorIndex].email.toString()
        txtTimeDate.text = formatDateAndTime(
            rasq.events[eventIndex].date,
            rasq.events[eventIndex].time
        )
        txtAddress.text = rasq.events[eventIndex].address.toString()
        txtDescription.text = rasq.events[eventIndex].description.toString()
        txtBid.text = rasq.events[eventIndex].selectedBid?.price.toString()
    }

    private fun formatDateAndTime(date: String, time: String): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val formattedDate = date.format(dateFormatter)
        val formattedTime = time.format(timeFormatter)

        return "$formattedDate $formattedTime"
    }
}
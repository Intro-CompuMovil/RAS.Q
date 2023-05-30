package com.example.rasq.controllers

import KeyMarker
import Payment
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rasq.R
import com.example.rasq.entities.Bid
import com.example.rasq.entities.Event
import com.example.rasq.model.RasQ
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalDate
import java.time.LocalTime

const val PATH_EVENTS = "events/"

class NewEventRequestActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private val storage = Firebase.storage
    private var selectedMarker: KeyMarker? = null

    companion object {
        private const val REQUEST_CODE_MAP = 1
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
        private const val EXTRA_SELECTED_MARKER = "extra_selected_marker"
    }



    /**
     * Método que se ejecuta al crear la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event_request)

        auth = Firebase.auth

        val editTxtTitle: EditText = findViewById(R.id.editPrice)
        val datePickr: DatePicker = findViewById(R.id.datePicker)
        val timePickr: TimePicker = findViewById(R.id.timePicker)
        val editTxtAddress: EditText = findViewById(R.id.editAddress)
        val editTxtDescription: EditText = findViewById(R.id.editDescription)
        val btnNewRequest: Button = findViewById(R.id.btnNewEventRequest)
        val btnUbicacionMapa: Button = findViewById(R.id.eventoMapa)
        var selectedTime: LocalTime = LocalTime.NOON
        timePickr.setOnTimeChangedListener { _, hourOfDay, minute ->
            selectedTime = LocalTime.of(hourOfDay, minute)
        }

        btnNewRequest.setOnClickListener {
            val year = datePickr.year
            val month = datePickr.month + 1
            val day = datePickr.dayOfMonth

            if (isValidDate(year, month, day)) {
                val selectedDate = LocalDate.of(year, month, day)
               // val selectedBid = Bid(0,"")
                //val selectedPayment = Payment(selectedDate.toString())
                val numericId = auth.currentUser!!.uid.hashCode()

                myRef = database.getReference(PATH_EVENTS + numericId)

                val user = auth.currentUser

                if (user != null) {
                    val hour = timePickr.hour
                    val minute = timePickr.minute
                    val selectedTime = LocalTime.of(hour, minute)

                    val event = Event()

                    event.address = editTxtAddress.text.toString()
                    event.description = editTxtDescription.text.toString()
                    event.title = editTxtTitle.text.toString()
                    event.date = selectedDate.toString()
                    event.time = selectedTime.toString()
                    event.selectedBid = Bid()
                    event.payment = Payment()
                    event.marker = selectedMarker
                    event.id = user.uid + event.date + event.time

                    myRef = database.getReference(PATH_EVENTS + event.id)
                    myRef.setValue(event)
                }

                val intentHome = Intent(this, ServicesActivity::class.java)
                startActivity(intentHome)


            } else {
                Toast.makeText(this, "Invalid date selected", Toast.LENGTH_SHORT).show()
            }
        }

        btnUbicacionMapa.setOnClickListener {
            if (areLocationPermissionsGranted()) {
                startMapActivity()
            } else {
                requestLocationPermissions()
            }
        }
        selectedMarker = intent.getParcelableExtra(EXTRA_SELECTED_MARKER)
    }


    private fun areLocationPermissionsGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun startMapActivity() {
        val intentMap = Intent(this, MapActivity::class.java)
        startActivityForResult(intentMap, REQUEST_CODE_MAP)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    startMapActivity()
                } else {
                    Toast.makeText(
                        this,
                        "Location permissions are required to access the map.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Método que se ejecuta cuando se obtiene un resultado de otra actividad.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK) {
            val marker = data?.getParcelableExtra<KeyMarker>(EXTRA_SELECTED_MARKER)
            if (marker != null) {
                selectedMarker = marker
                // Realiza las operaciones necesarias con el marcador seleccionado
                // Aquí puedes utilizar el valor de selectedMarker en tu evento
            }
        }
    }

    private fun isValidDate(year: Int, month: Int, day: Int): Boolean {
        val lastDayOfMonth = LocalDate.of(year, month, 1).lengthOfMonth()
        return day in 1..lastDayOfMonth
    }
}



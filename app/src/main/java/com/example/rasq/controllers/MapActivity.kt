package com.example.rasq.controllers

import KeyMarker
import User
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.rasq.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

const val EXTRA_SELECTED_MARKER = "extra_selected_marker"

class MapActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapClickListener {
    private var mMap: GoogleMap? = null
    private var location: Location? = null
    private lateinit var changeService: AvailableChangeService
    private var selectedMarker: KeyMarker? = null
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private lateinit var storageReference: StorageReference

    /**
     * Método que se ejecuta al crear la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        auth = Firebase.auth
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        myRef = database.getReference(PATH_USERS + auth.currentUser!!.uid)
        myRef.child("available").setValue(true)
        changeService = AvailableChangeService(this)
        changeService.startListening()
        val userImage: ShapeableImageView = findViewById(R.id.fireBaseImg)

        auth = Firebase.auth
        myRef = database.reference
        storageReference = FirebaseStorage.getInstance().reference

        val currentUser = auth.currentUser

        currentUser?.let { user ->
            myRef.child("users").child(user.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(User::class.java)

                    // Fetch the image URL and load it into the ImageView
                    val imageRef =
                        storageReference.child("images/profile/" + auth.currentUser!!.uid + "/image.jpg")
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Load the image using the URI into your ImageView
                        Glide.with(this@MapActivity)
                            .load(uri)
                            .into(userImage)
                    }.addOnFailureListener { exception ->
                        // Handle any errors while loading the image
                        Toast.makeText(
                            this@MapActivity,
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


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            enableMyLocation()
        }

        val btnDone: Button = findViewById(R.id.btnDone)
        btnDone.setOnClickListener {
            if (selectedMarker != null) {
                val intent = Intent()
                intent.putExtra(EXTRA_SELECTED_MARKER, selectedMarker)
                setResult(RESULT_OK, intent)
            } else {
                setResult(RESULT_CANCELED)
            }
            finish()
        }
    }

    /**
     * Método que se ejecuta para crear el menú de opciones en la barra de acción.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.bottom_nav_menu, menu)
        return true
    }

    /**
     * Método que se ejecuta al seleccionar un elemento del menú de opciones en la barra de acción.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnDone -> {
                // Obtener los datos del marcador seleccionado
                val marker = selectedMarker

                // Crear un Intent para volver a la actividad NewEventRequestActivity
                val intent = Intent(this, NewEventRequestActivity::class.java)

                // Agregar el marcador como extra al Intent
                intent.putExtra(EXTRA_SELECTED_MARKER, marker)

                // Iniciar la actividad NewEventRequestActivity con el Intent
                startActivity(intent)

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    /**
     * Método que se ejecuta cuando se hace clic en el mapa.
     */
    override fun onMapClick(latLng: LatLng) {
        // Agregar un marcador en la posición donde se hizo clic
        selectedMarker = KeyMarker(latLng.latitude, latLng.longitude, "Nuevo marcador")
        mMap?.addMarker(MarkerOptions().position(latLng).title("Nuevo marcador"))


        // Puedes guardar la ubicación en Firebase u realizar otras acciones según tus necesidades
        // ...
    }

    /**
     * Método que se ejecuta cuando el mapa está listo para ser utilizado.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true
        mMap!!.uiSettings?.isZoomControlsEnabled = true
        mMap!!.uiSettings?.isZoomGesturesEnabled = true
        mMap!!.isMyLocationEnabled = true
        mMap?.setOnMapClickListener(this)

        val jsonString: String
        try {
            val inputStream = assets.open("locations.json")
            jsonString = inputStream.bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            Log.e("MyApp", "Error al leer el archivo JSON: ${ioException.message}")
            return
        }

        val listMarkerType = object : TypeToken<List<KeyMarker>>() {}.type
        val listMarkers: List<KeyMarker> = Gson().fromJson(jsonString, listMarkerType)
        if (listMarkers != null) {
            for (marker in listMarkers) {
                val pos = LatLng(marker.latitude ?: 0.0, marker.longitude ?: 0.0)
                mMap!!.addMarker(MarkerOptions().position(pos).title(marker.name))
            }
        } else {
            Log.e("MyApp", "La lista de marcadores es nula")
        }

        if (jsonString.isNullOrEmpty()) {
            Log.e("MyApp", "El archivo JSON está vacío")
            return
        }

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isLocationEnabled) {
            Toast.makeText(this, "El proveedor de ubicación no está habilitado", Toast.LENGTH_LONG)
                .show()
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            enableMyLocation()
        }

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.7749, -122.4194), 10f))
    }

    /**
     * Método que se ejecuta cuando se hace clic en el botón de ubicación propia.
     */
    override fun onMyLocationButtonClick(): Boolean {
        // Aquí puedes centrar el mapa en la ubicación actual del usuario
        // ...
        return false
    }

    /**
     * Método que solicita los permisos de ubicación al usuario.
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    /**
     * Método que se ejecuta cuando se solicitan los permisos de ubicación al usuario.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Método que habilita la capa de ubicación en el mapa.
     */
    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap?.isMyLocationEnabled = true
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val PATH_USERS = "users/"
    }
}
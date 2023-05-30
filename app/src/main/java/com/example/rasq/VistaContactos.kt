package com.example.rasq

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class VistaContactos : AppCompatActivity() {

    private val CONTACTS_CODE = 9
    private var proyeccion: Array<String> = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    )
    private var cursor: Cursor? = null
    private lateinit var adapter: ContactsAdapter
    private lateinit var listaContactos: ListView

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACTS_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                permisoContactos()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_CONTACTS
                    )
                ) {
                    Toast.makeText(
                        this,
                        "Para acceder a los contactos, es necesario otorgar el permiso.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Sin acceso a los contactos, el funcionamiento de la app no será el esperado.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun permisoContactos() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                proyeccion,
                null,
                null,
                null
            )
            adapter.changeCursor(cursor)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_CODE
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_contactos)

        listaContactos = findViewById(R.id.listContacts)
        adapter = ContactsAdapter(this, null, 0)
        listaContactos.adapter = adapter

        permisoContactos()
    }

    override fun onDestroy() {
        super.onDestroy()
        cursor?.close()
    }
}

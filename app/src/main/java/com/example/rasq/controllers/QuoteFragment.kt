package com.example.rasq.controllers

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.rasq.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class QuoteFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var uri: Uri
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_quote, container, false)
        val btnCamera: ImageButton = view.findViewById(R.id.btnCamera)
        auth = Firebase.auth
        storageRef = Firebase.storage.reference.child("images/profile/${auth.currentUser!!.uid}/image.jpg")

        btnCamera.setOnClickListener {
            ImagePicker.with(this)
                .crop(1f, 1f)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ImagePicker.REQUEST_CODE -> {
                    val fileUri = data?.data
                    if (fileUri != null) {
                        storageRef.putFile(fileUri)
                            .addOnSuccessListener { taskSnapshot ->
                                val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
                                // Save the download URL to the user's profile in Firebase Database if needed
                                // ...
                            }
                            .addOnFailureListener { exception ->
                                // Error uploading image
                                // Handle the error
                            }
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment QuoteFragment.
         */
        @JvmStatic
        fun newInstance() = QuoteFragment()
    }
}

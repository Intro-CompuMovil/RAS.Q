package com.example.rasq.controllers

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.TextureView
import android.widget.ImageButton
import android.widget.Toast
import com.example.rasq.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CameraActivity : AppCompatActivity() {

    private lateinit var camera: Camera
    private lateinit var textureView: TextureView
    private lateinit var auth: FirebaseAuth
    private lateinit var uri: Uri
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private val storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        auth = FirebaseAuth.getInstance()
        myRef = database.getReference(PATH_USERS)

        textureView = findViewById(R.id.textureView)

        // Set up the TextureView to show the camera preview
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                camera = Camera.open()
                camera.setPreviewTexture(surfaceTexture)
                val parameters = camera.parameters
                parameters.set("orientation", "portrait")
                camera.parameters = parameters
                camera.setDisplayOrientation(90)
                camera.startPreview()
            }

            override fun onSurfaceTextureSizeChanged(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                camera.stopPreview()
                camera.release()
                return true
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
        }

        val button: ImageButton = findViewById(R.id.btnCapture)
        button.setOnClickListener {
            // Get a bitmap of the current camera preview
            val bitmap = textureView.bitmap

            // Save the bitmap to internal storage
            val file = File(getExternalFilesDir(null), "picture.jpg")
            val stream = FileOutputStream(file)
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
            stream.flush()
            stream.close()

            // Set the URI of the image file
            uri = Uri.fromFile(file)

            // Upload the file to Firebase Storage and update profile picture
            uploadFile()
        }
    }

    private fun uploadFile() {
        val file = uri
        val imageRef = storage.reference.child("images/profile/${auth.currentUser!!.uid}/image.jpg")

        imageRef.putFile(file)
            .addOnSuccessListener { taskSnapshot ->
                // Image upload success
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    // Update the user's profile picture URL in the Firebase Realtime Database
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        myRef.child("users").child(userId).child("profilePictureUrl").setValue(imageUrl)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Profile picture URL update success
                                    Toast.makeText(this, "Picture uploaded and URL updated successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Profile picture URL update failed
                                    Toast.makeText(this, "Failed to update profile picture URL.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // User is not logged in
                        Toast.makeText(this, "User is not logged in.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Image upload failed
                Toast.makeText(this, "Failed to upload picture.", Toast.LENGTH_SHORT).show()
            }
    }
}

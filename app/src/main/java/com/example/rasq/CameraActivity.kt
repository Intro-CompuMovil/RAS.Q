package com.example.rasq

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.TextureView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class CameraActivity : AppCompatActivity() {

    private lateinit var camera: Camera
    private lateinit var textureView: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

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

            // Show a toast to indicate that the picture was saved
            Toast.makeText(this, "Picture saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        }
    }
}
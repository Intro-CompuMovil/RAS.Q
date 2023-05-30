package com.example.rasq.controllers

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.rasq.R
import User
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

const val PATH_USERS="users/"




class SignUpActivity : AppCompatActivity()
{
    //private lateinit var rasq: RasQ
    private lateinit var auth: FirebaseAuth
    private lateinit var uri : Uri
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private val storage = Firebase.storage


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val txtName: EditText = findViewById(R.id.editPrice)
        val txtEmail: EditText = findViewById(R.id.editEmail)
        val txtPassword: EditText = findViewById(R.id.editPassword)
        val txtConfirmPassword: EditText = findViewById(R.id.editConfirmPassword)
        val txtPhoneNumber: EditText = findViewById(R.id.editPhoneNumber)
        val profilePicImg : ImageView = findViewById(R.id.profilePicImg)
        val profilePicBtn : FloatingActionButton = findViewById(R.id.profilePicBtn)

//        val txtAddress: EditText = findViewById(R.id.editAddress)

        val btnSignUp: Button = findViewById(R.id.btnBid)

        //rasq = intent.getSerializableExtra("RasQ") as RasQ
        listen(txtName, txtEmail, txtPassword, txtConfirmPassword, txtPhoneNumber, btnSignUp, profilePicImg, profilePicBtn)
    }

    private fun listen(pTxtName: EditText, pTxtEmail: EditText, pTxtPassword: EditText, pTxtConfirmPass: EditText, pTxtPhone: EditText, pBtnSignUp : Button, profilePicImg : ImageView, profilePicBtn : FloatingActionButton)
    {
        profilePicBtn.setOnClickListener {
            ImagePicker.with(this)
                .crop(1f, 1f) // Enforce 1:1 aspect ratio (width:height)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()

        }

        pBtnSignUp.setOnClickListener{
            if(pTxtPassword.text.toString() == pTxtConfirmPass.text.toString())
            {
                /*if (uri != null) {

                }*/
                signUp(pTxtEmail, pTxtPassword, pTxtName, pTxtPhone)

                /*if(rasq.createUser(this, pTxtName.text.toString(), pTxtEmail.text.toString(), pTxtPassword.text.toString(), pTxtPhone.text.toString()))
                {
                    val intentLogin = Intent(this, LoginActivity::class.java)
                    intentLogin.putExtra("RasQ", rasq)
                    startActivity(intentLogin)
                }
                else
                {
                    Toast.makeText(baseContext, "¡No se logró crear el usuario (Ya esta registrado el correo)!", Toast.LENGTH_LONG).show()
                }*/
            }
            else
            {
                Toast.makeText(baseContext, "¡La contraseña no es igual en los dos campos!", Toast.LENGTH_LONG).show()
            }

        }
    }


    private fun signUp(txtEmail: EditText, txtPassword: EditText, txtName: EditText, txtPhone: EditText) {
        if (validateForm(txtEmail, txtPassword)) {
            val cEmail = txtEmail.text.toString()
            val cPassword = txtPassword.text.toString()

            auth.createUserWithEmailAndPassword(cEmail, cPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        if (user != null) {
                            val myUser = User()
                            myUser.name = txtName.text.toString()
                            myUser.cellphone = txtPhone.text.toString()
                            myUser.password = txtPassword.text.toString()
                            myUser.email = txtEmail.text.toString()
                            myUser.uid = user.uid
                            myRef = database.getReference(PATH_USERS + auth.currentUser!!.uid)
                            myRef.setValue(myUser)
                            val upcrb = UserProfileChangeRequest.Builder()
                            upcrb.displayName = txtName.text.toString()
                            user.updateProfile(upcrb.build())

                            if (::uri.isInitialized) { // Check if uri is initialized
                                uploadFile()
                            } else {
                                // Handle the case when uri is not initialized
                                Toast.makeText(this, "Imagen no seleccionada", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "createUserWithEmail:Failure: " + task.exception.toString(),
                                Toast.LENGTH_SHORT).show()
                            task.exception?.message?.let { Log.e(TAG, it) }
                        }

                        Toast.makeText(
                            baseContext,
                            "Te has registrado correctamente!",
                            Toast.LENGTH_SHORT,
                        ).show()

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "No se pudo hacer el registro de usuario!",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(pTxtEmailIn: EditText, pTxtPasswordIn: EditText): Boolean {
        var valid = true
        val email = pTxtEmailIn.text.toString()
        if(TextUtils.isEmpty(email))
        {
            pTxtEmailIn.error = "Requerido"
            valid = false
        }
        else
        {
            if(isEmailValid(email))
            {
                pTxtEmailIn.error = null
            }
            else
            {
                pTxtEmailIn.error = "Email Invalido"
                valid = false
            }

        }
        val pass = pTxtPasswordIn.text.toString()
        if(TextUtils.isEmpty(pass))
        {
            pTxtPasswordIn.error = "Requerido"
            valid = false
        }
        else
        {
            pTxtPasswordIn.error = null
        }
        return valid
    }

    private fun isEmailValid(pEmail : String): Boolean {

        if(!pEmail.contains("@")||!pEmail.contains(".")||pEmail.length < 5)
        {
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
        {
            //Image Uri will not be null for RESULT_OK
            uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions
            val profilePicImg: ImageView = findViewById(R.id.profilePicImg)
            profilePicImg.setImageURI(uri)
        }
        else if (resultCode == ImagePicker.RESULT_ERROR)
        {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(this, "Imagen no seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadFile()
    {

        val file = uri
        val imageRef = storage.reference.child("images/profile/"+ auth.currentUser!!.uid +"/image.jpg")
        imageRef.putFile(file)
            .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                    // Get a URL to the uploaded content
                    Log.i("FBApp", "Successfully uploaded image")
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(exception: Exception) {
                    // Handle unsuccessful uploads
                    // ...
                }
            })
    }
}
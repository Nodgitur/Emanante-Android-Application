package com.example.emanate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.emanate.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // With this listener the user can click if they already have an account
        binding.alreadyRegistered.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            finish()
            startActivity(intent)
        }

        // This will perform the action inside the onClickListener when the submit button is pressed
        binding.signUpButton.setOnClickListener {
            val email = binding.emailCreateTyped.text.toString()
            val password = binding.passwordCreateTyped.text.toString()
            val confirmPassword = binding.passwordConfirmationTyped.text.toString()

            /*
                All of the conditions inside this statement is for verifying the credentials entered
                with the users on Firebase
             */
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this,
                                    it.exception.toString(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(
                        this,
                        "Passwords do not match",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Fields must not be empty",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}
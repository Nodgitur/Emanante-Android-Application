package com.example.emanate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.emanate.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "SignInActivity"
    }

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var startTime: Long = - 1

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        startTime = System.currentTimeMillis()

        /*
            When the phone opens with the splash screen, it depends on the first frame of the root
            activity for the splash to end. For Emanate this is near instant. To bring more
            attention to the splash screen, I added 3 seconds on top of the first frame to load.
            This may be seen to worsen the performance but is better for demonstration purposes.

            NOTE THAT SPLASH SCREENS IN THIS IMPLEMENTATION WILL ONLY WORK ON ANDROID 12 (API 31)
            BECAUSE FORMER VERSIONS NEEDED USER IMPLEMENTATION WITHOUT GOOGLE'S OWN FUNCTIONS
         */
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    val isReady = System.currentTimeMillis() - startTime > 1000
                    // Check if the screen is ready
                    return if (isReady) {
                        // The content is ready, start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content is not ready, wait.
                        false
                    }
                }
            }
        )

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.notRegistered.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            finish()
            startActivity(intent)
        }

        binding.signInButton.setOnClickListener {
            val email = binding.emailTyped.text.toString()
            val password = binding.passwordTyped.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
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
                    "Fields must not be empty",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    /*
        This function performs an authentication check for when the user is at the sign in page.
        Since the root of the application is the sign in activity, it is useful for when the user
        is still signed in. They will be directed to the main activity if they are which avoids the
        unnecessary sign in again.
     */
    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
            Log.d(TAG, "Redirected to main activity because user is authenticated")
        }
    }
}
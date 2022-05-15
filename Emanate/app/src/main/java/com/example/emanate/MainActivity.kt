package com.example.emanate

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.emanate.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    // Here binding is being declared. This makes the code cleaner, removing the need for
    // findViewById(), reducing boilerplate code. It is also safer by providing null and type safety
    private lateinit var binding: ActivityMainBinding
    private val gson = Gson()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.submitButton.setOnClickListener {
            val nodeId: String = binding.editTextInsertId.text.toString()
            if (nodeId.isNotEmpty()) {

                readNodeId(nodeId)

                hideKeyboard(currentFocus ?: View(this))

            } else {
                Toast.makeText(
                    this, "Please enter an id in the format xEm-*number(s)*",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account_button -> {
                firebaseAuth.signOut()

                val intent1 = Intent(applicationContext, SignInActivity::class.java)
                startActivity(intent1)
                finish()
            }
            R.id.notifications_button -> {
                val intent1 = Intent(applicationContext, NotificationsActivity::class.java)
                startActivity(intent1)
            }
        }
        return true
    }

    // This function is to hide the keyboard for when the user submits a metric to search
    private fun hideKeyboard(view: View){
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE)
                as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        this.menu = menu
        return true
    }

    private fun readNodeId(nodeId: String) {
        val database = Firebase.database
        val databaseReference = database.reference.child("BaseCamps").child("sasgPRBqdwQdgr9jCXIF82qFYjA2")
        //If the node id entered is available in the database, this success listener will retrieve
        // the data snapshot

        databaseReference.child(nodeId).get().addOnSuccessListener {

            if (it.exists()) {
                val value = it.children.last().value as Map<*, *>
                val json = gson.toJson(value)
                val record = gson.fromJson(json, Record::class.java)
                Log.d(TAG, record.toString())
                Log.d(TAG, it.value.toString())

                Toast.makeText(this, "Node found", Toast.LENGTH_SHORT).show()

                val altitudeWithUnits = record.altitude + "m"
                val gasWithUnits = record.gas + "ohms"
                val humidityWithUnits = record.humidity + "%"
                val localPressureWithUnits = record.localPressure + "hPa"
                val temperatureWithUnits = record.temperature + "°C"

                binding.editTextInsertId.text.clear()
                binding.textViewNodeId.text = record.dbNodeId
                binding.textViewAltitude.text = altitudeWithUnits
                binding.textViewGas.text = gasWithUnits
                binding.textViewHumidity.text = humidityWithUnits
                binding.textViewLocalPressure.text = localPressureWithUnits
                binding.textViewLocale.text = record.locale
                binding.textViewTemperature.text = temperatureWithUnits
            } else {
                Toast.makeText(this, "Node not found in expedition", Toast.LENGTH_SHORT)
                    .show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error! Try again?", Toast.LENGTH_SHORT).show()
        }
    }
}
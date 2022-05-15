package com.example.emanate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.example.emanate.databinding.ActivityNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.DatabaseError
import java.util.*
import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import java.lang.StringBuilder

class NotificationsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "NotificationsActivity"
    }

    // Here binding is being declared. This makes the code cleaner, removing the need for
    // findViewById(), reducing boilerplate code. It is also safer by providing null and type safety
    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val gson = Gson()
    private lateinit var adapter: ListAdapter
    private var altitudeSicknessFlag = false


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        isAuthenticated()

        adapter = ListAdapter(this)

        binding.nodesListView.adapter = adapter

        concerningMetrics()

    }

    private fun concerningMetrics() {
        val database = Firebase.database
        val databaseReference = database.reference.child("BaseCamps").child(
            "sasgPRBqdwQdgr9jCXIF82qFYjA2"
        )

        val valueEventListener = object : ValueEventListener {

            // The function will be run if data changes in the database, so it will be used a lot
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (nodeSnapshot in dataSnapshot.children) {
                    val name = nodeSnapshot.key
                    Log.d(TAG, name.toString())

                    /*
                        This variable takes in the 'event' of each of the nodes throughout the
                        database. The event is the recorded time with a record
                     */
                    val eventList = nodeSnapshot.children.toList()

                    /*
                        Transformation from list of type DataSnapshot to Record for the metrics
                        inside each record
                     */
                    val recordList: List<Record> = eventList
                        .subList(eventList.size - 2, eventList.size)
                        .map {
                            val value = it.value as Map<*, *>
                            val json = gson.toJson(value)
                            val record = gson.fromJson(json, Record::class.java)
                            Log.d(TAG, record.toString())
                            val twoNodesRecords = it.value.toString()
                            val timeNode = it.key.toString()
                            Log.d(TAG, record.toString())
                            Log.d(TAG, twoNodesRecords)
                            Log.d(TAG, timeNode)

                            return@map record
                        }

                    // Creating instance of TimeConverter to access getDateTime
                    val converter = TimeConverter()

                    /*
                       Using eventList this variable gets the last event of the nodes. It then
                       selects the key from the event(timestamp), passing it to the getDateTime
                       function. The null operator will check if the value is null as we move
                       through the declaration. If it fails somehow, the elvis operator will throw
                       an error
                     */
                    val timestampConvertedToTime = eventList.lastOrNull()?.key?.let(converter::getDateTime)
                        ?: error("Timestamp was null")

                    // Destructuring for the bottom two records
                    val (past, current) = recordList[0] to recordList[1]

                    // This StringBuilder will be added to the adapter for the notifications
                    val stringBuilder = StringBuilder()

                    /*
                     For all of the notifications on concerning metrics, they are passed into a
                     dataBuilder to be referenced in the public companion object. Since value event
                     listener will only take in an object and does not provide a return value.
                     */

                    if (current.altitude.toDouble() - past.altitude.toDouble() <= -10) {
                        stringBuilder.append(
                            "The altitude dropped over 30 metres " +
                                    "suddenly.\n"
                        )
                    }

                    /*
                        The additional flag in this check will be so when the hiker goes above 2500
                        metres they won't be notified if they have passed that altitude again
                     */
                    if (current.altitude.toDouble() >= 2500) {
                        if (!altitudeSicknessFlag) {
                            stringBuilder.append("Risk of altitude sickness.\n")
                        }
                        altitudeSicknessFlag = true
                    }

                    if (current.altitude.toDouble() <= 2500 && altitudeSicknessFlag) {
                        altitudeSicknessFlag = false
                    }

                    if (current.temperature.toInt() - past.temperature.toInt() >= 3 &&
                        current.humidity.toDouble() - past.humidity.toDouble() <= -3.0
                    ) {
                        stringBuilder.append("Suspected fire in local area.\n")
                    }

                    Log.d(TAG, "Printing timeList = " + eventList[0].key.toString())


                    Log.d("Jumble", "$timestampConvertedToTime ${current.dbNodeId} ${stringBuilder.toString()}")

                    /*
                        This will check if any concerning metrics were added to the stringBuilder.
                        If not, it won't be added to the notifications.
                     */

                    if (stringBuilder.isNotEmpty()) {
                        //Setting the current node id
                        adapter.nodeAlerts = adapter.nodeAlerts.plus(current.dbNodeId)
                        adapter.timeOfNotificationTrigger.add(timestampConvertedToTime)
                        WorkerMetrics.concerningNodes.add(current.dbNodeId)
                        adapter.description.add(stringBuilder.toString())
                        adapter.notifyDataSetChanged()
                    } else {
                        WorkerMetrics.concerningNodes.remove(current.dbNodeId)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }
        databaseReference.addListenerForSingleValueEvent(valueEventListener)
    }

    // Additional security check if the user gets past the sign in screen
    private fun isAuthenticated(){
        if(firebaseAuth.currentUser == null){
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            Log.d(TAG, "Redirected to sign in activity because user is not authenticated")
        }
    }
}
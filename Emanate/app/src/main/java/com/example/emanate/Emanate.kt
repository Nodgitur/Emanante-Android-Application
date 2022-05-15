package com.example.emanate

import android.app.Application
import androidx.work.*
import java.util.concurrent.TimeUnit

// This is the application class which is run when the application is created
class Emanate : Application() {
    override fun onCreate() {
        super.onCreate()

        myWorkManager()
    }

    /*
        When the app is created this is the function used to display a notification to the user every
        15 minutes
     */
    private fun myWorkManager(){
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .build()

        val myRequest = PeriodicWorkRequest.Builder(
            MyWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).setConstraints(constraints)
            .build()

        /*
            The minimum interval is 15 minutes. Even though it may be limiting in an emergency this
            can be seen as a good aspect, as it prevents the app from otherwise draining the battery
         */

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "15_minute_notification",
                ExistingPeriodicWorkPolicy.KEEP,
                myRequest
            )
    }
}

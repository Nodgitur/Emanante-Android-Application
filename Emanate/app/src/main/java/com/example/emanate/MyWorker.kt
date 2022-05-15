package com.example.emanate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(context: Context, workerParameters: WorkerParameters):
Worker(context, workerParameters){

    companion object{
        const val CHANNEL_ID = "emanate_node_warning"
        const val NOTIFICATION_ID = 201
        private const val TAG = "MyWorkerActivity"
    }

    override fun doWork(): Result {
        Log.d(TAG, "doWork: Success function called")

        if(WorkerMetrics.concerningNodes.isEmpty()){
            return Result.success()
        }

        showNotification()

        return Result.success()
    }

    /*
        Inside this function is the setup for the notifications across devices. Although importance
        of the notifications are determined as high, this may not always be the case as other
        processes in the system can overwrite the priority
     */
    private fun showNotification(){

        val intent = Intent(applicationContext,
        NotificationsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        //A pending intent is for allowing the application to execute a predefined piece of code
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.emanate_logo)
            .setContentTitle("Warning(s) for ${WorkerMetrics.concerningNodes.size} node(s)")
            .setContentText("Check notifications in app")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Notification channels are needed for Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val channelName = "Emanate Nodes"
            val channelDescription = "Channel is for monitoring node metrics on expeditions"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, channelName, channelImportance).apply{
                description = channelDescription
            }

            val notificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(applicationContext)){
            notify(NOTIFICATION_ID, notification.build(), )
        }
    }
}
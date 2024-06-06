package com.delhomme.jobber.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.delhomme.jobber.Activity.Candidature.DetailsCandidatureActivity
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")
        val notificationTitle = intent.getStringExtra("TITLE")
        val notificationMessage = intent.getStringExtra("MESSAGE")

        val detailsIntent = Intent(context, DetailsCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidatureId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, detailsIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val channel = NotificationChannel("JOBBER_CHANNEL", "Jobber Notifications", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Channel for Jobber notifications"
        }
        notificationManager.createNotificationChannel(channel)

        /*val builder = NotificationCompat.Builder(context, "JOBBER_CHANNEL")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notificationTitle ?: "Notification Title")
            .setContentText(notificationMessage ?: "Notification Message")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1001, builder.build())
        }*/
    }
}

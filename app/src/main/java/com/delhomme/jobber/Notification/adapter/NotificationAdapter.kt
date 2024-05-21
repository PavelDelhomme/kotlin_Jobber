package com.delhomme.jobber.Notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Notification.model.Notification
import com.delhomme.jobber.R
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationAdapter(
    private var notifications: List<Notification>,
    private val onDeleteClick: (Notification) -> Unit,
    private val onMarkAsReadClick: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.notificationTitle)
        private val message: TextView = view.findViewById(R.id.notificationMessage)
        private val date: TextView = view.findViewById(R.id.notificationDate)
        private val btnDelete: MaterialButton = view.findViewById(R.id.btnDeleteNotification)
        private val btnMarkAsRead: MaterialButton = view.findViewById(R.id.btnMarkAsRead)

        fun bind(notification: Notification, onDeleteClick: (Notification) -> Unit, onMarkAsReadClick: (Notification) -> Unit) {
            title.text = notification.titre
            message.text = notification.message
            date.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(notification.date)

            btnDelete.setOnClickListener { onDeleteClick(notification) }
            btnMarkAsRead.setOnClickListener { onMarkAsReadClick(notification) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position], onDeleteClick, onMarkAsReadClick)
    }

    override fun getItemCount(): Int = notifications.size

    fun updateNotifications(newList: List<Notification>) {
        notifications = newList
        notifyDataSetChanged()
    }
}

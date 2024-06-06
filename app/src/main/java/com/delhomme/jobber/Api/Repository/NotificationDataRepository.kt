package com.delhomme.jobber.Api.Repository

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.delhomme.jobber.Notification.model.Notification

class NotificationDataRepository(context: Context) : BaseDataRepository<Notification>(context, "notifications") {

    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)

    override fun updateOrAddItem(mutableItems: MutableList<Notification>, item: Notification) {
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
        saveItemsToPrefs(mutableItems)
    }

    fun getNotifications(): List<Notification> {
        return items?.sortedByDescending { it.date } ?: emptyList()
    }

    fun deleteNotification(notification: Notification) {
        deleteItem { it.id == notification.id }
        notifyDataChanged()
    }

    fun markNotificationAsRead(notification: Notification) {
        deleteNotification(notification)
    }

    private fun notifyDataChanged() {
        val intent = Intent("com.jobber.NOTIFICATION_LIST_UPDATED")
        localBroadcastManager.sendBroadcast(intent)
    }
}
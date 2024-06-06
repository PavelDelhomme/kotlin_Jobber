package com.delhomme.jobber.Fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Adapter.NotificationAdapter
import com.delhomme.jobber.Api.Repository.NotificationDataRepository
import com.delhomme.jobber.Notification.adapter.NotificationAdapter
import com.delhomme.jobber.R

class FragmentNotifications : Fragment() {
    private lateinit var adapter: NotificationAdapter
    private lateinit var notificationDataRepository: NotificationDataRepository

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            adapter.updateNotifications(notificationDataRepository.getNotifications())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Notifications"

        notificationDataRepository = NotificationDataRepository(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewNotifications)
        adapter = NotificationAdapter(
            notificationDataRepository.getNotifications(),
            onDeleteClick = { notification -> notificationDataRepository.deleteNotification(notification) },
            onMarkAsReadClick = { notification -> notificationDataRepository.markNotificationAsRead(notification) }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(updateReceiver, IntentFilter("com.jobber.NOTIFICATION_LIST_UPDATED"))
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(updateReceiver, IntentFilter("com.jobber.NOTIFICATION_LIST_UPDATED"))
        adapter.updateNotifications(notificationDataRepository.getNotifications())
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(updateReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(updateReceiver)
    }
}

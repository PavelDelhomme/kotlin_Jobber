// AppelAdapter.kt
package com.delhomme.jobber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.models.Appel

class AppelAdapter(private val appels: List<Appel>) :
        RecyclerView.Adapter<AppelAdapter.AppelViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppelViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_appel, parent, false)
                return AppelViewHolder(view)
        }

        override fun onBindViewHolder(holder: AppelViewHolder, position: Int) {
                val appel = appels[position]
                holder.bind(appel)
        }

        override fun getItemCount(): Int {
                return appels.size
        }

        class AppelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                private val txtDescription = itemView.findViewById<TextView>(R.id.txtDescription)
                private val txtDate = itemView.findViewById<TextView>(R.id.txtDate)

                fun bind(appel: Appel) {
                        txtDescription.text = appel.description
                        txtDate.text = appel.date
                }
        }
}

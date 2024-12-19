package com.example.aegishub2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TruckListAdapter(private val trucks: List<Truck>) : RecyclerView.Adapter<TruckListAdapter.TruckViewHolder>() {

    inner class TruckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val truckName: TextView = itemView.findViewById(R.id.truck_name)
        private val truckModel: TextView = itemView.findViewById(R.id.truck_model)
        private val truckCapacity: TextView = itemView.findViewById(R.id.truck_capacity)
        private val truckStatus: TextView = itemView.findViewById(R.id.truck_status)
        private val truckIcon: ImageView = itemView.findViewById(R.id.truck_icon)

        fun bind(truck: Truck) {
            truckName.text = truck.name
            truckModel.text = truck.model
            truckCapacity.text = truck.capacity
            truckStatus.text = truck.status
            truckIcon.setImageResource(truck.iconResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TruckViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.truck_data_item, parent, false)
        return TruckViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TruckViewHolder, position: Int) {
        holder.bind(trucks[position])
    }

    override fun getItemCount(): Int {
        return trucks.size
    }
}

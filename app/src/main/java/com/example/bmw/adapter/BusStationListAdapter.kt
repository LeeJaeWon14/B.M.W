package com.example.bmw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bmw.R
import com.example.bmw.network.dto.StationDTO

class BusStationListAdapter(private val busList: List<StationDTO>): RecyclerView.Adapter<BusStationListAdapter.BusStationListHolder>() {
    class BusStationListHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvStationName: TextView = view.findViewById(R.id.tv_bus_station_name)
        val rvBusList: RecyclerView = view.findViewById(R.id.rv_bus_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusStationListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bus_station_list_item, parent, false)
        return BusStationListHolder(view)
    }

    override fun onBindViewHolder(holder: BusStationListHolder, position: Int) {
        holder.tvStationName.text = busList[position].nodeName

    }

    override fun getItemCount(): Int {
        return busList.size
    }
}
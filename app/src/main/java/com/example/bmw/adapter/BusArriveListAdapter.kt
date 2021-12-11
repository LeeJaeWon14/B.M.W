package com.example.bmw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bmw.R
import com.example.bmw.network.dto.ArriveDTO

class BusArriveListAdapter(private val arriveList: List<ArriveDTO>) : RecyclerView.Adapter<BusArriveListAdapter.BusArriveListHolder>() {
    class BusArriveListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBusName: TextView = view.findViewById(R.id.tv_bus_arrive_name)
        val tvBusTime: TextView = view.findViewById(R.id.tv_bus_arrive_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusArriveListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bus_arrive_list_item, parent, false)
        return BusArriveListHolder(view)
    }

    override fun onBindViewHolder(holder: BusArriveListHolder, position: Int) {
        holder.apply {
            tvBusName.text = arriveList[position].routeNo.toString()
            tvBusTime.text = arriveList[position].arrTime.toString()
        }
    }

    override fun getItemCount(): Int {
        return arriveList.size
    }
}
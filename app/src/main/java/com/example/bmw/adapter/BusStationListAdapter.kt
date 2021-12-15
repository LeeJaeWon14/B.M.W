package com.example.bmw.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmw.R
import com.example.bmw.network.dto.SeoulDTO
import com.example.bmw.network.dto.StationDTO

class BusStationListAdapter() : RecyclerView.Adapter<BusStationListAdapter.BusStationListHolder>() {
    constructor(busList: Array<StationDTO>) : this() {
        this.busList = busList
    }
    constructor(seoulList: Array<SeoulDTO>) : this() {
        this.seoulList = seoulList
    }
    private var busList: Array<StationDTO>? = null
    private var seoulList: Array<SeoulDTO>? = null
    private lateinit var context: Context
    class BusStationListHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvStationName: TextView = view.findViewById(R.id.tv_bus_station_name)
        val rvBusList: RecyclerView = view.findViewById(R.id.rv_bus_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusStationListHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bus_station_list_item, parent, false)
        return BusStationListHolder(view)
    }

    override fun onBindViewHolder(holder: BusStationListHolder, position: Int) {
        holder.apply {
            // 서울 외 지역
            busList?.let {
                tvStationName.text = it[position].nodeName
                rvBusList.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = null
                }
            }
            // 서울
            seoulList?.let {
                tvStationName.text = it[position].stationNm
                rvBusList.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = null
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return (busList?.size ?: seoulList?.size)!!
    }
}
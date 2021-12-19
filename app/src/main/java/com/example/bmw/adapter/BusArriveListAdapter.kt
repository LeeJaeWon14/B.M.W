package com.example.bmw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.bmw.R
import com.example.bmw.network.dto.ArriveDTO

class BusArriveListAdapter(private val arriveList: List<ArriveDTO>?) : RecyclerView.Adapter<BusArriveListAdapter.BusArriveListHolder>() {
    class BusArriveListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBusName: TextView = view.findViewById(R.id.tv_bus_arrive_name)
        val tvBusTime: TextView = view.findViewById(R.id.tv_bus_arrive_time)
        val tvNoArriveInfo: TextView = view.findViewById(R.id.tv_no_arrive_info)
        val arriveLayout: LinearLayout = view.findViewById(R.id.arrive_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusArriveListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bus_arrive_list_item, parent, false)
        return BusArriveListHolder(view)
    }

    override fun onBindViewHolder(holder: BusArriveListHolder, position: Int) {
        holder.apply {
            arriveList?.let {
//                arriveLayout.isVisible = true
//                tvNoArriveInfo.isVisible = false
                viewVisibleSet(arriveLayout)
                viewVisibleSet(tvNoArriveInfo)
                tvBusName.text = it[position].routeNo.toString()
                tvBusTime.text = it[position].arrTime.toString()
            } ?: run {
//                arriveLayout.isVisible = false
//                tvNoArriveInfo.isVisible = true
                viewVisibleSet(arriveLayout)
                viewVisibleSet(tvNoArriveInfo)
            }
        }
    }

    override fun getItemCount(): Int {
        arriveList?.let {
            return@let it.size
        }
        return 1
    }

    private fun viewVisibleSet(view : View) {
        view.isVisible = !view.isVisible
    }
}
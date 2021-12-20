package com.example.bmw.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.bmw.R
import com.example.bmw.network.dto.ArriveDTO
import com.example.bmw.util.MyLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BusArriveListAdapter(private val arriveList: List<ArriveDTO>?) : RecyclerView.Adapter<BusArriveListAdapter.BusArriveListHolder>() {
    private lateinit var context: Context
    class BusArriveListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBusName: TextView = view.findViewById(R.id.tv_bus_arrive_name)
        val tvBusTime: TextView = view.findViewById(R.id.tv_bus_arrive_time)
        val tvNoArriveInfo: TextView = view.findViewById(R.id.tv_no_arrive_info)
        val arriveLayout: LinearLayout = view.findViewById(R.id.arrive_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusArriveListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bus_arrive_list_item, parent, false)
        context = parent.context
        return BusArriveListHolder(view)
    }

    override fun onBindViewHolder(holder: BusArriveListHolder, position: Int) {
        holder.apply {
            arriveList?.let {
                MyLogger.i("This item is >> Bus No.${it[position].routeNo}, position is $position, size is ${it.size}")
                tvBusName.text = it[position].routeNo.plus(" 번")
                tvBusTime.text = it[position].arrTime.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    while(true) {
                        delay(1000)
                        (context as Activity).runOnUiThread {
                            tvBusTime.text = (tvBusTime.text.toString().toLong() -1).toString()
                        }
                    }
                }
            } ?: run {
                viewVisibleSet(arriveLayout)
                viewVisibleSet(tvNoArriveInfo)
            }
        }
    }

    override fun getItemCount(): Int {
        // when size of arriveList is null, return 1
        return arriveList?.size ?: 1
    }

    private fun viewVisibleSet(view : View) {
        view.isVisible = !view.isVisible
    }
}
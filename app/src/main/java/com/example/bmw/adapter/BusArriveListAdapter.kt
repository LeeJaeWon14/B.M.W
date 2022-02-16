package com.example.bmw.adapter

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
import com.example.bmw.util.MyDateUtil
import kotlinx.coroutines.*

class BusArriveListAdapter(private val arriveList_: List<ArriveDTO>?) : RecyclerView.Adapter<BusArriveListAdapter.BusArriveListHolder>() {
    private val arriveList = arriveList_?.let { checkDistinct(it) }
    private lateinit var context: Context
    private var preTime = mutableListOf<Long>()
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
                tvBusName.text = it[position].routeNo.plus(" 번")
                preTime.add(position, it[position].arrTime)
                CoroutineScope(Dispatchers.Default).launch {
                    while(true) {
                        withContext(Dispatchers.Main) {
                            if(preTime[position] < 1) {
                                notifyDataSetChanged()
                                arriveList.removeAt(position)
                                preTime.removeAt(position)
                                // TODO: Activity에서 Adapter를 새로고침하기 ..?
                            }
                            else {
                                tvBusTime.text = run {
                                    preTime.set(position, preTime[position] -1)
                                    MyDateUtil.convertSec(preTime[position])
                                }
                            }
                            delay(1000)
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

    private fun checkDistinct(arriveList: List<ArriveDTO>) : MutableList<ArriveDTO> {
        var prevDto: ArriveDTO? = null
        val result: MutableList<ArriveDTO> = mutableListOf()
        for(idx in arriveList.indices) {
            if(arriveList[idx].routeNo == prevDto?.routeNo) {
                result.add(arriveList[idx])
            }
            else {
                prevDto = arriveList[idx]
            }
        }
        return result
    }
}
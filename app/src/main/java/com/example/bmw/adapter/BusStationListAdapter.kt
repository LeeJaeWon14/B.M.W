package com.example.bmw.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmw.R
import com.example.bmw.network.NetworkConstants
import com.example.bmw.network.RetroClient
import com.example.bmw.network.dto.ArriveDTO
import com.example.bmw.network.dto.ArriveResponse
import com.example.bmw.network.dto.SeoulDTO
import com.example.bmw.network.dto.StationDTO
import com.example.bmw.network.service.BusService
import com.example.bmw.util.MyLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    private lateinit var arriveInfo: ArriveDTO
    private var retryCount: Int = 0
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
                    CoroutineScope(Dispatchers.IO).launch {
                        val service = RetroClient.getInstance().create(BusService::class.java)
                        val call = service.getArriveInfo(NetworkConstants.BUS_STATION_SERVICE_KEY, it[position].cityCode.toString(), it[position].nodeId.toString())
                        call.enqueue(object : Callback<ArriveResponse> {
                            override fun onResponse(call: Call<ArriveResponse>, response: Response<ArriveResponse>) {
//                            MyLogger.e("Rest response is ${response.body()}")
                                response.body()?.body?.items?.item?.let {
                                    layoutManager = LinearLayoutManager(context)
                                    adapter = BusArriveListAdapter(it)
                                } ?: run {
                                    MyLogger.e("Rest response is null, reqeust is ${call.request()}")
                                    layoutManager = LinearLayoutManager(context)
                                    adapter = BusArriveListAdapter(null)
                                }
                            }

                            override fun onFailure(call: Call<ArriveResponse>, t: Throwable) {
                                MyLogger.e("Rest failure ${t.message}")
                            }
                        })
                    }
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

    private fun initArriveList(station: StationDTO) {

    }
}
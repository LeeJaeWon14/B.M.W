package com.example.bmw.activity

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmw.R
import com.example.bmw.adapter.BusStationListAdapter
import com.example.bmw.databinding.ActivityMainBinding
import com.example.bmw.model.LocationViewModel
import com.example.bmw.network.NetworkConstants
import com.example.bmw.network.RetroClient
import com.example.bmw.network.dto.ServiceResult
import com.example.bmw.network.dto.Station
import com.example.bmw.network.service.BusService
import com.example.bmw.util.MyDateUtil
import com.example.bmw.util.MyLogger
import com.example.bmw.util.VibrateManager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        actionBar?.hide()
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        // viewModel observe to update
        observeViewModel()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        checkPermission()
        init()
    }

    //permission check
    //출처 : https://github.com/ParkSangGwon/TedPermission
    private fun checkPermission() {
        val permissionListener : PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() { //권한 있음
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    initLocation()
                }
                else {
                    val dlg = AlertDialog.Builder(this@MainActivity)
                    dlg.setMessage(getString(R.string.str_gps_disabled))
                    dlg.setPositiveButton("활성화", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            // move to setting for enable gps
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                    })
                    dlg.setNegativeButton("취소", null)
                    dlg.setCancelable(false)
                    dlg.show()
                }
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) { //권한 없음
                Toast.makeText(this@MainActivity, getString(R.string.str_permission_check_msg), Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed(Runnable { finishAffinity() }, 1000)
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener) //Listener set
            .setDeniedMessage(getString(R.string.str_permission_denied_message)) //DeniedMessage (Do not granted)
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION) //Granted
            .check()
    }

    private fun init() {
        binding.apply {
            rvBusStationList.layoutManager = LinearLayoutManager(this@MainActivity)
            slBusStationList.apply {
                setProgressBackgroundColorSchemeColor(getColor(R.color.primary))
                setColorSchemeColors(getColor(R.color.primary_dark))
                setOnRefreshListener {
                    initLocation()
                    isRefreshing = false
                }
            }
        }
    }

    private fun initLocation() {
        // GPS 로 캐싱된 위치가 없다면 Network 에서 가져옴
        try {
            // set Location Updates
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    6000,
                    300.0f,
                    gpsListener
            )
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            viewModel.location.value = location
        } catch (e: SecurityException) {
            // no-op
        }
    }

    private var time : Long = 0
    override fun onBackPressed() { //뒤로가기 클릭 시 종료 메소드
        if(System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis()
            Toast.makeText(this@MainActivity, getString(R.string.str_back_operation), Toast.LENGTH_SHORT).show()
        }
        else if(System.currentTimeMillis() - time < 2000) {
            this.finishAffinity()
        }
    }

    private val gpsListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            viewModel.location.value = location
            Toast.makeText(this@MainActivity, getString(R.string.str_updated_location), Toast.LENGTH_SHORT).show()
        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(this@MainActivity, "GPS가 꺼져있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, viewModel.address.value)
                startActivity(Intent.createChooser(intent, ""))
            }
            R.id.menu_info -> Toast.makeText(this, getString(R.string.menu_info), Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun observeViewModel() {
        viewModel.run {
            address.observe(this@MainActivity, Observer {
                binding.toolbar.subtitle = it
            })

            location.observe(this@MainActivity, Observer {
                MyLogger.i("latitude = ${it.latitude}, longitude = ${it.longitude}")

                // toolbar initialize
                binding.toolbar.apply {
                    title = MyDateUtil.getDate(MyDateUtil.HANGUEL)

                    // get address for subtitle from GeoCoder
                    val builder = StringBuilder()
                    Thread {
                        val address = Geocoder(this@MainActivity).getFromLocation(it.latitude, it.longitude, 1)[0].getAddressLine(0).toString()
                        MyLogger.e("Now address >> $address")
                        val addressList = address.split(" ")

                        for (idx in 1 until addressList.size) {
                            builder.append("${addressList[idx]} ")
                        }
                        viewModel.address.postValue(builder.toString())

                        CoroutineScope(Dispatchers.IO).launch {
                            // 현재 위치가 서울인 경우
                            if (builder.toString().contains("서울") || builder.toString().contains("Seoul")) {
                                val service = Retrofit.Builder()
                                        .baseUrl(NetworkConstants.BASE_URL_SEOUL)
                                        .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
                                        .build().create(BusService::class.java)
                                val call = service?.getNearStationInSeoul(NetworkConstants.BUS_STATION_SERVICE_KEY, it.longitude, it.latitude)
                                call?.enqueue(object : Callback<ServiceResult> {
                                    override fun onResponse(
                                            call: Call<ServiceResult>,
                                            response: Response<ServiceResult>
                                    ) {
                                        if (response.isSuccessful) {
                                            MyLogger.i("Rest success, response is ${response.body()}")
                                            seoulList.postValue(response.body()?.msgBody?.itemList)
                                            listVisibleCheck(getString(R.string.str_not_supported_seoul))
                                        } else {
                                            MyLogger.e("Rest respone not success, code is ${response.code()} and request is here ${response.raw().request()}")
                                            listVisibleCheck(getString(R.string.str_get_near_station_null_msg))
                                        }
                                    }

                                    override fun onFailure(call: Call<ServiceResult>, t: Throwable) {
                                        if (t.message == "Missing closing '>' character in </ServiceResult at path /ServiceResult/text()") {
                                            call.clone().enqueue(this)
                                        } else {
                                            listVisibleCheck(getString(R.string.str_get_near_station_fail_msg))
                                            MyLogger.e("Rest failure ${t.message}")
                                            MyLogger.e("Rest failure ${call.request()}")
                                        }
                                    }
                                })
                            }
                            // 서울 외인 경우
                            else {
                                val service = RetroClient.getInstance().create(BusService::class.java)
                                val call = service?.getNearStation(NetworkConstants.BUS_STATION_SERVICE_KEY, it.latitude, it.longitude)
                                call?.enqueue(object : Callback<Station> {
                                    override fun onResponse(call: Call<Station>, response: Response<Station>) {
                                        if (response.isSuccessful) {
                                            stationList.postValue(response.body()?.body?.items?.item)
                                            listVisibleCheck(null)
                                        } else {
                                            MyLogger.e("Rest respone not success, code is ${response.code()} and request is here ${response.raw().request()}")
                                            listVisibleCheck(getString(R.string.str_get_near_station_null_msg))
                                        }
                                    }

                                    override fun onFailure(call: Call<Station>, t: Throwable) {
                                        when (t.message) {
                                            getString(R.string.str_rest_fail_message_1) -> {
                                                // When occurred unexpected end of stream, retry call
//                                                but we don't have limit retry count yet, will adding that.
                                                call.clone().enqueue(this)
                                            }
                                            getString(R.string.str_rest_fail_message_2) -> {
                                                // Checked not supported area: 부산
                                                listVisibleCheck(getString(R.string.str_not_supported_area))
                                                binding.rvBusStationList.adapter = null
                                            }
                                            else -> {
                                                listVisibleCheck(getString(R.string.str_get_near_station_fail_msg))
                                                MyLogger.e("Rest failure ${t.message}")
                                                MyLogger.e("Rest failure ${call.request()}")
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }.start()
                }



                VibrateManager.runVibrate(
                        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator,
                        longArrayOf(100, 200, 100, 200),
                        VibrateManager.NOT_REPEAT
                )
            })

            stationList.observe(this@MainActivity, Observer {
                binding.rvBusStationList.adapter = BusStationListAdapter(it.toTypedArray())
            })

            seoulList.observe(this@MainActivity, Observer {
                binding.rvBusStationList.adapter = BusStationListAdapter(it.toTypedArray())
            })

            arriveList.observe(this@MainActivity, Observer {

            })
        }
    }

    private fun listVisibleCheck(msg: String?) {
        msg?.let {
            binding.apply {
                llBusStationList.isVisible = false
                tvErrMsg.isVisible = true
                tvErrMsg.text = msg
            }
        } ?: run {
            binding.apply {
                llBusStationList.isVisible = true
                tvErrMsg.isVisible = false
            }
        }
    }
}
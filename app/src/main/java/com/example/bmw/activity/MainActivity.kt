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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmw.R
import com.example.bmw.adapter.BusStationListAdapter
import com.example.bmw.databinding.ActivityMainBinding
import com.example.bmw.model.LocationViewModel
import com.example.bmw.model.SampleValue
import com.example.bmw.network.NetworkConstants
import com.example.bmw.network.RetroClient
import com.example.bmw.network.dto.CityDTO
import com.example.bmw.network.service.BusService
import com.example.bmw.util.MyDateUtil
import com.example.bmw.util.MyLogger
import com.example.bmw.util.VibrateManager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        viewModel.location.observe(this, Observer {
            binding.toolbar.subtitle = it
        })
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        checkPermission()

        selectCity()
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
            rvBusStationList.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = BusStationListAdapter(SampleValue.getSampleList())
            }
            slBusStationList.apply {
                setProgressBackgroundColorSchemeColor(getColor(R.color.purple_500))
                setColorSchemeColors(getColor(R.color.white))
                setOnRefreshListener {
                    rvBusStationList.adapter = BusStationListAdapter(SampleValue.getSampleList())
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

            location?.let {
                // toolbar initialize
                binding.toolbar.apply {
                    title = MyDateUtil.getDate(MyDateUtil.HANGUEL)

                    // get address for subtitle from GeoCoder
                    val builder = StringBuilder()
                    Thread {
                        val address = Geocoder(this@MainActivity).getFromLocation(it.latitude, it.longitude, 1)[0].getAddressLine(0).toString()
                        val addressList = address.split(" ")

                        for(idx in 1 until addressList.size) {
                            builder.append("${addressList[idx]} ")
                        }
                        viewModel.location.postValue(builder.toString())
                    }.start()
                }
            } ?: run {
                Toast.makeText(this@MainActivity, getString(R.string.str_need_network_msg), Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed(Runnable { finishAffinity() }, 1000)
            }

            // TODO add BUS API

            VibrateManager.runVibrate(
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator,
                longArrayOf(100, 200, 100, 200),
                VibrateManager.NOT_REPEAT
            )
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
            MyLogger.i("latitude = ${location.latitude}, longitude = ${location.longitude}")
            Toast.makeText(this@MainActivity, getString(R.string.str_updated_location), Toast.LENGTH_SHORT).show()
            init()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {
//                Toast.makeText(this, getString(R.string.menu_share), Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, viewModel.location.value)
                startActivity(Intent.createChooser(intent, "Shared memo"))
            }
            R.id.menu_info -> Toast.makeText(this, getString(R.string.menu_info), Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun selectCity() {
        CoroutineScope(Dispatchers.IO).launch {
            val service = RetroClient.getInstance().create(BusService::class.java)
            val call = service?.getCityList(NetworkConstants.BUS_STATION_SERVICE_KEY)
            call?.enqueue(object : Callback<List<CityDTO>> {
                override fun onResponse(
                    call: Call<List<CityDTO>>,
                    response: Response<List<CityDTO>>
                ) {
                    if(response.isSuccessful) {
                        MyLogger.i("rest success >> ${response.body()}")
                    }
                    else {
                        MyLogger.e("rest response error >> code is ${response.code()} and request here, ${response.raw().request()}")
                    }
                }

                override fun onFailure(call: Call<List<CityDTO>>, t: Throwable) {
                    MyLogger.e("rest fail >> ${t.message}")
                }
            })
        }
    }
}
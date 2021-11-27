package com.example.bmw.activity

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmw.R
import com.example.bmw.adapter.BusStationListAdapter
import com.example.bmw.databinding.ActivityMainBinding
import com.example.bmw.model.SampleValue
import com.example.bmw.util.MyDateUtil
import com.example.bmw.util.MyLogger
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager

//    val PERMISSIONS = arrayOf(
//        android.Manifest.permission.ACCESS_FINE_LOCATION,
//        android.Manifest.permission.ACCESS_COARSE_LOCATION
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        actionBar?.hide()
        setSupportActionBar(binding.toolbar)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkPermission()

        init()

    }

    // will add Location Callback .

    //permission check
    //출처 : https://github.com/ParkSangGwon/TedPermission
    private fun checkPermission() {
        val permissionListener : PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() { //권한 있음
//                Toast.makeText(this@MainActivity, "권한 허용", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MainActivity, "권한을 설정해 주세요.", Toast.LENGTH_SHORT).show()
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

    private fun getAddress(latitude: Double, longitude: Double) : String {
        val address = Geocoder(this@MainActivity).getFromLocation(latitude, longitude, 1)[0].getAddressLine(0).toString()
        val addressList = address.split(" ")
        val builder = StringBuilder()
        for(idx in 1 until  addressList.size) {
           builder.append("${addressList[idx]} ")
        }
        return builder.toString()
    }

    private fun initLocation() {
        // GPS 로 캐싱된 위치가 없다면 Network 에서 가져옴
        try {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location?.let {
//                            Toast.makeText(this@MainActivity, "${it.longitude} / ${it.latitude}", Toast.LENGTH_SHORT).show()
                binding.toolbar.apply {
                    title = MyDateUtil.getDate(MyDateUtil.HANGUEL)
                    subtitle = getAddress(it.latitude, it.longitude)
                }
            }
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
        }
    }
}
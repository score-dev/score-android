package com.team.score

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.team.score.Group.GroupFragment
import com.team.score.Home.HomeFragment
import com.team.score.Record.RecordFragment
import com.team.score.Utils.DistanceUtil
import com.team.score.Utils.MyApplication
import com.team.score.Utils.TimeUtil
import com.team.score.Utils.TimerManager
import com.team.score.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var elapsedSeconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerRunnable: Runnable

    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    var isTracking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.run {
            fabRecord.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, RecordFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationView()
    }

    private fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, HomeFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.menu_record -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, RecordFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.menu_group -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, GroupFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                else -> false
            }
        }
    }

    fun hideKeyboard() {
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                currentFocusView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun hideBottomNavigation(state: Boolean) {
        if (state) {
            binding.run {
                bottomNavigationView.visibility = View.GONE
                fabRecord.visibility = View.GONE
            }
        } else  {
            binding.run {
                bottomNavigationView.visibility = View.VISIBLE
                fabRecord.visibility = View.VISIBLE
            }
        }
    }

    fun initTracking() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 3000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    DistanceUtil.onLocationUpdate(location)
                }
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startTracking() {
        if (TimerManager.startedAtMillis == null) {
            val now = System.currentTimeMillis()
            TimerManager.startedAtMillis = now
            TimerManager.startedAtIso = TimeUtil.convertMillisToIso(now)
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        elapsedSeconds = MyApplication.recordTimer
        timerRunnable = object : Runnable {
            override fun run() {
                elapsedSeconds++
                MyApplication.recordTimer = elapsedSeconds
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timerRunnable)

        TimerManager.isRunning = true
        isTracking = true
    }

    fun stopTracking() {
        if (::timerRunnable.isInitialized) {
            handler.removeCallbacks(timerRunnable)
        }
        fusedLocationClient.removeLocationUpdates(locationCallback)

        TimerManager.completedAtMillis = System.currentTimeMillis()
        TimerManager.completedAtIso = TimeUtil.convertMillisToIso(TimerManager.completedAtMillis!!)
        TimerManager.isRunning = false
        isTracking = false
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun toggleTracking(): Boolean {
        return if (isTracking) {
            stopTracking()
            false
        } else {
            startTracking()
            true
        }
    }

    fun resetTracking() {
        // 1. 타이머 중지
        if (::timerRunnable.isInitialized) {
            handler.removeCallbacks(timerRunnable)
        }

        // 2. 위치 추적 중지
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

        // 3. 상태 초기화
        TimerManager.reset() // startedAt, completedAt 등 모두 null로 설정된다고 가정
        isTracking = false
        elapsedSeconds = 0

        // 4. 기록 데이터 초기화
        MyApplication.recordTimer = 0
        MyApplication.totalDistance = 0
        MyApplication.locationList.clear()
    }
}
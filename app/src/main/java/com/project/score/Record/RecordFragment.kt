package com.project.score.Record

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.TimeUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.databinding.FragmentRecordBinding
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import com.project.score.API.TokenManager
import com.project.score.Mypage.Setting.SettingFragment
import com.project.score.Utils.MyApplication
import com.project.score.Utils.TimeUtil
import com.project.score.Utils.TimeUtil.convertMillisToIso
import com.project.score.Utils.TimerManager


class RecordFragment : Fragment() {

    lateinit var binding: FragmentRecordBinding
    lateinit var mainActivity: MainActivity

    // storage 권한 처리에 필요한 변수
    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99
    private val LOCATION_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    private var elapsedSeconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerRunnable: Runnable

    var isStart = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecordBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initView()

        binding.run {
            buttonCamera.setOnClickListener {
//                CallCamera()
            }
            buttonMap.setOnClickListener {
                if(TimerManager.startedAtIso != null) {
                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, RecordMapFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
            buttonRecord.setOnClickListener {
                isStart = !isStart

                if (isStart) {
                    buttonRecord.setImageResource(R.drawable.ic_temporary_stop)

                    if (!TimerManager.isRunning) {
                        TimerManager.isRunning = true
                        if(TimerManager.startedAtMillis == null) {
                            val now = System.currentTimeMillis()
                            TimerManager.startedAtMillis = now
                            TimerManager.startedAtIso = convertMillisToIso(now)
                            TimerManager.isRunning = true
                        }
                    }

                    startTimer()
                } else {
                    buttonRecord.setImageResource(R.drawable.ic_start)
                    TimerManager.isRunning = false
                    stopTimer()
                }
            }
            buttonStop.setOnClickListener {
                if(TimerManager.startedAtIso != null) {
                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, RecordTodayExerciseFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        return binding.root
    }

    private fun startTimer() {
        binding.buttonStop.isEnabled = TimerManager.startedAtIso != null
        
        elapsedSeconds = if(TimerManager.startedAtMillis != null) { MyApplication.recordTimer } else { 0 }

        timerRunnable = object : Runnable {
            override fun run() {
                elapsedSeconds++
                MyApplication.recordTimer = elapsedSeconds
                binding.textViewTimer.text = TimeUtil.formatRecordTime(elapsedSeconds)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timerRunnable)
    }


    private fun stopTimer() {
        if (::timerRunnable.isInitialized) {
            handler.removeCallbacks(timerRunnable)
        }

        val now = System.currentTimeMillis()
        TimerManager.completedAtMillis = now
        TimerManager.completedAtIso = convertMillisToIso(now)
        TimerManager.isRunning = false
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.run {
            toolbar.run {
                textViewHead.text = "기록하기"
                buttonBack.setOnClickListener {
                    MyApplication.recordTimer = 0
                    TimerManager.reset()
                    if (::timerRunnable.isInitialized) {
                        handler.removeCallbacks(timerRunnable)
                    }
                    fragmentManager?.popBackStack()
                }
            }

            isStart = TimerManager.isRunning
            if(isStart) {
                buttonRecord.setImageResource(R.drawable.ic_temporary_stop)
            } else {
                buttonRecord.setImageResource(R.drawable.ic_start)
            }
            textViewTimer.text = TimeUtil.formatRecordTime(elapsedSeconds)

            checkLocationPermission()

            buttonStop.isEnabled = TimerManager.startedAtIso != null
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionGranted = LOCATION_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(mainActivity, it) == PackageManager.PERMISSION_GRANTED
            }

            if (!permissionGranted) {
                ActivityCompat.requestPermissions(
                    mainActivity,
                    LOCATION_PERMISSIONS,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }

            return permissionGranted
        }

        return true // M 이하 버전은 자동 승인
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(mainActivity, "위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                // 위치 권한 승인 후 작업
            } else {
                Toast.makeText(mainActivity, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
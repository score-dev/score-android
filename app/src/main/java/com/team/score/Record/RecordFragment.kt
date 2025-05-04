package com.team.score.Record

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.FragmentRecordBinding
import com.team.score.Utils.MyApplication
import com.team.score.Utils.TimeUtil
import com.team.score.Utils.TimerManager
import java.io.File


class RecordFragment : Fragment() {

    lateinit var binding: FragmentRecordBinding
    lateinit var mainActivity: MainActivity

    private val LOCATION_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    var isStart = false

    private val timerUIHandler = Handler(Looper.getMainLooper())
    private lateinit var timerUIRunnable: Runnable

    private lateinit var imageUri: Uri
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {

            }
        }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecordBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initView()

        binding.run {
            buttonCamera.setOnClickListener {
                checkCameraPermissionAndOpen()
            }
            buttonMap.setOnClickListener {
                if (TimerManager.startedAtIso != null) {
                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, RecordMapFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
            buttonRecord.setOnClickListener {
                isStart = mainActivity.toggleTracking()
                binding.buttonRecord.setImageResource(
                    if (isStart) R.drawable.ic_temporary_stop else R.drawable.ic_start
                )
            }
            buttonStop.setOnClickListener {
                if (TimerManager.startedAtIso != null) {
                    // 타이머와 위치 추적 종료
                    mainActivity.stopTracking()
                    isStart = false
                    buttonRecord.setImageResource(R.drawable.ic_start)

                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, RecordTodayExerciseFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavigation(true)
    }


    fun initView() {
        mainActivity.run {
            initTracking()
        }

        binding.run {
            startTimerUIUpdater()

            toolbar.run {
                textViewHead.text = "기록하기"
                buttonBack.setOnClickListener {
                    stopTimerUIUpdater()
                    mainActivity.resetTracking()

                    fragmentManager?.popBackStack()
                }
            }

            if (isStart) {
                buttonRecord.setImageResource(R.drawable.ic_temporary_stop)
            } else {
                buttonRecord.setImageResource(R.drawable.ic_start)
            }
            textViewTimer.text = TimeUtil.formatRecordTime(MyApplication.recordTimer)

            checkLocationPermission()

            buttonStop.isEnabled = TimerManager.startedAtIso != null
        }
    }

    fun checkCameraPermissionAndOpen() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            cameraPermissionLauncher.launch(permission)
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionGranted = LOCATION_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(
                    mainActivity,
                    it
                ) == PackageManager.PERMISSION_GRANTED
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

    private fun startTimerUIUpdater() {
        timerUIRunnable = object : Runnable {
            override fun run() {
                binding.textViewTimer.text = TimeUtil.formatRecordTime(MyApplication.recordTimer)
                binding.buttonStop.isEnabled =
                    TimerManager.startedAtIso != null && MyApplication.locationList.size > 0
                timerUIHandler.postDelayed(this, 1000) // 1초마다 갱신
            }
        }
        timerUIHandler.post(timerUIRunnable)
    }

    private fun stopTimerUIUpdater() {
        if (::timerUIRunnable.isInitialized) {
            timerUIHandler.removeCallbacks(timerUIRunnable)
        }
    }

    fun openCamera() {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "camera_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // 갤러리 내 Pictures 폴더
        }

        val contentResolver = requireContext().contentResolver
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!

        takePictureLauncher.launch(imageUri)
    }
}
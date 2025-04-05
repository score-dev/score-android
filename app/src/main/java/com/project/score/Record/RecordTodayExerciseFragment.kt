package com.project.score.Record

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.project.score.MainActivity
import com.project.score.R
import com.project.score.Utils.MyApplication
import com.project.score.Utils.TimeUtil
import com.project.score.Utils.TimerManager
import com.project.score.databinding.FragmentRecordTodayExerciseBinding

class RecordTodayExerciseFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentRecordTodayExerciseBinding
    lateinit var mainActivity: MainActivity

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap

    private val CURRENT_LOCATION_CODE = 200
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private lateinit var locationSource: FusedLocationSource // 위치를 반환하는 구현체

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecordTodayExerciseBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.run {
            NaverMapSdk.getInstance(mainActivity).client =
                NaverMapSdk.NaverCloudPlatformClient("${com.project.score.BuildConfig.MAP_API_KEY}")

        }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE)

        return binding.root
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.run {
            textViewExerciseTime.text = TimeUtil.formatRecordTime(MyApplication.recordTimer)
            textViewExerciseDayValue.text = "${MyApplication.consecutiveDate}일째"

            val consecutiveDays = MyApplication.consecutiveDate.coerceIn(0, 7) // 0~7 사이로 제한

            val checkedDrawable = ContextCompat.getDrawable(mainActivity, R.drawable.ic_weekly_exercise_days)
            val consecutiveImageViews = listOf(
                binding.imageViewExerciseDay1,
                binding.imageViewExerciseDay2,
                binding.imageViewExerciseDay3,
                binding.imageViewExerciseDay4,
                binding.imageViewExerciseDay5,
                binding.imageViewExerciseDay6,
                binding.imageViewExerciseDay7
            )
            for (i in 0 until consecutiveDays) {
                consecutiveImageViews[i].setImageDrawable(checkedDrawable)
            }

            toolbar.run {
                textViewHead.text = "오늘의 운동 기록"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        initView()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(map: NaverMap) {
        Log.d("##", "onMapReady")
        naverMap = map

        // 지도 옵션 설정
        naverMap.run {
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
            isIndoorEnabled = true
            isNightModeEnabled = true
            uiSettings.run {
                isScaleBarEnabled = false
                isZoomControlEnabled = false
                isCompassEnabled = false
            }
        }

        // 위치 소스 연결
        naverMap.locationSource = locationSource

        // 확대 축소 범위 설정
        naverMap.maxZoom = 20.0
        naverMap.minZoom = 10.0

        // 마커 위치 설정
        val latitude = 37.2103
        val longitude = 127.2314

        /*
        var marker = Marker()
        marker.position = LatLng(latitude, longitude)
        marker.icon = OverlayImage.fromResource(R.drawable.ic_marker)
        marker.map = naverMap
         */

        // 초기 위치 및 줌 레벨 설정
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(
            LatLng(latitude, longitude),
            12.0 // 초기 줌 레벨
        ).animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

        // 지도 옵션 설정
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
        naverMap.isIndoorEnabled = true

        // 위치 추적 모드 설정
        naverMap.locationTrackingMode = LocationTrackingMode.None
    }

}
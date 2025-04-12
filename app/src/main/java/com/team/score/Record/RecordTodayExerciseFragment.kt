package com.team.score.Record

import android.os.Bundle
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
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import com.team.score.Utils.TimeUtil
import com.team.score.databinding.FragmentRecordTodayExerciseBinding
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModelProvider
import com.team.score.BuildConfig
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.DistanceUtil

class RecordTodayExerciseFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentRecordTodayExerciseBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }

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
                NaverMapSdk.NaverCloudPlatformClient("${com.team.score.BuildConfig.MAP_API_KEY}")

            buttonFeed.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, RecordFeedUploadFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE)

        return binding.root
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        viewModel.getWeather(mainActivity, BuildConfig.weather_api_key, MyApplication.locationList[0].latitude, MyApplication.locationList[0].longitude)

        binding.run {
            textViewExerciseTime.text = TimeUtil.formatRecordTime(MyApplication.recordTimer)
            textViewExerciseDayValue.text = "${MyApplication.consecutiveDate}일째"
            textViewExerciseDistanceValue.text = MyApplication.totalDistance.toString()
            var userWeight = if(MyApplication.userInfo?.weight != null) {
                MyApplication.userInfo?.weight
            } else {
                if(MyApplication.userInfo?.gender == "FEMALE") {
                    55
                } else if(MyApplication.userInfo?.gender == "MALE") {
                    74
                } else {
                    65
                }
            }
            textViewExerciseKcalValue.text =
                DistanceUtil.calculateKcal(userWeight!!, (MyApplication.recordTimer / 3600.0)).toString()

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
        naverMap = map

        // 기본 지도 설정
        naverMap.run {
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
            isIndoorEnabled = true
            isNightModeEnabled = true
            locationOverlay.isVisible = false
            uiSettings.run {
                isScaleBarEnabled = false
                isZoomControlEnabled = false
                isCompassEnabled = false
            }
            maxZoom = 20.0
            minZoom = 10.0
        }

        // 위치 소스 및 추적 모드 설정
        naverMap.locationSource = locationSource
//        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // 📍 초기 위치를 locationList 기준으로 설정
        if (MyApplication.locationList.isNotEmpty()) {
            val lastLatLng = MyApplication.locationList.last()
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(lastLatLng, 15.0).animate(CameraAnimation.Fly)
            naverMap.moveCamera(cameraUpdate)
        } else {
            // 🔁 fallback: 위치 받아올 때까지 기다렸다가 카메라 이동
            naverMap.addOnLocationChangeListener { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, 15.0).animate(CameraAnimation.Fly)
                naverMap.moveCamera(cameraUpdate)
            }
        }

        // 시작점 마커
        val startMarker = Marker().apply {
            position = MyApplication.locationList.first()
            icon = OverlayImage.fromResource(R.drawable.ic_map_start)
            width = Marker.SIZE_AUTO
            height = Marker.SIZE_AUTO
            anchor = android.graphics.PointF(0.5f, 0.5f) // 중심 정렬
            zIndex = 1
        }
        startMarker.map = naverMap

        if (MyApplication.locationList.size >= 2) {
            // 경로
            val path = PathOverlay().apply {
                coords = MyApplication.locationList
                color = "#FF6C3E".toColorInt()
                width = 8
                outlineWidth = 0
                zIndex = 0
            }
            path.map = naverMap

            // 끝점 마커
            val finishMarker = Marker().apply {
                position = MyApplication.locationList.last()
                icon = OverlayImage.fromResource(R.drawable.ic_map_finish)
                width = Marker.SIZE_AUTO
                height = Marker.SIZE_AUTO
                anchor = android.graphics.PointF(0.5f, 0.5f) // 중심 정렬
                zIndex = 1
            }
            finishMarker.map = naverMap
        }
    }
}
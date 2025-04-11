package com.team.score.Record

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
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
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentRecordMapBinding
import com.bumptech.glide.request.RequestOptions
import com.team.score.Utils.DistanceUtil
import com.team.score.Utils.TimeUtil


class RecordMapFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentRecordMapBinding
    lateinit var mainActivity: MainActivity

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap


    private val CURRENT_LOCATION_CODE = 200
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private lateinit var locationSource: FusedLocationSource // 위치를 반환하는 구현체

    private lateinit var marker: Marker
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var markerTimerRunnable: Runnable

    private val uiUpdateHandler = Handler(Looper.getMainLooper())
    private lateinit var uiUpdateRunnable: Runnable



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecordMapBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.run {
            NaverMapSdk.getInstance(mainActivity).client =
                NaverMapSdk.NaverCloudPlatformClient("${com.team.score.BuildConfig.MAP_API_KEY}")

        }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE)

        return binding.root
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.run {
            textViewDistance.text = "${MyApplication.totalDistance.toString()}m"
            textViewKcal.text = "${DistanceUtil.calculateKcal(MyApplication.userInfo?.weight ?: 0, MyApplication.recordTimer / 3600.0)}kcal"

            toolbar.run {
                textViewHead.text = "지도"
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
        stopUiUpdater()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        stopUiUpdater()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUiUpdater()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
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
            locationOverlay.isVisible = false
            uiSettings.run {
                isScaleBarEnabled = false
                isZoomControlEnabled = false
                isCompassEnabled = false
            }
        }


        // 확대 축소 범위 설정
        naverMap.maxZoom = 20.0
        naverMap.minZoom = 10.0

        marker = Marker()

        // 위치 소스 및 추적 모드 설정
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        naverMap.addOnLocationChangeListener { location ->
            val currentLocation = LatLng(location.latitude, location.longitude)

            val cameraUpdate = CameraUpdate.scrollAndZoomTo(currentLocation, 17.0).animate(CameraAnimation.Fly)
            naverMap.moveCamera(cameraUpdate)

            val timerText = TimeUtil.formatRecordTime(MyApplication.recordTimer)
            createCustomMarkerBitmap(mainActivity, timerText, MyApplication.userInfo?.profileImgUrl ?: "") { bitmap ->
                marker.icon = OverlayImage.fromBitmap(bitmap)
                marker.position = currentLocation
                marker.width = Marker.SIZE_AUTO
                marker.height = Marker.SIZE_AUTO
                marker.map = naverMap
            }

            // UI 자동 갱신 루프 시작
            startUiUpdater(currentLocation)
        }

        // 지도 옵션 설정
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
        naverMap.isIndoorEnabled = true
    }

    private fun startUiUpdater(currentLocation: LatLng) {
        uiUpdateRunnable = object : Runnable {
            override fun run() {
                // 마커 갱신
                val timerText = TimeUtil.formatRecordTime(MyApplication.recordTimer)
                createCustomMarkerBitmap(mainActivity, timerText, MyApplication.userInfo?.profileImgUrl ?: "") { bitmap ->
                    marker.icon = OverlayImage.fromBitmap(bitmap)
                    marker.position = currentLocation
                    marker.map = naverMap
                }

                // UI 갱신
                binding.textViewDistance.text = "${MyApplication.totalDistance}m"
                val hourTime = MyApplication.recordTimer / 3600.0
                binding.textViewKcal.text = "${DistanceUtil.calculateKcal(MyApplication.userInfo?.weight ?: 0, hourTime)}kcal"

                uiUpdateHandler.postDelayed(this, 1000)
            }
        }

        uiUpdateHandler.post(uiUpdateRunnable)
    }

    private fun stopUiUpdater() {
        if (::uiUpdateRunnable.isInitialized) {
            uiUpdateHandler.removeCallbacks(uiUpdateRunnable)
        }
    }



    fun createCustomMarkerBitmap(
        context: Context,
        timerText: String,
        profileUrl: String,
        onBitmapReady: (Bitmap) -> Unit
    ) {
        val markerView = LayoutInflater.from(context).inflate(R.layout.layout_marker, null)
        val timerView = markerView.findViewById<TextView>(R.id.textView_time)
        val imageView = markerView.findViewById<ImageView>(R.id.imageView_profile)

        timerView.text = timerText

        Glide.with(context)
            .asBitmap()
            .load(profileUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    imageView.setImageBitmap(resource)

                    markerView.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)

                    val bitmap = Bitmap.createBitmap(markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    markerView.draw(canvas)

                    onBitmapReady(bitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

}
package com.project.score.Record

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.BuildConfig
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
import com.project.score.databinding.FragmentRecordMapBinding
import androidx.core.graphics.createBitmap
import com.bumptech.glide.request.RequestOptions
import com.project.score.Utils.TimeUtil
import com.project.score.Utils.TimerManager


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
                NaverMapSdk.NaverCloudPlatformClient("${com.project.score.BuildConfig.MAP_API_KEY}")

        }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE)

        return binding.root
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.run {
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
        if (::markerTimerRunnable.isInitialized) {
            handler.removeCallbacks(markerTimerRunnable)
        }
        mapView.onPause()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        if (::markerTimerRunnable.isInitialized) {
            handler.removeCallbacks(markerTimerRunnable)
        }
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::markerTimerRunnable.isInitialized) {
            handler.removeCallbacks(markerTimerRunnable)
        }
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

        marker = Marker()


        val timerText = TimeUtil.formatRecordTime(MyApplication.recordTimer)

        createCustomMarkerBitmap(mainActivity, timerText, MyApplication.userInfo?.profileImgUrl ?: "") { bitmap ->
            marker = Marker()
            marker.icon = OverlayImage.fromBitmap(bitmap)
            marker.position =  LatLng(latitude, longitude)
            marker.width = Marker.SIZE_AUTO
            marker.height = Marker.SIZE_AUTO
            marker.map = naverMap
        }

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


        if(TimerManager.isRunning) {
            // 마커 타이머 시작
            markerTimerRunnable = object : Runnable {
                override fun run() {
                    val timerText = TimerManager.startedAtMillis?.let {
                        TimeUtil.formatRecordTime(MyApplication.recordTimer)
                    } ?: "00:00"

                    createCustomMarkerBitmap(mainActivity, timerText, MyApplication.userInfo?.profileImgUrl ?: "") { bitmap ->
                        marker = Marker()
                        marker.icon = OverlayImage.fromBitmap(bitmap)
                        marker.position =  LatLng(latitude, longitude)
                        marker.width = Marker.SIZE_AUTO
                        marker.height = Marker.SIZE_AUTO
                        marker.map = naverMap
                    }
                    handler.postDelayed(this, 1000)
                    MyApplication.recordTimer = MyApplication.recordTimer++
                }
            }
            handler.post(markerTimerRunnable)
        } else {
            val timerText = TimerManager.startedAtMillis?.let {
                TimeUtil.formatRecordTime(MyApplication.recordTimer)
            } ?: "00:00"

            createCustomMarkerBitmap(mainActivity, timerText, MyApplication.userInfo?.profileImgUrl ?: "") { bitmap ->
                marker = Marker()
                marker.icon = OverlayImage.fromBitmap(bitmap)
                marker.position =  LatLng(latitude, longitude)
                marker.width = Marker.SIZE_AUTO
                marker.height = Marker.SIZE_AUTO
                marker.map = naverMap
            }
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
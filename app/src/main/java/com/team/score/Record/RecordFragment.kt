package com.team.score.Record

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.FragmentRecordBinding
import com.team.score.Utils.MyApplication
import com.team.score.Utils.TimeUtil
import com.team.score.Utils.TimerManager


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

    var isStart = false

    private val timerUIHandler = Handler(Looper.getMainLooper())
    private lateinit var timerUIRunnable: Runnable



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
                isStart = mainActivity.toggleTracking()
                binding.buttonRecord.setImageResource(
                    if (isStart) R.drawable.ic_temporary_stop else R.drawable.ic_start
                )
                buttonStop.isEnabled = (TimerManager.startedAtIso != null && (MyApplication.locationList.size > 0))
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


    fun initView() {
        mainActivity.hideBottomNavigation(true)
        mainActivity.initTracking()

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

            if(isStart) {
                buttonRecord.setImageResource(R.drawable.ic_temporary_stop)
            } else {
                buttonRecord.setImageResource(R.drawable.ic_start)
            }
            textViewTimer.text = TimeUtil.formatRecordTime(MyApplication.recordTimer)

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

    private fun startTimerUIUpdater() {
        timerUIRunnable = object : Runnable {
            override fun run() {
                binding.textViewTimer.text = TimeUtil.formatRecordTime(MyApplication.recordTimer)
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


    // 카메라 권한, 저장소 권한
    // 요청 권한
//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        when(requestCode){
//            CAMERA_CODE -> {
//                for (grant in grantResults){
//                    if(grant != PackageManager.PERMISSION_GRANTED){
//                        Toast.makeText(mainActivity, "카메라 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//            STORAGE_CODE -> {
//                for(grant in grantResults){
//                    if(grant != PackageManager.PERMISSION_GRANTED){
//                        Toast.makeText(mainActivity, "저장소 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//        }
//    }
//
//    // 다른 권한등도 확인이 가능하도록
//    fun checkPermission(permissions: Array<out String>, type:Int):Boolean{
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            for (permission in permissions){
//                if(ContextCompat.checkSelfPermission(mainActivity, permission)
//                    != PackageManager.PERMISSION_GRANTED){
//                    ActivityCompat.requestPermissions(mainActivity, permissions, type)
//                    return false
//                }
//            }
//        }
//        return true
//    }
//
//    // 카메라 촬영 - 권한 처리
//    fun CallCamera(){
//        if(checkPermission(CAMERA, CAMERA_CODE) && checkPermission(STORAGE, STORAGE_CODE)){
//            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(itt, CAMERA_CODE)
//        }
//    }
//
//    // 사진 저장
//    fun saveFile(fileName:String, mimeType:String, bitmap: Bitmap): Uri?{
//
//        var CV = ContentValues()
//
//        // MediaStore 에 파일명, mimeType 을 지정
//        CV.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
//        CV.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
//
//        // 안정성 검사
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//            CV.put(MediaStore.Images.Media.IS_PENDING, 1)
//        }
//
//        // MediaStore 에 파일을 저장
//        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CV)
//        if(uri != null){
//            var scriptor = contentResolver.openFileDescriptor(uri, "w")
//
//            val fos = FileOutputStream(scriptor?.fileDescriptor)
//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//            fos.close()
//
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//                CV.clear()
//                // IS_PENDING 을 초기화
//                CV.put(MediaStore.Images.Media.IS_PENDING, 0)
//                contentResolver.update(uri, CV, null, null)
//            }
//        }
//        return uri
//    }
//
//    // 결과
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        val imageView = findViewById<ImageView>(R.id.avatars)
//
//        if(resultCode == Activity.RESULT_OK){
//            when(requestCode){
//                CAMERA_CODE -> {
//                    if(data?.extras?.get("data") != null){
//                        val img = data?.extras?.get("data") as Bitmap
//                        val uri = saveFile(RandomFileName(), "image/jpeg", img)
//                        imageView.setImageURI(uri)
//                    }
//                }
//                STORAGE_CODE -> {
//                    val uri = data?.data
//                    imageView.setImageURI(uri)
//                }
//            }
//        }
//    }
//
//    // 파일명을 날짜 저장
//    fun RandomFileName() : String{
//        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
//        return fileName
//    }
//
//    // 갤러리 취득
//    fun GetAlbum(){
//        if(checkPermission(STORAGE, STORAGE_CODE)){
//            val itt = Intent(Intent.ACTION_PICK)
//            itt.type = MediaStore.Images.Media.CONTENT_TYPE
//            startActivityForResult(itt, STORAGE_CODE)
//        }
//    }
}
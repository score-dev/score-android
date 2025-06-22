package com.team.score.Record

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.LinearLayout
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.Home.adapter.GroupRelayTodayUnexercisedMemberAdapter
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Record.adapter.FeedFrameAdpater
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.CalendarUtil.getAmPmAndTime
import com.team.score.Utils.CalendarUtil.getTodayDateFormatted
import com.team.score.Utils.CalendarUtil.getTodayFormatted
import com.team.score.Utils.GlobalApplication.Companion.firebaseAnalytics
import com.team.score.Utils.ImageUtil.captureAndSaveToUri
import com.team.score.Utils.ImageUtil.captureAndStoreImage
import com.team.score.Utils.MyApplication
import com.team.score.Utils.TimeUtil.calculateExerciseDurationWithEnglish
import com.team.score.databinding.FragmentRecordFeedImageBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class RecordFeedImageFragment : Fragment() {

    lateinit var binding: FragmentRecordFeedImageBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }

    var isImageUpload = false
    var imageUri: Uri? = null

    var selectedFrameIndex: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecordFeedImageBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the photo picker.
                if (uri != null) {
                    isImageUpload = true
                    imageUri = uri
                    Log.d("PhotoPicker", "Selected URI: $uri")

                    // 이미지 처리 및 압축
                    val resizedUri = convertResizeImage(uri) // 기존 이미지를 리사이징한 후 Uri 얻기
                    val compressedFile = File(resizedUri.path!!)

                    // 파일이 정상적으로 생성되었는지 확인
                    if (compressedFile.exists() && compressedFile.length() > 0) {
                        // RequestBody로 이미지 파일 생성
                        val requestFile: RequestBody =
                            compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

                        // MultipartBody.Part로 이미지 파일 준비
                        val feedImage = MultipartBody.Part.createFormData(
                            "file",  // 서버에서 기대하는 필드 이름
                            compressedFile.name, // 파일 이름
                            requestFile
                        )

                        // 이미지 파일 저장
                        MyApplication.recordFeedImage = feedImage
                        binding.run {
                            imageViewFeed.setImageURI(uri)
                            buttonUpload.isEnabled = true
                            layoutEmpty.visibility = View.GONE
                        }
                    } else {
                        Log.e("ImageCompression", "압축된 파일이 존재하지 않거나 비어 있습니다.")
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.run {
            imageViewFeed.setOnClickListener {
                firebaseAnalytics.logEvent("select_picture", null)

                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            buttonShare.setOnClickListener {
                if(isImageUpload) {
                    firebaseAnalytics.logEvent("click_share_to_SNS", null)

                    captureImage()

                    val intent = Intent(Intent.ACTION_SEND)

                    intent.type = ("image/*")
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                    startActivity(Intent.createChooser(intent, "Share img"))
                }
            }

            buttonUpload.setOnClickListener {
                firebaseAnalytics.logEvent("click_feed_final_upload", null)

                captureImage()

                viewModel.uploadFeed(mainActivity)
            }
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        initView()
    }

    fun captureImage() {
        val captureView = binding.layoutCaptureTarget  // 프레임까지 포함된 최상단 뷰
        captureAndStoreImage(captureView, requireContext())
        imageUri = captureAndSaveToUri(captureView, requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initView() {
        binding.run {
            val frames = listOf(R.drawable.img_frame1,R.drawable.img_frame2,R.drawable.img_frame3,R.drawable.img_frame4,R.drawable.img_frame5)

            val frameAdapter = FeedFrameAdpater(mainActivity, frames).apply {
                itemClickListener = object : FeedFrameAdpater.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        firebaseAnalytics.logEvent("select_frame", null)

                        layoutFrame1.layoutFrame.visibility = View.GONE
                        layoutFrame2.layoutFrame.visibility = View.GONE
                        layoutFrame3.layoutFrame.visibility = View.GONE
                        layoutFrame4.layoutFrame.visibility = View.GONE
                        layoutFrame5.layoutFrame.visibility = View.GONE

                        if (isImageUpload) {
                            selectedFrameIndex = position

                            when (position) {
                                0 -> layoutFrame1.layoutFrame.visibility = View.VISIBLE
                                1 -> layoutFrame2.layoutFrame.visibility = View.VISIBLE
                                2 -> layoutFrame3.layoutFrame.visibility = View.VISIBLE
                                3 -> layoutFrame4.layoutFrame.visibility = View.VISIBLE
                                4 -> layoutFrame5.layoutFrame.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                // selectedFrameIndex가 있다면 선택 반영
                selectedFrameIndex?.let {
                    setSelectedPosition(it)
                }
            }

            binding.recyclerViewFrame.run {
                adapter = frameAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }

            layoutFrame1.run {
                if(selectedFrameIndex == 0) {
                    layoutFrame.visibility = View.VISIBLE
                } else {
                    layoutFrame.visibility = View.GONE
                }
                textViewFrameToday.text = getTodayDateFormatted()
                textViewFrameDistance.text = "${(MyApplication.recordFeedInfo.distance / 1000.0).let { String.format("%.2f", it) }}km"
                textViewFrameTime.text = calculateExerciseDurationWithEnglish(MyApplication.recordFeedInfo.startedAt, MyApplication.recordFeedInfo.completedAt)
                textViewFrameKcal.text = "${MyApplication.recordFeedInfo.reducedKcal}Kcal"
            }
            layoutFrame2.run {
                if(selectedFrameIndex == 1) {
                    layoutFrame.visibility = View.VISIBLE
                } else {
                    layoutFrame.visibility = View.GONE
                }
                textViewFrameDistance.text = "${(MyApplication.recordFeedInfo.distance / 1000.0).let { String.format("%.2f", it) }}KM"
                textViewFrameLocation.text = "${MyApplication.recordFeedInfo.location}"
            }
            layoutFrame3.run {
                if(selectedFrameIndex == 2) {
                    layoutFrame.visibility = View.VISIBLE
                } else {
                    layoutFrame.visibility = View.GONE
                }
                textViewFrameTime.text = calculateExerciseDurationWithEnglish(MyApplication.recordFeedInfo.startedAt, MyApplication.recordFeedInfo.completedAt)
            }
            layoutFrame4.run {
                if(selectedFrameIndex == 3) {
                    layoutFrame.visibility = View.VISIBLE
                } else {
                    layoutFrame.visibility = View.GONE
                }
                val (amPm, time) = getAmPmAndTime(MyApplication.recordFeedInfo.completedAt)
                textViewFrameTimeAmpm.text = amPm
                textViewFrameCurrentTime.text = time
                textViewFrameLocation.text = MyApplication.recordFeedInfo.location
            }
            layoutFrame5.run {
                if(selectedFrameIndex == 4) {
                    layoutFrame.visibility = View.VISIBLE
                } else {
                    layoutFrame.visibility = View.GONE
                }
                textViewFrameDays.text = "${MyApplication.consecutiveDate}일 연속 기록 중"
            }

            toolbar.run {
                textViewHead.text = "오늘의 앨범"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    private fun convertResizeImage(imageUri: Uri): Uri {
        val contentResolver = requireContext().contentResolver
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

        // 이미지 리사이즈 (절반 크기로)
        val resizedBitmap =
            Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

        // 임시 파일 생성
        val tempFile = File.createTempFile("resized_image", ".jpg", requireContext().cacheDir)

        // 이미지 파일 쓰기
        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)

            FileOutputStream(tempFile).use { outputStream ->
                outputStream.write(byteArrayOutputStream.toByteArray())
                outputStream.flush()
            }

        } catch (e: Exception) {
            Log.e("ImageResize", "Failed to write file: ${e.message}")
        } finally {
            // 메모리 해제
            resizedBitmap.recycle()
        }

        return Uri.fromFile(tempFile)
    }

    fun captureViewAsBitmap(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY)
        )
        view.layout(view.left, view.top, view.right, view.bottom)

        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}
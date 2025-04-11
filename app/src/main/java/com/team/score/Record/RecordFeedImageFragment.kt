package com.team.score.Record

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Record.adapter.FeedFrameAdpater
import com.team.score.Utils.CalendarUtil.getTodayFormatted
import com.team.score.Utils.MyApplication
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

    var isImageUpload = false

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
                        val signUpProfile = MultipartBody.Part.createFormData(
                            "file",  // 서버에서 기대하는 필드 이름
                            compressedFile.name, // 파일 이름
                            requestFile
                        )

                        // 이미지 파일 저장
                        MyApplication.signUpImage = signUpProfile
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
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            buttonUpload.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, RecordFeedCompleteFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        binding.run {
            val frames = listOf(R.drawable.img_frame1,R.drawable.img_frame2,R.drawable.img_frame3,R.drawable.img_frame4,R.drawable.img_frame5)

            binding.recyclerViewFrame.run {
                adapter = FeedFrameAdpater(mainActivity, frames)
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
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
}
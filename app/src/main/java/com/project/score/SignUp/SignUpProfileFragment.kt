package com.project.score.SignUp

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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.project.score.OnBoarding.OnboardingActivity
import com.project.score.R
import com.project.score.Utils.MyApplication
import com.project.score.databinding.FragmentSignUpProfileBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SignUpProfileFragment : Fragment() {

    lateinit var binding: FragmentSignUpProfileBinding
    lateinit var onboardingActivity: OnboardingActivity

    var profileImage = 0

    var isImageUpload = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpProfileBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

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
                    } else {
                        Log.e("ImageCompression", "압축된 파일이 존재하지 않거나 비어 있습니다.")
                    }

                    binding.run {
                        imageViewProfile.setImageURI(uri)
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        initView()
        setProfileImage()


        binding.run {
            // 카메라 버튼 클릭 - 프로필 사진 업로드
            buttonCamera.setOnClickListener {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            buttonNext.setOnClickListener {
                if(profileImage != 0) {
                    val image =
                        when(profileImage) {
                            1 -> {"img_profile1.png"}
                            2 -> {"img_profile2.png"}
                            3 -> {"img_profile3.png"}
                            4 -> {"img_profile4.png"}
                            5 -> {"img_profile5.png"}
                            else -> {"img_profile1.png"}
                        }
                    val imagePart = getMultipartFromAssets(onboardingActivity, image)
                    MyApplication.signUpImage = imagePart
                }

                onboardingActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_onboarding, SignUpSchoolFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    fun setProfileImage() {
        binding.run {
            layoutProfile1.setOnClickListener {
                profileImage = 1
                imageViewProfile.setImageResource(R.drawable.ic_profile1)
                imageViewProfileBackground.setImageResource(R.drawable.background_profile1)
            }
            layoutProfile2.setOnClickListener {
                profileImage = 2
                imageViewProfile.setImageResource(R.drawable.ic_profile2)
                imageViewProfileBackground.setImageResource(R.drawable.background_profile2)
            }
            layoutProfile3.setOnClickListener {
                profileImage = 3
                imageViewProfile.setImageResource(R.drawable.ic_profile3)
                imageViewProfileBackground.setImageResource(R.drawable.background_profile3)
            }
            layoutProfile4.setOnClickListener {
                profileImage = 4
                imageViewProfile.setImageResource(R.drawable.ic_profile4)
                imageViewProfileBackground.setImageResource(R.drawable.background_profile4)
            }
            layoutProfile5.setOnClickListener {
                profileImage = 5
                imageViewProfile.setImageResource(R.drawable.ic_profile5)
                imageViewProfileBackground.setImageResource(R.drawable.background_profile5)
            }
        }
    }

    fun getMultipartFromAssets(context: Context, assetFileName: String, partKey: String = "file"): MultipartBody.Part {
        // 1. assets에서 이미지 읽기
        val inputStream = context.assets.open(assetFileName)
        val tempFile = File(context.cacheDir, assetFileName)

        // 2. 임시 파일로 복사
        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        inputStream.close()

        // 3. File → MultipartBody.Part 변환
        val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partKey, tempFile.name, requestFile)
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

    fun initView() {
        binding.toolbar.run {
            buttonBack.setOnClickListener {
                fragmentManager?.popBackStack()
            }
        }
    }
}
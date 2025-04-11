package com.team.score.Mypage

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.team.score.MainActivity
import com.team.score.Mypage.viewModel.MypageViewModel
import com.team.score.R
import com.team.score.SignUp.BottomSheet.SignUpGoalTimeBottomSheetFragment
import com.team.score.SignUp.BottomSheet.SignUpGoalTimeBottomSheetListener
import com.team.score.SignUp.BottomSheet.SignUpGradeBottomSheetFragment
import com.team.score.SignUp.BottomSheet.SignUpGradeBottomSheetListener
import com.team.score.SignUp.BottomSheet.SignUpSchoolBottomSheetFragment
import com.team.score.SignUp.BottomSheet.SignUpSchoolBottomSheetListener
import com.team.score.Utils.MyApplication
import com.team.score.Utils.TimeUtil
import com.team.score.databinding.FragmentMypageProfileEditBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MypageProfileEditFragment : Fragment(), SignUpGoalTimeBottomSheetListener,
    SignUpGradeBottomSheetListener, SignUpSchoolBottomSheetListener {

    lateinit var binding: FragmentMypageProfileEditBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: MypageViewModel by lazy {
        ViewModelProvider(requireActivity())[MypageViewModel::class.java]
    }

    var timeBottomSheet = SignUpGoalTimeBottomSheetFragment()
    val gradeBottomSheet = SignUpGradeBottomSheetFragment()

    var height: Int? = null
    var weight: Int? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageProfileEditBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initView()
        observeViewModel()

        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the photo picker.
                binding.imageViewProfile.setImageURI(null)
                if (uri != null) {
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
                        val userUpdateProfile = MultipartBody.Part.createFormData(
                            "file",  // 서버에서 기대하는 필드 이름
                            compressedFile.name, // 파일 이름
                            requestFile
                        )

                        // 이미지 파일 저장
                        MyApplication.userUpdateImage = userUpdateProfile
                        checkEnabled()
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

        binding.run {
            buttonEditProfile.setOnClickListener {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            buttonGoalTime.setOnClickListener {
                timeBottomSheet.show(childFragmentManager, timeBottomSheet.tag)
            }
            buttonSchool.setOnClickListener {
                val schoolBottomSheet = SignUpSchoolBottomSheetFragment(mainActivity)
                schoolBottomSheet.show(childFragmentManager, schoolBottomSheet.tag)
            }
            buttonGrade.setOnClickListener {
                gradeBottomSheet.show(childFragmentManager, gradeBottomSheet.tag)
            }

            editTextPhysicalHeight.addTextChangedListener(object : TextWatcher {
                private var isEditing = false
                private val unit = "cm"

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return
                    isEditing = true

                    val text = s.toString()

                    // 숫자만 추출
                    val numeric = text.replace(unit, "").trim()
                    val cleanNumeric = numeric.filter { it.isDigit() }

                    // 새로운 텍스트 + cm
                    val newText = "$cleanNumeric$unit"
                    editTextPhysicalHeight.setText(newText)

                    // 커서 위치를 숫자 끝으로 이동 (단위 앞)
                    editTextPhysicalHeight.setSelection(cleanNumeric.length)

                    // 실제 숫자 저장
                    if (cleanNumeric.isNotEmpty()) {
                        height = cleanNumeric.toInt()
                    }

                    if(height != MyApplication.userInfo?.height) {
                        checkEnabled()
                    }
                    isEditing = false
                }
            })


            editTextPhysicalWeight.addTextChangedListener(object : TextWatcher {
                private var isEditing = false
                private val unit = "kg"

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return
                    isEditing = true

                    val text = s.toString()
                    val numeric = text.replace(unit, "").trim()
                    val cleanNumeric = numeric.filter { it.isDigit() }

                    val newText = "$cleanNumeric$unit"
                    editTextPhysicalWeight.setText(newText)
                    editTextPhysicalWeight.setSelection(cleanNumeric.length)

                    if (cleanNumeric.isNotEmpty()) {
                        weight = cleanNumeric.toInt()
                    }

                    if(weight != MyApplication.userInfo?.weight) {
                        checkEnabled()
                    }
                    isEditing = false
                }
            })



            buttonEdit.setOnClickListener {
                MyApplication.userUpdateInfo?.userUpdateDto?.height = height ?: 0
                MyApplication.userUpdateInfo?.userUpdateDto?.weight = weight ?: 0

                viewModel.updateUserInfo(mainActivity)
            }

        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
    }

    fun checkEnabled() {
        binding.buttonEdit.isEnabled = true
    }

    fun observeViewModel() {
        viewModel.run {
            isUpdateUserInfo.observe(viewLifecycleOwner) {
                if(it == true) {
                    fragmentManager?.popBackStack()
                } else if(it == false) {
                    // 학교 정보 수정 불가 toast message
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.run {
            toolbar.run {
                textViewHead.text = "마이페이지"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }

            buttonEdit.isEnabled = false

            Glide.with(mainActivity).load(MyApplication.userInfo?.profileImgUrl).into(imageViewProfile)
            editTextNickname.setText(MyApplication.userInfo?.nickname)
            buttonGoalTime.text = TimeUtil.formatExerciseTimeToKorean(MyApplication.userInfo?.goal.toString())
            buttonSchool.text = MyApplication.userInfo?.schoolName
            buttonGrade.text = "${MyApplication.userInfo?.grade}학년"
            if(MyApplication.userInfo?.gender == "FEMALE") {
                buttonGenderFemale.run {
                    setBackgroundResource(R.drawable.background_main_circle)
                    setTextColor(resources.getColor(R.color.white))
                }
            } else {
                buttonGenderMale.run {
                    setBackgroundResource(R.drawable.background_main_circle)
                    setTextColor(resources.getColor(R.color.white))
                }
            }

            if(MyApplication.userInfo?.height != null) {
                height = MyApplication.userInfo?.height
                editTextPhysicalHeight.setText("${MyApplication.userInfo?.height.toString()}cm")
            }

            if(MyApplication.userInfo?.weight != null) {
                weight = MyApplication.userInfo?.weight
                editTextPhysicalWeight.setText("${MyApplication.userInfo?.weight.toString()}kg")
            }
        }
    }

    // 목표 운동 시간 선택
    override fun onTimeSelected(time: String) {
        checkEnabled()
        binding.run {
            buttonGoalTime.text = "매일 $time"
        }
    }

    // 학교 선택
    override fun onSchoolSelected(position: Int) {
        checkEnabled()
        binding.run {
            buttonSchool.text = MyApplication.signUpInfo?.schoolDto?.schoolName.toString()
            mainActivity.hideKeyboard()
        }
    }

    // 학년 선택
    override fun onGradeSelected(grade: String) {
        checkEnabled()
        binding.run {
            buttonGrade.text = "${grade}"
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
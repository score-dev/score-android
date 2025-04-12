package com.team.score.Group

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentCreateGroupBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CreateGroupFragment : Fragment() {

    lateinit var binding: FragmentCreateGroupBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: GroupViewModel

    var isImageUpload = false
    var isPublish = true
    var groupMemberNum = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateGroupBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[GroupViewModel::class.java]

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
                        val groupImage = MultipartBody.Part.createFormData(
                            "file",  // 서버에서 기대하는 필드 이름
                            compressedFile.name, // 파일 이름
                            requestFile
                        )

                        // 이미지 파일 저장
                        MyApplication.groupImage = groupImage
                    } else {
                        Log.e("ImageCompression", "압축된 파일이 존재하지 않거나 비어 있습니다.")
                    }

                    binding.run {
                        imageViewGroup.setImageURI(uri)
                        textViewGroupImageTitle.visibility = View.GONE
                        checkEnable()
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.run {
            // 그룹 사진 업로드
            imageViewGroup.setOnClickListener {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            editTextGroupName.addTextChangedListener {
                textViewGroupNameRegularExpression.text = "${editTextGroupName.text.length}/15"
                checkEnable()
            }

            editTextGroupDescription.addTextChangedListener {
                textViewGroupDescriptionRegularExpression.text = "${editTextGroupDescription.text.length}/200"
                checkEnable()
            }

            editTextGroupMemberNum.addTextChangedListener(object : TextWatcher {
                private var isEditing = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return

                    isEditing = true

                    val input = s.toString().replace("명", "") // 기존 "명" 제거
                    if (input.isNotEmpty()) {
                        groupMemberNum = input.toInt()
                        editTextGroupMemberNum.setText("${input}명")
                        editTextGroupMemberNum.setSelection(editTextGroupMemberNum.text.length - 1) // 커서를 "명" 앞에 위치
                    }

                    isEditing = false

                    checkEnable()
                }
            })

            editTextGroupPassword.addTextChangedListener {
                checkEnable()
            }

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                if(checkedId == R.id.radioButton_publish) {
                    // 전체 공개
                    editTextGroupPassword.visibility = View.GONE
                    isPublish = true
                    Log.d("##", "전체 공개 클릭")
                }
                else {
                    // 비공개
                    editTextGroupPassword.visibility = View.VISIBLE
                    isPublish = false
                    Log.d("##", "비공개 클릭")
                }
                checkEnable()
            }

            buttonCreate.setOnClickListener {
                viewModel.createGroup(mainActivity, editTextGroupName.text.toString(), editTextGroupDescription.text.toString(), groupMemberNum, editTextGroupPassword.text.toString(), !isPublish)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun checkEnable() {
        binding.run {
            if(isImageUpload && editTextGroupName.text.isNotEmpty() && editTextGroupMemberNum.text.isNotEmpty() && (isPublish xor (editTextGroupPassword.text.length == 4))) {
                buttonCreate.isEnabled = true
            } else {
                buttonCreate.isEnabled = false
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

    fun initView() {
        binding.run {
            root.setOnTouchListener { v, event ->
                mainActivity.hideKeyboard()
                false
            }

            mainActivity.hideBottomNavigation(true)

            editTextGroupPassword.visibility = View.GONE

            toolbar.run {
                textViewHead.text = "그룹 생성하기"
                buttonClose.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}
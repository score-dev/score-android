package com.team.score

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.team.score.API.TokenManager
import com.team.score.Mypage.MypageMainFragment
import com.team.score.Mypage.viewModel.MypageViewModel
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.MyApplication
import com.team.score.databinding.ActivityKakaoBinding

class KakaoActivity : AppCompatActivity() {

    lateinit var binding: ActivityKakaoBinding
    private val viewModel: MypageViewModel by lazy {
        ViewModelProvider(this)[MypageViewModel::class.java]
    }

    var userId = 0
    var nickname = ""
    var profileImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityKakaoBinding.inflate(layoutInflater)

        Log.d("DEEPLINK", intent?.data.toString())

        val uri = intent?.data
        userId = uri?.getQueryParameter("mate_id")?.toInt() ?: 0
        nickname = uri?.getQueryParameter("mate_name") ?: ""
        profileImage = uri?.getQueryParameter("mate_profile_image_url") ?: ""

        observeViewModel()

        binding.run {
            Glide.with(this@KakaoActivity).load(profileImage).into(imageViewProfile)
            textViewMateName.text = nickname

            buttonAddMate.setOnClickListener {
                viewModel.addNewFriends(this@KakaoActivity, userId)
            }

            buttonClose.setOnClickListener {
                finish()
            }
        }

        setContentView(binding.root)
    }

    fun observeViewModel() {
        viewModel.run {
            isAddMate.observe(this@KakaoActivity) {
                try {
                    val message = if (it == true) "친구 추가가 완료되었습니다" else "친구 추가에 실패했습니다.\n다시 시도해주세요"
                    Toast.makeText(this@KakaoActivity.applicationContext, message, Toast.LENGTH_SHORT).show()
                } catch (e: SecurityException) {
                    Log.e("KakaoActivity", "Toast SecurityException: ${e.message}")
                }
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        Log.d("DEEPLINK", intent?.data.toString())

        val uri = intent?.data
        userId = uri?.getQueryParameter("mate_id")?.toInt() ?: 0
        nickname = uri?.getQueryParameter("mate_name") ?: ""
        profileImage = uri?.getQueryParameter("mate_profile_image_url") ?: ""

        binding.run {
            Glide.with(this@KakaoActivity).load(profileImage).into(imageViewProfile)
            textViewMateName.text = nickname
        }
    }
}
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
import com.team.score.Mypage.MypageMainFragment
import com.team.score.Mypage.viewModel.MypageViewModel
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.databinding.ActivityKakaoBinding

class KakaoActivity : AppCompatActivity() {

    lateinit var binding: ActivityKakaoBinding
    private val viewModel: MypageViewModel by lazy {
        ViewModelProvider(this)[MypageViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityKakaoBinding.inflate(layoutInflater)

        val uri = intent?.data
        val userId = uri?.getQueryParameter("userId")
        val nickname = uri?.getQueryParameter("nickname")
        val profileImage = uri?.getQueryParameter("userProfileImage")

        observeViewModel()

        binding.run {
            Glide.with(this@KakaoActivity).load(profileImage).into(imageViewProfile)
            textViewMateName.text = nickname

            buttonAddMate.setOnClickListener {
                viewModel.addNewFriends(this@KakaoActivity, userId?.toInt() ?: 0)
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
                if(it == true) {
                    Toast.makeText(this@KakaoActivity, "친구 추가가 완료되었습니다", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@KakaoActivity, "친구 추가에 실패했습니다.\n다시 시도해주세요", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        val uri = intent?.data
        val userId = uri?.getQueryParameter("userId")
        val nickname = uri?.getQueryParameter("nickname")
        val profileImage = uri?.getQueryParameter("userProfileImage")

        binding.run {
            Glide.with(this@KakaoActivity).load(profileImage).into(imageViewProfile)
            textViewMateName.text = nickname
        }
    }
}
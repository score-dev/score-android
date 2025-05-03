package com.team.score

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.team.score.Mypage.MypageMainFragment
import com.team.score.databinding.ActivityKakaoBinding

class KakaoActivity : AppCompatActivity() {

    lateinit var binding: ActivityKakaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityKakaoBinding.inflate(layoutInflater)

        val uri = intent?.data
        val userId = uri?.getQueryParameter("userId")
        val nickname = uri?.getQueryParameter("nickname")
        val profileImage = uri?.getQueryParameter("userProfileImage")

        binding.run {
            Glide.with(this@KakaoActivity).load(profileImage).into(imageViewProfile)
            textViewMateName.text = nickname

            buttonAddMate.setOnClickListener {

            }

            buttonClose.setOnClickListener {
                
            }
        }

        setContentView(binding.root)
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
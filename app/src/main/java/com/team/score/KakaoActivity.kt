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


        setContentView(binding.root)
    }


}
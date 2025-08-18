package com.team.score

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.team.score.API.TokenManager
import com.team.score.Group.GroupFragment
import com.team.score.Home.HomeFragment
import com.team.score.Mypage.viewModel.MypageViewModel
import com.team.score.Record.RecordFragment
import com.team.score.Utils.GlobalApplication.Companion.firebaseAnalytics
import com.team.score.Utils.MyApplication
import com.team.score.Utils.TimerManager
import com.team.score.Utils.TrackingService
import com.team.score.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val viewModel: MypageViewModel by lazy {
        ViewModelProvider(this)[MypageViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel.getUserInfo(this, TokenManager(this).getUserId())

        intent?.let {
            initDeepLink(intent)
            if (it.getBooleanExtra("record", false)) {
                Handler(Looper.getMainLooper()).post {
                    goToRecord(it)
                    it.removeExtra("record") // 재진입 방지
                }
            }
        }

        binding.run {
            fabRecord.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, RecordFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationView()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent) // 새로운 Intent 설정

        intent.let {
            if (it != null) {
                initDeepLink(it)

                intent?.let {
                    if (it.getBooleanExtra("record", false)) {
                        Handler(Looper.getMainLooper()).post {
                            goToRecord(it)
                            it.removeExtra("record") // 재진입 방지
                        }
                    }
                }
            }
        } 
    }

    private fun initDeepLink(intent: Intent) {
        intent?.data?.let { uri ->
            when(uri.getQueryParameter("type")) {
                "mate" -> {
                    val kakaoIntent = Intent(this, KakaoActivity::class.java).apply {
                        data = uri // 딥링크 그대로 넘기기
                    }
                    startActivity(kakaoIntent)
                }
            }
        }
    }

    fun goToRecord(intent: Intent) {
        if (intent.getBooleanExtra("record", false)) {
            val container = findViewById<View>(R.id.fragmentContainerView_main)
            if (container != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, RecordFragment())
                    .addToBackStack(null)
                    .commit()
            } else {
                Log.e("MainActivity", "FragmentContainerView가 아직 초기화되지 않았습니다.")
            }
        }
    }

    private fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    firebaseAnalytics.logEvent("GNB_home", null)

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, HomeFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.menu_record -> {
                    firebaseAnalytics.logEvent("GNB_record", null)

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, RecordFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.menu_group -> {
                    firebaseAnalytics.logEvent("GNB_school_group", null)

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, GroupFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                else -> false
            }
        }
    }

    fun hideKeyboard() {
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                currentFocusView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun hideBottomNavigation(state: Boolean) {
        if (state) {
            binding.run {
                bottomNavigationView.visibility = View.GONE
                fabRecord.visibility = View.GONE
            }
        } else  {
            binding.run {
                bottomNavigationView.visibility = View.VISIBLE
                fabRecord.visibility = View.VISIBLE
            }
        }
    }

    fun startTrackingService() {
        val intent = Intent(this, TrackingService::class.java)
        ContextCompat.startForegroundService(this, intent)
        TimerManager.isRunning = true
    }

    fun stopTrackingService() {
        val intent = Intent(this, TrackingService::class.java)
        stopService(intent)
        TimerManager.isRunning = false
    }

    fun toggleTrackingService(): Boolean {
        return if (TimerManager.isRunning) {
            stopTrackingService()
            false
        } else {
            startTrackingService()
            true
        }
    }

    fun stopAndResetTracking() {
        // 1. TrackingService 종료
        stopTrackingService()

        // 2. 기록 관련 상태 전부 초기화
        TimerManager.reset()
        TimerManager.clear(this)

        MyApplication.run {
            recordTimer = 0
            totalDistance = 0
            locationList.clear()
            resetRecordFeedInfo()
        }
    }
}
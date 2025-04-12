package com.team.score.Record

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.team.score.API.TokenManager
import com.team.score.API.response.record.FriendResponse
import com.team.score.BuildConfig
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Record.BottomSheet.RecordFeedMateBottomSheetFragment
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.CalendarUtil.getTodayFormatted
import com.team.score.Utils.DistanceUtil
import com.team.score.Utils.MyApplication
import com.team.score.Utils.TimeUtil.convertToSimpleIso
import com.team.score.Utils.TimerManager
import com.team.score.databinding.FragmentRecordFeedUploadBinding

class RecordFeedUploadFragment : Fragment() {

    lateinit var binding: FragmentRecordFeedUploadBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }

    var getFriendList = mutableListOf<FriendResponse>()

    var isAlone = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecordFeedUploadBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

        binding.run {
            val feelingViews = listOf(
                Triple(layoutFeeling1, textViewFeeling1, imageViewFeeling1),
                Triple(layoutFeeling2, textViewFeeling2, imageViewFeeling2),
                Triple(layoutFeeling3, textViewFeeling3, imageViewFeeling3),
                Triple(layoutFeeling4, textViewFeeling4, imageViewFeeling4)
            )

            editTextPlace.addTextChangedListener {
                checkEnabled()
            }

            buttonAddExerciseMate.setOnClickListener {
                viewModel.getFriendList(mainActivity, 0)

                // 바텀시트
                val mateBottomSheet = RecordFeedMateBottomSheetFragment(mainActivity, getFriendList)
                mateBottomSheet.show(childFragmentManager, mateBottomSheet.tag)
            }

            buttonNoExerciseMate.setOnClickListener {
                isAlone = !isAlone
                checkExerciseMates()
            }

            for ((layout, textView, imageView) in feelingViews) {
                layout.setOnClickListener {
                    MyApplication.recordFeedInfo.feeling = textView.text.toString()

                    highlightSelectedFeeling(MyApplication.recordFeedInfo.feeling)
                }
            }

            buttonUpload.setOnClickListener {
                MyApplication.recordFeedInfo.run {
                    startedAt = convertToSimpleIso(TimerManager.startedAtIso.toString())
                    completedAt = convertToSimpleIso(TimerManager.completedAtIso.toString())
                    agentId = TokenManager(mainActivity).getUserId()
                    location = editTextPlace.text.toString()
                }

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, RecordFeedImageFragment())
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

    fun checkExerciseMates() {
        binding.run {
            if(isAlone) {
                MyApplication.recordFeedInfo.othersId = null
                buttonNoExerciseMate.run {
                    setBackgroundResource(R.drawable.background_main_circle)
                    setTextColor(resources.getColor(R.color.white))
                }
            } else {
                buttonNoExerciseMate.run {
                    setBackgroundResource(R.drawable.background_sub3_circle)
                    setTextColor(resources.getColor(R.color.main))
                }
            }
        }

        checkEnabled()
    }

    fun highlightSelectedFeeling(selectedFeeling: String?) {
        binding.run {
            val feelingViews = listOf(
                Triple(layoutFeeling1, textViewFeeling1, imageViewFeeling1),
                Triple(layoutFeeling2, textViewFeeling2, imageViewFeeling2),
                Triple(layoutFeeling3, textViewFeeling3, imageViewFeeling3),
                Triple(layoutFeeling4, textViewFeeling4, imageViewFeeling4)
            )

            for ((layout, textView, imageView) in feelingViews) {
                if (textView.text.toString() == selectedFeeling) {
                    imageView.setBackgroundResource(R.drawable.background_sub3_circle)  // 선택된 감정
                } else {
                    imageView.setBackgroundResource(R.drawable.background_grey2_circle)  // 초기화
                }
            }
        }
        checkEnabled()
    }


    fun checkEnabled() {
        binding.run {
            if(editTextPlace.text.isNotEmpty() && MyApplication.recordFeedInfo.feeling != null && (isAlone xor (MyApplication.recordFeedInfo?.othersId != null))) {
                buttonUpload.isEnabled = true
            } else {
                buttonUpload.isEnabled = false
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            friendList.observe(viewLifecycleOwner) {
                // 친구 정보
                getFriendList = it ?: mutableListOf<FriendResponse>()
            }
        }
    }

    fun initView() {
        binding.run {
            textViewToday.text = getTodayFormatted()
            var text = textViewFeelingTitle.text
            val spannable = SpannableString(text)

            val start = text.indexOf("소감")
            val end = start + "소감".length
            spannable.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.main)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            textViewFeelingTitle.text = spannable

            textViewWeatherValue.text = "${MyApplication.recordFeedInfo.temperature}°"
            textViewDustValue.text = MyApplication.recordFeedInfo.fineDust

            checkExerciseMates()
            highlightSelectedFeeling(MyApplication.recordFeedInfo.feeling)

            toolbar.run {
                textViewHead.text = "오늘의 운동 기록"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}
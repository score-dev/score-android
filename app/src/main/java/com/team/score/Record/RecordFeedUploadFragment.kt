package com.team.score.Record

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.TokenManager
import com.team.score.API.response.record.FriendResponse
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Record.BottomSheet.OnMateSelectedListener
import com.team.score.Record.BottomSheet.RecordFeedMateBottomSheetFragment
import com.team.score.Record.adapter.ExerciseMateAdapter
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.CalendarUtil.getTodayFormatted
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

    lateinit var exerciseMateAdapter: ExerciseMateAdapter
    var getFriendList = mutableListOf<FriendResponse>()
    var selectedExerciseMateList = mutableListOf<FriendResponse>()

    var isAlone = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecordFeedUploadBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
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
                val mateBottomSheet = RecordFeedMateBottomSheetFragment(mainActivity, getFriendList).apply {
                    onMateSelectedListener = object : OnMateSelectedListener {
                        override fun onMateSelected(mate: FriendResponse) {
                            // 선택된 친구 처리
                            selectedExerciseMateList.add(mate)
                            MyApplication.recordFeedInfo.othersId = listOf(mate.id)
                            isAlone = false
                            checkExerciseMates()
                        }
                    }
                }

                mateBottomSheet.show(childFragmentManager, mateBottomSheet.tag)
            }

            buttonNoExerciseMate.setOnClickListener {
                isAlone = true
                checkExerciseMates()
            }

            for ((layout, textView, imageView) in feelingViews) {
                layout.setOnClickListener {
                    MyApplication.recordFeedInfo.feeling = textView.text.toString()

                    highlightSelectedFeeling(MyApplication.recordFeedInfo.feeling)
                }
            }

            recyclerViewExerciseMate.apply {
                adapter = exerciseMateAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
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
                MyApplication.recordFeedInfo.othersId = emptyList()
                selectedExerciseMateList = emptyList<FriendResponse>().toMutableList()
                exerciseMateAdapter.updateList(selectedExerciseMateList)
                buttonNoExerciseMate.run {
                    setBackgroundResource(R.drawable.background_main_circle)
                    setTextColor(resources.getColor(R.color.white))
                }
            } else {
                buttonNoExerciseMate.run {
                    setBackgroundResource(R.drawable.background_sub3_circle)
                    setTextColor(resources.getColor(R.color.main))
                }

                exerciseMateAdapter.updateList(selectedExerciseMateList)
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
            if(editTextPlace.text.isNotEmpty() && MyApplication.recordFeedInfo.feeling != null && (isAlone xor (MyApplication.recordFeedInfo.othersId?.size != 0))) {
                buttonUpload.isEnabled = true
            } else {
                buttonUpload.isEnabled = false
            }
        }
    }

    fun initAdapter() {
        exerciseMateAdapter = ExerciseMateAdapter(
            mainActivity,
            selectedExerciseMateList
        ).apply {
            itemClickListener = object : ExerciseMateAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val updatedList = MyApplication.recordFeedInfo.othersId?.toMutableList()
                    updatedList?.remove(selectedExerciseMateList[position].id)
                    MyApplication.recordFeedInfo.othersId = updatedList

                    selectedExerciseMateList.removeAt(position)
                    isAlone = selectedExerciseMateList.size == 0
                    checkExerciseMates()
                }
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
            editTextPlace.setText(MyApplication.recordFeedInfo.location)

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
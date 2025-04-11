package com.team.score.Record

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.team.score.API.response.record.FriendResponse
import com.team.score.MainActivity
import com.team.score.Record.BottomSheet.RecordFeedMateBottomSheetFragment
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.CalendarUtil.getTodayFormatted
import com.team.score.databinding.FragmentRecordFeedUploadBinding

class RecordFeedUploadFragment : Fragment() {

    lateinit var binding: FragmentRecordFeedUploadBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }

    var getFriendList = mutableListOf<FriendResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecordFeedUploadBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonAddExerciseMate.setOnClickListener {
                viewModel.getFriendList(mainActivity, 0)

                // 바텀시트
                val mateBottomSheet = RecordFeedMateBottomSheetFragment(mainActivity, getFriendList)
                mateBottomSheet.show(childFragmentManager, mateBottomSheet.tag)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

            toolbar.run {
                textViewHead.text = "오늘의 운동 기록"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}
package com.project.score.Record

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.score.API.response.record.FeedDetailResponse
import com.project.score.API.response.user.FeedListResponse
import com.project.score.MainActivity
import com.project.score.Mypage.viewModel.MypageViewModel
import com.project.score.R
import com.project.score.Record.viewModel.RecordViewModel
import com.project.score.Utils.TimeUtil
import com.project.score.databinding.FragmentFeedDetailBinding

class FeedDetailFragment : Fragment() {

    lateinit var binding: FragmentFeedDetailBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }

    var getFeedDetailInfo: FeedDetailResponse? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFeedDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun observeViewModel() {
        viewModel.run {
            feedDetail.observe(viewLifecycleOwner) {
                getFeedDetailInfo = it

                binding.run {
                    Glide.with(mainActivity).load(getFeedDetailInfo?.uploaderProfileImgUrl).into(imageViewProfile)
                    textViewMyNickname.text = getFeedDetailInfo?.uploaderNickname
                    Glide.with(mainActivity).load(getFeedDetailInfo?.uploaderProfileImgUrl).into(imageViewMyProfile)
                    textViewMyNickname.text = getFeedDetailInfo?.uploaderNickname
                    Glide.with(mainActivity).load(getFeedDetailInfo?.feedImg).into(imageViewFeed)

                    textViewLocation.text = getFeedDetailInfo?.location

                    if(getFeedDetailInfo?.taggedNicknames?.size != 0) {
                        layoutOthers.visibility = View.VISIBLE
                        Glide.with(mainActivity).load(getFeedDetailInfo?.taggedProfileImgUrls?.get(0)).into(imageViewOtherProfile)
                        textViewOtherNickname.text = getFeedDetailInfo?.taggedNicknames?.get(0)
                    } else {
                        layoutOthers.visibility = View.GONE
                        textViewFeedDescription3.visibility = View.GONE
                    }

                    textViewDate.text = TimeUtil.getTimeAgo(getFeedDetailInfo?.completedAt.toString())
                    textViewExerciseTime.text = TimeUtil.calculateExerciseDuration(getFeedDetailInfo?.startedAt.toString(), getFeedDetailInfo?.completedAt.toString())

                    textViewTagWeather.text = "# ${getFeedDetailInfo?.weather}"
                    textViewTagFeeling.text = "# ${getFeedDetailInfo?.feeling}"
                }
            }
        }
    }

    fun initView() {
        viewModel.getFeedDetail(mainActivity, arguments?.getInt("feedId") ?: 0)
        mainActivity.hideBottomNavigation(true)

        binding.run {
            toolbar.run {
                textViewHead.text = "피드"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}
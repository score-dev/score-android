package com.team.score.Record

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.team.score.API.TokenManager
import com.team.score.API.response.record.FeedDetailResponse
import com.team.score.API.response.record.FeedEmotionResponse
import com.team.score.Group.FeedReportFragment
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Record.BottomSheet.FeedEmotionBottomSheetFragment
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.MyApplication
import com.team.score.Utils.TimeUtil
import com.team.score.databinding.FragmentFeedDetailBinding

class FeedDetailFragment : Fragment() {

    lateinit var binding: FragmentFeedDetailBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }
    private val groupViewModel: GroupViewModel by lazy {
        ViewModelProvider(requireActivity())[GroupViewModel::class.java]
    }

    var getFeedDetailInfo: FeedDetailResponse? = null
    var getFeedEmotionList = mutableListOf<FeedEmotionResponse>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFeedDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

        binding.layoutFeedDetail.run {
            textViewEmotionPeople.setOnLongClickListener {
                // 바텀시트
                val emotionMateBottomSheet = FeedEmotionBottomSheetFragment(mainActivity, getFeedEmotionList)
                emotionMateBottomSheet.show(childFragmentManager, emotionMateBottomSheet.tag)

                true
            }
            layoutEmotionAdd.setOnClickListener {
                val popupView =
                    LayoutInflater.from(context).inflate(R.layout.layout_pop_menu_emotion, null)

                val popupWindow = PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
                )
                popupWindow.elevation = 100f

                // 팝업 클릭 이벤트 설정
                popupView.findViewById<TextView>(R.id.button_emotion_like).setOnClickListener {

                    viewModel.setFeedEmotion(mainActivity, getFeedDetailInfo?.feedId ?: 0, "LIKE")
                    popupWindow.dismiss()
                }

                popupView.findViewById<TextView>(R.id.button_emotion_best).setOnClickListener {

                    viewModel.setFeedEmotion(mainActivity, getFeedDetailInfo?.feedId ?: 0, "BEST")
                    popupWindow.dismiss()
                }

                popupView.findViewById<TextView>(R.id.button_emotion_congratulation).setOnClickListener {

                    viewModel.setFeedEmotion(mainActivity, getFeedDetailInfo?.feedId ?: 0, "CONGRAT")
                    popupWindow.dismiss()
                }

                popupView.findViewById<TextView>(R.id.button_emotion_support).setOnClickListener {

                    viewModel.setFeedEmotion(mainActivity, getFeedDetailInfo?.feedId ?: 0, "SUPPORT")
                    popupWindow.dismiss()
                }

                // 팝업 표시
                popupWindow.showAsDropDown(layoutEmotionAdd, 10, -200)
            }

            buttonKebab.setOnClickListener {
                if(getFeedDetailInfo?.uploaderNickname == MyApplication.userNickname) {
                    showMyFeedPopUp(buttonKebab)
                } else {
                    showOtherFeedPopUp(buttonKebab)
                }
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
            feedDetail.observe(viewLifecycleOwner) {
                getFeedDetailInfo = it

                binding.layoutFeedDetail.run {
                    Glide.with(mainActivity).load(getFeedDetailInfo?.uploaderProfileImgUrl).into(imageViewProfile)
                    textViewNickname.text = getFeedDetailInfo?.uploaderNickname
                    Glide.with(mainActivity).load(getFeedDetailInfo?.uploaderProfileImgUrl).into(imageViewUploaderProfile)
                    textViewUploaderNickname.text = getFeedDetailInfo?.uploaderNickname
                    Glide.with(mainActivity).load(getFeedDetailInfo?.feedImg).into(imageViewFeed)

                    textViewLocation.text = getFeedDetailInfo?.location

                    if(getFeedDetailInfo?.taggedNicknames?.size != 0) {
                        layoutOthers.visibility = View.VISIBLE
                        textViewFeedDescription3.visibility = View.VISIBLE
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

            feedEmotion.observe(viewLifecycleOwner) {
                // 감정 표현 정보
                getFeedEmotionList = it ?: mutableListOf<FeedEmotionResponse>()

                updateEmotionLayouts(getFeedEmotionList)
            }
        }
    }

    fun updateEmotionLayouts(
        emotions: List<FeedEmotionResponse>,
    ) {
        var count = 0
        var isAdd = false

        binding.layoutFeedDetail.run {
            layoutEmotionLike.visibility =
                if (emotions.any { it.emotionType == "LIKE" })  {
                    View.VISIBLE
                    count++
                } else View.GONE
            layoutEmotionBest.visibility =
                if (emotions.any { it.emotionType == "BEST" }) {
                    View.VISIBLE
                    count++
                } else View.GONE
            layoutEmotionSupport.visibility =
                if (emotions.any { it.emotionType == "SUPPORT" }) {
                    View.VISIBLE
                    count++
                } else View.GONE
            layoutEmotionCongratulation.visibility =
                if (emotions.any { it.emotionType == "CONGRAT" }) {
                    View.VISIBLE
                    count++
                } else View.GONE

            if (emotions.any { it.emotionType == "LIKE" && it.agentId == TokenManager(mainActivity).getUserId() }) {
                imageViewEmotionLike.setBackgroundResource(R.drawable.background_sub3_circle)
                isAdd = true
            } else {
                imageViewEmotionLike.setBackgroundResource(R.drawable.background_grey2_circle)
            }

            if (emotions.any { it.emotionType == "BEST" && it.agentId == TokenManager(mainActivity).getUserId() }) {
                imageViewEmotionBest.setBackgroundResource(R.drawable.background_sub3_circle)
                isAdd = true
            } else {
                imageViewEmotionBest.setBackgroundResource(R.drawable.background_grey2_circle)
            }

            if (emotions.any { it.emotionType == "SUPPORT" && it.agentId == TokenManager(mainActivity).getUserId() }) {
                imageViewEmotionSupport.setBackgroundResource(R.drawable.background_sub3_circle)
                isAdd = true
            } else {
                imageViewEmotionSupport.setBackgroundResource(R.drawable.background_grey2_circle)
            }

            if (emotions.any { it.emotionType == "CONGRAT" && it.agentId == TokenManager(mainActivity).getUserId() }) {
                imageViewEmotionCongratulation.setBackgroundResource(R.drawable.background_sub3_circle)
                isAdd = true
            } else {
                imageViewEmotionCongratulation.setBackgroundResource(R.drawable.background_grey2_circle)
            }

            if(isAdd) {
                layoutEmotionAdd.visibility = View.GONE
            } else {
                layoutEmotionAdd.visibility = View.VISIBLE
            }

            if(emotions.size > 1) {
                textViewEmotionPeople.run {
                    visibility = View.VISIBLE
                    text = "${emotions[0].agentNickname}님 외 ${emotions.size - 1}명"
                }
            } else {
                textViewEmotionPeople.visibility = View.GONE
            }
        }
    }

    fun initView() {
        viewModel.getFeedDetail(mainActivity, arguments?.getInt("feedId") ?: 0)
        viewModel.getFeedEmotion(mainActivity, arguments?.getInt("feedId") ?: 0)
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

    fun showOtherFeedPopUp(buttonKebab: ImageView) {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_other_feed_item, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 50f
        }

        // ✅ 블러 뷰 추가
        val blurOverlay = View(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#4D000000"))
            isClickable = true // 뒤쪽 클릭 막기
        }
        val rootView = mainActivity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(blurOverlay)

        popupWindow.setOnDismissListener {
            rootView.removeView(blurOverlay)
        }

        popupView.setOnClickListener {
            // 피드 신고하기
            var nextFragment = FeedReportFragment()

            val bundle = Bundle().apply {
                putInt("feedId", getFeedDetailInfo?.feedId ?: 0)
            }
            // 전달할 Fragment 생성
            nextFragment = FeedReportFragment().apply {
                arguments = bundle
            }

            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_main, nextFragment)
                .addToBackStack(null)
                .commit()

            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(buttonKebab, -200, 50)
    }

    fun showMyFeedPopUp(buttonKebab: ImageView) {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_my_feed_item, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 50f
        }

        // ✅ 블러 뷰 추가
        val blurOverlay = View(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#4D000000"))
            isClickable = true // 뒤쪽 클릭 막기
        }
        val rootView = mainActivity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(blurOverlay)

        popupWindow.setOnDismissListener {
            rootView.removeView(blurOverlay)
        }

        popupView.setOnClickListener {
            // 피드 삭제하기
            groupViewModel.deleteFeed(mainActivity, getFeedDetailInfo?.feedId ?: 0) {
                fragmentManager?.popBackStack()
            }

            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(buttonKebab, -200, 50)
    }

}
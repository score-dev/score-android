package com.team.score.Group.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.TokenManager
import com.team.score.API.response.record.FeedEmotionResponse
import com.team.score.API.response.user.FeedListResponse
import com.team.score.Group.FeedReportFragment
import com.team.score.Group.MateDetailFragment.MateType
import com.team.score.Group.MateReportFragment
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Record.BottomSheet.FeedEmotionBottomSheetFragment
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.MyApplication
import com.team.score.Utils.TimeUtil
import com.team.score.databinding.LayoutFeedDetailBinding
import com.team.score.databinding.RowFeedBinding

class MyGroupFeedAdapter(
    private var activity: MainActivity,
    private var context: Context,
    private var fragmentManager: FragmentManager,
    private val viewModel: GroupViewModel
) :
    RecyclerView.Adapter<MyGroupFeedAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    private var feedList = mutableListOf<FeedListResponse>()
    private val feedEmotionMap = mutableMapOf<Int, List<FeedEmotionResponse>>()
    private var currentUserId: Int = TokenManager(activity).getUserId()

    fun addFeeds(newFeeds: List<FeedListResponse>) {
        feedList.addAll(newFeeds)
        notifyDataSetChanged()
    }


    fun clearFeeds() {
        feedList = mutableListOf<FeedListResponse>()
        feedList.clear()
        notifyDataSetChanged()
    }

    fun updateAllEmotions(emotions: Map<Int, List<FeedEmotionResponse>>) {
        feedEmotionMap.clear()
        feedEmotionMap.putAll(emotions)
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            LayoutFeedDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = feedList[position]
        val emotions = feedEmotionMap[item.feedId] ?: emptyList()

        with(holder.binding) {
            // 업로더 프로필
            Glide.with(holder.itemView.context).load(item.uploaderProfileImgUrl)
                .into(imageViewProfile)
            Glide.with(holder.itemView.context).load(item.uploaderProfileImgUrl)
                .into(imageViewUploaderProfile)

            textViewNickname.text = item.uploaderNickname
            textViewUploaderNickname.text = item.uploaderNickname

            // 피드 이미지
            Glide.with(holder.itemView.context).load(item.feedImg).into(imageViewFeed)

            // 위치
            textViewLocation.text = item.location

            // 함께한 친구
            if (item.taggedNicknames.isNotEmpty()) {
                layoutOthers.visibility = View.VISIBLE
                Glide.with(holder.itemView.context).load(item.taggedProfileImgUrls[0])
                    .into(imageViewOtherProfile)
                textViewOtherNickname.text = item.taggedNicknames[0]
                textViewFeedDescription3.visibility = View.VISIBLE
            } else {
                layoutOthers.visibility = View.GONE
                textViewFeedDescription3.visibility = View.GONE
            }

            // 날짜 및 운동 시간
            textViewDate.text = TimeUtil.getTimeAgo(item.completedAt)
            textViewExerciseTime.text =
                TimeUtil.calculateExerciseDuration(item.startedAt, item.completedAt)

            // 태그
            textViewTagWeather.text = "# ${item.weather}"
            textViewTagFeeling.text = "# ${item.feeling}"

            // 감정 설정
            bindEmotionLayouts(this, emotions, currentUserId)
        }
    }

    fun bindEmotionLayouts(
        binding: LayoutFeedDetailBinding,  // 각 아이템에 대응하는 ViewBinding
        emotions: List<FeedEmotionResponse>,
        userId: Int
    ) {
        var count = 0
        var isAdd = false

        with(binding) {
            // 이모션 레이아웃 표시 여부
            layoutEmotionLike.visibility =
                if (emotions.any { it.emotionType == "LIKE" }) {
                    count++
                    View.VISIBLE
                } else View.GONE

            layoutEmotionBest.visibility =
                if (emotions.any { it.emotionType == "BEST" }) {
                    count++
                    View.VISIBLE
                } else View.GONE

            layoutEmotionSupport.visibility =
                if (emotions.any { it.emotionType == "SUPPORT" }) {
                    count++
                    View.VISIBLE
                } else View.GONE

            layoutEmotionCongratulation.visibility =
                if (emotions.any { it.emotionType == "CONGRAT" }) {
                    count++
                    View.VISIBLE
                } else View.GONE

            // 각 이모션의 선택 상태에 따라 배경 변경
            if (emotions.any { it.emotionType == "LIKE" && it.agentId == userId }) {
                imageViewEmotionLike.setBackgroundResource(R.drawable.background_sub3_circle)
                isAdd = true
            } else {
                imageViewEmotionLike.setBackgroundResource(R.drawable.background_grey2_circle)
            }

            if (emotions.any { it.emotionType == "BEST" && it.agentId == userId }) {
                imageViewEmotionBest.setBackgroundResource(R.drawable.background_sub3_circle)
                isAdd = true
            } else {
                imageViewEmotionBest.setBackgroundResource(R.drawable.background_grey2_circle)
            }

            if (emotions.any { it.emotionType == "SUPPORT" && it.agentId == userId }) {
                imageViewEmotionSupport.setBackgroundResource(R.drawable.background_sub3_circle)
                isAdd = true
            } else {
                imageViewEmotionSupport.setBackgroundResource(R.drawable.background_grey2_circle)
            }

            if (emotions.any { it.emotionType == "CONGRAT" && it.agentId == userId }) {
                imageViewEmotionCongratulation.setBackgroundResource(R.drawable.background_sub3_circle)
                isAdd = true
            } else {
                imageViewEmotionCongratulation.setBackgroundResource(R.drawable.background_grey2_circle)
            }

            // "추가" 버튼 숨김 여부
            layoutEmotionAdd.visibility = if (isAdd) View.GONE else View.VISIBLE

            // 참여자 표시
            if (emotions.size > 1) {
                textViewEmotionPeople.run {
                    visibility = View.VISIBLE
                    text = "${emotions[0].agentNickname}님 외 ${emotions.size - 1}명"
                }
            } else {
                textViewEmotionPeople.visibility = View.GONE
            }
        }
    }


    override fun getItemCount() = feedList.size

    inner class ViewHolder(val binding: LayoutFeedDetailBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.run {
                imageViewFeed.setOnClickListener {
                    itemClickListener?.onItemClick(adapterPosition)

                    // 클릭 리스너 호출
                    onItemClickListener?.invoke(position)
                }


                buttonKebab.setOnClickListener {
                    showPopUp(buttonKebab, adapterPosition)
                }

                textViewEmotionPeople.setOnClickListener {
                    // 바텀시트
                    println("emotion : ${feedEmotionMap[feedList.get(adapterPosition).feedId]}")
                    val emotionMateBottomSheet = FeedEmotionBottomSheetFragment(
                        activity,
                        feedEmotionMap[feedList[adapterPosition].feedId]
                    )
                    emotionMateBottomSheet.show(fragmentManager, emotionMateBottomSheet.tag)
                }

                layoutEmotionAdd.setOnClickListener { view ->
                    val item = feedList[adapterPosition]
                    val popupView =
                        LayoutInflater.from(context).inflate(R.layout.layout_pop_menu_emotion, null)

                    val popupWindow = PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                    )
                    popupWindow.elevation = 100f

                    // 클릭 리스너들
                    popupView.findViewById<TextView>(R.id.button_emotion_like).setOnClickListener {
                        viewModel.setFeedEmotion(activity, item.feedId, "LIKE")
                        popupWindow.dismiss()
                    }
                    popupView.findViewById<TextView>(R.id.button_emotion_best).setOnClickListener {
                        viewModel.setFeedEmotion(activity, item.feedId, "BEST")
                        popupWindow.dismiss()
                    }
                    popupView.findViewById<TextView>(R.id.button_emotion_congratulation)
                        .setOnClickListener {
                            viewModel.setFeedEmotion(activity, item.feedId, "CONGRAT")
                            popupWindow.dismiss()
                        }
                    popupView.findViewById<TextView>(R.id.button_emotion_support)
                        .setOnClickListener {
                            viewModel.setFeedEmotion(activity, item.feedId, "SUPPORT")
                            popupWindow.dismiss()
                        }

                    // 팝업 표시 (살짝 위로 위치 조정)
                    popupWindow.showAsDropDown(binding.layoutEmotionAdd, 0, -200)
                }
            }
        }
    }

    fun showPopUp(buttonKebab: ImageView, adapterPosition: Int) {
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
        val rootView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(blurOverlay)

        popupWindow.setOnDismissListener {
            rootView.removeView(blurOverlay)
        }

        popupView.setOnClickListener {
            // 피드 신고하기
            var nextFragment = FeedReportFragment()

            val bundle = Bundle().apply {
                putInt("feedId", feedList[adapterPosition].feedId ?: 0)
            }
            // 전달할 Fragment 생성
            nextFragment = FeedReportFragment().apply {
                arguments = bundle
            }

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_main, nextFragment)
                .addToBackStack(null)
                .commit()

            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(buttonKebab, -200, 50)
    }
}
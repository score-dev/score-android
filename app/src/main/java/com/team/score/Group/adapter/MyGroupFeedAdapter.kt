package com.team.score.Group.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.score.API.response.record.FeedEmotionResponse
import com.team.score.API.response.record.GroupFeedListResponse
import com.team.score.R
import com.team.score.Utils.TimeUtil
import com.team.score.databinding.LayoutFeedDetailBinding
import com.team.score.databinding.RowFeedBinding

class MyGroupFeedAdapter(
    private var context: Context,
) :
    RecyclerView.Adapter<MyGroupFeedAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    private var feedList = mutableListOf<GroupFeedListResponse>()

    fun addFeeds(newFeeds: List<GroupFeedListResponse>) {
        val start = feedList.size
        feedList.addAll(newFeeds)
        notifyItemRangeInserted(start, newFeeds.size)
    }

    fun clearFeeds() {
        feedList = mutableListOf<GroupFeedListResponse>()
        feedList.clear()
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
//            bindEmotionLayouts(holder.binding, item.emotions, userId)
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
        val profileImage = binding.imageViewProfile
        val nickname = binding.textViewNickname
        val date = binding.textViewDate

        val feedImage = binding.imageViewFeed
        val uploaderProfile = binding.imageViewUploaderProfile
        val uploaderNickname = binding.textViewUploaderNickname
        val location = binding.textViewLocation
        val otherProfile = binding.imageViewOtherProfile
        val otherNickname = binding.textViewOtherNickname
        val exerciseTime = binding.textViewExerciseTime

        val tagWeather = binding.textViewTagWeather
        val tagFeeling = binding.textViewTagFeeling

        val emotionPeople = binding.textViewEmotionPeople

        val buttonKebab = binding.buttonKebab


        init {
            binding.imageViewFeed.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}
package com.team.score.Mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.DefaultTemplate
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.team.score.API.TokenManager
import com.team.score.API.response.record.FriendResponse
import com.team.score.API.weather.response.Main
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Record.adapter.MateAdapter
import com.team.score.Record.viewModel.RecordViewModel
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentMateBinding


class MateFragment : Fragment() {

    lateinit var binding: FragmentMateBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }

    lateinit var mateAdapter: MateAdapter

    var isLoading = true
    var isLastPage = false
    var isFirstPage = false
    var currentPage = 0
    val pageSize = 20

    var mates: MutableList<FriendResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMateBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            buttonKakao.setOnClickListener {
                shareToKakaoWithCustomTemplate(mainActivity)
            }

            recyclerViewMate.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastVisibleItemPosition()

                    // 마지막 항목이 보이고, 로딩 중이 아니며, 마지막 페이지도 아닐 경우
                    if (!isLoading && !isLastPage && lastVisible >= totalItemCount - 1 && !(isFirstPage == true && isLastPage == true)) {
                        Log.d("##", "reload")
                        isLoading = true
                        viewModel.getFriendList(mainActivity, currentPage)
                    }
                }
            })
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun shareToKakaoWithCustomTemplate(context: Context) {
        val templateArgs = mapOf(
            "mate_id" to "${TokenManager(context).getUserId() ?: 0}",
            "mate_name" to (MyApplication.userNickname ?: ""),
            "mate_profile_image_url" to (MyApplication.userInfo?.profileImgUrl ?: ""),
            "type" to "mate"
        )

        val templateId = 120678L

        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            ShareClient.instance.shareCustom(context, templateId, templateArgs) { result, error ->
                if (error != null) {
                    Log.e("KakaoShare", "카카오톡 커스텀 템플릿 공유 실패", error)
                } else if (result != null) {
                    Log.d("KakaoShare", "공유 성공: ${result.intent.data}") // ✅ 딥링크 URI 로그
                    context.startActivity(result.intent)
                }
            }
        } else {
            WebSharerClient.instance.makeCustomUrl(templateId, templateArgs)?.let { url ->
                Log.d("KakaoShare", "웹 공유 URI: $url")
                context.startActivity(Intent(Intent.ACTION_VIEW, url))
            } ?: Log.e("KakaoShare", "웹 공유 URL 생성 실패")
        }
    }

    fun observeViewModel() {
        viewModel.run {
            friendList.observe(viewLifecycleOwner) { friendsResponse ->
                for (friends in friendsResponse) {
                    mates?.add(friends)
                }

                binding.recyclerViewMate.apply {
                    adapter = mateAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }

                if (currentPage == 0) mateAdapter.clearFriends()

                val friendsList = mutableListOf<FriendResponse>()
                for (i in 0..<(mates?.size ?: 0)) {
                    friendsList.add(
                        FriendResponse(
                            mates?.get(i)?.id!!,
                            mates?.get(i)?.nickname!!,
                            mates?.get(i)?.profileImgUrl!!
                        )
                    )
                }

                mateAdapter.addFriends(friendsList)

                // 다음 페이지 준비
                isLoading = false
                currentPage++
            }

            lastFriend.observe(viewLifecycleOwner) {
                // 마지막 페이지 확인
                isLastPage = it
            }

            firstFriend.observe(viewLifecycleOwner) {
                // 첫 페이지 확인
                isFirstPage = it
            }
        }
    }

    fun initAdapter() {
        mateAdapter = MateAdapter(mainActivity).apply {
            itemClickListener = object : MateAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {

                }
            }
        }
    }

    fun initView() {
        mateAdapter.clearFriends()

        currentPage = 0
        isLastPage = false
        isLoading = true

        viewModel.getFriendList(mainActivity, currentPage)

        binding.run {
            toolbar.run {
                textViewHead.text = "내 친구"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}
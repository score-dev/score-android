package com.team.score.Group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.team.score.API.response.group.GroupDetailResponse
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.databinding.FragmentCreateGroupCompleteBinding

class CreateGroupCompleteFragment : Fragment() {

    lateinit var binding: FragmentCreateGroupCompleteBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(this)[GroupViewModel::class.java]
    }

    var getGroupDetailInfo: GroupDetailResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateGroupCompleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initView()

        binding.run {
            toolbar.buttonClose.setOnClickListener {
                fragmentManager?.popBackStack()
            }

            buttonShare.setOnClickListener {
                // 카카오톡 공유
                shareToKakaoWithCustomTemplate(mainActivity)
            }
        }

        return binding.root
    }

    fun initView() {
        viewModel.getGroupDetail(mainActivity, arguments?.getInt("groupId") ?: 0)
    }

    fun observeViewModel() {
        viewModel.run {
            groupDetail.observe(viewLifecycleOwner) {
                getGroupDetailInfo = it
            }
        }
    }

    fun shareToKakaoWithCustomTemplate(context: Context) {
        val templateArgs = mapOf(
            "group_id" to (arguments?.getInt("groupId") ?: 0).toString(),
            "group_name" to (getGroupDetailInfo?.groupName ?: ""),
            "group_image_url" to (getGroupDetailInfo?.groupImg ?: ""),
            "type" to "group"
        )

        val templateId = 120711L

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
}
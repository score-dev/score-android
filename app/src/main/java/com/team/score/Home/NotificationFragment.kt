package com.team.score.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.response.home.NotificationResponse
import com.team.score.Home.adapter.NotificationAdapter
import com.team.score.Home.viewModel.HomeViewModel
import com.team.score.MainActivity
import com.team.score.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment(), ParticipateGroupDenyDialogInterface {

    lateinit var binding: FragmentNotificationBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }


    lateinit var notificationAdapter: NotificationAdapter

    var isLoading = true
    var isLastPage = false
    var isFirstPage = false
    var currentPage = 0
    val pageSize = 20

    var getNotificationList: MutableList<NotificationResponse> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNotificationBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        notificationAdapter = NotificationAdapter(mainActivity, requireContext(), viewModel).apply {
            itemClickListener = object : NotificationAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, accepted: Boolean) {
                    val notification = getNotificationList[position]

                    if(accepted) {
                        // 그룹 참여 수락
                        viewModel.acceptGroupParticipate(mainActivity, notification.notificationId, accepted) {
                            // UI 업데이트
                            notification.read = true
                            notification.joinRequestAccepted = accepted

                            // 어댑터 갱신
                            notificationAdapter.notifyItemChanged(position)
                        }

                        viewModel.readNotification(mainActivity, notification.notificationId) {

                        }
                    } else {
                        // 그룹 참여 거절
                        val dialog = ParticipateGroupDenyDialog(this@NotificationFragment, notification.notificationId, notification.senderNickname ?: "", position)
                        // 알림창이 띄워져있는 동안 배경 클릭 막기
                        dialog.isCancelable = false
                        mainActivity.let {
                            dialog.show(it.supportFragmentManager, "ParticipateGroupDenyDialog")
                        }
                    }
                }
            }
        }

        observeViewModel(notificationAdapter)

        binding.run {
            recyclerViewNotification.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = notificationAdapter
            }

            recyclerViewNotification.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastVisibleItemPosition()

                    // 마지막 항목이 보이고, 로딩 중이 아니며, 마지막 페이지도 아닐 경우
                    if (!isLoading && !isLastPage && lastVisible >= totalItemCount - 1 && !(isFirstPage == true && isLastPage == true)) {
                        Log.d("##", "reload")
                        isLoading = true
                        viewModel.getNotificationList(mainActivity, currentPage)
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

    fun observeViewModel(notificationAdapter: NotificationAdapter) {
        viewModel.run {
            notificationList.observe(viewLifecycleOwner) { feedResponse ->
                if (currentPage == 0) {
                    notificationAdapter.clearNotifications()
                    getNotificationList.clear()
                }

                getNotificationList.addAll(feedResponse)

                notificationAdapter.addNotifications(getNotificationList)

                isLoading = false
                currentPage++
            }

            lastNotification.observe(viewLifecycleOwner) {
                // 마지막 페이지 확인
                isLastPage = it
            }

            firstNotification.observe(viewLifecycleOwner) {
                // 첫 페이지 확인
                isFirstPage = it
            }
        }
    }

    fun initView() {
        // 기존 리스트 초기화
        notificationAdapter.clearNotifications()

        currentPage = 0
        isLastPage = false
        isLoading = true

        binding.root.requestLayout()

        viewModel.getNotificationList(mainActivity, 0)

        binding.run {
            toolbar.run {
                textViewHead.text = "알림"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    override fun onClickRightButton(notificationId: Int, position: Int) {
        // 그룹 참여 신청 거절
        val notification = getNotificationList[position]

        viewModel.acceptGroupParticipate(mainActivity, notification.notificationId, false) {
            // UI 업데이트
            notification.read = true
            notification.joinRequestAccepted = false

            // 어댑터 갱신
            notificationAdapter.notifyItemChanged(position)
        }

        viewModel.readNotification(mainActivity, notification.notificationId) {

        }
    }
}
package com.team.score.Group.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.team.score.API.ApiClient
import com.team.score.API.TokenManager
import com.team.score.API.request.group.CreateGroupRequest
import com.team.score.API.response.PagingResponse
import com.team.score.API.response.group.GroupDetailResponse
import com.team.score.API.response.group.GroupMateResponse
import com.team.score.API.response.group.GroupRankingResponse
import com.team.score.API.response.group.GroupUnexercisedMateResponse
import com.team.score.API.response.group.GroupInfoResponse
import com.team.score.API.response.group.GroupRanking
import com.team.score.API.response.group.SchoolGroupRankingResponse
import com.team.score.API.response.group.SearchGroupResponse
import com.team.score.API.response.login.UserInfoResponse
import com.team.score.API.response.record.FeedEmotionResponse
import com.team.score.API.response.user.FeedListResponse
import com.team.score.Group.CreateGroupCompleteFragment
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupViewModel: ViewModel() {

    var isValidPassword = MutableLiveData<Boolean?>()
    var isParticipated = MutableLiveData<Boolean?>()

    var schoolGroupRanking = MutableLiveData<SchoolGroupRankingResponse?>()

    var myGroupList = MutableLiveData<MutableList<GroupInfoResponse>>()

    var searchGroupList = MutableLiveData<MutableList<GroupInfoResponse>>()
    var recommendGroupList = MutableLiveData<MutableList<GroupInfoResponse>>()

    var groupDetail = MutableLiveData<GroupDetailResponse?>()
    var groupRanking = MutableLiveData<GroupRankingResponse?>()
    var groupMateList = MutableLiveData<MutableList<GroupMateResponse>>()
    var groupUnexercisedMateList = MutableLiveData<MutableList<GroupUnexercisedMateResponse>>()

    var groupFeedList = MutableLiveData<MutableList<FeedListResponse>>()
    var lastFeed: MutableLiveData<Boolean> = MutableLiveData()
    var firstFeed: MutableLiveData<Boolean> = MutableLiveData()
    var feedEmotion: MutableLiveData<MutableList<FeedEmotionResponse>?> = MutableLiveData()
    val feedEmotionsMap = MutableLiveData<Map<Int, List<FeedEmotionResponse>>>()

    var userInfo: MutableLiveData<UserInfoResponse> = MutableLiveData()

    // 내 그룹 리스트 정보
    fun getMyGroupList(activity: Activity) {

        val tempGroupList = mutableListOf<GroupInfoResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getMyGroupList(tokenManager.getAccessToken().toString(), tokenManager.getUserId()).enqueue(object :
            Callback<List<GroupInfoResponse>> {
            override fun onResponse(
                call: Call<List<GroupInfoResponse>>,
                response: Response<List<GroupInfoResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: List<GroupInfoResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    val resultGroupList = result ?: emptyList()

                    for (group in resultGroupList) {

                        val groupItem = GroupInfoResponse(
                            id = group.id,
                            name = group.name,
                            description = group.description,
                            groupImg = group.groupImg,
                            userLimit = group.userLimit,
                            currentMembers = group.currentMembers,
                            recentMembersPic = group.recentMembersPic,
                            otherMembers = group.otherMembers,
                            private = group.private
                        )

                        tempGroupList.add(groupItem)
                    }

                    val groupIdList = tempGroupList.map { it.id }
                    MyApplication.myGroupList = groupIdList

                    myGroupList.value = tempGroupList
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: List<GroupInfoResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<List<GroupInfoResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 그룹 생성
    fun createGroup(activity: MainActivity,
                    groupName: String,
                    groupDescription: String?,
                    groupMemberNum: Int,
                    groupPassword: String?,
                    isPrivate: Boolean
    ) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        var groupInfo = CreateGroupRequest(tokenManager.getUserId(), groupName, groupDescription, groupMemberNum, groupPassword, isPrivate)

        val gson = Gson()

        // JSON 변환 (Gson 사용)
        val groupDtoJson = gson.toJson(groupInfo)

        // RequestBody 변환 (application/json 설정)
        val groupDtoRequestBody = groupDtoJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiClient.apiService.createGroup(tokenManager.getAccessToken().toString(), groupDtoRequestBody, MyApplication.groupImage!!).enqueue(object :
            Callback<Int> {
            override fun onResponse(
                call: Call<Int>,
                response: Response<Int>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: Int? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, CreateGroupCompleteFragment())
                        .commit()
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: Int? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 그룹 참여 신청
    fun participateGroup(activity: MainActivity, groupId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.participateGroup(tokenManager.getAccessToken().toString(), groupId, tokenManager.getUserId()).enqueue(object :
            Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: String? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    isParticipated.value = true
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: String? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    isParticipated.value = false
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
                isParticipated.value = false
            }
        })
    }

    // 그룹 비밀번호 확인
    fun verifyGroupPassword(activity: MainActivity, groupId: Int, password: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.verifyGroupPassword(tokenManager.getAccessToken().toString(), password, groupId).enqueue(object :
            Callback<Boolean> {
            override fun onResponse(
                call: Call<Boolean>,
                response: Response<Boolean>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: Boolean? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    isValidPassword.value = result

                    if(result == true) {
                        participateGroup(activity, groupId)
                    }
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: Boolean? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    isValidPassword.value = null
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())

                isValidPassword.value = null
            }
        })
    }

    // 학교 그룹 랭킹 조회
    fun getSchoolGroupRanking(activity: MainActivity, date: String?) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getSchoolGroupRanking(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), date).enqueue(object :
            Callback<SchoolGroupRankingResponse> {
            override fun onResponse(
                call: Call<SchoolGroupRankingResponse>,
                response: Response<SchoolGroupRankingResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: SchoolGroupRankingResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    schoolGroupRanking.value = result

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: SchoolGroupRankingResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    when(response.code()) {
                        404 -> {
                            schoolGroupRanking.value == null
                        }
                         500 -> {
                             schoolGroupRanking.value == null
                         }
                    }
                }
            }

            override fun onFailure(call: Call<SchoolGroupRankingResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 학교 그룹 검색
    fun searchSchoolGroup(activity: MainActivity, schoolId: Int, keyword: String?) {

        val tempGroupList = mutableListOf<GroupInfoResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.searchSchoolGroup(tokenManager.getAccessToken().toString(), schoolId, keyword).enqueue(object :
            Callback<SearchGroupResponse> {
            override fun onResponse(
                call: Call<SearchGroupResponse>,
                response: Response<SearchGroupResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: SearchGroupResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    val resultGroupList = result?.groups ?: emptyList()

                    for (group in resultGroupList) {

                        val groupItem = GroupInfoResponse(
                            id = group.id,
                            name = group.name,
                            description = group.description,
                            groupImg = group.groupImg,
                            userLimit = group.userLimit,
                            currentMembers = group.currentMembers,
                            recentMembersPic = group.recentMembersPic,
                            otherMembers = group.otherMembers,
                            private = group.private
                        )

                        tempGroupList.add(groupItem)
                    }

                    searchGroupList.value = tempGroupList

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: SearchGroupResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<SearchGroupResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 학교 그룹 추천
    fun getRecommendGroup(activity: MainActivity, schoolId: Int) {

        val tempGroupList = mutableListOf<GroupInfoResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getRecommendGroup(tokenManager.getAccessToken().toString(), schoolId).enqueue(object :
            Callback<List<GroupInfoResponse>> {
            override fun onResponse(
                call: Call<List<GroupInfoResponse>>,
                response: Response<List<GroupInfoResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: List<GroupInfoResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    val resultGroupList = result ?: emptyList()

                    for (group in resultGroupList) {

                        val groupItem = GroupInfoResponse(
                            id = group.id,
                            name = group.name,
                            description = group.description,
                            groupImg = group.groupImg,
                            userLimit = group.userLimit,
                            currentMembers = group.currentMembers,
                            recentMembersPic = group.recentMembersPic,
                            otherMembers = group.otherMembers,
                            private = group.private
                        )

                        tempGroupList.add(groupItem)
                    }

                    recommendGroupList.value = tempGroupList

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: List<GroupInfoResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<List<GroupInfoResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 그룹 상세 정보 조회
    fun getGroupDetail(activity: Activity, groupId: Int) {

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getGroupDetail(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), groupId).enqueue(object :
            Callback<GroupDetailResponse> {
            override fun onResponse(
                call: Call<GroupDetailResponse>,
                response: Response<GroupDetailResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: GroupDetailResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    groupDetail.value = result

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: GroupDetailResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<GroupDetailResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 그룹 내 랭킹 조회
    fun getGroupRanking(activity: Activity, groupId: Int, date: String?) {

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getGroupRanking(tokenManager.getAccessToken().toString(), groupId, date).enqueue(object :
            Callback<GroupRankingResponse> {
            override fun onResponse(
                call: Call<GroupRankingResponse>,
                response: Response<GroupRankingResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: GroupRankingResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    groupRanking.value = result

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: GroupRankingResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    when(response.code()) {
                        404 -> {
                            groupRanking.value = null
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GroupRankingResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 그룹 피드 리스트 정보
    fun getGroupFeedList(activity: Activity, groupId: Int, currentPage: Int) {

        val tempFeedList = mutableListOf<FeedListResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getGroupFeedList(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), groupId, currentPage).enqueue(object :
            Callback<PagingResponse<FeedListResponse>> {
            override fun onResponse(
                call: Call<PagingResponse<FeedListResponse>>,
                response: Response<PagingResponse<FeedListResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: PagingResponse<FeedListResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    lastFeed.value = (result?.last == true)
                    firstFeed.value = (result?.first == true)

                    val resultFeedList = result?.content ?: emptyList()

                    for (feed in resultFeedList) {
                        val feedItem = FeedListResponse(
                            feedId = feed.feedId,
                            uploaderNickname = feed.uploaderNickname ?: "",
                            uploaderProfileImgUrl = feed.uploaderProfileImgUrl ?: "",
                            feedImg = feed.feedImg ?: "",
                            startedAt = feed.startedAt ?: "",
                            completedAt = feed.completedAt ?: "",
                            location = feed.location ?: "",
                            weather = feed.weather ?: "",
                            temperature = feed.temperature ?: 0,
                            fineDust = feed.fineDust ?: "",
                            feeling = feed.feeling ?: "",
                            taggedNicknames = feed.taggedNicknames ?: emptyList(),
                            taggedProfileImgUrls = feed.taggedProfileImgUrls ?: emptyList()
                        )

                        tempFeedList.add(feedItem)
                    }

                    groupFeedList.value = tempFeedList
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: PagingResponse<FeedListResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<PagingResponse<FeedListResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 피드 감정 표현 정보
    fun getFeedEmotion(activity: MainActivity, feedId: Int) {

        val tempFeedEmotion = mutableListOf<FeedEmotionResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getFeedEmotion(tokenManager.getAccessToken().toString(), feedId).enqueue(object :
            Callback<List<FeedEmotionResponse>> {
            override fun onResponse(
                call: Call<List<FeedEmotionResponse>>,
                response: Response<List<FeedEmotionResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: List<FeedEmotionResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    val resultFeedEmotionList = result ?: emptyList()

                    for (feed in resultFeedEmotionList) {
                        val feedEmotionList = FeedEmotionResponse(
                            feed.agentId,
                            feed.agentProfileImgUrl,
                            feed.agentNickname,
                            feed.emotionType,
                            feed.reactedAt
                        )

                        tempFeedEmotion.add(feedEmotionList)
                    }

                    feedEmotion.value = tempFeedEmotion

                    val currentMap = feedEmotionsMap.value?.toMutableMap() ?: mutableMapOf()
                    currentMap[feedId] = response.body() ?: emptyList()
                    feedEmotionsMap.value = currentMap
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: List<FeedEmotionResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<List<FeedEmotionResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 피드 감정표현 추가
    fun setFeedEmotion(activity: MainActivity, feedId: Int, type: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.setFeedEmotion(tokenManager.getAccessToken().toString(), tokenManager.getUserId(), feedId, type).enqueue(object :
            Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: String? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    getFeedEmotion(activity, feedId)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: String? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 그룹 메이트 리스트 정보
    fun getGroupMateList(activity: Activity, groupId: Int) {

        val tempGroupMateList = mutableListOf<GroupMateResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getGroupMate(tokenManager.getAccessToken().toString(), groupId).enqueue(object :
            Callback<List<GroupMateResponse>> {
            override fun onResponse(
                call: Call<List<GroupMateResponse>>,
                response: Response<List<GroupMateResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: List<GroupMateResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    val resultGroupList = result ?: emptyList()

                    for (group in resultGroupList) {

                        val mate = GroupMateResponse(
                            id = group.id,
                            nickname = group.nickname,
                            profileImgUrl = group.profileImgUrl
                        )

                        tempGroupMateList.add(mate)
                    }

                    groupMateList.value = tempGroupMateList
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: List<GroupMateResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<List<GroupMateResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 메이트 정보 조회
    fun getUserInfo(activity: Activity, userId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getUserInfo(userId, tokenManager.getAccessToken().toString()).enqueue(object :
            Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: UserInfoResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    userInfo.value = result!!
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: UserInfoResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // 그룹 운동 쉰 메이트 리스트 정보
    fun getGroupUnexercisedMateList(activity: Activity, groupId: Int) {

        val tempGroupUnexercisedMateList = mutableListOf<GroupUnexercisedMateResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getGroupUnexercisedMate(tokenManager.getAccessToken().toString(), groupId, tokenManager.getUserId()).enqueue(object :
            Callback<List<GroupUnexercisedMateResponse>> {
            override fun onResponse(
                call: Call<List<GroupUnexercisedMateResponse>>,
                response: Response<List<GroupUnexercisedMateResponse>>
            ) {
                Log.d("##", "onResponse 성공: " + response.body().toString())
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: List<GroupUnexercisedMateResponse>? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    val resultGroupList = result ?: emptyList()

                    for (group in resultGroupList) {

                        val mate = GroupUnexercisedMateResponse(
                            userId = group.userId,
                            nickname = group.nickname,
                            profileImageUrl = group.profileImageUrl,
                            canTurnOverBaton = group.canTurnOverBaton
                        )

                        tempGroupUnexercisedMateList.add(mate)
                    }

                    groupUnexercisedMateList.value = tempGroupUnexercisedMateList
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: List<GroupUnexercisedMateResponse>? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                }
            }

            override fun onFailure(call: Call<List<GroupUnexercisedMateResponse>>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}
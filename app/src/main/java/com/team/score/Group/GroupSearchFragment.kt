package com.team.score.Group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.score.API.response.group.GroupInfoResponse
import com.team.score.Group.adapter.MyGroupListAdapter
import com.team.score.Group.adapter.RecentSearchGroupKeywordAdapter
import com.team.score.Group.adapter.SearchGroupAdapter
import com.team.score.Group.viewModel.GroupViewModel
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentGroupSearchBinding

class GroupSearchFragment : Fragment() {

    lateinit var binding: FragmentGroupSearchBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: GroupViewModel by lazy {
        ViewModelProvider(requireActivity())[GroupViewModel::class.java]
    }

    lateinit var searchGroupAdapter : SearchGroupAdapter
    lateinit var searchKewordAdapter : RecentSearchGroupKeywordAdapter
    lateinit var recommendGroupAdapter : SearchGroupAdapter

    var getGroupInfo: List<GroupInfoResponse>? = null
    var getRecommendGroupInfo: List<GroupInfoResponse>? = null

    var schoolId = 0
    var keyword = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGroupSearchBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            searchBar.run {
                editTextSearch.addTextChangedListener {
                    if(editTextSearch.text.isNotEmpty()) {
                        buttonClear.visibility = View.VISIBLE
                    } else {
                        buttonClear.visibility = View.GONE
                    }
                }

                editTextSearch.setOnEditorActionListener { v, actionId, event ->
                    // 검색
                    if(editTextSearch.text.isNotEmpty()) {
                        keyword = editTextSearch.text.toString()
                        viewModel.searchSchoolGroup(mainActivity, schoolId, keyword)
                    }

                    true
                }

                buttonClear.setOnClickListener {
                    editTextSearch.text.clear()
                    layoutSearchEmpty.visibility = View.VISIBLE
                    layoutSearchResult.visibility = View.GONE
                    searchKewordAdapter.updateList(MyApplication.preferences.getRecentSearchesLimited(mainActivity))
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        searchGroupAdapter = SearchGroupAdapter(mainActivity, getGroupInfo).apply {
            itemClickListener = object : SearchGroupAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    if(MyApplication.myGroupList.contains(getGroupInfo?.get(position)?.id ?: 0)) {
                        // 내 그룹 화면
                        val bundle = Bundle().apply {
                            putInt("groupId", getGroupInfo?.get(position)?.id ?: 0)
                        }

                        // 전달할 Fragment 생성
                        val  nextFragment = MyGroupDetailFragment().apply {
                            arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                        }
                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, nextFragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        // 다른 그룹 화면
                        val bundle = Bundle().apply {
                            putInt("groupId", getGroupInfo?.get(position)?.id ?: 0)
                        }

                        // 전달할 Fragment 생성
                        val  nextFragment = OtherGroupDetailFragment().apply {
                            arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                        }
                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, nextFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        }

        searchKewordAdapter = RecentSearchGroupKeywordAdapter(mainActivity, MyApplication.preferences.getRecentSearchesLimited(mainActivity)).apply {
            itemClickListener = object : RecentSearchGroupKeywordAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    keyword = MyApplication.preferences.getRecentSearchesLimited(mainActivity).get(position)
                    binding.searchBar.editTextSearch.setText(keyword)
                    viewModel.searchSchoolGroup(mainActivity, schoolId, keyword)
                }
            }
        }

        recommendGroupAdapter = SearchGroupAdapter(mainActivity, getRecommendGroupInfo).apply {
            itemClickListener = object : SearchGroupAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    if(MyApplication.myGroupList.contains(getRecommendGroupInfo?.get(position)?.id ?: 0)) {
                        // 내 그룹 화면
                        val bundle = Bundle().apply {
                            putInt("groupId", getRecommendGroupInfo?.get(position)?.id ?: 0)
                        }

                        // 전달할 Fragment 생성
                        val  nextFragment = MyGroupDetailFragment().apply {
                            arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                        }
                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, nextFragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        // 다른 그룹 화면
                        val bundle = Bundle().apply {
                            putInt("groupId", getRecommendGroupInfo?.get(position)?.id ?: 0)
                        }

                        // 전달할 Fragment 생성
                        val  nextFragment = OtherGroupDetailFragment().apply {
                            arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                        }
                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, nextFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        }

        binding.run {
            recyclerViewSearchResult.apply {
                adapter = searchGroupAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            recyclerViewSearchWord.apply {
                adapter = searchKewordAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }

            recyclerViewRecommend.apply {
                adapter = recommendGroupAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            searchGroupList.observe(viewLifecycleOwner) {
                getGroupInfo = it

                MyApplication.preferences.saveRecentSearchLimited(mainActivity, keyword)
                binding.run {
                    layoutSearchResult.visibility = View.VISIBLE
                    layoutSearchEmpty.visibility = View.GONE
                    textViewSearchTitle.text = "‘${keyword}’ 검색 결과"
                }

                searchGroupAdapter.updateList(getGroupInfo)
            }

            recommendGroupList.observe(viewLifecycleOwner) {
                getRecommendGroupInfo = it

                recommendGroupAdapter.updateList(getRecommendGroupInfo)
            }
        }
    }

    fun initView() {
        schoolId = arguments?.getInt("schoolId") ?: 0

        viewModel.getRecommendGroup(mainActivity, schoolId)

        binding.run {
            searchBar.editTextSearch.hint = "그룹 이름을 입력해주세요"
            layoutSearchEmpty.visibility = View.VISIBLE
            layoutSearchResult.visibility = View.GONE

            toolbar.run {
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}
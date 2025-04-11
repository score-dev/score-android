package com.team.score.SignUp.BottomSheet

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.team.score.API.neis.response.SchoolDto
import com.team.score.API.request.signUp.UserSchool
import com.team.score.API.request.user.UserSchoolInfo
import com.team.score.BuildConfig
import com.team.score.Login.viewModel.UserViewModel
import com.team.score.SignUp.adapter.SchoolListAdapter
import com.team.score.Utils.MyApplication
import com.team.score.databinding.FragmentSignUpSchoolBottomSheetBinding

interface SignUpSchoolBottomSheetListener {
    fun onSchoolSelected(position: Int)
}

class SignUpSchoolBottomSheetFragment(var activity: Activity) : BottomSheetDialogFragment() {
    private lateinit var listener: SignUpSchoolBottomSheetListener
    lateinit var binding: FragmentSignUpSchoolBottomSheetBinding
    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    var getSchoolList: MutableList<SchoolDto>? = null
    lateinit var schoolAdapter: SchoolListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as SignUpSchoolBottomSheetListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpSchoolBottomSheetBinding.inflate(inflater, container, false)

        initAdapter()
        observeViewModel()

        binding.run {

            recyclerViewSchool.run {
                adapter = schoolAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            searchBar.run {
                editTextSearch.run {
                    addTextChangedListener {
                        if(editTextSearch.text.isNotEmpty()) {
                            buttonClear.visibility = View.VISIBLE
                        } else {
                            buttonClear.visibility = View.GONE
                        }
                    }
                    setOnEditorActionListener { v, actionId, event ->
                        viewModel.getSchoolList(activity, BuildConfig.school_api_key, editTextSearch.text.toString())

                        true
                    }
                }

                buttonClear.setOnClickListener {
                    editTextSearch.setText("")
                }
            }
        }

        return binding.root
    }

    fun observeViewModel() {
        viewModel.run {
            schoolInfoList.observe(viewLifecycleOwner) {
                getSchoolList = it

                Log.d("##", "school list : ${getSchoolList}")

                schoolAdapter.updateList(getSchoolList)
            }
        }
    }

    fun initAdapter() {
        schoolAdapter = SchoolListAdapter(
            activity,
            getSchoolList
        ).apply {
            itemClickListener = object : SchoolListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    MyApplication.signUpInfo?.schoolDto = UserSchool(
                        getSchoolList?.get(position)?.SCHUL_NM!!, getSchoolList?.get(position)?.ORG_RDNMA!!, getSchoolList?.get(position)?.SD_SCHUL_CODE!!
                    )
                    MyApplication.userUpdateInfo?.userUpdateDto?.school = UserSchoolInfo(
                        getSchoolList?.get(position)?.SCHUL_NM!!, getSchoolList?.get(position)?.ORG_RDNMA!!, getSchoolList?.get(position)?.SD_SCHUL_CODE!!
                    )
                    Log.d("##", "school : ${MyApplication.signUpInfo?.schoolDto}")

                    listener.onSchoolSelected(position)

                    dismiss()
                }
            }
        }
    }
}
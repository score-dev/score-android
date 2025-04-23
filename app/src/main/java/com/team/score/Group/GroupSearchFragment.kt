package com.team.score.Group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.team.score.API.weather.response.Main
import com.team.score.MainActivity
import com.team.score.R
import com.team.score.databinding.FragmentGroupSearchBinding

class GroupSearchFragment : Fragment() {

    lateinit var binding: FragmentGroupSearchBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGroupSearchBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            layoutSearchbar.run {
                editTextSearch.addTextChangedListener {
                    if(editTextSearch.text.isNotEmpty()) {
                        buttonClear.visibility = View.VISIBLE
                    } else {
                        buttonClear.visibility = View.GONE
                    }
                }

                editTextSearch.setOnEditorActionListener { v, actionId, event ->
                    // 검색

                    true
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        binding.run {
            layoutSearchbar.editTextSearch.hint = "그룹 이름을 입력해주세요"
            layoutSearchEmpty.visibility = View.VISIBLE
            layoutSearch.visibility = View.GONE

            toolbar.run {
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}
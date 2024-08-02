package com.otclub.humate.mission.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentMatchingBinding
import com.otclub.humate.mission.adapter.ClearedMissionAdapter
import com.otclub.humate.mission.adapter.MatchingAdapter
import com.otclub.humate.mission.viewModel.MatchingViewModel

class MatchingFragment : Fragment() {
    private val matchingViewModel: MatchingViewModel by activityViewModels()
    private var mBinding: FragmentMatchingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMatchingBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as? MainActivity
        activity?.let {
            val toolbar = it.getToolbar() // MainActivity의 Toolbar를 가져옴
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            it.setToolbarTitle("동행 목록")

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = true
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
        }

        // RecyclerView 설정
        mBinding?.matchingRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ClearedMissionAdapter(emptyList()) {
                    matching ->
                findNavController().navigate(
                    R.id.action_matchingFragment_to_missionFragment
                )
            }
        }

        // ViewModel에서 데이터 관찰
        matchingViewModel.matchingResponseDTOList.observe(viewLifecycleOwner) { response ->
            response?.let {
                val adapter = MatchingAdapter(it) { matching ->
                    findNavController().navigate(
                        R.id.action_matchingFragment_to_missionFragment
                    )
                }
                Log.i("adapter : ", adapter.toString())
                mBinding?.matchingRecyclerView?.adapter = adapter
            }
        }

        matchingViewModel.fetchMatching()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }


}
package com.thesis.client.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.thesis.client.GlobalViewModel
import com.thesis.client.data.DATA_SELECTION_TYPE
import com.thesis.client.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val globalViewModel: GlobalViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        bindDataSelection()


        return root
    }

    private fun bindDataSelection() {

        globalViewModel.dataSelectionType.value.let { type ->
            when (type) {
                DATA_SELECTION_TYPE.PARTITION -> {
                    binding.dataPartition.isChecked = true
                }
                DATA_SELECTION_TYPE.CLASS_HALF -> {
                    binding.dataClassesHalf.isChecked = true
                }
                DATA_SELECTION_TYPE.CLASS_FULL -> {
                    binding.dataClassesFull.isChecked = true
                }
                else -> {
                    binding.dataPartition.isChecked = true
                }
            }
        }

        binding.dataPartition.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                globalViewModel.changeDataSelection(DATA_SELECTION_TYPE.PARTITION)
            }
        }

        binding.dataClassesFull.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                globalViewModel.changeDataSelection(DATA_SELECTION_TYPE.CLASS_FULL)
            }
        }

        binding.dataClassesHalf.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                globalViewModel.changeDataSelection(DATA_SELECTION_TYPE.CLASS_HALF)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
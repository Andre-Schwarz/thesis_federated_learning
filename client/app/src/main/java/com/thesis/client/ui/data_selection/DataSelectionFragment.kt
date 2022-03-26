package com.thesis.client.ui.data_selection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.thesis.client.GlobalViewModel
import com.thesis.client.data.DATA_CLASSES
import com.thesis.client.data.DATA_CLASSES.*
import com.thesis.client.data.DATA_SELECTION_TYPE
import com.thesis.client.data.ModelArchitecture
import com.thesis.client.databinding.FragmentDataSelectionBinding

class DataSelectionFragment : Fragment() {

    private lateinit var dataSelectionViewModel: DataSelectionViewModel
    private var _binding: FragmentDataSelectionBinding? = null
    private val globalViewModel: GlobalViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val classes = listOf(
        AIRPLANE,
        AUTOMOBILE,
        BIRD,
        CAT,
        DEER,
        DOG,
        FROG,
        HORSE,
        SHIP,
        TRUCK
    )

    private val selectedClasses = mutableListOf<DATA_CLASSES>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataSelectionViewModel =
            ViewModelProvider(this).get(DataSelectionViewModel::class.java)

        _binding = FragmentDataSelectionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        bindArchitectureSelection()
        bindDataSelection()
        bindClassSelection()

        return root
    }

    private fun bindArchitectureSelection() {
        globalViewModel.selectedModelArchitecture.value.let { type ->
            when (type) {
                ModelArchitecture.CUSTOM -> {
                    binding.architectureCustom.isChecked = true
                }
                ModelArchitecture.MOBILENET -> {
                    binding.architectureMobinet.isChecked = true
                }
                else -> {
                }
            }
        }

        binding.architectureCustom.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                globalViewModel.changeSelectedModelArchitecture(ModelArchitecture.CUSTOM)
            }
        }

        binding.architectureMobinet.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                globalViewModel.changeSelectedModelArchitecture(ModelArchitecture.MOBILENET)
            }
        }

    }

    private fun bindClassSelection() {
        globalViewModel.dataSelectionType.observe(viewLifecycleOwner) {
            binding.classContainer.visibility = if (it == DATA_SELECTION_TYPE.PARTITION) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        globalViewModel.selectedDataClasses.value?.let { selectedClasses.addAll(it) }
        for (dataClass in classes) {
            val checkBox = CheckBox(this.context)
            checkBox.text = dataClass.className
            checkBox.isChecked = selectedClasses.contains(dataClass)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    Log.e("TAG", "onCreateView: " + dataClass.className)
                    selectedClasses.add(dataClass)

                } else {
                    selectedClasses.remove(dataClass)
                }

                selectedClasses.distinct()
                globalViewModel.changeDataClassSelection(selectedClasses)

            }
            binding.classContainer.addView(checkBox)
        }
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
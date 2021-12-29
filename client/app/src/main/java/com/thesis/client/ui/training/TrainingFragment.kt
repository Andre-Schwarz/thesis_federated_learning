package com.thesis.client.ui.training

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.thesis.client.data.FlowerClient
import com.thesis.client.databinding.FragmentHomeBinding

class TrainingFragment : Fragment() {

    // region Fields

    private lateinit var homeViewModel: TrainingViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    // endregion

    // region Lifecycle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        homeViewModel =
//            ViewModelProvider(this).get(TrainingViewModel::class.java)

        val flowerClient = FlowerClient(this.requireContext())

        homeViewModel = ViewModelProvider(
            this,
            TrainingViewModelFactory(this.requireContext(), flowerClient)
        ).get(
            TrainingViewModel::class.java
        )


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        bindViewData()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // endregion

    // region Private Methods
    private fun bindViewData() {
        binding?.let { binding ->
            homeViewModel.loadDataButtonText.observe(viewLifecycleOwner, {
                binding.buttonLoadData.text = it
            })
            homeViewModel.establishConnectionButtonText.observe(viewLifecycleOwner, {
                binding.buttonEstablishConnection.text = it
            })
            homeViewModel.startTrainingButtonText.observe(viewLifecycleOwner, {
                binding.buttonStartTraining.text = it
            })

            context?.let { context ->
                homeViewModel.loadDataImageDrawable.observe(viewLifecycleOwner, {
                    binding.imageLoadData.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            it
                        )
                    )
                })

                homeViewModel.establishConnectionImageDrawable.observe(viewLifecycleOwner, {
                    binding.imageEstablishConnection.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            it
                        )
                    )
                })

                homeViewModel.startTrainingImageDrawable.observe(viewLifecycleOwner, {
                    binding.imageStartTraining.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            it
                        )
                    )
                })
            }

            binding.buttonLoadData.setOnClickListener {
                homeViewModel.handleLoadDataButton(1)
            }
            binding.buttonEstablishConnection.setOnClickListener {
                homeViewModel.handleEstablishConnectionButton()
            }
            binding.buttonStartTraining.setOnClickListener {
                homeViewModel.handleStartTrainingButton()
            }
        }
    }

    // endregion
}
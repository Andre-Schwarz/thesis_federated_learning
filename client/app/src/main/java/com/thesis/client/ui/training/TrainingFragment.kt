package com.thesis.client.ui.training

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.thesis.client.GlobalViewModel
import com.thesis.client.data.FlowerClient
import com.thesis.client.databinding.FragmentTrainingBinding
import java.util.*


class TrainingFragment : Fragment() {

    // region Fields

    private val globalViewModel: GlobalViewModel by activityViewModels()
    private lateinit var trainingViewModel: TrainingViewModel
    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding

    // endregion

    // region Lifecycle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val flowerClient = FlowerClient(this.requireContext())

        trainingViewModel = ViewModelProvider(
            this,
            TrainingViewModelFactory(
                this.requireContext(),
                flowerClient,
                setResultText,
                globalViewModel
            )
        ).get(
            TrainingViewModel::class.java
        )

        _binding = FragmentTrainingBinding.inflate(inflater, container, false)
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
            trainingViewModel.loadDataButtonText.observe(viewLifecycleOwner) {
                binding.buttonLoadData.text = it
            }
            trainingViewModel.establishConnectionButtonText.observe(viewLifecycleOwner) {
                binding.buttonEstablishConnection.text = it
            }
            trainingViewModel.startTrainingButtonText.observe(viewLifecycleOwner) {
                binding.buttonStartTraining.text = it
            }

            context?.let { context ->
                trainingViewModel.loadDataImageDrawable.observe(viewLifecycleOwner) {
                    binding.imageLoadData.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            it
                        )
                    )
                }

                trainingViewModel.establishConnectionImageDrawable.observe(viewLifecycleOwner) {
                    binding.imageEstablishConnection.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            it
                        )
                    )
                }

                trainingViewModel.startTrainingImageDrawable.observe(viewLifecycleOwner) {
                    binding.imageStartTraining.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            it
                        )
                    )
                }
            }

            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            sharedPref?.let { sharedPreferences ->
                val clientId =
                    sharedPreferences.getString("clientId", "")
                if (clientId != null) {
                    binding.editClientId.setText(clientId)
                }
            }

            binding.buttonLoadData.setOnClickListener {
                val clientId = binding.editClientId.text.toString()

                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                sharedPref?.let { sharedPreferences ->
                    with(sharedPreferences.edit()) {
                        putString("clientId", clientId)
                        apply()
                    }
                }

                trainingViewModel.handleLoadDataButton(clientId)
            }
            binding.buttonEstablishConnection.setOnClickListener {
                val ip = binding.editServerIp.text.toString()
                val port = Integer.valueOf(binding.editServerPort.text.toString())

                setResultText("Establish Connection to$ip $port")

                trainingViewModel.handleEstablishConnectionButton(ip, port)
            }
            binding.buttonStartTraining.setOnClickListener {
                trainingViewModel.handleStartTrainingButton()
            }

            trainingViewModel.loadDataButtonEnabled.observe(viewLifecycleOwner) {
                binding.buttonLoadData.isEnabled = it
                if (it == false) {
                    binding.editClientId.isEnabled = false
                    binding.editServerIp.isEnabled = false
                    binding.editServerPort.isEnabled = false
                }
            }
            trainingViewModel.establishConnectionButtonEnabled.observe(viewLifecycleOwner) {
                binding.buttonEstablishConnection.isEnabled = it
            }
            trainingViewModel.startTrainingButtonEnabled.observe(viewLifecycleOwner) {
                binding.buttonStartTraining.isEnabled = it
            }

            binding.grpcResponseText.movementMethod = ScrollingMovementMethod()
        }
    }

    val setResultText: (String) -> Unit = { text: String ->
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        val time = dateFormat.format(Date())

        binding?.grpcResponseText?.append("\n$time   $text")
//        globalViewModel.addTextToLogging("\n$time   $text")

    }

    // endregion
}
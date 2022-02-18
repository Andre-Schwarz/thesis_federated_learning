package com.thesis.client.ui.logging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.thesis.client.GlobalViewModel
import com.thesis.client.databinding.FragmentLoggingBinding

class LoggingFragment : Fragment() {

    private val globalViewModel: GlobalViewModel by activityViewModels()
    private lateinit var loggingViewModel: LoggingViewModel
    private var _binding: FragmentLoggingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        loggingViewModel =
            ViewModelProvider(this).get(LoggingViewModel::class.java)

        _binding = FragmentLoggingBinding.inflate(inflater, container, false)
        val root: View = binding.root


//        globalViewModel.notifications.observe(viewLifecycleOwner, Observer { set ->
//            binding.grpcResponseText.text = set
//        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
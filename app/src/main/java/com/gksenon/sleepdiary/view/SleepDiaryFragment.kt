package com.gksenon.sleepdiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.view.utils.HeaderItemDecoration
import com.gksenon.sleepdiary.viewmodels.SleepDiaryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SleepDiaryFragment : Fragment() {

    private val viewModel: SleepDiaryViewModel by viewModels()
    private val sleepDiaryAdapter = SleepDiaryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sleep_diary, container, false)

        val sleepDiaryView: RecyclerView = view.findViewById(R.id.sleep_diary)
        sleepDiaryView.layoutManager = LinearLayoutManager(requireContext())
        sleepDiaryView.adapter = sleepDiaryAdapter
        sleepDiaryView.addItemDecoration(HeaderItemDecoration(requireContext()))

        viewModel.sleepDiary.observe(viewLifecycleOwner) { sleepDiary ->
            sleepDiaryAdapter.submitList(sleepDiary)
        }

        val addButton: FloatingActionButton = view.findViewById(R.id.add_button)
        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_sleepDiaryFragment_to_manualSleepCreationFragment)
        }

        return view
    }

}
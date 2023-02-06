package com.gksenon.sleepdiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.viewmodels.SleepCreationViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class SleepCreationFragment : Fragment() {

    private val viewModel: SleepCreationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sleep_creation, container, false)

        val startDateView: TextView = view.findViewById(R.id.start_date)
        val endDateView: TextView = view.findViewById(R.id.end_date)
        val startTimeView: TextView = view.findViewById(R.id.start_time)
        val endTimeView: TextView = view.findViewById(R.id.end_time)

        val dateFormat = SimpleDateFormat.getDateInstance()
        val timeFormat = SimpleDateFormat.getTimeInstance()
        viewModel.getStartDate().observe(viewLifecycleOwner) { startDate ->
            startDateView.text = dateFormat.format(startDate)
            startTimeView.text = timeFormat.format(startDate)
        }
        viewModel.getEndDate().observe(viewLifecycleOwner) { endDate ->
            endDateView.text = dateFormat.format(endDate)
            endTimeView.text = timeFormat.format(endDate)
        }

        startDateView.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_start_date)
                .build()

            picker.addOnPositiveButtonClickListener { startDate ->
                viewModel.updateStartDate(startDate)
            }

            picker.show(childFragmentManager, "start_date_picker")

        }

        endDateView.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_end_date)
                .build()

            picker.addOnPositiveButtonClickListener { startDate ->
                viewModel.updateEndDate(startDate)
            }

            picker.show(childFragmentManager, "end_date_picker")
        }

        startTimeView.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTitleText(R.string.select_start_time)
                .build()

            picker.addOnPositiveButtonClickListener {
                viewModel.updateStartTime(picker.hour, picker.minute)
            }

            picker.show(childFragmentManager, "start_time_picker")
        }

        endTimeView.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTitleText(R.string.select_end_time)
                .build()

            picker.addOnPositiveButtonClickListener {
                viewModel.updateEndTime(picker.hour, picker.minute)
            }

            picker.show(childFragmentManager, "end_time_picker")
        }

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save_sleep -> {
                    viewModel.saveSleep()
                    true
                }
                else -> false
            }
        }

        viewModel.getSaveStatus().observe(viewLifecycleOwner) { saved ->
            if (saved) findNavController().popBackStack()
        }

        return view
    }
}
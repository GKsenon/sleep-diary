package com.gksenon.sleepdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gksenon.sleepdiary.domain.Tracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(private val tracker: Tracker) : ViewModel() {

    val state = tracker.observe()

    fun start() {
        viewModelScope.launch { tracker.start() }
    }

    fun stop() {
        viewModelScope.launch { tracker.stop() }
    }
}
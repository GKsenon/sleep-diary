package com.gksenon.sleepdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gksenon.sleepdiary.domain.Diary
import com.gksenon.sleepdiary.domain.Sleep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SleepEditorViewModel @Inject constructor(private val diary: Diary) : ViewModel() {

    fun onSaveButtonClicked(start: Instant, end: Instant, onSleepSaved: () -> Unit) {
        viewModelScope.launch {
            val sleep = Sleep(id = UUID.randomUUID(), start = Date.from(start), end = Date.from(end))
            diary.saveSleep(sleep)
            onSleepSaved()
        }
    }
}
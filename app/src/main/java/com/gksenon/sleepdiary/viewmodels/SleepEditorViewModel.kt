package com.gksenon.sleepdiary.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gksenon.sleepdiary.data.Sleep
import com.gksenon.sleepdiary.data.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SleepEditorViewModel @Inject constructor(
    private val sleepRepository: SleepRepository
) : ViewModel() {

    private val dateFormatter = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        .apply { isLenient = false }
    private val timeFormatter = SimpleDateFormat("HHmm", Locale.getDefault())
        .apply { isLenient = false }

    private var _sleepEditorState = MutableStateFlow(
        SleepEditorState(
            startDateTextFieldState = TextFieldState(
                value = dateFormatter.format(Date()),
                validationStatus = ValidationStatus.VALID
            ),
            startTimeTextFieldState = TextFieldState(
                value = timeFormatter.format(Date()),
                validationStatus = ValidationStatus.VALID
            ),
            endDateTextFieldState = TextFieldState(
                value = dateFormatter.format(Date()),
                validationStatus = ValidationStatus.VALID
            ),
            endTimeTextFieldState = TextFieldState(
                value = timeFormatter.format(Date()),
                validationStatus = ValidationStatus.VALID
            )
        )
    )
    val sleepEditorState = _sleepEditorState.asStateFlow()

    fun onStartDateChanged(value: String) {
        _sleepEditorState.update { currentState ->
            currentState.copy(
                startDateTextFieldState = TextFieldState(
                    value = value.filter { it.isDigit() }.take(8),
                    validationStatus = ValidationStatus.VALID
                )
            )
        }
    }

    fun onStartTimeChanged(value: String) {
        _sleepEditorState.update { currentState ->
            currentState.copy(
                startTimeTextFieldState = TextFieldState(
                    value = value.filter { it.isDigit() }.take(4),
                    validationStatus = ValidationStatus.VALID
                )
            )
        }
    }

    fun onEndDateChanged(value: String) {
        _sleepEditorState.update { currentState ->
            currentState.copy(
                endDateTextFieldState = TextFieldState(
                    value = value.filter { it.isDigit() }.take(8),
                    validationStatus = ValidationStatus.VALID
                )
            )
        }
    }

    fun onEndTimeChanged(value: String) {
        _sleepEditorState.update { currentState ->
            currentState.copy(
                endTimeTextFieldState = TextFieldState(
                    value = value.filter { it.isDigit() }.take(4),
                    validationStatus = ValidationStatus.VALID
                )
            )
        }
    }

    fun saveSleep(onSaveSleep: () -> Unit) {
        val currentState = _sleepEditorState.value

        val startDate = parseDate(currentState.startDateTextFieldState.value)
        val startTime = parseTime(currentState.startTimeTextFieldState.value)
        val endDate = parseDate(currentState.endDateTextFieldState.value)
        val endTime = parseTime(currentState.endTimeTextFieldState.value)

        val startDateValidationStatus = when {
            startDate == null -> ValidationStatus.INVALID_FORMAT
            startDate.after(Date()) -> ValidationStatus.VALUE_IN_FUTURE
            else -> ValidationStatus.VALID
        }

        val startTimeValidationStatus = when {
            startTime == null -> ValidationStatus.INVALID_FORMAT
            startDate != null && startDate.before(Date()) && merge(startDate, startTime).after(Date()) -> ValidationStatus.VALUE_IN_FUTURE
            else -> ValidationStatus.VALID
        }

        val endDateValidationStatus = when {
            endDate == null -> ValidationStatus.INVALID_FORMAT
            endDate.after(Date()) -> ValidationStatus.VALUE_IN_FUTURE
            else -> ValidationStatus.VALID
        }

        val endTimeValidationStatus = when {
            endTime == null -> ValidationStatus.INVALID_FORMAT
            endDate != null && endDate.before(Date()) && merge(endDate, endTime).after(Date()) -> ValidationStatus.VALUE_IN_FUTURE
            else -> ValidationStatus.VALID
        }

        if(startDateValidationStatus == ValidationStatus.VALID
            && startTimeValidationStatus == ValidationStatus.VALID
            && endDateValidationStatus == ValidationStatus.VALID
            && endTimeValidationStatus == ValidationStatus.VALID) {
            viewModelScope.launch {
                val sleep = Sleep(
                    id = UUID.randomUUID(),
                    start = merge(startDate!!, startTime!!),
                    end = merge(endDate!!, endTime!!)
                )
                sleepRepository.saveSleep(sleep)
            }
            onSaveSleep()
        } else {
            _sleepEditorState.update {
                SleepEditorState(
                    startDateTextFieldState = it.startDateTextFieldState.copy(validationStatus = startDateValidationStatus),
                    startTimeTextFieldState = it.startTimeTextFieldState.copy(validationStatus = startTimeValidationStatus),
                    endDateTextFieldState = it.endDateTextFieldState.copy(validationStatus = endDateValidationStatus),
                    endTimeTextFieldState = it.endTimeTextFieldState.copy(validationStatus = endTimeValidationStatus)
                )
            }
        }
    }

    private fun parseDate(date: String): Date? = try {
        dateFormatter.parse(date)
    } catch (e: ParseException) {
        null
    }

    private fun parseTime(time: String): Date? = try {
        timeFormatter.parse(time)
    } catch (e: ParseException) {
        null
    }

    private fun merge(date: Date, time: Date): Date {
        val dateCalendar = Calendar.getInstance().also { it.time = date }
        val timeCalendar = Calendar.getInstance().also { it.time = time }
        dateCalendar.add(Calendar.HOUR, timeCalendar.get(Calendar.HOUR_OF_DAY))
        dateCalendar.add(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
        return dateCalendar.time
    }
}

data class SleepEditorState(
    val startDateTextFieldState: TextFieldState,
    val startTimeTextFieldState: TextFieldState,
    val endDateTextFieldState: TextFieldState,
    val endTimeTextFieldState: TextFieldState
)

data class TextFieldState(val value: String, val validationStatus: ValidationStatus)

enum class ValidationStatus {
    VALID, INVALID_FORMAT, VALUE_IN_FUTURE
}

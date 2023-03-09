package com.gksenon.sleepdiary.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.data.Sleep
import com.gksenon.sleepdiary.view.utils.HeaderItemDecoration
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SleepDiaryAdapter :
    ListAdapter<Sleep, SleepDiaryAdapter.DiaryViewHolder>(SleepDiaryDiffCallback),
    HeaderItemDecoration.Adapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sleep_diary_entry, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val diaryEntry = getItem(position)
        holder.bind(diaryEntry)
    }

    override fun hasHeader(position: Int): Boolean {
        val currentDiaryEntry = getItem(position)
        val currentCalendar = Calendar.getInstance().apply { time = currentDiaryEntry.start }

        val previousDiaryEntry = if (position > 0) getItem(position - 1) else null
        return if (previousDiaryEntry != null) {
            val previousCalendar = Calendar.getInstance().apply { time = previousDiaryEntry.start }
            previousCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR)
                    || previousCalendar.get(Calendar.MONTH) != currentCalendar.get(Calendar.MONTH)
                    || previousCalendar.get(Calendar.DAY_OF_MONTH) != currentCalendar.get(Calendar.DAY_OF_MONTH)
        } else true
    }

    override fun getItemHeader(position: Int): String {
        val dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT)
        val diaryEntry = getItem(position)
        return dateFormat.format(diaryEntry.start)
    }

    class DiaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val timeRangeTextView: TextView = view.findViewById(R.id.time_range)

        fun bind(sleep: Sleep) {
            val dateFormat =
                SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
            val start = dateFormat.format(sleep.start)
            val end = dateFormat.format(sleep.end)
            timeRangeTextView.text =
                timeRangeTextView.context.getString(R.string.sleep_time_range, start, end)
        }
    }
}

object SleepDiaryDiffCallback : DiffUtil.ItemCallback<Sleep>() {

    override fun areItemsTheSame(oldItem: Sleep, newItem: Sleep) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Sleep, newItem: Sleep) = oldItem == newItem
}

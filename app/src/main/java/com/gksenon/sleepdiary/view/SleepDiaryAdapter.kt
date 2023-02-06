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
import java.text.DateFormat
import java.text.SimpleDateFormat

class SleepDiaryAdapter :
    ListAdapter<Sleep, SleepDiaryAdapter.DiaryViewHolder>(SleepDiaryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sleep_diary_entry, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val diaryEntry = getItem(position)
        holder.bind(diaryEntry)
    }

    class DiaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val timeRangeTextView: TextView = view.findViewById(R.id.time_range)

        fun bind(sleep: Sleep) {
            val dateFormat =
                SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
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

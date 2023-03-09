package com.gksenon.sleepdiary.view.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

private const val DEFAULT_HEADER_PADDING_DP = 8
private const val DEFAULT_HEADER_TEXT_SIZE_SP = 32

class HeaderItemDecoration(context: Context): RecyclerView.ItemDecoration() {

    private val density = context.resources.displayMetrics?.density ?: 1f

    private val textPaint = Paint().apply {
        val textAppearanceValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.textAppearanceTitleLarge,
            textAppearanceValue,
            true
        )
        val textSizeValue = context.obtainStyledAttributes(
            textAppearanceValue.data,
            intArrayOf(android.R.attr.textSize)
        )
        textSize = textSizeValue.getDimensionPixelSize(0, (DEFAULT_HEADER_TEXT_SIZE_SP * density).toInt()).toFloat()

        val textColorValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurface,
            textColorValue,
            true
        )
        color = textColorValue.data
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter
        if (adapter is Adapter) {
            parent.children.forEach { view ->
                val position = parent.getChildAdapterPosition(view)
                if (adapter.hasHeader(position)) {
                    val bounds = Rect()
                    parent.getDecoratedBoundsWithMargins(view, bounds)
                    c.drawText(
                        adapter.getItemHeader(position),
                        bounds.left.toFloat() + DEFAULT_HEADER_PADDING_DP * density,
                        bounds.top.toFloat() + textPaint.textSize + DEFAULT_HEADER_PADDING_DP * density,
                        textPaint
                    )
                }
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val adapter = parent.adapter
        if(adapter is Adapter && adapter.hasHeader(parent.getChildAdapterPosition(view))) {
            outRect.top = (2 * DEFAULT_HEADER_PADDING_DP * density + textPaint.textSize).toInt()
        }
    }

    interface Adapter {
        fun hasHeader(position: Int): Boolean
        fun getItemHeader(position: Int): String
    }
}
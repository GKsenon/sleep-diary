package com.gksenon.sleepdiary.view.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DateVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val date = buildString {
            text.forEachIndexed { i, c ->
                if (i == 2 || i == 4) append('/')
                append(c)
            }
        }

        return TransformedText(
            text = AnnotatedString(date),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return if (offset <= 2) offset
                    else if (offset <= 4) offset + 1
                    else offset + 2
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return if (offset <= 4) offset
                    else if (offset <= 6) offset - 1
                    else offset - 2
                }
            })
    }
}
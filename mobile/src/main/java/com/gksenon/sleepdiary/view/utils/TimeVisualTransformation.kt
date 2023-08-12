package com.gksenon.sleepdiary.view.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class TimeVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val time = buildString {
            text.forEachIndexed { i, c ->
                if (i == 2) append(':')
                append(c)
            }
        }

        return TransformedText(
            text = AnnotatedString(time),
            offsetMapping = object : OffsetMapping {

                override fun originalToTransformed(offset: Int) =
                    if (offset <= 2) offset else offset + 1

                override fun transformedToOriginal(offset: Int) =
                    if (offset <= 3) offset else offset - 1
            }
        )
    }
}
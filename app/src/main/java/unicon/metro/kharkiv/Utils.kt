package unicon.metro.kharkiv

import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.view.ScaleGestureDetector

fun getTextBackgroundSize(
    x: Float,
    y: Float,
    text: String,
    paint: TextPaint
): Rect {
    val fontMetrics: Paint.FontMetrics = paint.fontMetrics
    val halfTextLength = paint.measureText(text) / 2 + 5
    return Rect(
        (x - halfTextLength).toInt(),
        (y + fontMetrics.top).toInt(),
        (x + halfTextLength).toInt(),
        (y + fontMetrics.bottom).toInt()
    )
}
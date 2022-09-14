package unicon.metro.kharkiv

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.TypedValue
import androidx.annotation.AttrRes

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

fun Context.getColorByAttr(@AttrRes id: Int): Int {
    val typedValue: TypedValue = TypedValue()
    theme.resolveAttribute(id, typedValue, true)
    return typedValue.data
}
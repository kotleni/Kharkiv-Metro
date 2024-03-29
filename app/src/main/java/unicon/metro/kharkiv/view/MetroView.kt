package unicon.metro.kharkiv.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import unicon.metro.kharkiv.*
import unicon.metro.kharkiv.types.Point
import unicon.metro.kharkiv.types.Size
import unicon.metro.kharkiv.types.Vector
import unicon.metro.kharkiv.types.elements.BaseElement
import unicon.metro.kharkiv.types.elements.BranchElement
import unicon.metro.kharkiv.types.elements.TransElement


class MetroView(var ctx: Context, attr: AttributeSet) : View(ctx, attr) {
    private var data = ArrayList<BaseElement>() // данные для отображения

    private val size = Size(240, 320)
    private val paint = Paint()
    private val textPaint = TextPaint()

    // цвета
    private val colorTextA = ctx.getColorByAttr(R.attr.colorAccent) // text rect
    private val colorTextB = ctx.getColorByAttr(R.attr.colorOnPrimary) // text color
    private val colorTrans = Color.parseColor(COLOR_TRANS)

    // настройки отрисовки
    private val padding = 32f
    private var scale = 2f

    // scroll
    private var scrollX = 0f
    private var scrollY = 0f

    // временные координаты
    private var dX = 0f
    private var dY = 0f

    private var mScaleGestureDetector: MyScaleGestureDetector? = null
    private var mScaleFactor = 1.0f
    private var lock = false
    private var mod = false

    // лямба для слушателя
    private var onItemClickListener: ((st: Point) -> Unit)? = null

    /* подготовить к работе */
    fun prepare() {
        val thiz = this

        // настраиваем пеинт для текста
        textPaint.color = colorTextB
        textPaint.strokeWidth = 3f
        textPaint.textAlign = Paint.Align.CENTER

        // настраиваем детектор зума
        mScaleGestureDetector = MyScaleGestureDetector(ctx, ScaleListener(this))
        mScaleGestureDetector!!.isQuickScaleEnabled = SCALE_QUICK_ENABLE

        // обрабатываем нажатия
        this.setOnTouchListener(object : OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event!!.pointerCount > 1) {
                    mScaleGestureDetector!!.onTouchEvent(event)
                    lock = true
                    return true
                } else {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            dX = event.rawX
                            dY = event.rawY
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (!lock) {
                                mod = true

                                val x = -(dX - event.rawX)
                                val y = -(dY - event.rawY)

                                if(DEBUG) println("x: $x, y: $y")

                                scrollX += x / mScaleFactor
                                scrollY += y / mScaleFactor

                                dX = event.rawX
                                dY = event.rawY

                                invalidate() // перерисовываем
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            if(!mod) {
                                val st = thiz.onTouch(Vector(event.x.toInt(), event.y.toInt()))
                                if(onItemClickListener != null)
                                    if (st != null)
                                        onItemClickListener!!.invoke(st)
                            }

                            // сбрашиваем
                            lock = false
                            mod = false
                        }
                        else -> return false
                    }
                }

                return true
            }
        })
    }

    /* установить данные для карты */
    fun setData(arr: ArrayList<BaseElement>) {
        this.data = arr
    }

    /* установить слушатель */
    fun setOnItemClickListener(func: (st: Point) -> Unit) {
        onItemClickListener = func
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        size.w = w
        size.h = h

        scrollX = (w / 2f) - 512
        scrollY = (h / 2f) - 512

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun performClick(): Boolean =
        super.performClick()

    fun onTouch(vec: Vector) : Point? {
        data.forEach { it ->
            if(it is BranchElement) {
                it.points.forEach {
                    if(it.name != null) {
                        val rect = getTextBackgroundSize(
                            scrollX + (padding + it.pos.x + 0f) * scale,
                            scrollY + (padding + it.pos.y + 0f) * scale,
                            resources.getString(it.name!!),
                            textPaint
                        )

                        val rrect = RectF(
                            rect.left - BUBLE_SCALE,
                            rect.top - BUBLE_SCALE,
                            rect.right + BUBLE_SCALE,
                            rect.bottom + BUBLE_SCALE
                        )

                        if (rrect.contains(vec.x.toFloat(), vec.y.toFloat())) {
                            return it
                        }
                    }
                }
            }
        }

        return null
    }

    private var defVector = Vector(-1, -1)
    private var lastBranchVec = defVector

    override fun onDraw(canvas: Canvas?) {
        // отрисовка линий
        data.forEach {
            if (it is BranchElement) {
                lastBranchVec = defVector

                it.points.forEach { p: Point ->
                    paint.style = Paint.Style.FILL
                    paint.color = it.color
                    paint.strokeWidth = LINE_WIDTH

                    paint.strokeCap = Paint.Cap.ROUND

                    if(lastBranchVec.x > 0)
                        canvas!!.drawLine(scrollX + (padding + lastBranchVec.x.toFloat()) * scale, scrollY + (padding + lastBranchVec.y.toFloat()) * scale, scrollX + (padding + p.pos.x.toFloat()) * scale, scrollY + (padding + p.pos.y.toFloat()) * scale, paint)

                    lastBranchVec = p.pos
                }
            }
        }

        // отрисовка пересадок
        data.forEach { el ->
            if (el is TransElement) {
                paint.style = Paint.Style.FILL
                paint.color = colorTrans
                paint.strokeWidth = LINE_WIDTH

                canvas!!.drawLine(
                    scrollX + (padding + el.posA.x.toFloat()) * scale,
                    scrollY + (padding + el.posA.y.toFloat()) * scale,
                    scrollX + (padding + el.posB.x.toFloat()) * scale,
                    scrollY + (padding + el.posB.y.toFloat()) * scale,
                    paint)
            }
        }

        // отрисовка станций
        data.forEach {
            if (it is BranchElement) {
                it.points.forEach { p: Point ->
                    if(p.name != null) {
                        paint.color = colorTextA

                        paint.style = Paint.Style.FILL

                        val rect = getTextBackgroundSize(scrollX + (padding + p.pos.x + 0f) * scale, scrollY + (padding + p.pos.y + 0f) * scale, resources.getString(p.name!!), textPaint)
                        canvas!!.drawRoundRect(
                            RectF(rect.left.toFloat() - BUBLE_SCALE,
                                rect.top.toFloat() - BUBLE_SCALE,
                                rect.right.toFloat() + BUBLE_SCALE,
                                rect.bottom.toFloat() + BUBLE_SCALE),
                            28f, 28f, paint)
                        canvas.drawText(resources.getString(p.name!!), scrollX + (padding + p.pos.x + 0f) * scale, scrollY + (padding + p.pos.y + 0f) * scale, textPaint)
                    }
                }
            }
        }
    }

    private class ScaleListener(var thiz: MetroView) : MyScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: MyScaleGestureDetector?): Boolean {
            thiz.mScaleFactor *= detector!!.getScaleFactor()
            thiz.mScaleFactor = Math.max(SCALE_FACTOR_MIN, Math.min(thiz.mScaleFactor, SCALE_FACTOR_MAX))

            thiz.scaleX = thiz.mScaleFactor
            thiz.scaleY = thiz.mScaleFactor

            return super.onScale(detector)
        }
    }
}
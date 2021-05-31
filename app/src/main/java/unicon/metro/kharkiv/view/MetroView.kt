package unicon.metro.kharkiv.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import unicon.metro.kharkiv.*
import unicon.metro.kharkiv.types.Point
import unicon.metro.kharkiv.types.Size
import unicon.metro.kharkiv.types.Vector
import unicon.metro.kharkiv.types.elements.BaseElement
import unicon.metro.kharkiv.types.elements.BranchElement
import unicon.metro.kharkiv.types.elements.TransElement


class MetroView(var ctx: Context, var attr: AttributeSet) : View(ctx, attr) {
    private var data = ArrayList<BaseElement>() // данные для отображения

    private val size = Size(240, 320)
    private val paint = Paint()
    private val textPaint = TextPaint()

    // цвета
    private val colorTextA = Color.parseColor(COLOR_TEXT_A)
    private val colorTextB = Color.parseColor(COLOR_TEXT_B)
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
    fun prepare(root: View) {
        val thiz = this

        // настраиваем пеинт для текста
        textPaint.color = colorTextB
        textPaint.strokeWidth = 3f
        textPaint.textAlign = Paint.Align.CENTER

        // настраиваем детектор зума
        mScaleGestureDetector = MyScaleGestureDetector(ctx, ScaleListener(this))
        mScaleGestureDetector!!.isQuickScaleEnabled = SCALE_QUICK_ENABLE

        // обрабатываем нажатия
        this.setOnTouchListener(object : View.OnTouchListener {
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

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun onTouch(vec: Vector) : Point? {
        data.forEach {
            if(it is BranchElement) {
                it.points.forEach {
                    if(it.name != null) {
                        val rect = getTextBackgroundSize(
                            scrollX + (padding + it.pos.x + 0f) * scale,
                            scrollY + (padding + it.pos.y + 0f) * scale,
                            resources.getString(it.name!!),
                            textPaint
                        )

                        if (rect.contains(vec.x, vec.y))
                            return it
                    }
                }
            }
        }

        return null
    }

    var lastBranchVec = Vector(-1, -1)
    override fun onDraw(canvas: Canvas?) {
        // отрисовка линий
        data.forEach {
            if (it is BranchElement) {
                lastBranchVec = Vector(-1, -1)

                (it as BranchElement).points.forEach { p: Point ->
                    paint.style = Paint.Style.FILL
                    paint.color = it.color
                    paint.strokeWidth = 6f

                    paint.strokeCap = Paint.Cap.ROUND

                    if(lastBranchVec.x > 0)
                        canvas!!.drawLine(scrollX + (padding + lastBranchVec.x.toFloat()) * scale, scrollY + (padding + lastBranchVec.y.toFloat()) * scale, scrollX + (padding + p.pos.x.toFloat()) * scale, scrollY + (padding + p.pos.y.toFloat()) * scale, paint)

                    lastBranchVec = p.pos
                }
            }
        }

        // отрисовка пересадок
        data.forEach {
            if (it is TransElement) {
                val el = (it as TransElement)

                paint.style = Paint.Style.FILL
                paint.color = colorTrans
                paint.strokeWidth = 6f

                canvas!!.drawLine(scrollX + (padding + el.posA.x.toFloat()) * scale, scrollY + (padding + el.posA.y.toFloat()) * scale, scrollX + (padding + el.posB.x.toFloat()) * scale, scrollY + (padding + el.posB.y.toFloat()) * scale, paint)
            }
        }

        // отрисовка станций
        data.forEach {
            if (it is BranchElement) {
                (it as BranchElement).points.forEach { p: Point ->
                    if(p.name != null) {
                        paint.color = colorTextA
                        paint.style = Paint.Style.FILL

                        val rect = getTextBackgroundSize(scrollX + (padding + p.pos.x + 0f) * scale, scrollY + (padding + p.pos.y + 0f) * scale, resources.getString(p.name!!), textPaint)
                        canvas!!.drawRect(rect, paint)
                        canvas!!.drawText(resources.getString(p.name!!), scrollX + (padding + p.pos.x + 0f) * scale, scrollY + (padding + p.pos.y + 0f) * scale, textPaint)
                    }
                }
            }
        }
    }

    private class ScaleListener(var thiz: MetroView) : MyScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: MyScaleGestureDetector?): Boolean {
            thiz.mScaleFactor *= scaleGestureDetector!!.getScaleFactor()
            thiz.mScaleFactor = Math.max(SCALE_FACTOR_MIN, Math.min(thiz.mScaleFactor, SCALE_FACTOR_MAX))

            thiz.scaleX = thiz.mScaleFactor
            thiz.scaleY = thiz.mScaleFactor

            return super.onScale(scaleGestureDetector)
        }
    }
}
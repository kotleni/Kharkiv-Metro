package unicon.metro.kharkiv

import android.util.AttributeSet
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import unicon.metro.kharkiv.types.Point
import unicon.metro.kharkiv.types.Size
import unicon.metro.kharkiv.types.Vector
import unicon.metro.kharkiv.types.elements.BaseElement
import unicon.metro.kharkiv.types.elements.BranchElement
import unicon.metro.kharkiv.types.elements.TransElement

class MetroView(var ctx: Context, var attr: AttributeSet) : View(ctx, attr) {
    var data = ArrayList<BaseElement>()

    private val size = Size(240, 320)
    private val paint = Paint()
    private val textPaint = TextPaint()

    private val colorTextA = Color.parseColor("#F2F2F2")
    private val colorTextB = Color.parseColor("#76919A")
    private val colorTrans = Color.MAGENTA

    private val padding = 32f
    private val scale = 2f

    private var dX = 0f
    private var dY = 0f

    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1.0f
    private var lock = false
    private var mod = false

    private var onItemClickListener: ((st: Point) -> Unit)? = null

    fun prepare() {
        val thiz = this
        // map.prepare()

        textPaint.color = colorTextB
        textPaint.strokeWidth = 3f
        textPaint.textAlign = Paint.Align.CENTER

        mScaleGestureDetector = ScaleGestureDetector(ctx, MetroView.ScaleListener(this))

        this.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event!!.pointerCount > 1) {
                    mScaleGestureDetector!!.onTouchEvent(event)
                    lock = true
                    return true
                } else {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            dX = thiz.x - event.rawX
                            dY = thiz.y - event.rawY
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (!lock) {
                                mod = true

                                val x = event.rawX + dX
                                val y = event.rawY + dY
                                thiz.animate()
                                        .x(x)
                                        .y(y)
                                        .setDuration(0)
                                        .start()
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            if(!mod) {
                                val st = thiz.onTouch(Vector(event.x.toInt(), event.y.toInt()))
                                if(onItemClickListener != null)
                                    if (st != null)
                                        onItemClickListener!!.invoke(st)
                            }

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

    fun setOnItemClickListener(func: (st: Point) -> Unit) {
        onItemClickListener = func
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        size.w = w
        size.h = h

        this.animate()
            .x((w / 2f) - 512)
            .y((h / 2f) - 512)
            .setDuration(600)
            .start()

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
                            (padding + it.pos.x + 0f) * scale,
                            (padding + it.pos.y + 0f) * scale,
                            it.name!!,
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
                        canvas!!.drawLine((padding + lastBranchVec.x.toFloat()) * scale, (padding + lastBranchVec.y.toFloat()) * scale, (padding + p.pos.x.toFloat()) * scale, (padding + p.pos.y.toFloat()) * scale, paint)

                    lastBranchVec = p.pos
                }
            }
        }

        // отрисовка пересадок
        data.forEach {
            if (it is TransElement) {
                var el = (it as TransElement)

                paint.style = Paint.Style.FILL
                paint.color = colorTrans
                paint.strokeWidth = 6f

                canvas!!.drawLine((padding + el.posA.x.toFloat()) * scale, (padding + el.posA.y.toFloat()) * scale, (padding + el.posB.x.toFloat()) * scale, (padding + el.posB.y.toFloat()) * scale, paint)
            }
        }

        // отрисовка станций
        data.forEach {
            if (it is BranchElement) {
                (it as BranchElement).points.forEach { p: Point ->
                    if(p.name != null) {
                        paint.color = colorTextA
                        paint.style = Paint.Style.FILL

                        val rect = getTextBackgroundSize((padding + p.pos.x + 0f) * scale, (padding + p.pos.y + 0f) * scale, p.name!!, textPaint)
                        canvas!!.drawRect(rect, paint)
                        canvas!!.drawText(p.name!!, (padding + p.pos.x + 0f) * scale, (padding + p.pos.y + 0f) * scale, textPaint)

                    }
                }
            }
        }
    }

    private class ScaleListener(var thiz: MetroView) : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            thiz.mScaleFactor *= scaleGestureDetector.scaleFactor
            thiz.mScaleFactor = Math.max(
                    0.9f,
                    Math.min(thiz.mScaleFactor, 6.0f)
            )
            thiz.scaleX = thiz.mScaleFactor
            thiz.scaleY = thiz.mScaleFactor
            return true
        }
    }
}
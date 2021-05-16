package unicon.metro.kharkiv

import android.content.Context
import android.os.Build
import android.os.Handler
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ViewConfiguration

class MyScaleGestureDetector @JvmOverloads constructor(
    private val mContext: Context,
    private val mListener: OnScaleGestureListener,
    handler: Handler? = null
) {
    interface OnScaleGestureListener {
        fun onScale(detector: MyScaleGestureDetector?): Boolean
        fun onScaleBegin(detector: MyScaleGestureDetector?): Boolean
        fun onScaleEnd(detector: MyScaleGestureDetector?)
    }

    open class SimpleOnScaleGestureListener :
        OnScaleGestureListener {
        override fun onScale(detector: MyScaleGestureDetector?): Boolean {
            return false
        }

        override fun onScaleBegin(detector: MyScaleGestureDetector?): Boolean {
            return true
        }

        override fun onScaleEnd(detector: MyScaleGestureDetector?) {
            // Intentionally empty
        }
    }

    private var mFocusX = 0f
    private var mFocusY = 0f
    private var mQuickScaleEnabled = false

    var isStylusScaleEnabled = false
    private var mCurrSpan = 0f
    private var mPrevSpan = 0f
    private var mInitialSpan = 0f
    private var mCurrSpanX = 0f
    private var mCurrSpanY = 0f
    private var mPrevSpanX = 0f
    private var mPrevSpanY = 0f
    private var mCurrTime: Long = 0
    private var mPrevTime: Long = 0

    var isInProgress = false
        private set

    private val mSpanSlop: Int

    private val mMinSpan: Int
    private val mHandler: Handler?
    private var mAnchoredScaleStartX = 0f
    private var mAnchoredScaleStartY = 0f
    private var mAnchoredScaleMode = ANCHORED_SCALE_MODE_NONE

    private var mGestureDetector: GestureDetector? = null
    private var mEventBeforeOrAboveStartingGestureEvent = false

    fun onTouchEvent(event: MotionEvent): Boolean {
//        if (mInputEventConsistencyVerifier != null) {
//            mInputEventConsistencyVerifier.onTouchEvent(event, 0)
//        }
        mCurrTime = event.eventTime
        val action = event.actionMasked

        // Forward the event to check for double tap gesture
        if (mQuickScaleEnabled) {
            mGestureDetector!!.onTouchEvent(event)
        }
        val count = event.pointerCount
        val isStylusButtonDown =
            event.buttonState and MotionEvent.BUTTON_STYLUS_PRIMARY != 0
        val anchoredScaleCancelled =
            mAnchoredScaleMode == ANCHORED_SCALE_MODE_STYLUS && !isStylusButtonDown
        val streamComplete =
            action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || anchoredScaleCancelled
        if (action == MotionEvent.ACTION_DOWN || streamComplete) {
            // Reset any scale in progress with the listener.
            // If it's an ACTION_DOWN we're beginning a new event stream.
            // This means the app probably didn't give us all the events. Shame on it.
            if (isInProgress) {
                mListener.onScaleEnd(this)
                isInProgress = false
                mInitialSpan = 0f
                mAnchoredScaleMode = ANCHORED_SCALE_MODE_NONE
            } else if (inAnchoredScaleMode() && streamComplete) {
                isInProgress = false
                mInitialSpan = 0f
                mAnchoredScaleMode = ANCHORED_SCALE_MODE_NONE
            }
            if (streamComplete) {
                return true
            }
        }
        if (!isInProgress && isStylusScaleEnabled && !inAnchoredScaleMode()
            && !streamComplete && isStylusButtonDown
        ) {
            // Start of a button scale gesture
            mAnchoredScaleStartX = event.x
            mAnchoredScaleStartY = event.y
            mAnchoredScaleMode = ANCHORED_SCALE_MODE_STYLUS
            mInitialSpan = 0f
        }
        val configChanged =
            action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_POINTER_DOWN || anchoredScaleCancelled
        val pointerUp = action == MotionEvent.ACTION_POINTER_UP
        val skipIndex = if (pointerUp) event.actionIndex else -1

        // Determine focal point
        var sumX = 0f
        var sumY = 0f
        val div = if (pointerUp) count - 1 else count
        val focusX: Float
        val focusY: Float
        if (inAnchoredScaleMode()) {
            // In anchored scale mode, the focal pt is always where the double tap
            // or button down gesture started
            focusX = mAnchoredScaleStartX
            focusY = mAnchoredScaleStartY
            mEventBeforeOrAboveStartingGestureEvent = if (event.y < focusY) {
                true
            } else {
                false
            }
        } else {
            for (i in 0 until count) {
                if (skipIndex == i) continue
                sumX += event.getX(i)
                sumY += event.getY(i)
            }
            focusX = sumX / div
            focusY = sumY / div
        }

        // Determine average deviation from focal point
        var devSumX = 0f
        var devSumY = 0f
        for (i in 0 until count) {
            if (skipIndex == i) continue

            // Convert the resulting diameter into a radius.
            devSumX += Math.abs(event.getX(i) - focusX)
            devSumY += Math.abs(event.getY(i) - focusY)
        }
        val devX = devSumX / div
        val devY = devSumY / div

        // Span is the average distance between touch points through the focal point;
        // i.e. the diameter of the circle with a radius of the average deviation from
        // the focal point.
        val spanX = devX * 2
        val spanY = devY * 2
        val span: Float
        span = if (inAnchoredScaleMode()) {
            spanY
        } else {
            Math.hypot(spanX.toDouble(), spanY.toDouble()).toFloat()
        }

        // Dispatch begin/end events as needed.
        // If the configuration changes, notify the app to reset its current state by beginning
        // a fresh scale event stream.
        val wasInProgress = isInProgress
        mFocusX = focusX
        mFocusY = focusY
        if (!inAnchoredScaleMode() && isInProgress && (span < mMinSpan || configChanged)) {
            mListener.onScaleEnd(this)
            isInProgress = false
            mInitialSpan = span
        }
        if (configChanged) {
            mCurrSpanX = spanX
            mPrevSpanX = mCurrSpanX
            mCurrSpanY = spanY
            mPrevSpanY = mCurrSpanY
            mCurrSpan = span
            mPrevSpan = mCurrSpan
            mInitialSpan = mPrevSpan
        }
        val minSpan = if (inAnchoredScaleMode()) mSpanSlop else mMinSpan
        if (!isInProgress && span >= minSpan &&
            (wasInProgress || Math.abs(span - mInitialSpan) > mSpanSlop)
        ) {
            mCurrSpanX = spanX
            mPrevSpanX = mCurrSpanX
            mCurrSpanY = spanY
            mPrevSpanY = mCurrSpanY
            mCurrSpan = span
            mPrevSpan = mCurrSpan
            mPrevTime = mCurrTime
            isInProgress = mListener.onScaleBegin(this)
        }

        // Handle motion; focal point and span/scale factor are changing.
        if (action == MotionEvent.ACTION_MOVE) {
            mCurrSpanX = spanX
            mCurrSpanY = spanY
            mCurrSpan = span
            var updatePrev = true
            if (isInProgress) {
                updatePrev = mListener.onScale(this)
            }
            if (updatePrev) {
                mPrevSpanX = mCurrSpanX
                mPrevSpanY = mCurrSpanY
                mPrevSpan = mCurrSpan
                mPrevTime = mCurrTime
            }
        }
        return true
    }

    private fun inAnchoredScaleMode(): Boolean {
        return mAnchoredScaleMode != ANCHORED_SCALE_MODE_NONE
    }

    var isQuickScaleEnabled: Boolean
        get() = mQuickScaleEnabled
        set(scales) {
            mQuickScaleEnabled = scales
            if (mQuickScaleEnabled && mGestureDetector == null) {
                val gestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        // Double tap: start watching for a swipe
                        mAnchoredScaleStartX = e.x
                        mAnchoredScaleStartY = e.y
                        mAnchoredScaleMode =
                            ANCHORED_SCALE_MODE_DOUBLE_TAP
                        return true
                    }
                }
                mGestureDetector = GestureDetector(mContext, gestureListener, mHandler)
            }
        }

    fun getFocusX(): Float {
        return mFocusX
    }

    fun getFocusY(): Float {
        return mFocusY
    }

    fun getCurrentSpan(): Float {
        return mCurrSpan
    }

    fun getCurrentSpanX(): Float {
        return mCurrSpanX
    }

    fun getCurrentSpanY(): Float {
        return mCurrSpanY
    }

    fun getPreviousSpan(): Float {
        return mPrevSpan
    }

    fun getPreviousSpanX(): Float {
        return mPrevSpanX
    }

    fun getPreviousSpanY(): Float {
        return mPrevSpanY
    }

    fun getScaleFactor(): Float {
        if (inAnchoredScaleMode()) {
            // Drag is moving up; the further away from the gesture
            // start, the smaller the span should be, the closer,
            // the larger the span, and therefore the larger the scale
            val scaleUp =
                mEventBeforeOrAboveStartingGestureEvent && mCurrSpan < mPrevSpan ||
                        !mEventBeforeOrAboveStartingGestureEvent && mCurrSpan > mPrevSpan
            val spanDiff =
                Math.abs(1 - mCurrSpan / mPrevSpan) * SCALE_FACTOR
            return if (mPrevSpan <= mSpanSlop) 1f else if (scaleUp) 1 + spanDiff else 1 - spanDiff
        }
        return if (mPrevSpan > 0) mCurrSpan / mPrevSpan else 1f
    }

    fun getTimeDelta(): Long {
        return mCurrTime - mPrevTime
    }

    fun getEventTime(): Long {
        return mCurrTime
    }

    companion object {
        private const val TAG = "ScaleGestureDetector"
        private const val TOUCH_STABILIZE_TIME: Long = 128 // ms
        private const val SCALE_FACTOR = .5f
        private const val ANCHORED_SCALE_MODE_NONE = 0
        private const val ANCHORED_SCALE_MODE_DOUBLE_TAP = 1
        private const val ANCHORED_SCALE_MODE_STYLUS = 2
    }
    
    init {
        val viewConfiguration = ViewConfiguration.get(mContext)
        mSpanSlop = viewConfiguration.scaledTouchSlop * 2
        mMinSpan = 0
        mHandler = handler
        // Quick scale is enabled by default after JB_MR2
        val targetSdkVersion = mContext.applicationInfo.targetSdkVersion
        if (targetSdkVersion > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // quickScaleEnabled = true
        }
        // Stylus scale is enabled by default after LOLLIPOP_MR1
        if (targetSdkVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            // stylusScaleEnabled = true
        }
    }
}
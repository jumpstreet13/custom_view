package com.example.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.layout_search_toolbarl.view.*
import kotlin.math.sqrt

class SearchToolbar : FrameLayout, MotionLayout.TransitionListener {

    //параметры для квадрата
    private val paintRect = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectBack = RectF()
    private val path = Path()

    //параметры для кривой элемента
    private val rectEl = Rect()
    private val paintEl = Paint(Paint.ANTI_ALIAS_FLAG)

    private val radiusSmall = 24
    private val radiusBig = 1100

    private val completeDisposable = CompositeDisposable()

    constructor(context: Context) : super(context) {
        initial()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initial()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initial()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paintRect.color = Color.BLUE
        paintRect.style = Paint.Style.FILL

        val bottomUsers = ivUsers.bottom.toFloat()
        val bottomPost = ivPost.bottom.toFloat()

        //val t = radiusBig - sqrt(radiusBig * radiusBig - width * width / 4f).toDouble()

        //рисуем квадрат
        rectBack.set(0f, 0f, width.toFloat(), bottomPost)
        canvas?.drawRect(rectBack, paintRect)

        //рисуем полукруг
        path.reset()
        path.moveTo(0f, bottomPost)
        path.quadTo(width / 2.toFloat(), height.toFloat(), width.toFloat(), bottomPost)

        val t = ivPost.bottom
        if (t != 0) {
            val r = 0.5 * (t + width * width / (4 * t))
            val y = sqrt(r * r - bottomPost*bottomPost)
            Log.d("SearchToolbar", y.toString())
        }
        canvas?.drawPath(path, paintRect)

        paintElement(canvas, ivUsers)

    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
        invalidate()
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {

    }

    override fun onDetachedFromWindow() {
        completeDisposable.clear()
        super.onDetachedFromWindow()
    }

    private fun initial() {
        View.inflate(context, R.layout.layout_search_toolbarl, this)
        setWillNotDraw(false)
        motion.setTransitionListener(this)
    }

    private fun paintElement(canvas: Canvas?, view: ImageView) {
        //рисуем сложную фигуру
        paintEl.color = Color.GRAY
        paintEl.style = Paint.Style.FILL

        //большой круг для элемента
        canvas?.drawCircle(
            (view.left + (view.right - view.left) / 2).toFloat(),
            view.bottom.toFloat(),
            (view.bottom - view.top) / 2.toFloat(),
            paintEl
        )

        //квадрат для элемента
        rectEl.set(
            view.left - radiusSmall,
            view.bottom - radiusSmall,
            view.right + radiusSmall,
            view.bottom
        )

        canvas?.drawRect(rectEl, paintEl)

        //первый круг для сложного элемента

        canvas?.drawCircle(
            (view.left - radiusSmall).toFloat(),
            (view.bottom - radiusSmall).toFloat(),
            radiusSmall.toFloat(),
            paintRect
        )

        //второй круг для сложного элемента

        canvas?.drawCircle(
            (view.right + radiusSmall).toFloat(),
            (view.bottom - radiusSmall).toFloat(),
            radiusSmall.toFloat(),
            paintRect
        )
    }

}
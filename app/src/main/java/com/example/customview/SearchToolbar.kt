package com.example.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.motion.widget.MotionLayout
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.layout_search_toolbarl.view.*

class SearchToolbar : FrameLayout, MotionLayout.TransitionListener {

    //параметры для квадрата
    private val paintRect = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private val path = Path()
    //параметры для кривой элемента
    private val pathBz = Path()
    private val paintBz = Paint(Paint.ANTI_ALIAS_FLAG)

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

        paintRect.color = Color.GREEN
        paintRect.style = Paint.Style.FILL

        val bottomUsers = ivUsers.bottom.toFloat()
        val bottomPost = ivPost.bottom.toFloat()

        //рисуем квадрат
        rect.set(0f, 0f, width.toFloat(), bottomPost)
        canvas?.drawRect(rect, paintRect)

        //рисуем полукруг
        path.reset()
        path.moveTo(0f, bottomPost)
        path.quadTo(width / 2.toFloat(), bottomUsers, width.toFloat(), bottomPost)
        canvas?.drawPath(path, paintRect)

        //рисуем кривую безье для конкретного элемента
        paintBz.color = Color.BLACK
        paintBz.style = Paint.Style.FILL

        pathBz.reset()
        pathBz.moveTo(ivUsers.left.toFloat(), ivUsers.bottom.toFloat())
        pathBz.quadTo(
            (ivUsers.left + (ivUsers.right - ivUsers.left) / 2).toFloat(), ivUsers.top.toFloat(),
            ivUsers.right.toFloat(), ivUsers.bottom.toFloat()
        )

        Log.d("onDraw", "$bottomUsers")

        canvas?.drawPath(pathBz, paintBz)
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

}
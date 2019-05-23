package com.example.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class ShadeView : View {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    constructor(context: Context) : super(context) {
        initial(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initial(attrs)
    }

    constructor(
        context: Context, attrs: AttributeSet, defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initial(attrs)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        with(path) {
            moveTo(0f, 0f)
            quadTo(height / 2f, width / 2f, width.toFloat(), 0f)
        }

        canvas?.drawPath(path, paint)
    }

    private fun initial(attrs: AttributeSet?) {
        initPaint()
    }

    private fun initPaint() {
        paint.apply {
            color = Color.BLUE
            strokeWidth = 3f
            style = Paint.Style.FILL
            setShadowLayer(12f, 0f, 0f, Color.BLACK)
        }
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
    }

}
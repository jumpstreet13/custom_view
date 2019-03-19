package com.example.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.*
import java.lang.Math.*


class TestView : View {

    private lateinit var blue: Paint
    private lateinit var white: Paint
    private lateinit var red: Paint
    private lateinit var black: Paint

    private lateinit var pathRect: Path

    private var R0 = 0f
    private val R1 = 200f
    private val R2 = 50f
    private val angle = 30.0

    constructor(context: Context) : super(context) {
        initial()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initial()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initial()
    }

    private fun initial() {
        blue = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = 3f
                color = Color.BLUE
            }

        white = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = 3f
                color = Color.WHITE
            }

        red = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = 3f
                color = Color.RED
            }

        black = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = 3f
                color = Color.BLACK
            }

        pathRect = Path()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        R0 = width / 2f

        canvas?.drawCircle(width / 2f, width / 2f, R0, blue)

        circle(canvas, angle)
        smallCircle(canvas, angle)
        //rectangle(canvas, angle)
    }

    //рисуем маленькие круги
    private fun smallCircle(canvas: Canvas?, angle: Double) {
        val cosA = ((R0 * R0 + (R0 - R2) * (R0 - R2) - (R2 + R1) * (R2 + R1))) / (2.0 * R0 * (R0 - R2))
        val A = acos(cosA)
        val B = PI * angle / 180.0

        val xl = R0 - (cos(B - A)) * (R0 - R2)
        val yl = sin(B - A) * (R0 - R2)

        val xr = R0 - (cos(B + A)) * (R0 - R2)
        val yr = sin(B + A) * (R0 - R2)

        canvas?.drawCircle(xl.toFloat(), yl.toFloat() + R0, R2, red)
        canvas?.drawCircle(xr.toFloat(), yr.toFloat() + R0, R2, red)
    }

    //рисуем средний круг
    private fun circle(canvas: Canvas?, angle: Double) {
        val B = PI * angle / 180
        val y = sin(B) * R0
        val x = (1 - cos(B)) * R0
        canvas?.drawCircle(x.toFloat(), y.toFloat() + R0, R1, white)
    }

    /*//рисуем прямоугольник
    private fun rectangle(canvas: Canvas?, angle: Double) {
        val x = width / 2f
        val y = width / 2f

        pathRect.moveTo(x - 100, y - 100)
        pathRect.lineTo(x + 100, y - 100)
        pathRect.lineTo(x + 100, y + 100)
        pathRect.lineTo(x - 100, y + 100)

        canvas?.drawPath(pathRect, black)
    }*/
}
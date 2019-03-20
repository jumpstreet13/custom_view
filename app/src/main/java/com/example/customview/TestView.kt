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
    private val angle = 120.0//угол в градусах(переделать в pi)

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
                color = Color.BLUE
            }

        black = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = 3f
                color = Color.WHITE
            }

        pathRect = Path()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        R0 = width / 2f

        canvas?.drawCircle(width / 2f, width / 2f, R0, blue)

        circle(canvas, angle)
    }

    //рисуем маленькие круги
    private fun circle(canvas: Canvas?, angle: Double) {
        val cosA = ((R0 * R0 + (R0 - R2) * (R0 - R2) - (R2 + R1) * (R2 + R1))) / (2.0 * R0 * (R0 - R2))
        val A = acos(cosA)
        val B = PI * angle / 180.0

        //находим центр левого круга
        val xl = R0 - (cos(B - A)) * (R0 - R2)
        val yl = R0 + sin(B - A) * (R0 - R2)

        //находим центр правого круга
        val xr = R0 - (cos(B + A)) * (R0 - R2)
        val yr = R0 + sin(B + A) * (R0 - R2)

        //находим центр среднего круга
        val x = (1 - cos(B)) * R0
        val y = R0 + sin(B) * R0

        //находим точки основания трапеции
        val xta = R0 * (1 - cos(A + B) / cos(A))
        val yta = R0 * (1 + sin(A + B) / cos(A))
        val xtb = R0 * (1 - cos(B - A) / cos(A))
        val ytb = R0 * (1 + sin(B - A) / cos(A))

        //находим точки вершины трапеции
        val xtc = R0 - cos(B - A) * (R0 + R2 * (tan(A) - 1))
        val ytc = R0 + sin(B - A) * (R0 + R2 * (tan(A) - 1))
        val xtd = R0 - cos(B + A) * (R0 + R2 * (tan(A) - 1))
        val ytd = R0 + sin(B + A) * (R0 + R2 * (tan(A) - 1))

        //помечаем точки для трапеции
        pathRect.moveTo(xtc.toFloat(), ytc.toFloat())
        pathRect.lineTo(xtd.toFloat(), ytd.toFloat())
        pathRect.lineTo(xta.toFloat(), yta.toFloat())
        pathRect.lineTo(xtb.toFloat(), ytb.toFloat())

        //рисуем трапецию
        canvas?.drawPath(pathRect, black)
        //рисуем левый маленький круг
        canvas?.drawCircle(xl.toFloat(), yl.toFloat(), R2, red)
        //рисуем правый маленький круг
        canvas?.drawCircle(xr.toFloat(), yr.toFloat(), R2, red)
        //рисуем средний круг
        canvas?.drawCircle(x.toFloat(), y.toFloat(), R1, white)
    }
}
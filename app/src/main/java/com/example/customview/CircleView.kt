package com.example.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.*
import android.util.Log
import android.view.ViewTreeObserver
import java.lang.Math.*

class CircleView : View {

    private lateinit var blue: Paint
    private lateinit var white: Paint
    private lateinit var red: Paint
    private lateinit var black: Paint

    private lateinit var pathTrapezium: Path

    private var R0 = 0f //радиус большого круга
    private var R1 = 100f //радиус среднего круга
    private var R2 = 40f //радиус маленьких кругов
    private var angle = PI / 4

    private var xDeviation = 0f //отклонение от начала по x координат
    private var yDeviation = 0f //отклонение от начала по y координат

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

        pathTrapezium = Path()

        this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                R0 = width / 2f
                this@CircleView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })

        setBackgroundColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //рисуем круг
        canvas?.drawCircle(R0 + xDeviation, R0 + yDeviation, R0, blue)
        //рисуем вырез
        backgroundItem(canvas, angle)
    }

    fun setSmallCircle(r: Float) {
        R1 = r
        invalidate()
    }

    fun setCirle(r: Float) {
        R2 = r
        invalidate()
    }

    private fun backgroundItem(canvas: Canvas?, angle: Double) {
        val cosA = ((R0 * R0 + (R0 - R2) * (R0 - R2) - (R2 + R1) * (R2 + R1))) / (2.0 * R0 * (R0 - R2))
        val A = acos(cosA)
        val B = angle

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
        val cosW = ((R0 - R2) * (R0 - R2) + (R1 + R2) * (R1 + R2) - R0 * R0) / (2.0 * (R0 - R2) * (R1 + R2))
        val W = acos(cosW)
        val xtd = R0 - sign(B) * sin(B - A) * (R0 - (sin(W + A - PI / 2) * R1)) / cos(A)
        val ytd = R0 + sign(B) * cos(B - A) * (R0 - (sin(W + A - PI / 2) * R1)) / cos(A)
        val xtc = R0 - sign(B) * sin(B + A) * (R0 - (sin(W + A - PI / 2) * R1)) / cos(A)
        val ytc = R0 + sign(B) * cos(B + A) * (R0 - (sin(W + A - PI / 2) * R1)) / cos(A)

        //помечаем точки для трапеции
        pathTrapezium.reset()
        pathTrapezium.moveTo(xtc.toFloat() + xDeviation, ytc.toFloat() + yDeviation)
        pathTrapezium.lineTo(xtd.toFloat() + xDeviation, ytd.toFloat() + yDeviation)
        pathTrapezium.lineTo(xta.toFloat() + xDeviation, yta.toFloat() + yDeviation)
        pathTrapezium.lineTo(xtb.toFloat() + xDeviation, ytb.toFloat() + yDeviation)

        //рисуем левый маленький круг
        canvas?.drawCircle(xl.toFloat() + xDeviation, yl.toFloat() + yDeviation, R2, red)
        //рисуем правый маленький круг
        canvas?.drawCircle(xr.toFloat() + xDeviation, yr.toFloat() + yDeviation, R2, red)
        //рисуем средний круг
        canvas?.drawCircle(x.toFloat() + xDeviation, y.toFloat() + yDeviation, R1, white)
        //рисуем трапецию
        canvas?.drawPath(pathTrapezium, black)

    }

    private fun sign(A: Double) = if (A > 0 && A < PI / 2 || A > PI && A < 3 * PI / 2) 1 else -1
}
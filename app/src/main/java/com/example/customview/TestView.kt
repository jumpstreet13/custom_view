package com.example.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.*
import java.lang.Math.*

//view правильно рисуется при R1 >= 2*R2
class TestView : View {

    private lateinit var blue: Paint
    private lateinit var white: Paint

    private lateinit var pathTrapezium: Path

    private var R0 = 0f //радиус большого круга
    private var R1 = 100f //радиус среднего круга
    private var R2 = 50f //радиус маленьких кругов
    private val angle = 3 * PI / 4

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

        pathTrapezium = Path()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        R0 = width / 2f

        //рисуем круг
        canvas?.drawCircle(width / 2f, width / 2f, R0, blue)

        backgroundItem(canvas, angle)
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
        val xtc = R0 - cos(B - A) * (R0 + R2 * (tan(A) - 1))
        val ytc = R0 + sin(B - A) * (R0 + R2 * (tan(A) - 1))
        val xtd = R0 - cos(B + A) * (R0 + R2 * (tan(A) - 1))
        val ytd = R0 + sin(B + A) * (R0 + R2 * (tan(A) - 1))

        //помечаем точки для трапеции
        pathTrapezium.moveTo(xtc.toFloat(), ytc.toFloat())
        pathTrapezium.lineTo(xtd.toFloat(), ytd.toFloat())
        pathTrapezium.lineTo(xta.toFloat(), yta.toFloat())
        pathTrapezium.lineTo(xtb.toFloat(), ytb.toFloat())

        //рисуем трапецию
        canvas?.drawPath(pathTrapezium, white)
        //рисуем левый маленький круг
        canvas?.drawCircle(xl.toFloat(), yl.toFloat(), R2, blue)
        //рисуем правый маленький круг
        canvas?.drawCircle(xr.toFloat(), yr.toFloat(), R2, blue)
        //рисуем средний круг
        canvas?.drawCircle(x.toFloat(), y.toFloat(), R1, white)
    }
}
package com.example.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.*


class TestView : View {

    lateinit var p: Paint
    lateinit var path: Path
    lateinit var point1: Point
    lateinit var point21: Point
    lateinit var point22: Point

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
        p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.strokeWidth = 3f
        path = Path()

        point1 = Point(200, 300)
        point21 = Point(500, 600)
        point22 = Point(900, 200)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawARGB(80, 102, 204, 255);


        // первая линия
        p.color = Color.BLACK;
        canvas?.drawLine(100f, 100f, 600f, 100f, p);

        // точка отклонения для первой линии
        p.style = Paint.Style.FILL;
        p.color = Color.GREEN;
        canvas?.drawCircle(point1.x.toFloat(), point1.y.toFloat(), 10f, p);

        // квадратичная кривая
        path.reset();
        path.moveTo(100f, 100f);
        path.quadTo(point1.x.toFloat(), point1.y.toFloat(), 600f, 100f);
        p.style = Paint.Style.STROKE;
        canvas?.drawPath(path, p);


        // вторая линия
        p.color = Color.BLACK;
        canvas?.drawLine(400f, 400f, 1100f, 400f, p);

        // точки отклонения для второй линии
        p.style = Paint.Style.FILL;
        p.color = Color.BLUE;
        canvas?.drawCircle(point21.x.toFloat(), point21.y.toFloat(), 10f, p);
        canvas?.drawCircle(point22.x.toFloat(), point22.y.toFloat(), 10f, p);

        // кубическая кривая
        path.reset();
        path.moveTo(400f, 400f);
        path.cubicTo(point21.x.toFloat(), point21.y.toFloat(), point22.x.toFloat(), point22.y.toFloat(), 1100f, 400f);
        p.style = Paint.Style.STROKE;
        canvas?.drawPath(path, p);
    }
}
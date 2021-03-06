package com.example.customview

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import kotlin.math.*

class CustomToolbar : FrameLayout {

    private val colorLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val grayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val greenPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val yellowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blackPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val pathRectBlue = Path()
    private val pathRectWhite = Path()
    private val pathTr = Path()

    private val listView = mutableListOf<SelectView>()

    private val heightRect = resources.getDimension(R.dimen.toolbar_size)
    private val delta = resources.getDimension(R.dimen.delta)
    private val margin = resources.getDimension(R.dimen.margin_extra).toInt()

    private var durationAnim = 200L//скорость анимации
    private var borderRadius = 50f//отступы от круга
    private var radiusView = 60f//радиус большого круга
    private var radiusGradient = 500f//радиус градиента
    private var progress = 0f//изгиб кривой

    private var colorBackground: Int = 0 //цвет фона
    private var colorSelect: Int = 0 //цвет выделения
    private var colorLineBottom: Int = 0//цвет нижней линии

    private lateinit var curveBezierTop: CurveBezier
    private lateinit var curveBezierBottom: CurveBezier

    private var clickListener: OnPositionClickListener? = null

    //анимация для переключения элементов
    private var progressAnim = 0f

    constructor(context: Context) : super(context) {
        initial(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initial(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initial(attrs)
    }

    private fun initial(attrs: AttributeSet?) {
        setWillNotDraw(false)
        initAttrs(attrs)
        initPaint()
        initView()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        with(curveBezierTop) {
            Px0 = 0f
            Py0 = heightRect
            Px1 = width / 2f
            Px2 = width * 1f
            Py2 = heightRect
            Py1 = heightRect * (1 + progress)
        }

        with(curveBezierBottom) {
            Px0 = 0f
            Py0 = heightRect + delta
            Px1 = width / 2f
            Px2 = width * 1f
            Py2 = heightRect + delta
            Py1 = heightRect * (1 + progress) + delta
        }

        listView.forEachIndexed { index, value ->
            with(value) {
                val hu = abs(heightRect - view.height) / 2
                val hs = (heightRect + view.height) / 2
                val dhs = curveBezierTop.getY((view.x + view.width / 2) / width) - curveBezierTop.Py0

                val defX = width * (index + 1) / (listView.size + 1f) - view.width
                val defY = (if (!select) hu else hs) + dhs

                view.x = defX
                view.y = defY
                defaultX = defX
                defaultY = defY
            }
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        with(pathRectBlue) {
            reset()
            moveTo(curveBezierTop.Px0, curveBezierTop.Py0)
            quadTo(curveBezierTop.Px1, curveBezierTop.Py1, curveBezierTop.Px2, curveBezierTop.Py2)
            lineTo(curveBezierTop.Px2, 0f)
            lineTo(0f, 0f)
        }

        with(pathRectWhite) {
            reset()
            moveTo(curveBezierTop.Px0, curveBezierTop.Py0)
            quadTo(curveBezierTop.Px1, curveBezierTop.Py1, curveBezierTop.Px2, curveBezierTop.Py2)
            lineTo(curveBezierBottom.Px2, curveBezierBottom.Py2)
            quadTo(curveBezierBottom.Px1, curveBezierBottom.Py1, curveBezierBottom.Px0, curveBezierBottom.Py0)

        }

        canvas?.drawRect(0f, 0f, width.toFloat(), heightRect, backgroundPaint)
        canvas?.drawPath(pathRectBlue, backgroundPaint)

        listView.find { it.select }?.let { drawShape(it.view, radiusView, canvas, progressAnim) }

        canvas?.drawPath(pathRectWhite, colorLinePaint)

    }

    fun addImage(@DrawableRes idRes: Int) {
        if (listView.size > 5) return

        val iv = ImageView(context)
            .apply {
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setImageResource(idRes)
                drawable.setTint(colorLineBottom)
                this@CustomToolbar.touchDelegate =
                    TouchDelegate(
                        Rect(
                            top - margin,
                            left - margin,
                            bottom + margin,
                            right + margin
                        ), this
                    )
            }
        val sizeIv = if (iv.drawable.minimumHeight > iv.drawable.minimumWidth)
            iv.drawable.minimumHeight else iv.drawable.minimumWidth
        radiusView = if (sizeIv > radiusView) sizeIv.toFloat() else radiusView

        listView.add(SelectView(iv, 0f, 0f, false))
        listView.forEachIndexed { i, s ->
            s.view.setOnClickListener {
                select(i, true)
                clickListener?.onClickPosition(i, s.view)
            }
        }
        addView(iv)
        invalidate()
    }

    fun setOnPositionClickListener(listener: OnPositionClickListener) {
        this.clickListener = listener
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        curveBezierTop.Py1 = heightRect * (1 + progress)
        curveBezierBottom.Py1 = heightRect * (1 + progress) + delta
        changeView()
        invalidate()
    }

    fun getProgress() = progress

    fun setSelectColor(@ColorInt color: Int) {
        listView.forEach { v -> v.takeIf { it.select }?.view?.drawable?.setTint(color) }
        colorSelect = color
        invalidate()
    }

    fun setUnselectColor(@ColorInt color: Int) {
        listView.forEach { v -> v.takeIf { !it.select }?.view?.drawable?.setTint(color) }
        colorLinePaint.color = color
        colorLineBottom = color
        invalidate()
    }

    fun setDuration(duration: Long) {
        durationAnim = duration
    }

    fun setColorBackground(@ColorInt color: Int) {
        colorBackground = color
        backgroundPaint.color = color
        invalidate()
    }

    fun setRadiusGradient(radius: Float) {
        radiusGradient = radius
        invalidate()
    }

    fun setBorderRadius(radius: Float) {
        borderRadius = radius
        invalidate()
    }

    fun select(position: Int, animation: Boolean = false) {

        val currentView = listView.find { it.select }
        val selectView = listView[position]

        if (currentView == selectView) return

        currentView?.select = false
        selectView.select = true

        val hs = (heightRect + selectView.view.height) / 2
        val dhs = curveBezierTop.getY((selectView.view.x + selectView.view.width / 2) / width) - curveBezierTop.Py0
        val pathAnimSelect = Path().apply {
            moveTo(selectView.view.x, selectView.view.y)
            lineTo(selectView.view.x, hs + dhs)
        }
        if (animation) {
            ObjectAnimator.ofFloat(0f, 1f)
                .apply {
                    duration = durationAnim
                    addUpdateListener {
                        progressAnim = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }

            ObjectAnimator.ofFloat(selectView.view, View.X, View.Y, pathAnimSelect)
                .apply {
                    duration = durationAnim
                    addUpdateListener { selectView.defaultY = hs }
                    start()
                }

            ObjectAnimator.ofObject(ArgbEvaluator(), colorLineBottom, colorSelect)
                .apply {
                    duration = durationAnim
                    addUpdateListener {
                        val colorValue = it.animatedValue as Int
                        selectView.view.drawable.setTint(colorValue)
                    }
                    start()
                }
        } else {
            progressAnim = 1f
            selectView.defaultY = hs
            selectView.view.drawable.setTint(colorSelect)
            selectView.view.y = hs + dhs
            invalidate()
        }


        currentView?.let { v ->
            val hc = abs(heightRect - v.view.height) / 2
            val dhc = curveBezierTop.getY((v.view.x + v.view.width / 2f) / width) - curveBezierTop.Py0
            val pathAnimCurrent = Path().apply {
                moveTo(v.view.x, v.view.y)
                lineTo(v.view.x, hc + dhc)
            }
            if (animation) {
                ObjectAnimator.ofFloat(v.view, View.X, View.Y, pathAnimCurrent)
                    .apply {
                        duration = durationAnim
                        addUpdateListener { v.defaultY = hc }
                        start()
                    }

                ObjectAnimator.ofObject(ArgbEvaluator(), colorSelect, colorLineBottom)
                    .apply {
                        duration = durationAnim
                        addUpdateListener { v.view.drawable.setTint(it.animatedValue as Int) }
                        start()
                    }
            } else {
                v.defaultY = hc
                v.view.drawable.setTint(colorLineBottom)
                v.view.y = hc + dhc
                invalidate()
            }
        }
    }

    private fun drawShape(view: View, radius: Float, canvas: Canvas?, progress: Float) {
        val R = radius * progress

        //относительные величины
        val center = (view.x + view.width / 2f) / width
        //в зависимости от прогресса расстояние вычисляется расстояние справа
        val right = (view.x + view.width / 2 - (borderRadius + R / 2) * progress) / width
        //в зависимости от прогресса расстояние вычисляется расстояние слева
        val left = (view.x + view.width / 2 + (borderRadius + R / 2) * progress) / width

        val rh = (view.x + view.width / 2 + radiusGradient) / width
        val lh = (view.x + view.width / 2 - radiusGradient) / width

        //коэфициент для разных сторон
        val k = if (center > 0.5f) -1 else 1

        //находим центр большого круга
        val x0 = curveBezierTop.getX(center)
        //расчитывае высоту в зависимости от прогреса анимации
        val y0 = curveBezierTop.getY(center)
        //находим точки пересечения  левого маленького круга и кривой безье
        val xo1 = curveBezierTop.getX(left)
        //расчитывае высоту в зависимости от прогреса анимации
        val yo1 = curveBezierTop.getY(left)
        //находим точки пересечения правого маленького круга и кривой безье
        val xo2 = curveBezierTop.getX(right)
        //расчитывае высоту в зависимости от прогреса анимации
        val yo2 = curveBezierTop.getY(right)

        //точки для пересечения градиента и кривой безье
        val xlh = curveBezierTop.getX(lh)
        val ylh = curveBezierTop.getY(lh)
        val xrh = curveBezierTop.getX(rh)
        val yrh = curveBezierTop.getY(rh)

        //расстояние от низа левого маленького круга до центра большого
        val b = sqrt((xo1 - x0) * (xo1 - x0) + (yo1 - y0) * (yo1 - y0))
        //расстояние от края большого круга до низа маленького левого
        val e = b - R
        //радиус маленького круга левого
        val Rml = e * (1 + (e / (2 * R)))
        val cosa = (b * b + Rml * Rml - (R + Rml) * (R + Rml)) / (2 * b * Rml)
        val d = abs(y0 - yo1)
        val cosb = d / b
        val q = PI - acos(cosa) - acos(cosb)
        //координаты левого маленького круга
        val x2 = xo1 + k * sin(q.toFloat()) * Rml
        val y2 = yo1 - cos(q.toFloat()) * Rml

        //расстояние от низа правого маленького круга до центра большого
        val a = sqrt((xo2 - x0) * (xo2 - x0) + (yo2 - y0) * (yo2 - y0))
        val p = abs(y0 - yo2)
        //расстояние от края большого круга до низа маленького правого
        val f = a - R
        //радиус маленького круга правого
        val Rmr = f * (1 + f / (2 * R))
        val sinc = p / a
        val cosd = (a * a + (Rmr + R) * (Rmr + R) - Rmr * Rmr) / (2 * a * (Rmr + R))
        //координаты правого маленького круга
        val x3 = x0 - cos(asin(sinc) + k * acos(cosd)) * (Rmr + R)
        val y3 = y0 - k * sin(asin(sinc) + k * acos(cosd)) * (Rmr + R)

        val xdl = x0 - x2
        val ydl = y0 - y2
        val s = (Rml * Rml - R * R - xdl * xdl - ydl * ydl) / (2 * xdl)
        val z = ydl / xdl
        //точки пересечения правого и центрального кругов
        val yt = s * z / (1 + z * z)
        val xt = (Rml * Rml - R * R - xdl * xdl - 2 * ydl * yt - ydl * ydl) / (2 * xdl)

        val xdr = x0 - x3
        val ydr = y0 - y3
        val u = (Rmr * Rmr - R * R - xdr * xdr - ydr * ydr) / (2 * xdr)
        val j = ydr / xdr
        //точки пересечения левого и центрального кругов
        val yk = u * j / (1 + j * j)
        val xk = (Rmr * Rmr - R * R - xdr * xdr - 2 * ydr * yk - ydr * ydr) / (2 * xdr)

        val ac = sqrt((x0 - xlh) * (x0 - xlh) + (y0 - ylh) * (y0 - ylh))
        val bc = sqrt((x0 - xrh) * (x0 - xrh) + (y0 - yrh) * (y0 - yrh))

        val sins = (y0 - ylh) / ac
        //откуда начинает рисоваться градиент
        val aos = 180f * (1 + asin(sins) / PI.toFloat()) - 5f

        val sinb = (yrh - y0) / bc
        //на сколько градусов рисуется градиент
        val aoc = 180 - asin(sins) / PI.toFloat() * 180 + asin(sinb) / PI.toFloat() * 180 + 10f

        //строим фигуру для прикрывания цвета
        pathTr.reset()
        pathTr.moveTo(x0 + xt, y0 + yt)
        pathTr.lineTo(x0 + xk, y0 + yk)
        pathTr.lineTo(xo2, yo2)
        pathTr.lineTo(xo2, if (center > 0.5f) 2 * yo2 - y0 else 2 * y0 - yo2)
        pathTr.lineTo(xo1, if (center > 0.5f) 2 * y0 - yo1 else 2 * yo1 - y0)
        pathTr.lineTo(xo1, yo1)

        //шаблон градиента
        gradientPaint.shader = RadialGradient(
            x0,
            y0,
            radiusGradient * if (progress == 0f) 0.0001f else progress,
            colorSelect,
            colorBackground,
            Shader.TileMode.CLAMP
        )

        //рисуем градиент
        canvas?.drawArc(
            x0 - radiusGradient,
            y0 - radiusGradient,
            x0 + radiusGradient,
            y0 + radiusGradient,
            aos,
            aoc,
            true,
            gradientPaint
        )

        //рисуем фигуру для цвета
        canvas?.drawPath(pathTr, colorLinePaint)
        //рисуем левый круг
        canvas?.drawCircle(x2, y2, Rml, gradientPaint)
        //русуем правый круг
        canvas?.drawCircle(x3, y3, Rmr, gradientPaint)
        //рисуем центральный полукруг
        canvas?.drawArc(
            x0 - R,
            y0 - R,
            x0 + R,
            y0 + R,
            aos,
            aoc,
            true,
            colorLinePaint
        )

    }

    private fun initPaint() {

        backgroundPaint.apply {
            color = colorBackground
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

        colorLinePaint.apply {
            color = colorLineBottom
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

        grayPaint.apply {
            color = Color.GRAY
            strokeWidth = 3f
            style = Paint.Style.FILL
        }
        greenPaint.apply {
            color = Color.GREEN
            strokeWidth = 3f
            style = Paint.Style.FILL
        }
        yellowPaint.apply {
            color = Color.YELLOW
            strokeWidth = 3f
            style = Paint.Style.FILL
        }
        redPaint.apply {
            color = Color.RED
            strokeWidth = 3f
            style = Paint.Style.FILL
        }
        whitePaint.apply {
            color = Color.WHITE
            strokeWidth = 3f
            style = Paint.Style.FILL
        }
        blackPaint.apply {
            color = Color.BLACK
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

    }

    private fun initAttrs(attrs: AttributeSet?) {
        val a = context?.obtainStyledAttributes(attrs, R.styleable.CustomToolbar)
        a?.let {
            colorSelect = it.getColor(R.styleable.CustomToolbar_select_color, Color.BLUE)
            colorBackground = it.getColor(R.styleable.CustomToolbar_background_color, Color.BLACK)
            colorLineBottom = it.getColor(R.styleable.CustomToolbar_unselect_color, Color.WHITE)
            durationAnim = it.getInteger(R.styleable.CustomToolbar_duration, 200).toLong()
            radiusGradient = it.getDimension(R.styleable.CustomToolbar_radius_gradient, 300f)
            borderRadius = it.getDimension(R.styleable.CustomToolbar_border_radius, 50f)
            progress = it.getFloat(R.styleable.CustomToolbar_progress, 0f)
        }
        a?.recycle()
    }

    private fun initView() {
        minimumHeight = resources.getDimension(R.dimen.toolbar_height).toInt()
        curveBezierTop = CurveBezier()
        curveBezierBottom = CurveBezier()
    }

    private fun changeView() {
        listView.forEach {
            it.view.y =
                it.defaultY + curveBezierTop.getY((it.view.x + it.view.width / 2f) / width) - heightRect
        }
    }

}
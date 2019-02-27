package com.example.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import at.wirecube.additiveanimations.additive_animator.AdditiveAnimator
import at.wirecube.additiveanimations.additive_animator.AnimationEndListener
import at.wirecube.additiveanimations.helper.FloatProperty
import at.wirecube.additiveanimations.helper.evaluators.ColorEvaluator
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.layout_search_toolbarl.view.*
import java.util.concurrent.TimeUnit

class SearchToolbar : FrameLayout, MotionLayout.TransitionListener {

    private val paintRect = Paint()
    private val rect = RectF()
    private val path = Path()
    private lateinit var views: List<SelectionView>
    private val completeDisposable = CompositeDisposable()
    private var bottomOffset: Float = 0f

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

        rect.set(0f, 0f, width.toFloat(), bottomPost)
        canvas?.drawRect(rect, paintRect)

        path.reset()
        path.moveTo(0f, bottomPost)
        path.quadTo(width / 2.toFloat(), bottomUsers, width.toFloat(), bottomPost)
        canvas?.drawPath(path, paintRect)
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
        initView()
        initBtn()
    }

    private fun initBtn() {
        views = listOf(
            SelectionView(R.id.ivPost, ivPost, true),
            SelectionView(R.id.ivUsers, ivUsers, true),
            SelectionView(R.id.ivTags, ivTags, true),
            SelectionView(R.id.ivPlace, ivPlace, true)
        )

        val listBtn = ArrayList<Observable<Int>>()

        listBtn.add(ivUsers.clicks().map { R.id.ivUsers })
        listBtn.add(ivPost.clicks().map { R.id.ivPost })
        listBtn.add(ivPlace.clicks().map { R.id.ivPlace })
        listBtn.add(ivTags.clicks().map { R.id.ivTags })

        Observable.merge(listBtn)
            .throttleFirst(400, TimeUnit.MILLISECONDS)
            .subscribe(
                { select(it, true) },
                { it.printStackTrace() })
            .also { completeDisposable.add(it) }

        ivUsers.setOnClickListener { Log.d("SearchToolbar", "${R.id.ivUsers}") }
        ivPlace.setOnClickListener { Log.d("SearchToolbar", "${R.id.ivPlace}") }
        ivPost.setOnClickListener { Log.d("SearchToolbar", "${R.id.ivPost}") }
        ivTags.setOnClickListener { Log.d("SearchToolbar", "${R.id.ivTags}") }
    }

    private fun initView() {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                ivUsers.translationY = bottomOffset
                ivPost.translationY = bottomOffset
                ivPlace.translationY = bottomOffset
                ivTags.translationY = bottomOffset

                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
    }

    private fun select(id: Int, animate: Boolean) {
        val prevSelectedView = views.firstOrNull { it.isSelected }
        val curSelectedView = views.firstOrNull { it.id == id } ?: return
        if (prevSelectedView?.view?.id == curSelectedView.view.id) return

        if (animate) {
            AdditiveAnimator.animate(curSelectedView.view)
                .apply {
                    translationY(bottomOffset)
                    property(
                        ContextCompat.getColor(context, R.color.pink).toFloat(),
                        ColorEvaluator(),
                        getPropertyPink()
                    )
                    prevSelectedView?.let {
                        target(it.view)
                        translationY(0f)
                        property(
                            ContextCompat.getColor(context, android.R.color.white).toFloat(),
                            ColorEvaluator(),
                            getPropertyWhite()
                        )
                    }

                    addEndAction(object : AnimationEndListener() {
                        override fun onAnimationEnd(wasCancelled: Boolean) {
                            prevSelectedView?.isSelected = false
                            curSelectedView.isSelected = true
                        }

                    })
                }
        } else {

        }
    }

    private fun getPropertyPink() = object : FloatProperty<View>("colorPink") {

        override fun set(iv: View?, value: Float?) {
            (iv as? AppCompatImageView)?.drawable?.setTint(ContextCompat.getColor(context, R.color.pink))
        }

        override fun get(iv: View?): Float {
            return ContextCompat.getColor(context, R.color.pink).toFloat()
        }

    }

    private fun getPropertyWhite() = object : FloatProperty<View>("colorWhite") {

        override fun set(iv: View?, value: Float?) {
            (iv as? AppCompatImageView)?.drawable?.setTint(Color.WHITE)
        }

        override fun get(iv: View?): Float {
            return Color.WHITE.toFloat()
        }

    }

    data class SelectionView(
        val id: Int,
        val view: ImageView,
        var isSelected: Boolean
    )

}
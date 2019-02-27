package com.example.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class TestView : View {

    private val path = Path()
    private val paint = Paint()

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

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


    }
}
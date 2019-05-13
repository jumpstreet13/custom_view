package com.example.customview

import android.widget.ImageView

data class SelectView(
    val view: ImageView,
    var defaultX: Float,
    var defaultY: Float,
    var select: Boolean
)
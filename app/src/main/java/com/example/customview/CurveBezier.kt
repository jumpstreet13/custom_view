package com.example.customview

class CurveBezier(
    var Px0: Float=0f,
    var Py0: Float=0f,
    var Px1: Float=0f,
    var Py1: Float=0f,
    var Px2: Float=0f,
    var Py2: Float=0f
) {
    fun getX(t: Float) = (1 - t) * (1 - t) * Px0 + 2 * t * (1 - t) * Px1 + t * t * Px2
    fun getY(t: Float) = (1 - t) * (1 - t) * Py0 + 2 * t * (1 - t) * Py1 + t * t * Py2
}
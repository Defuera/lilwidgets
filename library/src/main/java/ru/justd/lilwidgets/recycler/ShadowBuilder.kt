package ru.justd.lilwidgets.recycler

import android.graphics.Canvas
import android.graphics.Point
import android.view.View

/**
 * Created by shc on 21/03/2017.
 */
class ShadowBuilder(val shadowView: View) : View.DragShadowBuilder(shadowView) {

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        super.onProvideShadowMetrics(size, touch)

        touch.set(0, touch.y)
    }

    override fun onDrawShadow(canvas: Canvas) {
        shadowView.draw(canvas)
    }

}
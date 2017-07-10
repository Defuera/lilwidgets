package ru.justd.lilwidgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.SeekBar

/**
 * Created by shc on 06/07/2017.
 */
class LilSeekBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.seekBarStyle
) : SeekBar(context, attrs, defStyleAttr) {

    private var orientation: Orientation = Orientation.HORIZONTAL

    init {
        attrs?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.LilSeekBar)
            orientation = styleAttrToOrientation(attributes.getInt(R.styleable.LilSeekBar_lilOrientation, 0))
            attributes.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isRegularOrientation()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            super.onMeasure(heightMeasureSpec, widthMeasureSpec)
            setMeasuredDimension(
                    measuredHeight,
                    measuredWidth
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (isRegularOrientation()) {
            super.onSizeChanged(w, h, oldw, oldh)
        } else {
            super.onSizeChanged(h, w, oldh, oldw)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!isRegularOrientation()) {
            canvas.rotate(-90f)
            canvas.translate(-height.toFloat(), 0f)
        }

        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean =
    if (isRegularOrientation()) {
        super.onTouchEvent(event)
    } else {
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                isSelected = true
                isPressed = true

                true
            }

            MotionEvent.ACTION_MOVE -> {
                isSelected = true
                isPressed = true

                progress = (max - (max * event.y / height).toInt())
                        .let {
                            when {
                                it < 0 -> 0
                                it > max -> max
                                else -> it
                            }
                        }

                true
            }

            MotionEvent.ACTION_UP -> {
                isSelected = false
                isPressed = false

                true
            }

            MotionEvent.ACTION_CANCEL -> {
                isSelected = false
                isPressed = false

                false
            }

            else -> false

        }
    }

    override fun setProgress(progress: Int) {
        synchronized(this) {
            super.setProgress(progress)

            if (!isRegularOrientation()) {
                onSizeChanged(width, height, 0, 0)
            }
        }
    }

    private fun isRegularOrientation() = orientation == Orientation.HORIZONTAL

    private fun styleAttrToOrientation(orientation: Int): Orientation =
            Orientation.values()
                    .filter { it.value == orientation }
                    .firstOrNull() ?: Orientation.HORIZONTAL

    private enum class Orientation(val value: Int) {
        HORIZONTAL(0),
        VERTICAL(1)
    }

}
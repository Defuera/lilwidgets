package ru.justd.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import java.util.*

/**
 * Created by shc on 16/08/2017.
 */
class SnapActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_list)

        val list = findViewById(R.id.list) as RecyclerView
        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        list.adapter = SnapAdapter()
        SnapSnapHelper(Gravity.START).attachToRecyclerView(list)

        val anotherList = findViewById(R.id.mp_list) as RecyclerView
        anotherList.layoutManager = MagicLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false, anotherList)
        anotherList.adapter = MPSnapAdapter()
        SnapSnapHelper(Gravity.START).attachToRecyclerView(anotherList)

    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, SnapActivity::class.java)
            context.startActivity(intent)
        }

    }

}

open class SnapAdapter : RecyclerView.Adapter<SnapViewHolder>() {

    val count = 50
    val colors = (0..count)
            .map {
                Color.argb(
                        1,
                        Random().nextInt(),
                        Random().nextInt(),
                        Random().nextInt()
                )
            }
            .toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes(), parent, false)
        return SnapViewHolder(view)
    }

    override fun getItemCount(): Int = count

    override fun onBindViewHolder(holder: SnapViewHolder, position: Int) {
        holder.bind("item $position", colors[position])
    }

    open fun layoutRes() = R.layout.item_demo_snap

}

class MPSnapAdapter : SnapAdapter() {

    override fun layoutRes(): Int = R.layout.item_demo_snap_mp

}

class SnapViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(text: String, i: Int) {
        (itemView.findViewById(R.id.text) as TextView).text = text
        (itemView.findViewById(R.id.image) as ImageView).background = ColorDrawable(i)
    }

}

class SlowRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttrs: Int = 0
) : RecyclerView(context, attrs, defStyleAttrs)

class TestLayoutManager constructor(context: Context) : LinearLayoutManager(context) {

    private val factor = .9
    private val viewCache = SparseArray<View>()

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        fill(recycler)
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        val anchor = getAnchorView()
        viewCache.clear()

        (0 until childCount)
                .map { getChildAt(it) }
                .forEach { viewCache.put(getPosition(it), it) }

        (0 until viewCache.size())
                .map { viewCache.valueAt(it) }
                .forEach { detachView(it) }

        fillToStart(recycler, anchor)
        fillToEnd(recycler, anchor)

        (0 until viewCache.size())
                .map { viewCache.valueAt(it) }
                .forEach { recycler.recycleView(it) }
    }

    private fun fillToStart(recycler: RecyclerView.Recycler, anchor: View?) {
        val anchorPos = anchor?.let { getPosition(it) } ?: 0
        val anchorStart = anchor?.let { getDecoratedLeft(it) } ?: 0

        var fillUp = true
        var pos = anchorPos - 1
        var viewEnd = anchorStart
        val viewWidth = (width * factor).toInt()
        val ws = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY)
        val hs = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        while (fillUp && pos >= 0) {
            var view = viewCache.get(pos)
            if (view != null) {
                attachView(view)
                viewCache.remove(pos)
            } else {
                view = recycler.getViewForPosition(pos)
                addView(view, 0)
                measureChildWithDecorationsAndMargin(view, ws, hs)
                layoutDecorated(view, viewEnd - viewWidth, 0, viewEnd, getDecoratedMeasuredHeight(view))
            }

            viewEnd = getDecoratedLeft(view)
            fillUp = viewEnd > 0
            pos--
        }
    }

    private fun fillToEnd(recycler: RecyclerView.Recycler, anchor: View?) {
        val anchorPos = anchor?.let { getPosition(it) } ?: 0
        val anchorStart = anchor?.let { getDecoratedLeft(it) } ?: 0

        var fillDown = true
        var pos = anchorPos
        var viewStart = anchorStart

        val itemCount = itemCount
        val width = width
        val viewWidth = (width * factor).toInt()
        val widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        while (fillDown && pos < itemCount) {
            var view = viewCache.get(pos)
            if (view != null) {
                attachView(view)
                viewCache.remove(pos)
            } else {
                view = recycler.getViewForPosition(pos)
                addView(view)
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec)
                layoutDecorated(view, viewStart, 0, viewStart + viewWidth, getDecoratedMeasuredHeight(view))
            }

            viewStart = getDecoratedRight(view)
            fillDown = viewStart <= width
            pos++
        }

    }

    override fun canScrollHorizontally(): Boolean = true

    override fun canScrollVertically(): Boolean = false

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val delta = scrollHorizontallyInternal(dx)
        offsetChildrenHorizontal(-delta)
        fill(recycler)
        return delta
    }

    private fun measureChildWithDecorationsAndMargin(child: View, widthSpec: Int, heightSpec: Int) {
        val decorRect = Rect()
        calculateItemDecorationsForChild(child, decorRect)
        val layoutParams: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams

        val ws = updateSpecWithExtra(widthSpec, layoutParams.leftMargin + decorRect.left, layoutParams.rightMargin + decorRect.right)
        val hs = updateSpecWithExtra(heightSpec, layoutParams.topMargin + decorRect.top, layoutParams.bottomMargin + decorRect.bottom)

        child.measure(ws, hs)
    }

    private fun updateSpecWithExtra(spec: Int, startInset: Int, endInset: Int): Int {
        if (startInset == 0 && endInset == 0) {
            return spec
        }

        val mode = View.MeasureSpec.getMode(spec)
        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            return View.MeasureSpec.makeMeasureSpec(
                    View.MeasureSpec.getSize(spec) - startInset - endInset,
                    mode
            )
        }

        return spec
    }

    private fun scrollHorizontallyInternal(dx: Int): Int {
        val childCount = childCount
        val itemCount = itemCount

        if (childCount == 0) {
            return 0
        }

        val startView = getChildAt(0)
        val endView = getChildAt(childCount - 1)

        val viewSpan = getDecoratedRight(endView) - getDecoratedLeft(startView)
        if (viewSpan < height) {
            return 0
        }

        val delta = when {
            dx < 0 -> {
                val firstView = getChildAt(0)
                val firstViewAdapterPos = getPosition(firstView)
                if (firstViewAdapterPos > 0) {
                    dx
                } else {
                    val viewStart = getDecoratedLeft(firstView)
                    Math.max(viewStart, dx)
                }
            }
            dx > 0 -> {
                val lastView = getChildAt(childCount - 1)
                val lastViewAdapterPos = getPosition(lastView)
                if (lastViewAdapterPos < itemCount - 1) {
                    dx
                } else {
                    val viewEnd = getDecoratedRight(lastView)
                    val parentEnd = width
                    Math.min(viewEnd - parentEnd, dx)
                }
            }
            else -> 0
        }

        return delta
    }

    private fun getAnchorView(): View? {
        val childCount = childCount
        val viewsOnScreen = mutableMapOf<Int, View>()
        val mainRect = Rect(0, 0, width, height)

        0.until(childCount)
                .map { getChildAt(it) }
                .forEach {
                    val top = getDecoratedTop(it)
                    val bottom = getDecoratedBottom(it)
                    val left = getDecoratedLeft(it)
                    val right = getDecoratedRight(it)
                    val viewRect = Rect(left, top, right, bottom)
                    if (viewRect.intersect(mainRect)) {
                        val square = viewRect.width() * viewRect.height()
                        viewsOnScreen.put(square, it)
                    }
                }

        if (viewsOnScreen.isEmpty()) {
            return null
        }

        var maxSquare = -1
        viewsOnScreen
                .keys
                .forEach { maxSquare = Math.max(maxSquare, it) }

        return viewsOnScreen[maxSquare]
    }

}

class MagicLinearLayoutManager @JvmOverloads constructor(
        context: Context,
        orientation: Int = LinearLayoutManager.VERTICAL,
        reverse: Boolean = false,
        val recyclerView: RecyclerView
) : LinearLayoutManager(context, orientation, reverse) {

    override fun measureChildWithMargins(child: View?, widthUsed: Int, heightUsed: Int) {
        if (child == null) return

        val decorRect = Rect()
        calculateItemDecorationsForChild(child, decorRect)
        val layoutParams: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams

        val viewWidth = (width * 0.9).toInt()
        val widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        val ws = updateSpecWithExtra(widthSpec, layoutParams.leftMargin + decorRect.left, layoutParams.rightMargin + decorRect.right)
        val hs = updateSpecWithExtra(heightSpec, layoutParams.topMargin + decorRect.top, layoutParams.bottomMargin + decorRect.bottom)

        child.measure(ws, hs)
    }

    private fun updateSpecWithExtra(spec: Int, startInset: Int, endInset: Int): Int {
        if (startInset == 0 && endInset == 0) {
            return spec
        }

        val mode = View.MeasureSpec.getMode(spec)
        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            return View.MeasureSpec.makeMeasureSpec(
                    View.MeasureSpec.getSize(spec) - startInset - endInset,
                    mode
            )
        }

        return spec
    }

}

class SnapSnapHelper(gravity: Int) : GravitySnapHelper(gravity) {

    lateinit var layoutManager: LinearLayoutManager
    lateinit var list: RecyclerView
    var scrollXDistance: Int = -1

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)

        layoutManager = recyclerView?.layoutManager?.let {
            it as? LinearLayoutManager ?: throw IllegalArgumentException("Only linear layout manager is supported")
        } ?: throw NullPointerException("cannot be attached to null recycler view")

        list = recyclerView
    }

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
        Log.d("snap", "onFling: velocityX = $velocityX, velocityY = $velocityY")

        return super.onFling((velocityX * 0.3).toInt(), velocityY)
    }

    override fun calculateScrollDistance(velocityX: Int, velocityY: Int): IntArray {
        val scrollDistance = super.calculateScrollDistance(velocityX, velocityY)

        if (scrollXDistance < 0) {
            val orientationHelper = OrientationHelper.createHorizontalHelper(layoutManager)
            scrollXDistance = orientationHelper.totalSpace
        }

        scrollDistance[0] = scrollXDistance * Math.signum(velocityX.toDouble()).toInt()

        Log.d("snap", "calculated scroll distance: ${scrollDistance[0]}, ${scrollDistance[1]}")

        return scrollDistance
    }

    override fun createSnapScroller(layoutManager: RecyclerView.LayoutManager): LinearSmoothScroller =
            object : LinearSmoothScroller(list.context) {

                override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {

                    val snapDistances = calculateDistanceToFinalSnap(list.layoutManager, targetView)
                            ?: throw NullPointerException("snap distances array is null")

                    val dx = snapDistances[0]
                    val dy = snapDistances[1]
                    val time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)))
                    if (time > 0) {
                        action.update(dx, dy, time, mDecelerateInterpolator)
                    }

                }

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return 200f / displayMetrics.densityDpi
                }

                override fun calculateTimeForDeceleration(dx: Int): Int =
                        Math.ceil(calculateTimeForScrolling(dx) / 1.18546).toInt()

            }

}
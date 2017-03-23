package ru.justd.lilwidgets.recycler

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import android.view.MotionEvent
import ru.justd.lilwidgets.recycler.LilRecyclerView.DragTrigger.HANDLE
import ru.justd.lilwidgets.recycler.LilRecyclerView.DragTrigger.LONG_PRESS

/**
 * Created by shc on 21/03/2017.
 *
 * This extension of [RecyclerView] allows you to drag and drop it's items.
 *
 * It supports two modes:
 * + drag on long press [LONG_PRESS]
 * + drag on handle touch [HANDLE]
 * Switch them using [dragTrigger].
 *
 * In [LONG_PRESS] mode drag starts on long press on list's item.
 *
 * [HANDLE] mode allows you to set handle view's id (using [setHandleViewId] method)
 * tap on which initiates drag's start.
 *
 * *NOTE:* don't forget to set [moveCallback] and update your adapter's model there
 * to prevent data inconsistency.
 */
class LilRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), LilMoveCallback {

    var moveCallback: LilMoveCallback? = null
    var handleViewId: Int? = null
    var dragTrigger: DragTrigger = LONG_PRESS
        set(value) {
            field = value
            itemTouchCallback.longPressEnabled = value == LONG_PRESS
        }

    private val itemTouchCallback = LilItemTouchHelperCallback(this)
    private val itemTouchHelper = ItemTouchHelper(itemTouchCallback)

    init {
        itemTouchHelper.attachToRecyclerView(this)
        addOnItemTouchListener(LilItemTouchListener())
    }

    override fun onItemMoved(current: ViewHolder, target: ViewHolder) {
        adapter?.notifyItemMoved(current.adapterPosition, target.adapterPosition)
        moveCallback?.onItemMoved(current, target)
    }

    /**
     * The purpose of this listener is to start drag if [HANDLE] mode is active and user
     * touches handle view
     */
    inner class LilItemTouchListener :  OnItemTouchListener {

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (dragTrigger == HANDLE
                    && handleViewId != null
                    && e.action == MotionEvent.ACTION_DOWN) {

                val view = findChildViewUnder(e.x, e.y)
                val handle = view?.findViewById(handleViewId!!)

                val handleRect = Rect()
                handle?.getHitRect(handleRect)

                val xInParent = e.x - view.left
                val yInParent = e.y - view.top

                if (handleRect.contains(xInParent.toInt(), yInParent.toInt())) {
                    val viewHolder = getChildViewHolder(view)
                    itemTouchHelper.startDrag(viewHolder)
                    return true
                }
            }

            return false
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) { }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) { }

    }

    enum class DragTrigger {
        LONG_PRESS, HANDLE
    }

}
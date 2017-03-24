package ru.justd.lilwidgets.recycler

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import android.view.MotionEvent
import ru.justd.lilwidgets.recycler.LilRecyclerView.DragMode.HANDLE
import ru.justd.lilwidgets.recycler.LilRecyclerView.DragMode.LONG_PRESS

/**
 * Created by shc on 21/03/2017.
 *
 * This extension of [RecyclerView] allows you to drag and drop it's items.
 *
 * It supports two modes:
 * + drag on long press [LONG_PRESS]
 * + drag on handle touch [HANDLE]
 * Switch them using [dragMode].
 *
 * In [LONG_PRESS] mode drag starts on long press on list's item.
 *
 * [HANDLE] mode allows you to set handle view's id (using [setDragModeHandle] method)
 * tap on which initiates drag's start.
 *
 * *NOTE:* don't forget to set [moveListener] and update your adapter's model there
 * to prevent data inconsistency.
 */
class LilRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var moveListener: MoveListener? = null

    private val itemTouchCallback = LilItemTouchHelperCallback(
            object : MoveListener {
                override fun onItemMoved(current: ViewHolder, target: ViewHolder) {
                    adapter?.notifyItemMoved(current.adapterPosition, target.adapterPosition)
                    moveListener?.onItemMoved(current, target)
                }
            }
    )
    private val itemTouchHelper = ItemTouchHelper(itemTouchCallback)

    private var handleViewId: Int? = null
    private var dragMode: DragMode = LONG_PRESS
        set(value) {
            field = value
            itemTouchCallback.longPressEnabled = value == LONG_PRESS
        }

    init {
        layoutManager = LinearLayoutManager(context)
        itemTouchHelper.attachToRecyclerView(this)
        addOnItemTouchListener(LilItemTouchListener())
    }

    fun setDragModeLongPress() {
        dragMode = LONG_PRESS
    }

    fun setDragModeHandle(handleViewId: Int) {
        dragMode = HANDLE
        this.handleViewId = handleViewId
    }

    override fun setLayoutManager(lm: LayoutManager?) {
        if (layoutManager == null) {
            super.setLayoutManager(lm)
        } else {
            throw IllegalArgumentException("LilRecyclerView works only with vertical " +
                    "LinearLayoutManager at the moment, and it's preset. Sorry.")
        }
    }

    /**
     * The purpose of this listener is to start drag if [HANDLE] mode is active and user
     * touches handle view
     */
    private inner class LilItemTouchListener :  OnItemTouchListener {

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (dragMode == HANDLE
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

    /**
     * Created by shc on 22/03/2017.
     *
     * Callback that notifies client about items' movements in list
     */
    interface MoveListener {

        /**
         * Notifies client that [current] and [target] items were swapped
         *
         * @param current currently dragging item
         * @param target item that [current] crosses in this moment
         */
        fun onItemMoved(current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder)

    }

    enum class DragMode {
        LONG_PRESS, HANDLE
    }

}
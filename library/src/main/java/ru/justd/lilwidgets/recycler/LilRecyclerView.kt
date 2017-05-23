package ru.justd.lilwidgets.recycler

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.*
import android.util.AttributeSet
import android.view.MotionEvent
import ru.justd.lilwidgets.recycler.LilRecyclerView.DragMode.*

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
 *
 * You can face situation when not any item can be moved or replaced. In these cases you should
 * set following predicates:
 * + [dragPredicate] to determine which items can be moved
 * + [replacePredicate] to determine if target item can be replaced with current one.
 */
open class LilRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var moveListener: MoveListener? = null

    var allowVerticalDrag: Boolean? = null
        set(value) {
            field = value
            setupDragFlags()
        }

    var allowHorizontalDrag: Boolean? = null
        set(value) {
            field = value
            setupDragFlags()
        }

    /**
     * Predicate that determines if [target] item can be replaced with [current] one
     * @param current item which is currently moving
     * @param target item which is crossed by [current] now and which potentially can be replaced
     */
    var replacePredicate: ((current: ViewHolder?, target: ViewHolder?) -> Boolean)? = null
        set(value) {
            field = value
            itemTouchCallback.replacePredicate = value
        }

    /**
     * Predicate that determines if given item can be moved
     */
    var dragPredicate: ((ViewHolder) -> Boolean)? = null
        set(value) {
            field = value
            itemTouchCallback.dragPredicate = value
        }

    var activeItemElevation: Float? = null
        set(value) {
            field = value
            itemTouchCallback.activeItemElevation = value
        }

    private val itemTouchCallback = LilItemTouchHelperCallback(
            object : MoveListener {
                override fun onItemMoved(current: ViewHolder, target: ViewHolder) {
                    adapter?.notifyItemMoved(current.adapterPosition, target.adapterPosition)
                    moveListener?.onItemMoved(current, target)
                }

                override fun onItemDropped(current: ViewHolder) {
                    moveListener?.onItemDropped(current)
                }

                override fun onItemPicked(current: ViewHolder) {
                    moveListener?.onItemPicked(current)
                }
            }
    )

    private val itemTouchHelper = ItemTouchHelper(itemTouchCallback)

    private var handleViewId: Int? = null
    private var dragMode: DragMode = NONE
        set(value) {
            field = value
            itemTouchCallback.longPressEnabled = value == LONG_PRESS
        }

    init {
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

    fun setDragModeNone() {
        dragMode = NONE
        this.handleViewId = null
    }

    override fun setLayoutManager(lm: LayoutManager) {
        super.setLayoutManager(lm)

        if (allowVerticalDrag == null) {
            allowVerticalDrag = lm.canScrollVertically()
        }

        if (allowHorizontalDrag == null) {
            val isMultiColumn = when (lm) {
                is GridLayoutManager -> lm.spanCount > 1
                is StaggeredGridLayoutManager -> lm.spanCount > 1
                else -> false
            }

            allowHorizontalDrag = lm.canScrollHorizontally() or isMultiColumn
        }

    }

    private fun setupDragFlags() {
        val verticalDragFlags = if (allowVerticalDrag ?: false) (UP or DOWN) else 0
        val horizontalDragFlags = if (allowHorizontalDrag ?: false) (LEFT or RIGHT) else 0

        itemTouchCallback.dragFlags = verticalDragFlags or horizontalDragFlags
    }

    /**
     * The purpose of this listener is to start drag if [HANDLE] mode is active and user
     * touches handle view
     */
    private inner class LilItemTouchListener : OnItemTouchListener {

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (dragMode == HANDLE
                    && handleViewId != null
                    && e.action == MotionEvent.ACTION_DOWN) {

                val view = findChildViewUnder(e.x, e.y) ?: return false

                val handle = view.findViewById(handleViewId!!)
                val handleRect = Rect()
                handle?.getHitRect(handleRect)

                val xInParent = e.x - view.left
                val yInParent = e.y - view.top

                if (handleRect.contains(xInParent.toInt(), yInParent.toInt())) {
                    val viewHolder = getChildViewHolder(view)
                    if (dragPredicate?.invoke(viewHolder) ?: true) {
                        itemTouchHelper.startDrag(viewHolder)
                        return true
                    }
                }
            }

            return false
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    }

    /**
     * Created by shc on 22/03/2017.
     *
     * Callback that notifies client about items' movements in list
     */
    interface MoveListener {

        /**
         * Notifies client that drag started
         *
         * @param current currently dragging item
         */
        fun onItemPicked(current: RecyclerView.ViewHolder)

        /**
         * Notifies client that [current] and [target] items were swapped
         *
         * @param current currently dragging item
         * @param target item that [current] crosses in this moment
         */
        fun onItemMoved(current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder)

        /**
         * Notifies client that [current] item was dropped (i.e. drag is finished).
         *
         * @param current item which was dragged
         */
        fun onItemDropped(current: RecyclerView.ViewHolder)

    }

    enum class DragMode {
        LONG_PRESS, HANDLE, NONE
    }

}
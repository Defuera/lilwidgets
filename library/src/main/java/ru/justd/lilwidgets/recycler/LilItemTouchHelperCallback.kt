package ru.justd.lilwidgets.recycler

import android.graphics.Canvas
import android.support.v4.view.ViewCompat
import android.support.v7.recyclerview.R
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import ru.justd.lilwidgets.recycler.LilRecyclerView.MoveListener

/**
 * Created by shc on 22/03/2017.
 */
internal class LilItemTouchHelperCallback(
        val listener: MoveListener
) : ItemTouchHelper.Callback() {

    internal var longPressEnabled: Boolean = false
    internal var dragPredicate: ((ViewHolder) -> Boolean)? = null
    internal var replacePredicate: ((current: ViewHolder?, target: ViewHolder?) -> Boolean)? = null
    internal var dragFlags: Int = 0
    internal var activeItemElevation : Float? = null

    private var lastTargetPosition: Int = -1

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        return makeMovementFlags(
                if (dragPredicate?.invoke(viewHolder) ?: true) dragFlags else 0,
                0
        )
    }

    override fun canDropOver(recyclerView: RecyclerView?, current: ViewHolder?, target: ViewHolder?): Boolean =
        replacePredicate?.invoke(current, target) ?: super.canDropOver(recyclerView, current, target)

    override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
        val targetPosition = target.adapterPosition
        if (targetPosition != lastTargetPosition) {
            lastTargetPosition = targetPosition
            listener.onItemMoved(viewHolder, target)
        }

        return true
    }

    override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (viewHolder != null && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            listener.onItemPicked(viewHolder)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
        listener.onItemDropped(viewHolder)

        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (isCurrentlyActive) {
            val view = viewHolder?.itemView ?: return
            var originalElevation: Any? = view.getTag(R.id.item_touch_helper_previous_elevation)
            if (originalElevation == null) {
                originalElevation = ViewCompat.getElevation(view)
                val newElevation = activeItemElevation ?: 1f
                ViewCompat.setElevation(view, newElevation)
                view.setTag(R.id.item_touch_helper_previous_elevation, originalElevation)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: ViewHolder?, direction: Int) {}

    override fun isLongPressDragEnabled(): Boolean = longPressEnabled

}
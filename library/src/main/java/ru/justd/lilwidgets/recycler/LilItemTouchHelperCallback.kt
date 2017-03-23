package ru.justd.lilwidgets.recycler

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.DOWN
import android.support.v7.widget.helper.ItemTouchHelper.UP

/**
 * Created by shc on 22/03/2017.
 */
internal class LilItemTouchHelperCallback(
        val callback: LilMoveCallback
) : ItemTouchHelper.Callback() {

    internal var longPressEnabled: Boolean = true

    private var lastTargetPosition: Int = -1

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int =
            makeMovementFlags(UP or DOWN, 0)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val targetPosition = target.adapterPosition
        if (targetPosition != lastTargetPosition) {
            lastTargetPosition = targetPosition
            callback.onItemMoved(viewHolder, target)
        }

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) { }

    override fun isLongPressDragEnabled(): Boolean = longPressEnabled

}
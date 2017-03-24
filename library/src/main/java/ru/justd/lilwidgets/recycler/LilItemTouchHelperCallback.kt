package ru.justd.lilwidgets.recycler

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.DOWN
import android.support.v7.widget.helper.ItemTouchHelper.UP
import ru.justd.lilwidgets.recycler.LilRecyclerView.MoveListener

/**
 * Created by shc on 22/03/2017.
 */
internal class LilItemTouchHelperCallback(
        val listener: MoveListener
) : ItemTouchHelper.Callback() {

    internal var longPressEnabled: Boolean = true
    internal var dragPredicate: ((ViewHolder) -> Boolean)? = null

    private var lastTargetPosition: Int = -1

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        val dragFlag = if (dragPredicate?.invoke(viewHolder) ?: true) (UP or DOWN) else 0
        return makeMovementFlags(dragFlag, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
        val targetPosition = target.adapterPosition
        if (targetPosition != lastTargetPosition) {
            lastTargetPosition = targetPosition
            listener.onItemMoved(viewHolder, target)
        }

        return true
    }

    override fun onSwiped(viewHolder: ViewHolder?, direction: Int) {}

    override fun isLongPressDragEnabled(): Boolean = longPressEnabled

}
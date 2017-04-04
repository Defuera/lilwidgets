package ru.justd.lilwidgets.recycler

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
    internal var dragFlags: Int = 0

    private var lastTargetPosition: Int = -1

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        return makeMovementFlags(
                if (dragPredicate?.invoke(viewHolder) ?: true) dragFlags else 0,
                0
        )
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
        val targetPosition = target.adapterPosition
        if (targetPosition != lastTargetPosition) {
            lastTargetPosition = targetPosition
            listener.onItemMoved(viewHolder, target)
        }

        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
        listener.onItemDropped(viewHolder)

        super.clearView(recyclerView, viewHolder)
    }

    override fun onSwiped(viewHolder: ViewHolder?, direction: Int) {}

    override fun isLongPressDragEnabled(): Boolean = longPressEnabled

}
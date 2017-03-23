package ru.justd.lilwidgets.recycler

import android.support.v7.widget.RecyclerView

/**
 * Created by shc on 22/03/2017.
 *
 * Callback that notifies client about items' movements in list
 */
interface LilMoveCallback {

    /**
     * Notifies client that [current] and [target] items were swapped
     *
     * @param current currently dragging item
     * @param target item that [current] crosses in this moment
     */
    fun onItemMoved(current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder)

}
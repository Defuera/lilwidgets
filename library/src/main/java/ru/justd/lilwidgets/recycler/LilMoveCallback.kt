package ru.justd.lilwidgets.recycler

import android.support.v7.widget.RecyclerView

/**
 * Created by shc on 22/03/2017.
 */
interface LilMoveCallback {

    fun onItemMoved(current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder)

}
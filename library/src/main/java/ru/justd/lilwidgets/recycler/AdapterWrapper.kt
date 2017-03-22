package ru.justd.lilwidgets.recycler

import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.ViewGroup

/**
 * Created by shc on 21/03/2017.
 */
class AdapterWrapper<VH : RecyclerView.ViewHolder>(
        val wrappedAdapter: RecyclerView.Adapter<VH>
) : RecyclerView.Adapter<VH>(), LilMoveCallback {

    var handleId: Int? = null
    var onStartDragListener: OnStartDragListener? = null

    override fun onItemMoved(current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
        notifyItemMoved(current.adapterPosition, target.adapterPosition)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        wrappedAdapter.onBindViewHolder(holder, position)

        if (handleId != null) {
            holder.itemView
                    .findViewById(handleId!!)
                    .setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            onStartDragListener?.onStartDrag(holder)
                            true
                        } else {
                            false
                        }
                    }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH = wrappedAdapter.onCreateViewHolder(parent, viewType)

    override fun getItemCount(): Int = wrappedAdapter.itemCount

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

}
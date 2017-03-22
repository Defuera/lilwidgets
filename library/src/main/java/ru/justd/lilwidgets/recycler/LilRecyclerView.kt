package ru.justd.lilwidgets.recycler

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import ru.justd.lilwidgets.recycler.LilRecyclerView.DragTrigger.HANDLE
import ru.justd.lilwidgets.recycler.LilRecyclerView.DragTrigger.LONG_TAP

/**
 * Created by shc on 21/03/2017.
 */
class LilRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), LilMoveCallback {

    val itemTouchCallback = LilItemTouchHelperCallback(this)
    val itemTouchHelper = ItemTouchHelper(itemTouchCallback)

    var adapterWrapper: AdapterWrapper<*>? = null

    var dragTrigger: DragTrigger = LONG_TAP
        set(value) {
            itemTouchCallback.longPressEnabled = value == LONG_TAP
            adapterWrapper?.onStartDragListener =
                    if (value == HANDLE) {
                        object : AdapterWrapper.OnStartDragListener {
                            override fun onStartDrag(viewHolder: ViewHolder) {
                                itemTouchHelper.startDrag(viewHolder)
                            }
                        }
                    }
                    else null
        }

    init {
        itemTouchHelper.attachToRecyclerView(this)
    }

    fun setHandleViewId(id: Int) {
        adapterWrapper?.handleId = id
    }

    override fun onItemMoved(current: ViewHolder, target: ViewHolder) {
        adapter?.notifyItemMoved(current.adapterPosition, target.adapterPosition)
    }

    override fun setAdapter(adapter: Adapter<*>) {
        adapterWrapper = AdapterWrapper(adapter)
        super.setAdapter(adapterWrapper)
    }

    enum class DragTrigger {
        LONG_TAP, HANDLE
    }

}
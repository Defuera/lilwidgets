package ru.justd.demo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.justd.lilwidgets.recycler.LilRecyclerView
import java.util.*

/**
 * Created by shc on 21/03/2017.
 */
class TestAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), LilRecyclerView.MoveListener {

    val typeHeader = 0
    val typeRegular = 1

    val items = ArrayList<Int>()

    init {
        items += 0..50
    }

    override fun getItemViewType(position: Int): Int =
            if (position == 0) typeHeader else typeRegular

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RegularViewHolder) {
            holder.bind(items[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                typeHeader -> FixedViewHolder(LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_demo_header, parent, false))
                typeRegular -> RegularViewHolder(LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_demo, parent, false))
                else -> throw IllegalArgumentException("unknown item type")
            }

    override fun onItemMoved(current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
        Collections.swap(items, current.adapterPosition, target.adapterPosition)
    }

    fun itemIsDraggable(holder: RecyclerView.ViewHolder): Boolean = holder.itemViewType == typeRegular

    inner class RegularViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(value: Int) {
            (view.findViewById(R.id.text) as TextView).text = value.toString()
        }

    }

    inner class FixedViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
package ru.justd.demo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by shc on 21/03/2017.
 */
class TestAdapter : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    val items = ArrayList<Int>()

    init {
        items += 0..50
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: TestViewHolder?, position: Int) {
        holder?.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder =
            TestViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.item_demo, parent, false)
            )

    inner class TestViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(value: Int) {
            (view.findViewById(R.id.text) as TextView).text = value.toString()
        }

    }

    fun swapItems(from: Int, to: Int) {
        Collections.swap(items, from, to)
    }

}
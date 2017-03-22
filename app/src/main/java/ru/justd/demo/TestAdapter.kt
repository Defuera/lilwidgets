package ru.justd.demo

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by shc on 21/03/2017.
 */
class TestAdapter : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    val items = ArrayList<Int>()

    init {
        items.add(0)
        items.add(1)
        items.add(2)
        items.add(3)
        items.add(4)
        items.add(5)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: TestViewHolder?, position: Int) {
        holder?.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = TextView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.setPadding(10, 30, 10, 30)
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
        view.setBackgroundColor(Color.LTGRAY)

        return TestViewHolder(
                LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_demo, parent, false)
        )
    }

    inner class TestViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(value: Int) {
            (view.findViewById(R.id.text) as TextView).text = value.toString()
        }

    }

}
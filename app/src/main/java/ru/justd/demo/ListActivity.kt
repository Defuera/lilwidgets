package ru.justd.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import ru.justd.lilwidgets.recycler.LilRecyclerView

/**
 * Created by shc on 21/03/2017.
 */
class ListActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val list = findViewById(R.id.list) as LilRecyclerView
        val testAdapter = TestAdapter()
        val layoutManager = LinearLayoutManager(this)

        list.layoutManager = layoutManager
        list.adapter = testAdapter
        list.moveListener = testAdapter
        list.allowHorizontalDrag = false
        list.dragPredicate = { testAdapter.itemIsDraggable(it) }
        list.activeItemElevation = 20f
        list.replacePredicate = { _, target -> testAdapter.itemIsDraggable(target) }

        list.setDragModeHandle(R.id.handle)
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, ListActivity::class.java))
        }

    }

}
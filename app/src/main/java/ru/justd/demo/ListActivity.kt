package ru.justd.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ru.justd.lilwidgets.recycler.LilMoveCallback
import ru.justd.lilwidgets.recycler.LilRecyclerView
import ru.justd.lilwidgets.recycler.LilRecyclerView.DragTrigger.HANDLE

/**
 * Created by shc on 21/03/2017.
 */
class ListActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val list = findViewById(R.id.list) as LilRecyclerView
        val testAdapter = TestAdapter()

        list.adapter = testAdapter
        list.layoutManager = LinearLayoutManager(this)

        list.dragTrigger = HANDLE
        list.handleViewId = R.id.handle

        list.moveCallback = object : LilMoveCallback {
            override fun onItemMoved(current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
                testAdapter.swapItems(current.adapterPosition, target.adapterPosition)
            }
        }
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, ListActivity::class.java))
        }

    }

}
package ru.justd.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Created by shc on 06/07/2017.
 */
class SeekBarActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seek_bar)
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, SeekBarActivity::class.java)
            context.startActivity(intent)
        }

    }

}
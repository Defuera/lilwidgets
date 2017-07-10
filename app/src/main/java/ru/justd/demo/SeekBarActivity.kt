package ru.justd.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import ru.justd.lilwidgets.LilSeekBar

/**
 * Created by shc on 06/07/2017.
 */
class SeekBarActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seek_bar)

        val seekBar = findViewById(R.id.seek_bar) as LilSeekBar
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("SeekBarActivity", "progress: $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { }

        })
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, SeekBarActivity::class.java)
            context.startActivity(intent)
        }

    }

}
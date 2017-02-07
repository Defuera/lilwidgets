package ru.justd.lilwidgets

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ru.justd.library.ProgressDialogFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.progress_dialog_button).setOnClickListener {
            ProgressDialogFragment.Builder(supportFragmentManager)
                    .setTitle("Please wait")
                    .setCancelable(true)
                    .create()
        }

        findViewById(R.id.delay).setOnClickListener {
            ProgressDialogFragment.Builder(supportFragmentManager)
                    .setTitle("Please wait delayed")
                    .setCancelable(true)
                    .setDelay(1000)
                    .create()
        }
    }
}

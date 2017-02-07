package ru.justd.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import ru.justd.lilwidgets.ProgressDialogFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.progress_dialog_button).setOnClickListener {
            ProgressDialogFragment.Builder(supportFragmentManager)
                    .setTitle("Please wait")
                    .setCancelable(true)
                    .setOnDismissListener({ Toast.makeText(this, "Dialog dismissed", Toast.LENGTH_SHORT).show() })
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

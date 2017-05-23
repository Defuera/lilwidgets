package ru.justd.demo

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import ru.justd.lilwidgets.LilLoaderDialog
import ru.justd.lilwidgets.LilLoaderWidget

class MainActivity : AppCompatActivity() {

    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDisplayProgressDialog()

        initDisplayProgressDialogDelayed()

        initShowLoaderWidget()

        initDisplayProgressDialogWithCustomWidget()

        findViewById(R.id.open_list).setOnClickListener {
            ListActivity.start(this)
        }

    }

    private fun initDisplayProgressDialog() {
        findViewById(R.id.show_cancelable_loader_dialog).setOnClickListener {
            LilLoaderDialog.Builder(supportFragmentManager)
                    .setTitle("Please wait")
                    .setCancelable(true)
                    .setOnDismissListener({ Toast.makeText(this, "Dialog dismissed", Toast.LENGTH_SHORT).show() })
                    .create()
        }
    }

    private fun initDisplayProgressDialogDelayed() {
        findViewById(R.id.show_loader_dialog_delayed).setOnClickListener {
            LilLoaderDialog.Builder(supportFragmentManager)
                    .setTitle("Please wait delayed")
                    .setCancelable(true)
                    .setDelay(1000)
                    .create()
        }
    }

    private fun initShowLoaderWidget() {
        findViewById(R.id.show_loader_widget).setOnClickListener {

            val loader = findViewById(R.id.loader) as LilLoaderWidget
            loader.showLoading()
            handler.postDelayed(
                    { loader.showNetworkError() },
                    1000
            )

            handler.postDelayed(
                    { loader.showNoDataError() },
                    2000
            )

            handler.postDelayed(
                    { loader.hide() },
                    4000
            )

            loader.setOnErrorClicked(
                    {
                        error ->
                        if (error != null) {
                            Toast.makeText(this, error.javaClass.simpleName, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "loader clicked", Toast.LENGTH_SHORT).show()
                        }
                    }
            )

        }
    }

    private fun initDisplayProgressDialogWithCustomWidget() {
        findViewById(R.id.show_loader_dialog_custom_view).setOnClickListener {

            val textView = TextView(this)
            textView.text = "Once upon a time"

            LilLoaderDialog.Builder(supportFragmentManager)
                    .setTitle("Custom widget dialog")
                    .setView(textView)
                    .setCancelable(true)
                    .create()
        }
    }

}

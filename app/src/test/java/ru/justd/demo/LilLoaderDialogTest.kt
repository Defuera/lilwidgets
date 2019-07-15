package ru.justd.demo

import android.os.Build
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.justd.lilwidgets.LilLoaderDialog

/**
 * Created by defuera on 23/05/2017.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(Build.VERSION_CODES.M))
class LilLoaderDialogTest {

    private val activity: MainActivity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .start()
            .resume()
            .get()

    private val fragmentManager: FragmentManager = activity.supportFragmentManager

    @Before
    @Throws(Exception::class)
    fun setUp() {

    }

    @Test
    @Throws(Exception::class)
    fun simpleTest() {

        //setup
        LilLoaderDialog
                .Builder(fragmentManager)
                .setTitle("title")
                .setMessage("message")
                .create()

        val dialog = fragmentManager.fragments[0]


        //actual test
        assertNotNull(dialog)

        val view = dialog.view

        view
                ?.let {
                    assertTextView(view, R.id.title, "title")
                    assertTextView(view, R.id.message, "message")
                }
                ?: throw IllegalArgumentException("view must not be null")


        assertNotNull(view.findViewById(R.id.progress_bar))
    }

    private fun assertTextView(view: View, resId: Int, text: String) {
        val title = view.findViewById(resId) as TextView
        assertNotNull(title)
        assertEquals(text, title.text)
    }

}
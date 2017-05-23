package ru.justd.demo

import android.os.Build
import android.support.annotation.IdRes
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.TextView
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import ru.justd.lilwidgets.LilLoaderDialog
import android.content.pm.ActivityInfo



/**
 * Created by defuera on 23/05/2017.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(Build.VERSION_CODES.M))
class LilLoaderDialogTest {

    val controller: ActivityController<MainActivity> = Robolectric.buildActivity(MainActivity::class.java)
    val activity: MainActivity = controller
            .create()
            .start()
            .resume()
            .get()

    val fragmentManager: FragmentManager = activity.supportFragmentManager

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

        //actual test
        assertDialogShowing()

        val view = getLoaderDialogRootView()
        assertTextView(view, R.id.title, "title")
        assertTextView(view, R.id.message, "message")

        assertNotNull(view.findViewById(R.id.progress_bar))
    }


    @Test
    @Throws(Exception::class)
    fun cancelableTest() {
        assertDialogNotShowing()

        click(R.id.show_cancelable_loader_dialog)

        assertDialogShowing()

        activity.findViewById(android.R.id.content).performClick()

//        assertDialogNotShowing() //todo this is not working
    }

    @Test
    @Throws(Exception::class)
    fun customViewTest() {
        //setup

        val customView = TextView(activity)
        customView.id = 111
        customView.text = "custom text"

        LilLoaderDialog
                .Builder(fragmentManager)
                .setView(customView)
                .create()

        val view = getLoaderDialogRootView()

        //actual test
        assertDialogShowing()

        assertTextView(view, customView.id, "custom text")

        controller.pause()
        controller.resume()

        assertTextView(view, customView.id, "custom text")

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        assertTextView(view, customView.id, "custom text")
    }


    //region helper methods

    private fun assertDialogShowing() {
        if (fragmentManager.fragments != null) {
            val dialog = getLilLoaderDialog()
            assertNotNull(dialog)
        } else {
            throw IllegalArgumentException("dialog is not showing")
        }
    }


    private fun assertDialogNotShowing() {
        if (fragmentManager.fragments != null) {
            val dialog = getLilLoaderDialog()
            assertFalse(dialog.isAdded)
        }
    }

    private fun assertTextView(view: View, resId: Int, text: String) {
        val title = view.findViewById(resId) as TextView
        assertNotNull(title)
        assertEquals(text, title.text)
    }

    private fun click(@IdRes resId: Int) {
        activity
                .findViewById(resId)
                .performClick()
    }

    private fun getLoaderDialogRootView(): View = getLilLoaderDialog().view!!

    private fun getLilLoaderDialog(): LilLoaderDialog = fragmentManager.fragments[0] as LilLoaderDialog

    //endregion

}
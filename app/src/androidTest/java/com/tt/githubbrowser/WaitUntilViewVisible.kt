package com.tt.githubbrowser

import android.app.Activity
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.test.espresso.IdlingResource
import com.tt.githubbrowser.util.isOnScreen

class WaitUntilViewVisible(private val activity: Activity, private val id: Int, private val visible: Boolean = true) : IdlingResource {
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return javaClass.name
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = callback
    }

    override fun isIdleNow(): Boolean {
        val topView = activity.findViewById<ViewGroup>(android.R.id.content)
        val viewToCheck = findOnScreenView(topView, id, activity, visible)

        var matched = false

        if (this.visible == false) {
            if (viewToCheck == null || (viewToCheck.visibility == INVISIBLE || viewToCheck.visibility == GONE)) {
                matched = true
            }
        }
        else {
            if (viewToCheck != null && viewToCheck.visibility == VISIBLE) matched = true
        }

        if (matched) resourceCallback?.onTransitionToIdle()
        return matched
    }

    fun findOnScreenView(parent: ViewGroup, id: Int, activity: Activity, visible: Boolean): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child is ViewGroup) {
                if (child.id == id && isOnScreen(child, activity)) {
                    return child
                }

                val childReturn = findOnScreenView(child, id, activity, visible)
                if (childReturn != null) return childReturn
            }
            else {
                if (child != null && child.id == id && isOnScreen(
                        child,
                        activity
                    )
                ) {
                    return child
                }
            }
        }

        return null
    }
}
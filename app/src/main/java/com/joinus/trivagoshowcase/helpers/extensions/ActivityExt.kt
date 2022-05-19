package com.joinus.trivagoshowcase.helpers.extensions

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.joinus.trivagoshowcase.R

fun Activity.setStatusBarTransparent(view: View, color: Int) {
    this.apply {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, color)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.transparent)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(view) { root, windowInset ->
            val inset = windowInset.getInsets(WindowInsetsCompat.Type.systemBars())
            root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = inset.left
                rightMargin = inset.right
            }
            WindowInsetsCompat.CONSUMED
        }
    }
}

fun Activity.getNavigationBarHeight(): Int {
    this.resources.let {
        val resId = it.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resId > 0) it.getDimensionPixelSize(resId) else 0
    }
}
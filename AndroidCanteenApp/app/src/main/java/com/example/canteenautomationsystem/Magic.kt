package com.example.canteenautomationsystem

import android.view.View

/**
 * Kotlin file with a method to expand or collapse the MagicButton
 */
fun doWith(view : View) {
    var animation : MagicAnimation
    animation = MagicAnimation(view)
    animation.duration = 200
    view.startAnimation(animation)
}
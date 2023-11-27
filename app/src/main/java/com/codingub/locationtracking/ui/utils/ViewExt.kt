package com.codingub.locationtracking.ui.utils

import android.view.View

fun View.setVisible(visible: Boolean?) {
    this.visibility = when (visible) {
        true -> View.VISIBLE
        false -> View.INVISIBLE
        null -> View.GONE
    }
}
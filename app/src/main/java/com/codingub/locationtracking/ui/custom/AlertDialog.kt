package com.codingub.locationtracking.ui.custom

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.codingub.locationtracking.R

open class AlertDialog(context: Context) : AlertDialog(context, R.style.AlertDialog) {

    init {
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
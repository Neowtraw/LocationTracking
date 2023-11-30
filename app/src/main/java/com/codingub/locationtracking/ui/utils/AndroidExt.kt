package com.codingub.locationtracking.ui.utils

import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils

/*
    Int
 */

val Int.dp get() = AndroidUtil.dp(this)

@ColorInt
fun Int.mixWith(
    @ColorInt color: Int,
    @FloatRange(from = 0.0, to = 1.0) ratio: Float
): Int = ColorUtils.blendARGB(this, color, ratio)

/*
    Float
 */

val Float.dp get() = AndroidUtil.dp(this)

val Float.px get() = AndroidUtil.px(this)

val Int.sp get() = this / Resources.getSystem().displayMetrics.density

val Float.sp get() = this / Resources.getSystem().displayMetrics.density

/*
    Boolean
*/

fun Boolean.asInt(): Int = if (this) 1 else 0

fun Boolean.asFloat(): Float = asInt().toFloat()
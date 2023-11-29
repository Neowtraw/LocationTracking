package com.codingub.locationtracking.ui.custom

data class AlertDialogPermissionBuilder(
    val warning: Int,
    val positiveText: Int,
    val positiveOnClick: (() -> Unit),
)
package com.codingub.locationtracking.ui.custom

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.codingub.locationtracking.databinding.AlertDialogPermissionBinding
import com.codingub.locationtracking.ui.utils.Resource

class AlertDialogPermissionView constructor(context: Context, builder: AlertDialogPermissionBuilder) : FrameLayout(context){

    private var binding: AlertDialogPermissionBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = AlertDialogPermissionBinding.inflate(inflater)

        //create logic
        binding.title.text = Resource.string(builder.warning)
        binding.positive.text = Resource.string(builder.positiveText)
        binding.positive.setOnClickListener {
            builder.positiveOnClick()
        }

        addView(binding.root)
    }


}

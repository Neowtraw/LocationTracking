package com.codingub.locationtracking.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {

    protected open fun observeChanges() {}

    fun setBackPressedCallback(){
        val callback: OnBackPressedCallback =
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    fun onBackPressed() {
        findNavController().popBackStack()
    }

    fun hideKeyboard(){
        requireActivity().getSystemService<InputMethodManager>()?.let { imm ->
            var view = requireActivity().currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showMessage(msg: String) {
        if(isAdded) {
            val view = requireActivity().findViewById<View>(android.R.id.content)
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
        }
    }

    fun showMessage(@StringRes stringRes: Int) = showMessage(getString(stringRes))
}
package com.codingub.locationtracking.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingub.locationtracking.databinding.FragmentLoginBinding
import com.codingub.locationtracking.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inf: LayoutInflater,
        con: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inf, con, false)

        return binding.root
    }
}
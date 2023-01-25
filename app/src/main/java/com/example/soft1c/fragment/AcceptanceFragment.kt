package com.example.soft1c.fragment

import android.os.Bundle
import android.view.View
import com.example.soft1c.R
import com.example.soft1c.databinding.FragmentAcceptanceBinding

class AcceptanceFragment :
    BaseFragment<FragmentAcceptanceBinding>(FragmentAcceptanceBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        with(binding) {
            includeToolbar.toolbar.title = resources.getString(R.string.text_title_acceptance)
            includeToolbar.toolbar.setNavigationOnClickListener {
                closeActivity()
            }

            btnClose.setOnClickListener {
                closeActivity()
            }
            btnCloseCopy.setOnClickListener {
                closeActivity()
            }

            etxtAutoNumber.requestFocus()
        }
    }

    private fun closeActivity() {
        activity?.onBackPressed()
    }
}
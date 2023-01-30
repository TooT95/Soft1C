package com.example.soft1c.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.soft1c.R
import com.example.soft1c.Utils
import com.example.soft1c.databinding.FragmentAcceptanceWeightBinding
import com.example.soft1c.model.Acceptance
import com.example.soft1c.viewmodel.AcceptanceViewModel

class AcceptanceWeightFragment :
    BaseFragment<FragmentAcceptanceWeightBinding>(FragmentAcceptanceWeightBinding::inflate) {

    private lateinit var acceptance: Acceptance
    private val viewModel: AcceptanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val acceptanceNumber = arguments?.getString(KEY_WEIGHT_ACCEPTANCE_NUMBER, "") ?: ""
        acceptance = if (acceptanceNumber.isNotEmpty()) {
            viewModel.getAcceptance(acceptanceNumber)
            Acceptance(number = acceptanceNumber)
        } else {
            Acceptance(number = "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        observeViewModels()
    }


    private fun observeViewModels() {
        viewModel.acceptanceLiveData.observe(viewLifecycleOwner, ::showDetails)
        viewModel.toastLiveData.observe(viewLifecycleOwner, ::toast)
        viewModel.createUpdateLiveData.observe(viewLifecycleOwner, ::createUpdateAcceptance)
    }

    private fun createUpdateAcceptance(pair: Pair<Acceptance, Boolean>) {
        if (!pair.second) return
        Utils.refreshList = true
        activity?.onBackPressed()
    }

    private fun initUI() {
        showPbLoading(true)
        setInitFocuses()
        showAcceptance()
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

            btnSave.setOnClickListener {
                createUpdateAcceptance()
            }
            btnSaveCopy.setOnClickListener {
                createUpdateAcceptance()
            }
        }
    }

    private fun createUpdateAcceptance() {
        with(binding) {
            val weight = etxtWeight.text.toString()
            if (weight.isNotEmpty())
                acceptance.allWeight = weight.toDouble()
        }
        viewModel.createUpdateAcceptance(acceptance)
    }

    private fun showPbLoading(show: Boolean) {
        with(binding) {
            pbLoading.isVisible = show
            scrollMain.isVisible = !show
        }

    }

    private fun closeActivity() {
        activity?.onBackPressed()
    }

    private fun showDetails(acc: Acceptance) {
        acceptance = acc
        if (this.acceptance.ref.isEmpty()) {
            binding.pbLoading.isVisible = false
            return
        }
        showAcceptance()
        setInitFocuses()
        showPbLoading(false)
    }

    private fun showAcceptance() {
        with(binding) {
            setCheckEmptyText(txtZone, acceptance.zone)
            etxtCodeClient.setText(acceptance.client)
            etxtSeatsNumber.setText(acceptance.countSeat.toString())
            etxtDocumentNumber.setText(acceptance.number)
            setCheckEmptyText(txtPackage, acceptance._package)
            chbExclamation.isChecked = acceptance.glass
            chbCurrency.isChecked = acceptance.expensive
            chbArrow.isChecked = acceptance.notTurnOver
            chbBrand.isChecked = acceptance.brand
            chbZ.isChecked = acceptance.z
            etxtWeight.setText(acceptance.allWeight.toString())
        }
    }


    private fun setInitFocuses() {
        with(binding) {
            with(etxtWeight) {
                requestFocus()
                val length = text?.length ?: 0
                if (length > 0) setSelection(length)
            }
        }
    }

    private fun setCheckEmptyText(textV: TextView, text: String) {
        if (text.isNotEmpty()) {
            textV.text = text
        }
    }

    companion object {
        const val KEY_WEIGHT_ACCEPTANCE_NUMBER = "acceptance_number"
    }
}

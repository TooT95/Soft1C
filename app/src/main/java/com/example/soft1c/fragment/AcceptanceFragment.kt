package com.example.soft1c.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.soft1c.R
import com.example.soft1c.databinding.FragmentAcceptanceBinding
import com.example.soft1c.model.Acceptance
import com.example.soft1c.viewmodel.AcceptanceViewModel

class AcceptanceFragment :
    BaseFragment<FragmentAcceptanceBinding>(FragmentAcceptanceBinding::inflate) {

    private var acceptanceNumber = ""
    private val viewModel: AcceptanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        acceptanceNumber = arguments?.getString(KEY_ACCEPTANCE_NUMBER, "") ?: ""
        if (acceptanceNumber.isNotEmpty()) {
            viewModel.getAcceptance(acceptanceNumber)
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
    }

    private fun initUI() {
        if (acceptanceNumber.isNotEmpty())
            showPbLoading(true)

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
        if (acc.ref.isEmpty()) {
            binding.pbLoading.isVisible = false
            return
        }

        with(binding) {
            etxtAutoNumber.setText(acc.autoNumber)
            setCheckEmptyText(txtZone, acc.zone)
            etxtCardNumber.setText(acc.idCard)
            etxtCodeClient.setText(acc.client)
            etxtSeatsNumber.setText(acc.countSeat.toString())
            etxtDocumentNumber.setText(acc.number)
            setCheckEmptyText(txtStoreAddress, acc.storeAddressName)
            etxtStoreNumber.setText(acc.storeName)
            etxtRepresentative.setText(acc.representativeName)
            etxtStorePhone.setText(acc.phoneNumber)
            setCheckEmptyText(txtProductType, acc.productTypeName)
            setCheckEmptyText(txtPackage, acc._package)
            etxtSeatsNumberCopy.setText(acc.countSeat.toString())
            etxtPackageCount.setText(acc.countPackage.toString())
            etxtCountInPackage.setText(acc.countInPackage.toString())
            etxtDocumentNumberCopy.setText(acc.number)
        }
        showPbLoading(false)
    }

    private fun setCheckEmptyText(textV: TextView, text: String) {
        if (text.isNotEmpty()) {
            textV.text = text
        }
    }

    companion object {
        const val KEY_ACCEPTANCE_NUMBER = "acceptance_number"
    }
}

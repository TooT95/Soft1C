package com.example.soft1c.fragment

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.soft1c.R
import com.example.soft1c.databinding.FragmentAcceptanceBinding
import com.example.soft1c.model.Acceptance
import com.example.soft1c.model.Client
import com.example.soft1c.viewmodel.AcceptanceViewModel
import com.google.android.material.textfield.TextInputEditText

class AcceptanceFragment :
    BaseFragment<FragmentAcceptanceBinding>(FragmentAcceptanceBinding::inflate) {

    private var acceptanceNumber = ""
    private var clientFound = false
    private val viewModel: AcceptanceViewModel by viewModels()
//    private val codeClientDelay = 300L

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
        viewModel.clientLiveData.observe(viewLifecycleOwner, ::clientObserve)
    }

    private fun clientObserve(client: Client) {
        enableFieldsAfterFieldClient(true)
        closeDialogLoading()
    }

    private fun initUI() {
        if (acceptanceNumber.isNotEmpty())
            showPbLoading(true)
        else {
            enableFieldsAfterFieldClient(false)
            setInitFocuses()
        }
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
            etxtCodeClient.setOnKeyListener(::customSetOnKeyListener)
        }
    }

    private fun customSetOnKeyListener(view: View, key: Int, keyEvent: KeyEvent): Boolean {
        if (key == 66 && keyEvent.action == KeyEvent.ACTION_UP) {
            with(binding) {
                val etxtView = view as TextInputEditText
                if (etxtView.text!!.isEmpty()) {
                    etxtView.error = resources.getString(R.string.text_field_is_empyt)
                    return true
                }
                when (etxtView) {
                    etxtCodeClient -> {
                        showDialogLoading()
                        viewModel.getClient(etxtView.text.toString())
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }
        }
        return false
    }

    private fun enableFieldsAfterFieldClient(enable: Boolean) {
        clientFound = enable
        with(binding) {
            etxtAutoNumber.isEnabled = enable
            etxtCardNumber.isEnabled = enable
            etxtStoreNumber.isEnabled = enable
            etxtRepresentative.isEnabled = enable
            etxtStorePhone.isEnabled = enable
            etxtSeatsNumber.isEnabled = enable
            etxtPackageCount.isEnabled = enable
            etxtCountInPackage.isEnabled = enable
            etxtSeatsNumberCopy.isEnabled = enable
            chbBrand.isEnabled = enable
            chbArrow.isEnabled = enable
            chbCurrency.isEnabled = enable
            chbExclamation.isEnabled = enable
            chbZ.isEnabled = enable
            chPassport.isEnabled = enable
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
        setInitFocuses()
        showPbLoading(false)
    }

    private fun setInitFocuses() {
        with(binding) {
            with(etxtCodeClient) {
                requestFocus()
                val length = text?.length ?: 0
                if (length > 0) setSelection(length)
            }
//            etxtCodeClient.setOnFocusChangeListener(::etxtFocusChangeListener)
        }
    }

//    private fun etxtFocusChangeListener(view: View, hasFocus: Boolean) {
//        if (hasFocus) {
//            view.postDelayed({
//                activity?.let {
//                    val imm =
//                        it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.showSoftInput(view.findFocus(), 0)
//                }
//            }, codeClientEmit)
//
//        }
//    }

    private fun setCheckEmptyText(textV: TextView, text: String) {
        if (text.isNotEmpty()) {
            textV.text = text
        }
    }

    companion object {
        const val KEY_ACCEPTANCE_NUMBER = "acceptance_number"
    }
}

package com.example.soft1c.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.soft1c.R
import com.example.soft1c.Utils
import com.example.soft1c.databinding.FragmentAcceptanceBinding
import com.example.soft1c.model.Acceptance
import com.example.soft1c.model.AnyModel
import com.example.soft1c.model.Client
import com.example.soft1c.viewmodel.AcceptanceViewModel
import com.google.android.material.textfield.TextInputEditText

class AcceptanceFragment :
    BaseFragment<FragmentAcceptanceBinding>(FragmentAcceptanceBinding::inflate) {

    private var clientFound = false
    private lateinit var acceptance: Acceptance
    private val viewModel: AcceptanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val acceptanceNumber = arguments?.getString(KEY_ACCEPTANCE_NUMBER, "") ?: ""
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
        viewModel.clientLiveData.observe(viewLifecycleOwner, ::clientObserve)
        viewModel.createUpdateLiveData.observe(viewLifecycleOwner, ::createUpdateAcceptance)
    }

    private fun createUpdateAcceptance(pair: Pair<Acceptance, Boolean>) {
        if (!pair.second) return
        Utils.refreshList = true
        activity?.onBackPressed()
    }

    private fun clientObserve(pair: Pair<Client, Boolean>) {
        clientFound = pair.second
        enableFieldsAfterFieldClient(clientFound)
        closeDialogLoading()
        acceptance.client = pair.first.code
        setInitFocuses()
        showAcceptance()
        if (clientFound) {
            binding.etxtZone.requestFocus()
        }
    }

    private fun initUI() {
        if (acceptance.number.isNotEmpty() && !clientFound)
            showPbLoading(true)
        else if (acceptance.number.isEmpty()) {
            enableFieldsAfterFieldClient(clientFound)
            setInitFocuses()
        }
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
            etxtCodeClient.setOnKeyListener(::customSetOnKeyListener)
            etxtCardNumber.setOnKeyListener(::customSetOnKeyListener)
            etxtStorePhone.setOnKeyListener(::customSetOnKeyListener)
            etxtSeatsNumberCopy.setOnKeyListener(::customSetOnKeyListener)
            etxtPackageCount.setOnKeyListener(::customSetOnKeyListener)
            etxtCountInPackage.setOnKeyListener(::customSetOnKeyListener)

            etxtZone.setAdapter(ArrayAdapter(requireContext(),
                android.R.layout.simple_list_item_1, Utils.zones.map {
                    (it as AnyModel.Zone).name
                }))
            etxtZone.setOnKeyListener(::autoCompleteOnKeyListener)

            etxtStoreAddress.setAdapter(ArrayAdapter(requireContext(),
                android.R.layout.simple_list_item_1, Utils.addressess.map {
                    (it as AnyModel.AddressModel).name
                }))
            etxtStoreAddress.setOnKeyListener(::autoCompleteOnKeyListener)

            etxtProductType.setAdapter(ArrayAdapter(requireContext(),
                android.R.layout.simple_list_item_1, Utils.productTypes.map {
                    (it as AnyModel.ProductType).name
                }))
            etxtProductType.setOnKeyListener(::autoCompleteOnKeyListener)

            etxtPackage.setAdapter(ArrayAdapter(requireContext(),
                android.R.layout.simple_list_item_1, Utils.packages.map {
                    (it as AnyModel.PackageModel).name
                }))
            etxtPackage.setOnKeyListener(::autoCompleteOnKeyListener)

            etxtCodeClient.setOnFocusChangeListener(::etxtFocusChangeListener)
            etxtStoreNumber.setOnFocusChangeListener(::etxtFocusChangeListener)
            etxtCardNumber.setOnFocusChangeListener(::etxtFocusChangeListener)
            etxtAutoNumber.setOnFocusChangeListener(::etxtFocusChangeListener)
            etxtRepresentative.setOnFocusChangeListener(::etxtFocusChangeListener)
            etxtCountInPackage.setOnFocusChangeListener(::etxtFocusChangeListener)
            etxtPackageCount.setOnFocusChangeListener(::etxtFocusChangeListener)
            etxtSeatsNumberCopy.setOnFocusChangeListener(::etxtFocusChangeListener)
            etxtStorePhone.setOnFocusChangeListener(::etxtFocusChangeListener)
            etxtZone.setOnFocusChangeListener(::etxtAutoCompleteFocusChangeListener)

            chbZ.setOnClickListener(::setCheckResult)
            chbExclamation.setOnClickListener(::setCheckResult)
            chbCurrency.setOnClickListener(::setCheckResult)
            chbArrow.setOnClickListener(::setCheckResult)
            chbBrand.setOnClickListener(::setCheckResult)

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
            acceptance.client = etxtCodeClient.text.toString()
            acceptance.autoNumber = etxtAutoNumber.text.toString()
            acceptance.idCard = etxtCardNumber.text.toString()
            acceptance.storeName = etxtStoreNumber.text.toString()
            acceptance.representativeName = etxtRepresentative.text.toString()
            acceptance.phoneNumber = etxtStorePhone.text.toString()
            val seatCount = etxtSeatsNumberCopy.text.toString()
            if (seatCount.isNotEmpty())
                acceptance.countSeat = seatCount.toInt()
            val packageCount = etxtPackageCount.text.toString()
            if (packageCount.isNotEmpty())
                acceptance.countPackage = packageCount.toInt()
            val countInPackage = etxtCountInPackage.text.toString()
            if (countInPackage.isNotEmpty())
                acceptance.countInPackage = countInPackage.toInt()
        }
        viewModel.createUpdateAcceptance(acceptance)
    }

    private fun setCheckResult(view: View) {
        view as CheckBox
        with(binding) {
            when (view) {
                chbZ -> {
                    acceptance.z = chbZ.isChecked
                }
                chbExclamation -> {
                    acceptance.glass = chbExclamation.isChecked
                }
                chbCurrency -> {
                    acceptance.expensive = chbCurrency.isChecked
                }
                chbArrow -> {
                    acceptance.notTurnOver = chbArrow.isChecked
                }
                chbBrand -> {
                    acceptance.brand = chbBrand.isChecked
                }
            }
        }
    }

    private fun autoCompleteOnKeyListener(view: View, key: Int, keyEvent: KeyEvent): Boolean {
        if (key == 66 && keyEvent.action == KeyEvent.ACTION_UP) {
            view as AutoCompleteTextView
            with(binding) {
                when (view) {
                    etxtZone -> {
                        val element = Utils.zones.find {
                            (it as AnyModel.Zone).name == view.text.toString()
                        }
                        if (element == null) {
                            view.requestFocus()
                            return false
                        }
                        element as AnyModel.Zone
                        acceptance.zone = element.name
                        acceptance.zoneUid = element.ref
                        etxtCardNumber.requestFocus()
                        return true
                    }
                    etxtStoreAddress -> {
                        if (view.adapter.count == 0) {
                            view.requestFocus()
                            return false
                        }
                        val textElement = view.adapter.getItem(0)
                        val element = Utils.addressess.find {
                            (it as AnyModel.AddressModel).name == textElement
                        }
                        element as AnyModel.AddressModel
                        acceptance.storeAddressName = element.name
                        acceptance.storeUid = element.ref
                        view.setText(element.name)
                        etxtStoreNumber.requestFocus()
                        return true
                    }
                    etxtProductType -> {
                        if (view.adapter.count == 0) {
                            view.requestFocus()
                            return false
                        }
                        val textElement = view.adapter.getItem(0)
                        val element = Utils.productTypes.find {
                            (it as AnyModel.ProductType).name == textElement
                        }
                        element as AnyModel.ProductType
                        acceptance.productTypeName = element.name
                        acceptance.productType = element.ref
                        view.setText(element.name)
                        etxtPackage.requestFocus()
                        return true
                    }
                    etxtPackage -> {
                        if (view.adapter.count == 0) {
                            view.requestFocus()
                            return false
                        }
                        val textElement = view.adapter.getItem(0)
                        val element = Utils.packages.find {
                            (it as AnyModel.PackageModel).name == textElement
                        }
                        element as AnyModel.PackageModel
                        acceptance._package = element.name
                        acceptance.packageUid = element.ref
                        view.setText(element.name)
                        etxtSeatsNumberCopy.requestFocus()
                        return true
                    }
                    else -> return false
                }
            }
        }
        return false
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
                    etxtCardNumber -> {
                        etxtStoreAddress.requestFocus()
                        return true
                    }
                    etxtStorePhone -> {
                        etxtProductType.requestFocus()
                        return true
                    }
                    etxtSeatsNumberCopy -> {
                        etxtPackageCount.requestFocus()
                        return true
                    }
                    etxtPackageCount -> {
                        etxtCountInPackage.requestFocus()
                        return true
                    }
                    etxtCountInPackage -> {
                        btnSaveCopy.requestFocus()
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
        acceptance = acc
        if (this.acceptance.ref.isEmpty()) {
            binding.pbLoading.isVisible = false
            return
        }
        clientFound = true
        showAcceptance()
        setInitFocuses()
        showPbLoading(false)
    }

    private fun showAcceptance() {
        with(binding) {
            etxtAutoNumber.setText(acceptance.autoNumber)
            etxtZone.setText(acceptance.zone)
            etxtCardNumber.setText(acceptance.idCard)
            etxtCodeClient.setText(acceptance.client)
            etxtSeatsNumber.setText(acceptance.countSeat.toString())
            etxtDocumentNumber.setText(acceptance.number)
            etxtStoreAddress.setText(acceptance.storeAddressName)
            etxtStoreNumber.setText(acceptance.storeName)
            etxtRepresentative.setText(acceptance.representativeName)
            etxtStorePhone.setText(acceptance.phoneNumber)
            etxtProductType.setText(acceptance.productTypeName)
            etxtPackage.setText(acceptance._package)
            etxtSeatsNumberCopy.setText(acceptance.countSeat.toString())
            etxtPackageCount.setText(acceptance.countPackage.toString())
            etxtCountInPackage.setText(acceptance.countInPackage.toString())
            etxtDocumentNumberCopy.setText(acceptance.number)
            chbExclamation.isChecked = acceptance.glass
            chbCurrency.isChecked = acceptance.expensive
            chbArrow.isChecked = acceptance.notTurnOver
            chbBrand.isChecked = acceptance.brand
            chbZ.isChecked = acceptance.z
        }
    }


    private fun setInitFocuses() {
        if (clientFound) return
        with(binding) {
            with(etxtCodeClient) {
                requestFocus()
                val length = text?.length ?: 0
                if (length > 0) setSelection(length)
            }
        }
    }

    private fun etxtFocusChangeListener(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            view as TextInputEditText
            view.text?.let {
                view.setSelection(it.length)
            }
        }
    }

    private fun etxtAutoCompleteFocusChangeListener(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            view as AutoCompleteTextView
            view.text?.let {
                view.setSelection(it.length)
            }
        }
    }

    companion object {
        const val KEY_ACCEPTANCE_NUMBER = "acceptance_number"
    }
}

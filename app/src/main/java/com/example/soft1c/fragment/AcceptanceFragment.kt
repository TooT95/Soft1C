package com.example.soft1c.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.soft1c.R
import com.example.soft1c.utils.Utils
import com.example.soft1c.databinding.FragmentAcceptanceBinding
import com.example.soft1c.model.Acceptance
import com.example.soft1c.model.AcceptanceEnableVisible
import com.example.soft1c.model.AnyModel
import com.example.soft1c.model.Client
import com.example.soft1c.repository.AcceptanceRepository
import com.example.soft1c.viewmodel.AcceptanceViewModel
import com.google.android.material.textfield.TextInputEditText

class AcceptanceFragment :
    BaseFragment<FragmentAcceptanceBinding>(FragmentAcceptanceBinding::inflate) {

    private var clientFound = false
    private lateinit var acceptance: Acceptance
    private val viewModel: AcceptanceViewModel by viewModels()
    private var hasFocusCanSave = false
    private lateinit var propertyList: List<AcceptanceEnableVisible>

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

    private fun createUpdateAcceptance(pair: Pair<Acceptance, String>) {
        if (pair.second.isNotEmpty()) {
            toast(pair.second)
            return
        }
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

            etxtZone.setAdapter(
                ArrayAdapter(requireContext(),
                    android.R.layout.simple_list_item_1, Utils.zones.map {
                        (it as AnyModel.Zone).name
                    })
            )
            etxtZone.setOnKeyListener(::autoCompleteOnKeyListener)

            etxtStoreAddress.setAdapter(
                ArrayAdapter(requireContext(),
                    android.R.layout.simple_list_item_1, Utils.addressess.map {
                        (it as AnyModel.AddressModel).name
                    })
            )
            etxtStoreAddress.setOnKeyListener(::autoCompleteOnKeyListener)

            etxtProductType.setAdapter(
                ArrayAdapter(requireContext(),
                    android.R.layout.simple_list_item_1, Utils.productTypes.map {
                        (it as AnyModel.ProductType).name
                    })
            )
            etxtProductType.setOnKeyListener(::autoCompleteOnKeyListener)

            etxtPackage.setAdapter(
                ArrayAdapter(requireContext(),
                    android.R.layout.simple_list_item_1, Utils.packages.map {
                        (it as AnyModel.PackageModel).name
                    })
            )
            etxtPackage.setOnKeyListener(::autoCompleteOnKeyListener)
            etxtSave.setOnKeyListener(::autoCompleteOnKeyListener)
            etxtSave.setOnFocusChangeListener(::setAutoCompleteFocusListener)
            etxtSaveCopy.setOnFocusChangeListener(::setAutoCompleteFocusListener)

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
            etxtStoreAddress.setOnFocusChangeListener(::etxtAutoCompleteFocusChangeListener)
            etxtProductType.setOnFocusChangeListener(::etxtAutoCompleteFocusChangeListener)
            etxtPackage.setOnFocusChangeListener(::etxtAutoCompleteFocusChangeListener)

            chbZ.setOnClickListener(::setCheckResult)
            chbExclamation.setOnClickListener(::setCheckResult)
            chbCurrency.setOnClickListener(::setCheckResult)
            chbArrow.setOnClickListener(::setCheckResult)
            chbBrand.setOnClickListener(::setCheckResult)

//            btnSave.setOnClickListener {
//                createUpdateAcceptance()
//            }
//            btnSaveCopy.setOnClickListener {
//                createUpdateAcceptance()
//            }
        }
    }

    private fun setAutoCompleteFocusListener(view: View, hasFocus: Boolean) {
        view as AutoCompleteTextView
        with(binding) {
            when (view) {
                etxtSave, etxtSaveCopy -> if (hasFocus) {
                    if (hasFocusCanSave) {
                        hasFocusCanSave = !hasFocusCanSave
                        return@with
                    }
                    createUpdateAcceptance()
                }
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
                        findAndFillAnyModel(
                            Utils.zones,
                            Utils.ObjectModelType.ZONE,
                            view
                        )
                        if (etxtCardNumber.isEnabled && etxtCardNumber.isVisible) {
                            etxtCardNumber.requestFocus()
                            return true
                        }
                        return false
                    }
                    etxtStoreAddress -> {
                        findAndFillAnyModel(
                            Utils.addressess,
                            Utils.ObjectModelType.ADDRESS,
                            view
                        )
                        if (etxtStoreNumber.isEnabled && etxtStoreNumber.isVisible) {
                            etxtStoreNumber.requestFocus()
                            return true
                        }
                        return false
                    }
                    etxtProductType -> {
                        findAndFillAnyModel(
                            Utils.productTypes,
                            Utils.ObjectModelType.PRODUCT_TYPE,
                            view
                        )
                        if (etxtPackage.isEnabled && etxtPackage.isVisible) {
                            etxtPackage.requestFocus()
                            return true
                        }
                        return false
                    }
                    etxtPackage -> {
                        findAndFillAnyModel(
                            Utils.packages,
                            Utils.ObjectModelType._PACKAGE,
                            view
                        )
                        if (etxtSeatsNumberCopy.isEnabled && etxtSeatsNumberCopy.isVisible) {
                            etxtSeatsNumberCopy.requestFocus()
                            return true
                        }
                        return false
                    }
                    else -> return false
                }
            }
        } else if (key == 66 && keyEvent.action == KeyEvent.ACTION_DOWN) {
            view as AutoCompleteTextView
            with(binding) {
                when (view) {
                    etxtSave -> {
                        createUpdateAcceptance()
                        return true
                    }
                    else -> false
                }
            }
        }
        return false
    }

    private fun findAndFillAnyModel(
        anyModelList: List<AnyModel>,
        model: Int,
        view: AutoCompleteTextView,
    ) {
        val textElement = when {
            (view.adapter.count != 0) -> view.adapter.getItem(0).toString()
            view.text.isNotEmpty() -> view.text.toString()
            else -> ""
        }
        val element = anyModelList.find {
            when (it) {
                is AnyModel.ProductType -> it.name == textElement
                is AnyModel.PackageModel -> it.name == textElement
                is AnyModel.Zone -> it.name == textElement
                is AnyModel.AddressModel -> it.name == textElement
            }
        }
        if (element != null) {
            when (model) {
                Utils.ObjectModelType.PRODUCT_TYPE -> {
                    element as AnyModel.ProductType
                    acceptance.productTypeName = element.name
                    acceptance.productType = element.ref
                    view.setText(element.name)
                }
                Utils.ObjectModelType.ADDRESS -> {
                    element as AnyModel.AddressModel
                    acceptance.storeAddressName = element.name
                    acceptance.storeUid = element.ref
                    view.setText(element.name)
                }
                Utils.ObjectModelType.ZONE -> {
                    element as AnyModel.Zone
                    acceptance.zone = element.name
                    acceptance.zoneUid = element.ref
                    view.setText(element.name)
                }
                Utils.ObjectModelType._PACKAGE -> {
                    element as AnyModel.PackageModel
                    acceptance._package = element.name
                    acceptance.packageUid = element.ref
                    view.setText(element.name)
                }
                else -> {}
            }
        } else {
            view.text.clear()
        }
    }

    private fun customSetOnKeyListener(view: View, key: Int, keyEvent: KeyEvent): Boolean {
        if (key == 66 && keyEvent.action == KeyEvent.ACTION_UP) {
            with(binding) {
                val etxtView = view as TextInputEditText
//                if (etxtView.text!!.isEmpty()) {
//                    etxtView.error = resources.getString(R.string.text_field_is_empyt)
//                    return true
//                }
                when (etxtView) {
                    etxtCodeClient -> {
                        showDialogLoading()
                        viewModel.getClient(etxtView.text.toString())
                        return true
                    }
                    etxtCardNumber -> {
                        if (etxtStoreAddress.isEnabled && etxtStoreAddress.isVisible) {
                            etxtStoreAddress.requestFocus()
                            return true
                        }
                        return false
                    }
                    etxtStorePhone -> {
                        if (etxtProductType.isEnabled && etxtProductType.isVisible) {
                            etxtProductType.requestFocus()
                            return true
                        }
                        return false
                    }
                    etxtSeatsNumberCopy -> {
                        if (etxtPackageCount.isEnabled && etxtPackageCount.isVisible) {
                            etxtPackageCount.requestFocus()
                            return true
                        }
                        return false
                    }
                    etxtPackageCount -> {
                        if (etxtCountInPackage.isEnabled && etxtCountInPackage.isVisible) {
                            etxtCountInPackage.requestFocus()
                            return true
                        }
                        return false
                    }
                    etxtCountInPackage -> {
                        hasFocusCanSave = true
                        etxtSave.requestFocus()
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }
        } else if (key == 66 && keyEvent.action == KeyEvent.ACTION_DOWN) {
            with(binding) {
                val etxtView = view as TextInputEditText
//                if (etxtView.text!!.isEmpty()) {
//                    etxtView.error = resources.getString(R.string.text_field_is_empyt)
//                    return true
//                }
                when (etxtView) {
                    etxtCountInPackage -> {
                        hasFocusCanSave = true
                        etxtSave.requestFocus()
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

    private fun showDetails(pair: Pair<Acceptance, List<AcceptanceEnableVisible>>) {
        acceptance = pair.first
        propertyList = pair.second
        if (this.acceptance.ref.isEmpty()) {
            binding.pbLoading.isVisible = false
            return
        }
        clientFound = true
        showAcceptance()
        setInitFocuses()
        showPbLoading(false)
        //enableVisibleList()
    }

    private fun enableVisibleList() {
        val enableVisibleMap = mutableMapOf<View, AcceptanceEnableVisible>()
        with(binding) {
            propertyList.forEach { enableVisible ->
                when (enableVisible.field) {
                    AcceptanceRepository.CLIENT_KEY -> enableVisibleMap[etxtCodeClient] =
                        enableVisible
                    AcceptanceRepository.NUMBER_KEY -> {
                        enableVisibleMap[etxtDocumentNumber] = enableVisible
                        enableVisibleMap[etxtDocumentNumberCopy] = enableVisible
                    }
                    AcceptanceRepository.AUTO_NUMBER_KEY -> enableVisibleMap[etxtAutoNumber] =
                        enableVisible
                    AcceptanceRepository.ID_CARD_KEY -> enableVisibleMap[etxtCardNumber] =
                        enableVisible
                    AcceptanceRepository.ZONE_KEY -> enableVisibleMap[elayoutZone] =
                        enableVisible
                    AcceptanceRepository.COUNT_SEAT_KEY -> {
                        enableVisibleMap[etxtSeatsNumber] =
                            enableVisible
                        enableVisibleMap[etxtSeatsNumberCopy] =
                            enableVisible
                    }
                    AcceptanceRepository.COUNT_IN_PACKAGE_KEY -> enableVisibleMap[etxtCountInPackage] =
                        enableVisible
                    AcceptanceRepository.COUNT_PACKAGE_KEY -> enableVisibleMap[etxtPackageCount] =
                        enableVisible
                    AcceptanceRepository.PACKAGE_UID_KEY -> enableVisibleMap[elayoutPackage] =
                        enableVisible
                    AcceptanceRepository.PRODUCT_TYPE_KEY -> enableVisibleMap[elayoutProductType] =
                        enableVisible
                    AcceptanceRepository.STORE_UID_KEY -> enableVisibleMap[elayoutStoreAddress] =
                        enableVisible
                    AcceptanceRepository.PHONE_KEY -> enableVisibleMap[etxtStorePhone] =
                        enableVisible
                    AcceptanceRepository.STORE_NAME_KEY -> enableVisibleMap[etxtStoreNumber] =
                        enableVisible
                    AcceptanceRepository.REPRESENTATIVE_NAME_KEY -> enableVisibleMap[etxtRepresentative] =
                        enableVisible
                    AcceptanceRepository.Z_KEY -> enableVisibleMap[chbZ] =
                        enableVisible
                    AcceptanceRepository.BRAND_KEY -> enableVisibleMap[chbBrand] =
                        enableVisible
                    AcceptanceRepository.GLASS_KEY -> enableVisibleMap[chbExclamation] =
                        enableVisible
                    AcceptanceRepository.EXPENSIVE_KEY -> enableVisibleMap[chbCurrency] =
                        enableVisible
                    AcceptanceRepository.NOT_TURN_OVER_KEY -> enableVisibleMap[chbArrow] =
                        enableVisible
                    else -> {}
                }
            }
        }
        enableVisibleMap.forEach {
            val enableVisible = it.value
            val view = it.key
            view.isVisible = enableVisible.visible
            view.isEnabled = enableVisible.enable
        }
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
                view.selectAll()
            }
        }
    }

    private fun etxtAutoCompleteFocusChangeListener(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            view as AutoCompleteTextView
            view.text?.let {
                view.selectAll()
            }
        }
    }

    companion object {
        const val KEY_ACCEPTANCE_NUMBER = "acceptance_number"
    }
}

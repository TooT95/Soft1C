package com.example.soft1c.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soft1c.R
import com.example.soft1c.adapter.AcceptanceSizeAdapter
import com.example.soft1c.databinding.FragmentAcceptanceSizeBinding
import com.example.soft1c.model.Acceptance
import com.example.soft1c.model.SizeAcceptance
import com.example.soft1c.viewmodel.AcceptanceViewModel

class AcceptanceSizeFragment :
    BaseFragment<FragmentAcceptanceSizeBinding>(FragmentAcceptanceSizeBinding::inflate) {

    private lateinit var acceptanceGuid: String
    private val viewModel: AcceptanceViewModel by viewModels()
    private lateinit var acceptanceSize: SizeAcceptance
    private lateinit var acceptance: Acceptance
    private lateinit var sizeAdapter: AcceptanceSizeAdapter
    private var indexSeatNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        acceptanceGuid = arguments?.getString(KEY_ACCEPTANCE_GUID, "") ?: ""
        val acceptanceNumber =
            arguments?.getString(AcceptanceFragment.KEY_ACCEPTANCE_NUMBER, "") ?: ""
        viewModel.getAcceptanceSizeData(acceptanceGuid)
        viewModel.getAcceptance(acceptanceNumber)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        observeViewModels()
    }

    private fun observeViewModels() {
        viewModel.toastLiveData.observe(viewLifecycleOwner, ::toast)
        viewModel.acceptanceSizeLiveData.observe(viewLifecycleOwner, ::acceptanceSizeDetail)
        viewModel.acceptanceLiveData.observe(viewLifecycleOwner, ::showAcceptanceDetail)
        viewModel.updateAcceptanceSizeLiveData.observe(viewLifecycleOwner) {
            if (it) {
                closeDialogLoading()
                closeActivity()
            }
        }
    }

    private fun showAcceptanceDetail(acc: Acceptance) {
        acceptance = acc
        with(binding) {
            txtSeatCount.text = acceptance.countSeat.toString()
            txtPackage.text = acceptance._package
        }
        showPbLoading(false)
    }

    private fun acceptanceSizeDetail(sizeAcceptance: SizeAcceptance) {
        acceptanceSize = sizeAcceptance
        showPbLoading(false)
        with(binding) {
            txtSum.text = acceptanceSize.sum.toString()
            txtWeight.text = acceptanceSize.allWeight.toString()
            txtPriceM3.text = acceptanceSize.priceM3.toString()
            txtPriceWeight.text = acceptanceSize.priceWeight.toString()
            sizeAdapter.submitList(sizeAcceptance.dataArray)
        }
        fillIndexSeatNumber()
        enableFields()
    }

    private fun fillIndexSeatNumber() {
        indexSeatNumber = 0
        acceptanceSize.dataArray.forEach {
            if (it.weight != 0) {
                indexSeatNumber += 1
            }
        }
        if(acceptanceSize.recordAllowed)
            acceptanceSize.recordAllowed = indexSeatNumber != acceptanceSize.dataArray.size
        if (indexSeatNumber == 0) indexSeatNumber = 1
        binding.etxtCurrentIndex.setText(indexSeatNumber.toString())
    }

    private fun initUI() {
        sizeAdapter = AcceptanceSizeAdapter()
        showPbLoading(true)
        with(binding) {
            includeToolbar.toolbar.title = resources.getString(R.string.text_title_acceptance)
            includeToolbar.toolbar.setNavigationOnClickListener {
                closeActivity()
            }
            rvMain.adapter = sizeAdapter
            rvMain.setHasFixedSize(true)
            rvMain.layoutManager = LinearLayoutManager(requireContext())

            btnOk.setOnClickListener {
                fillList()
                etxtLength.requestFocus()
            }

            ivSave.setOnClickListener {
                viewModel.updateAcceptanceSize(acceptanceGuid, acceptanceSize)
                showDialogLoading()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fillList() {
        if (!checkedFillFields()) return
        val listData = acceptanceSize.dataArray.toMutableList()
        with(binding) {
            val seatNumber = etxtChangeColumnsNumber.text.toString()
            val length = etxtLength.text.toString().toInt()
            val width = etxtWidth.text.toString().toInt()
            val height = etxtHeight.text.toString().toInt()

            for (index in 0 until seatNumber.toInt()) {
                if (listData.size < indexSeatNumber) {
                    acceptanceSize.recordAllowed = false
                    continue
                }
                val listElement = listData[indexSeatNumber - 1]
                listElement.length = length
                listElement.width = width
                listElement.height = height
                listElement.weight = length * width * height
                indexSeatNumber += 1
            }
            etxtLength.text.clear()
            etxtWidth.text.clear()
            etxtHeight.text.clear()
            etxtChangeColumnsNumber.text.clear()
            fillIndexSeatNumber()
            enableFields()
        }
        sizeAdapter.submitList(listData)
        sizeAdapter.notifyDataSetChanged()
        acceptanceSize.dataArray = listData
    }

    private fun checkedFillFields(): Boolean {
        with(binding) {
            if (!checkedEditTextField(etxtChangeColumnsNumber)) return false
            if (!checkedEditTextField(etxtLength)) return false
            if (!checkedEditTextField(etxtWidth)) return false
            if (!checkedEditTextField(etxtHeight)) return false
        }
        return true
    }

    private fun checkedEditTextField(editText: EditText): Boolean {
        val checkField = editText.text.toString()
        return if (checkField.isEmpty()) {
            editText.error = resources.getString(R.string.text_field_is_empyt)
            false
        } else {
            editText.error = null
            true
        }
    }

    private fun enableFields() {
        with(binding) {
            if (acceptanceSize.recordAllowed) etxtLength.requestFocus()
            etxtLength.isEnabled = acceptanceSize.recordAllowed
            etxtWidth.isEnabled = acceptanceSize.recordAllowed
            etxtHeight.isEnabled = acceptanceSize.recordAllowed
            etxtChangeColumnsNumber.isEnabled = acceptanceSize.recordAllowed
            btnOk.isEnabled = acceptanceSize.recordAllowed
            if (!acceptanceSize.recordAllowed)
                ivSave.requestFocus()
        }
    }

    private fun closeActivity() {
        activity?.onBackPressed()
    }

    private fun showPbLoading(show: Boolean) {
        with(binding) {
            pbLoading.isVisible = show
            containerMain.isVisible = !show
        }
    }

    companion object {
        const val KEY_ACCEPTANCE_GUID = "acceptance_guid"
    }

}
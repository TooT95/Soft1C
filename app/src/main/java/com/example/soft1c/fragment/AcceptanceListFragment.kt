package com.example.soft1c.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soft1c.R
import com.example.soft1c.utils.Utils
import com.example.soft1c.adapter.AcceptanceAdapter
import com.example.soft1c.databinding.FragmentAcceptanceListBinding
import com.example.soft1c.model.Acceptance
import com.example.soft1c.model.AcceptanceEnableVisible
import com.example.soft1c.model.AnyModel
import com.example.soft1c.model.ItemClicked
import com.example.soft1c.viewmodel.AcceptanceViewModel
import com.example.soft1c.viewmodel.BaseViewModel
import com.google.android.material.textfield.TextInputEditText

class AcceptanceListFragment :
    BaseFragment<FragmentAcceptanceListBinding>(FragmentAcceptanceListBinding::inflate) {

    private lateinit var acceptanceAdapter: AcceptanceAdapter
    private val viewModel: AcceptanceViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private var showColumnZone = true
    private var showText = true

    private var requiredTypes = 4
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel.downloadType(Utils.ObjectModelType.ADDRESS)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeToolbar.toolbar.title =
            resources.getString(R.string.text_title_acceptance_list)
        initUI()
        observeViewModels()
        if (requiredTypes > 0)
            showDialogLoading()
        setTextDialogLoading(resources.getString(R.string.text_address))
    }

    private fun observeViewModels() {
        viewModel.toastLiveData.observe(viewLifecycleOwner, ::toast)
        viewModel.acceptanceListLiveData.observe(viewLifecycleOwner, ::showAcceptanceList)
        viewModel.acceptanceLiveData.observe(viewLifecycleOwner, ::acceptanceByNumber)
        baseViewModel.anyObjectLiveData.observe(viewLifecycleOwner, ::checkModelAndDownload)
    }

    private fun showAcceptanceList(list: List<Acceptance>) {
        showPbLoading(false)
        acceptanceAdapter.submitList(list)
    }

    private fun acceptanceByNumber(pair: Pair<Acceptance, List<AcceptanceEnableVisible>>) {
        val acceptance = pair.first
        closeDialogLoading()
        binding.etxtDocumentNumber.text?.clear()
        if (acceptance.ref.isNotEmpty())
            onItemClicked(ItemClicked.ITEM, acceptance)
        else
            toast(resources.getString(R.string.text_element_not_found))
    }

    private fun initUI() {
        if (Utils.refreshList) viewModel.getAcceptanceList()
        showPbLoading(true)
        initRvList()

        val chbListener = View.OnClickListener {
            with(binding)
            {
                val isAccept = it.id == binding.chbAcceptance.id
                val isWeight = it.id == binding.chbWeight.id
                val isCapacity = it.id == binding.chbSize.id

                if ((it as CheckBox).isChecked) {
                    chbAcceptance.isChecked = isAccept
                    chbWeight.isChecked = isWeight
                    chbSize.isChecked = isCapacity
                }
            }

        }
        with(binding) {
            chbAcceptance.setOnClickListener(chbListener)
            chbWeight.setOnClickListener(chbListener)
            chbSize.setOnClickListener(chbListener)

            ivAdd.setOnClickListener {
                findNavController().navigate(R.id.action_acceptanceFragment_to_acceptanceFragment)
            }
            etxtDocumentNumber.setOnKeyListener(::findOpenDocumentByNumber)
            chbAcceptance.isChecked = true

            chbVisibiliy.setOnClickListener {
                showColumnZone = !showColumnZone
                txtZone.isVisible = showColumnZone
                initRvList()
                viewModel.getAcceptanceList()
            }
            chbShowText.setOnClickListener {
                showText = !showText
                linearText.isVisible = showText
            }
            ivRefresh.setOnClickListener {
                showPbLoading(true)
                viewModel.getAcceptanceList()
            }
        }

    }

    private fun initRvList() {
        acceptanceAdapter = AcceptanceAdapter(::onItemClicked, showColumnZone)
        with(binding.rvAcceptanceList) {
            adapter = acceptanceAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun findOpenDocumentByNumber(eView: View, key: Int, event: KeyEvent): Boolean {
        if (key == 66 && event.action == KeyEvent.ACTION_UP) {
            val thisView = (eView as TextInputEditText)
            if (thisView.text!!.isEmpty()) {
                thisView.error = resources.getString(R.string.text_field_is_empyt)
                thisView.requestFocus()
                return false
            }
            thisView.error = null
            showDialogLoading()
            viewModel.getAcceptance(thisView.text.toString())
            return true
        }
        return false
    }


    private fun checkModelAndDownload(pairOf: Pair<Int, List<AnyModel>>) {
        requiredTypes -= 1
        when (pairOf.first) {
            Utils.ObjectModelType.ADDRESS -> {
                Utils.addressess = pairOf.second.sortedBy {
                    (it as AnyModel.AddressModel).name
                }
                setTextDialogLoading(resources.getString(R.string.text_package))
                baseViewModel.downloadType(Utils.ObjectModelType._PACKAGE)
            }
            Utils.ObjectModelType._PACKAGE -> {
                Utils.packages = pairOf.second
                setTextDialogLoading(resources.getString(R.string.text_product_type))
                baseViewModel.downloadType(Utils.ObjectModelType.PRODUCT_TYPE)
            }
            Utils.ObjectModelType.PRODUCT_TYPE -> {
                Utils.productTypes = pairOf.second
                setTextDialogLoading(resources.getString(R.string.text_zone))
                baseViewModel.downloadType(Utils.ObjectModelType.ZONE)
            }
            Utils.ObjectModelType.ZONE -> {
                Utils.zones = pairOf.second
                closeDialogLoading()
                viewModel.getAcceptanceList()
            }
        }
    }

    private fun onItemClicked(itemClicked: ItemClicked, acceptance: Acceptance) {
        when (itemClicked) {
            ItemClicked.ITEM -> {
                val args = Bundle().apply {
                    putString(AcceptanceFragment.KEY_ACCEPTANCE_NUMBER, acceptance.number)
                    putString(AcceptanceSizeFragment.KEY_ACCEPTANCE_GUID, acceptance.ref)
                }
                openAcceptanceDetail(args)
            }
            else -> return
        }
    }

    private fun openAcceptanceDetail(bundle: Bundle) {
        with(binding) {
            findNavController().navigate(
                when {
                    chbAcceptance.isChecked -> R.id.action_acceptanceFragment_to_acceptanceFragment
                    chbWeight.isChecked -> R.id.action_acceptanceListFragment_to_acceptanceWeightFragment
                    chbSize.isChecked -> R.id.action_acceptanceListFragment_to_acceptanceSizeFragment
                    else -> {
                        R.id.action_acceptanceFragment_to_acceptanceFragment
                    }
                }, bundle
            )
        }
    }

    private fun showPbLoading(show: Boolean) {
        with(binding) {
            rvAcceptanceList.isVisible = !show
            pbLoading.isVisible = show
        }
    }

}
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
import com.example.soft1c.adapter.AcceptanceAdapter
import com.example.soft1c.databinding.FragmentAcceptanceListBinding
import com.example.soft1c.model.Acceptance
import com.example.soft1c.model.ItemClicked
import com.example.soft1c.viewmodel.AcceptanceViewModel
import com.google.android.material.textfield.TextInputEditText
import timber.log.Timber

class AcceptanceListFragment :
    BaseFragment<FragmentAcceptanceListBinding>(FragmentAcceptanceListBinding::inflate) {

    private lateinit var acceptanceAdapter: AcceptanceAdapter
    private val viewModel: AcceptanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAcceptanceList()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeToolbar.toolbar.title =
            resources.getString(R.string.text_title_acceptance_list)
        initUI()
        obserViewModels()
    }

    private fun obserViewModels() {
        viewModel.toastLiveData.observe(viewLifecycleOwner, ::toast)
        viewModel.acceptanceListLiveData.observe(viewLifecycleOwner) {
            showPbLoading(false)
            acceptanceAdapter.submitList(it)
        }
        viewModel.acceptanceLiveData.observe(viewLifecycleOwner) {
            closeDialogLoading()
            if (it.ref.isNotEmpty())
                onItemClicked(ItemClicked.ITEM, it)
            else
                toast("Element not found")
        }
    }

    private fun initUI() {
        showPbLoading(true)
        acceptanceAdapter = AcceptanceAdapter(::onItemClicked)
        with(binding.rvAcceptanceList) {
            adapter = acceptanceAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL))
        }

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
                openAcceptanceDetail(Bundle())
            }
            etxtDocumentNumber.setOnKeyListener { eView, key, event ->
                if (key == 66 && event.action == KeyEvent.ACTION_UP) {
                    Timber.d("etxtDocumentNumber.setOnKeyListener $key")
                    val thisView = (eView as TextInputEditText)
                    if (thisView.text!!.isEmpty()) {
                        thisView.error = resources.getString(R.string.text_field_is_empyt)
                        thisView.requestFocus()
                        return@setOnKeyListener false
                    }
                    thisView.error = null
                    showDialogLoading()
                    viewModel.getAcceptance(thisView.text.toString())
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }
    }

    private fun onItemClicked(itemClicked: ItemClicked, acceptance: Acceptance) {
        when (itemClicked) {
            ItemClicked.ITEM -> {
                val args = Bundle().apply {
                    putString(AcceptanceFragment.KEY_ACCEPTANCE_NUMBER, acceptance.number)
                }
                openAcceptanceDetail(args)
            }
            else -> return
        }
    }

    private fun openAcceptanceDetail(bundle: Bundle) {
        findNavController().navigate(R.id.action_acceptanceFragment_to_acceptanceFragment, bundle)
    }

    private fun showPbLoading(show: Boolean) {
        with(binding) {
            rvAcceptanceList.isVisible = !show
            pbLoading.isVisible = show
        }
    }

}
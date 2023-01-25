package com.example.soft1c.fragment

import android.os.Bundle
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
import com.example.soft1c.viewmodel.AcceptanceViewModel

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
        binding.includeToolbar.toolbar.title = resources.getString(R.string.text_title_acceptance_list)

        initUI()
        obserViewModels()
    }

    private fun obserViewModels() {
        viewModel.toastLiveData.observe(viewLifecycleOwner, ::toast)
        viewModel.acceptanceListLiveData.observe(viewLifecycleOwner) {
            showPbLoading(false)
            acceptanceAdapter.submitList(it)
        }
    }

    private fun initUI() {
        showPbLoading(true)
        acceptanceAdapter = AcceptanceAdapter()
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

                findNavController().navigate(R.id.action_acceptanceFragment_to_acceptanceFragment)
            }
        }
    }

    private fun showPbLoading(show: Boolean) {
        with(binding) {
            rvAcceptanceList.isVisible = !show
            pbLoading.isVisible = show
        }
    }
}
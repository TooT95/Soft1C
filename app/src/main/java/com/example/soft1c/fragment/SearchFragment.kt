package com.example.soft1c.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.util.Util
import com.example.soft1c.R
import com.example.soft1c.Utils
import com.example.soft1c.databinding.FragmentSearchBinding
import com.example.soft1c.model.AnyModel
import timber.log.Timber

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    lateinit var listAdapter: ArrayAdapter<String>
    lateinit var modelList: ArrayList<String>
    var model: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modelList = ArrayList()
        arguments?.let {
            model = it.getInt(KEY_MODEL, 0)
        }
        with(binding.includeToolbar.toolbar) {
            var resourseId = 0
            when (model) {
                Utils.ObjectModelType.ZONE -> {
                    resourseId = R.string.text_zone
                    modelList.addAll(Utils.zones.map {
                        (it as AnyModel.Zone).name
                    })
                }
                else -> {
                    resourseId = R.string.text_title_search
                }
            }
            title = resources.getString(resourseId)
            setNavigationOnClickListener {
                activity?.onBackPressed()
            }
        }
        initUI()
    }

    override fun onResume() {
        super.onResume()
        binding.svText.requestFocus()
    }

    private fun initUI() {
        with(binding) {
            svText.setOnQueryTextFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    view.postDelayed({
                        activity?.let {
                            val imm =
                                it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(view.findFocus(), 0)
                        }
                    }, 200)

                }
            }
            svText.isIconified = false
            svText.requestFocusFromTouch()

            listAdapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                modelList
            )
            listviewResult.adapter = listAdapter

            listviewResult.setOnItemClickListener { _, _, position, p3 ->
                TestFragment.resultForSearch = listAdapter.getItem(position).toString()
                activity?.onBackPressed()
            }

            svText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    val element = when (model) {
                        Utils.ObjectModelType.ZONE -> {
                            Utils.zones.find {
                                (it as AnyModel.Zone).name == query
                            }
                        }
                        else -> null
                    } ?: return false
                    Utils.zone = element
                    activity?.onBackPressed()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    listAdapter.filter.filter(newText)
                    return false
                }
            })
        }
    }

    companion object {
        const val KEY_MODEL = "key_model"
    }

}
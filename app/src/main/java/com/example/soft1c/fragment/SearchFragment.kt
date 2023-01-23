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
import com.example.soft1c.R
import com.example.soft1c.databinding.FragmentSearchBinding
import timber.log.Timber

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    lateinit var listAdapter: ArrayAdapter<String>
    lateinit var programmingLanguagesList: ArrayList<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            it.title = resources.getString(R.string.text_title_search)
        }
        initUI()
    }

    override fun onResume() {
        super.onResume()
        binding.svText.requestFocus()
    }

    private fun initUI() {
        programmingLanguagesList = ArrayList()
        programmingLanguagesList.add("koropka")
        programmingLanguagesList.add("korupka#")
        programmingLanguagesList.add("koropka")
        programmingLanguagesList.add("shtuka")
        programmingLanguagesList.add("shtika")
        programmingLanguagesList.add("korpus")
        programmingLanguagesList.add("shtose")

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
                programmingLanguagesList
            )
            listviewResult.adapter = listAdapter

            listviewResult.setOnItemClickListener { _, _, position, p3 ->
                TestFragment.resultForSearch = listAdapter.getItem(position).toString()
                activity?.onBackPressed()
            }

            svText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    TestFragment.resultForSearch = listAdapter.getItem(0).toString()
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
        const val KEY_RESULT = "key_result"
    }

}
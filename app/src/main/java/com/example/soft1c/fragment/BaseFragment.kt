package com.example.soft1c.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.soft1c.R
import timber.log.Timber

open class BaseFragment<T : ViewBinding>(
    private val layoutInflater: (
        layoutInflate: LayoutInflater,
        viewGroup: ViewGroup?,
        attachToParent: Boolean,
    ) -> T,
) : Fragment() {

    private var _binding: T? = null
    private lateinit var dialogLoading: AlertDialog

    val binding: T
        get() = _binding!!

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = layoutInflater.invoke(inflater, container, false)
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun toast(text: String) {
        closeDialogLoading()
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        Timber.d(text)
    }

    fun setSharedPref(key: String, value: String) {
        val sharedPreferences = requireContext().getSharedPreferences("", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    fun getSharedPref(key: String): String {
        val sharedPreferences = requireContext().getSharedPreferences("", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "") ?: ""
    }

    fun showDialogLoading() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_loading, null)
        dialogLoading = AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(false)
            .create()
        if (!dialogLoading.isShowing) {
            dialogLoading.show()
        }
    }

    fun closeDialogLoading() {
        if (dialogLoading.isShowing) {
            dialogLoading.setCancelable(true)
            dialogLoading.cancel()
        }
    }

}
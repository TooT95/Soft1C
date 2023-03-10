package com.example.soft1c.fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.soft1c.utils.Demo
import com.example.soft1c.R
import com.example.soft1c.utils.Utils
import com.example.soft1c.databinding.FragmentAuthBinding
import com.example.soft1c.network.Network
import com.example.soft1c.utils.MainActivity
import com.example.soft1c.viewmodel.BaseViewModel
import com.google.android.material.textfield.TextInputEditText

class AuthFragment : BaseFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate) {

    private val viewModel: BaseViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeToolbar.toolbar.title = resources.getString(R.string.text_title_auth)
        binding.includeToolbar.toolbar.setOnMenuItemClickListener(object :
            Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                activity?.let { fragActivity ->
                    when (item?.itemId) {
                        R.id.item_chinese -> {
                            fragActivity as MainActivity
                            fragActivity.setLocale("zh")
                            return false
                        }
                        R.id.item_english -> {
                            fragActivity as MainActivity
                            fragActivity.setLocale("eng")
                            return false
                        }
                        R.id.item_russian -> {
                            fragActivity as MainActivity
                            fragActivity.setLocale("ru")
                            return false
                        }
                        else -> {
                            return false
                        }
                    }
                }
                return false
            }

        })
        initUI()
        obserViewModels()
    }

    private fun obserViewModels() {
        viewModel.authLiveData.observe(viewLifecycleOwner) {
            showPbLoading(false)
            if (it) {
                findNavController().navigate(R.id.action_authFragment_to_acceptanceFragment)
            }
        }
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            showPbLoading(false)
            toast(it)
            binding.txtError.text = it
        }
    }

    private fun initUI() {
        loadFromSharedPref()
        with(binding) {
            cardLogin.setOnClickListener {
                setBase()
                val demo = Demo()
                demo.loadProfile()
                viewModel.auth()
                showPbLoading(true)
            }
        }
    }

    private fun loadFromSharedPref() {
        with(binding) {
            val address = getSharedPref(Network.KEY_ADDRESS)
            val port = getSharedPref(Network.KEY_PORT)
            val url = getSharedPref(Network.KEY_BASE_URL)
            val baseName = getSharedPref(Network.KEY_BASENAME)
            val username = getSharedPref(Network.KEY_USERNAME)
            val password = getSharedPref(Network.KEY_PASSWORD)
            etxtUrlAdress.setText(address)
            etxtUrlPort.setText(port)
            etxtBasename.setText(baseName)
            etxtUsername.setText(username)
            etxtPassword.setText(password)
            Utils.setAttributes(url, baseName, username, password,resources.getString(R.string.app_lang))
        }
    }

    private fun setBase() {
        with(binding) {
            val address = checkFieldReturn(etxtUrlAdress)
            setSharedPref(Network.KEY_ADDRESS, address)
            if (address.isEmpty()) return
            var port = etxtUrlPort.text.toString()
            setSharedPref(Network.KEY_PORT, port)
            if (port.isEmpty()) port = "" else port = ":${port}"
            val url = "${spinnerProtocols.selectedItem}://${address}${port}"

            setSharedPref(Network.KEY_BASE_URL, url)
            val baseName = checkFieldReturn(etxtBasename)
            if (baseName.isEmpty()) return else setSharedPref(Network.KEY_BASENAME, baseName)
            val username = checkFieldReturn(etxtUsername)
            if (username.isEmpty()) return else setSharedPref(Network.KEY_USERNAME, username)
            val password = checkFieldReturn(etxtPassword)
            if (password.isEmpty()) return else setSharedPref(Network.KEY_PASSWORD, password)
            Utils.setAttributes(url, baseName, username, password,resources.getString(R.string.app_lang))
        }
    }

    private fun checkFieldReturn(etxt: TextInputEditText): String {
        if (etxt.text!!.isEmpty()) {
            etxt.error = resources.getString(R.string.text_field_is_empyt)
            return ""
        }
        etxt.error = null
        return etxt.text.toString()
    }

    private fun showPbLoading(show: Boolean) {
        with(binding) {
            cardAddress.isEnabled = !show
            cardLogin.isEnabled = !show
            cardUser.isEnabled = !show
            pbLoading.isVisible = show
        }
    }
}
package com.example.soft1c.utils

import com.example.soft1c.model.AnyModel

object Utils {
    var base_url = ""
    var basename = ""
    var username = ""
    var password = ""
    var auth = ""
    var lang = ""
    var productTypes: List<AnyModel> = listOf()
    var addressess: List<AnyModel> = listOf()
    var packages: List<AnyModel> = listOf()
    var zones: List<AnyModel> = listOf()
    var anyModel: AnyModel? = null
    var refreshList: Boolean = false

    fun setAttributes(
        baseUrl: String,
        base_name: String,
        user_name: String,
        _password: String,
        _lang: String
    ) {
        base_url = baseUrl
        basename = base_name
        username = user_name
        password = _password
        auth = "/${base_name}/hs/PriemkiAPI/"
        lang = _lang
    }

    object Contracts {
        const val REF_KEY = "Ссылка"
        const val NAME_KEY = "Наименование"
        const val CODE_KEY = "Код"
    }

    object ObjectModelType {
        const val ADDRESS = 1
        const val _PACKAGE = 2
        const val PRODUCT_TYPE = 3
        const val ZONE = 4
        const val EMPTY = 5
    }
}
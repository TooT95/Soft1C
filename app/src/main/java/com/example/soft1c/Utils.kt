package com.example.soft1c

object Utils {
    var base_url = ""
    var basename = ""
    var username = ""
    var password = ""
    var auth = ""

    fun setAttributes(baseUrl: String, base_name: String, user_name: String, _password: String) {
        base_url = baseUrl
        basename = base_name
        username = user_name
        password = _password
        auth = "/${base_name}/hs/PriemkiAPI/"
    }

}
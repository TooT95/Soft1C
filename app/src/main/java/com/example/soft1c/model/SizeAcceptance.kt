package com.example.soft1c.model

data class SizeAcceptance(
    val recordAllowed: Boolean = false,
    val sum: Int = 0,
    val allWeight: Int = 0,
    val priceM3: Int = 0,
    val priceWeight: Int = 0,
    val dataArray: List<SizeData>,
) {

    data class SizeData(
        var seatNumber: Int = 0,
        var length: Int = 0,
        var width: Int = 0,
        var height: Int = 0,
        var weight: Int = 0,
    )

}
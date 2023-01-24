package com.example.soft1c.model

data class Acceptance(
    val ref: String,
    val number: String,
    val client: String,
    val date: String,
    val weight: Boolean,
    val capacity: Boolean,
    val zone: String,
    val productTypeName: String,
    val _package: String,
    val packageUid: String = "",
    val zoneUid: String = "",
    val productType: String = "",
)
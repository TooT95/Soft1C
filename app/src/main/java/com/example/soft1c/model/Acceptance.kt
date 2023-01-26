package com.example.soft1c.model

data class Acceptance(
    val number: String, // Номер
    val ref: String = "", // Ссылка
    val client: String = "", // Клиент
    val date: String = "", // Дата
    val weight: Boolean = false, //
    val capacity: Boolean = false,
    val z: Boolean = false, // ZТовар
    val brand: Boolean = false, //Брэнд
    val zone: String = "",
    val autoNumber: String = "", //НомерАвто
    val idCard: String = "", //IDПродавца
    val productTypeName: String = "",
    val storeName: String = "", // НаименованиеМагазина
    val storeAddressName: String = "", // НаименованиеМагазина
    val phoneNumber: String = "", //ТелефонМагазина
    val representativeName: String = "", // ИмяПредставителя
    val countInPackage: Int = 0, // КоличествоВУпаковке
    val countPackage: Int = 0, // КоличествоТиповУпаковок
    val countSeat: Int = 0, // КоличествоМест
//    val allWeight: Double = 0.0, // ОбщийВес
    val _package: String = "",
    val packageUid: String = "", //ТипУпаковки
    val storeUid: String = "", //АдресМагазина
    val zoneUid: String = "", // Зона
    val productType: String = "", // ВидТовара
    val batchGuid: String = "", // GUIDПартии
)
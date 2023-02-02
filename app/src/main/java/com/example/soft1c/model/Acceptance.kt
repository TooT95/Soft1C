package com.example.soft1c.model

data class Acceptance(
    val number: String, // Номер
    var ref: String = "", // Ссылка
    var client: String = "", // Клиент
    val date: String = "", // Дата
    var weight: Boolean = false, //
    var capacity: Boolean = false,
    var z: Boolean = false, // ZТовар
    var brand: Boolean = false, //Брэнд
    var glass: Boolean = false, //Стекло
    var expensive: Boolean = false, //Дорогой
    var notTurnOver: Boolean = false, //Некантовать
    var zone: String = "",
    var autoNumber: String = "", //НомерАвто
    var idCard: String = "", //IDПродавца
    var productTypeName: String = "",
    var storeName: String = "", // НаименованиеМагазина
    var storeAddressName: String = "", // НаименованиеМагазина
    var phoneNumber: String = "", //ТелефонМагазина
    var representativeName: String = "", // ИмяПредставителя
    var countInPackage: Int = 0, // КоличествоВУпаковке
    var countPackage: Int = 0, // КоличествоТиповУпаковок
    var countSeat: Int = 0, // КоличествоМест
    var allWeight: Double = 0.0, // ОбщийВес
    var _package: String = "",
    var packageUid: String = "", //ТипУпаковки
    var storeUid: String = "", //АдресМагазина
    var zoneUid: String = "", // Зона
    var productType: String = "", // ВидТовара
    var batchGuid: String = "", // GUIDПартии
)
package com.pratik.opalWishAdmin

data class ProductModel(
    var prouct_id: String = "", // Primary key
    var name: String = "",
    var price: Double = -1.0,
    var disp: String = "",
    var details: String = "",
    var imageUrl: String = "",
    var category: String = ""
)


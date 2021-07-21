package com.example.androidkickoff.model

import org.json.JSONArray

class OrderDetails (

    val orderId: Int,
    val resName: String,
    val orderDate: String,
    val food: JSONArray
)
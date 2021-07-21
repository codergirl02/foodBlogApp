package com.example.androidkickoff.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
class OrderEntity (
    @PrimaryKey val resId: String,
    @ColumnInfo(name = "food_items") val foodItems: String
)

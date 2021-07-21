package com.example.androidkickoff.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FoodEntity::class, OrderEntity::class], version = 1, exportSchema = false)

abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun OrderDao() : OrderDao
}
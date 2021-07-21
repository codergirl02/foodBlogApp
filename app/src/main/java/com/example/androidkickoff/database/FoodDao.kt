package com.example.androidkickoff.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface FoodDao {

    @Insert
    fun insertFood(foodEntity: FoodEntity)

    @Delete
    fun deleteFood(foodEntity: FoodEntity)

    @Query("SELECT * FROM foods")
    fun getAllFoods() : List<FoodEntity>

    @Query("SELECT * FROM foods WHERE id = :food_id")
    fun getFoodById(food_id : String) : FoodEntity

}
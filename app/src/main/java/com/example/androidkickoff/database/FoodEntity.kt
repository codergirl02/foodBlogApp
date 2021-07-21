package com.example.androidkickoff.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "foods")
data class FoodEntity (

    @PrimaryKey val id : Int,
//    @ColumnInfo(name = "name") val foodName : String,
//    @ColumnInfo(name = "rating") val foodRating : String,
//    @ColumnInfo(name = "price") val foodPrice : String,
//    @ColumnInfo(name = "image") val foodImage : String

    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "rating") val rating : String,
    @ColumnInfo(name = "cost_for_two") val costForTwo : String,
    @ColumnInfo(name = "image_url") val imageUrl : String


)
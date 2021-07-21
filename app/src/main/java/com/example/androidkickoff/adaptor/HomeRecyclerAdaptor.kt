package com.example.androidkickoff.adaptor

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.androidkickoff.R
import com.example.androidkickoff.model.Food
import com.example.androidkickoff.database.FoodEntity
import com.example.androidkickoff.database.FoodDatabase
import com.example.androidkickoff.fragment.RestaurentsFragment
import com.example.androidkickoff.model.FoodItem
import com.squareup.picasso.Picasso
import java.lang.reflect.Array.get
import java.nio.file.Paths.get
import java.util.ArrayList

class HomeRecyclerAdaptor(private var restaurants: ArrayList<Food>, val context: Context):
     RecyclerView.Adapter<HomeRecyclerAdaptor.HomeViewHolder>()
{

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HomeViewHolder {

        val view = LayoutInflater.from(p0.context)
                .inflate(R.layout.recycler_home_single_row,
        p0, false)

        return HomeViewHolder(view)

    }

    override fun getItemCount(): Int {
        
        return restaurants.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(p0: HomeViewHolder, p1: Int) {

        val resObject = restaurants.get(p1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            p0.imgFoodImage.clipToOutline = true
        }
        p0.txtFoodName.text = resObject.name
        p0.txtFoodRating.text = resObject.rating
        val costForTwo = "${resObject.costForTwo.toString()}/person"
        p0.txtFoodPrice.text = costForTwo
        Picasso.get().load(resObject.imageUrl).error(R.drawable.foodlogo).into(p0.imgFoodImage)

        val listOfFav = GetAllFavAsyncTask(context).execute().get()

        if (listOfFav.isNotEmpty() && listOfFav.contains(resObject.id.toString())){

            p0.imgFoodImage.setImageResource(R.drawable.ic_action_fav_checked)

        }else{
            p0.imgFoodImage.setImageResource(R.drawable.ic_action_fav)
        }

        p0.favImage.setOnClickListener {
            val restaurantEntity = FoodEntity(
                    resObject.id,
                    resObject.name,
                    resObject.rating,
                    resObject.costForTwo.toString(),
                    resObject.imageUrl
            )

            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                        DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    p0.favImage.setImageResource(R.drawable.ic_action_fav_checked)
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    p0.favImage.setImageResource(R.drawable.ic_action_fav)
                }
            }
        }
        p0.cardFood.setOnClickListener {
//            Toast.makeText(context, "Clicked on: ${p0.txtFoodName.text}", Toast.LENGTH_SHORT)
//                    .show()
            val fragment = RestaurentsFragment()
            val args = Bundle()
            args.putInt("id", resObject.id)
            args.putString("name", resObject.name)
            fragment.arguments = args
            val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, fragment)
            transaction.commit()
            (context as AppCompatActivity).supportActionBar?.title = p0.txtFoodName.text.toString()

        }
    }
    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFoodImage = view.findViewById(R.id.imgFoodImage) as ImageView
        val txtFoodName = view.findViewById(R.id.txtFoodName) as TextView
        val txtFoodRating = view.findViewById(R.id.txtFoodRating) as TextView
        val txtFoodPrice = view.findViewById(R.id.txtFoodPrice) as TextView
        val cardFood = view.findViewById(R.id.cardRestaurant) as CardView
        val favImage = view.findViewById(R.id.imgFoodImage) as ImageView
    }

    class DBAsyncTask(context: Context, val restaurantEntity: FoodEntity, val mode: Int) :
            AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {

                1 -> {
                    val res: FoodEntity? =
                            db.foodDao().getFoodById(restaurantEntity.id.toString())
                    db.close()
                    return res != null
                }

                2 -> {
                    db.foodDao().insertFood(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.foodDao().deleteFood(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }


    /*Since the outcome of the above background method is always a boolean, we cannot use the above here.
    * We require the list of favourite restaurants here and hence the outcome would be list.
    * For simplicity we obtain the list of restaurants and then extract their ids which is then compared to the ids
    * inside the list sent to the adapter */

    class GetAllFavAsyncTask(
            context: Context
    ) :
            AsyncTask<Void, Void, List<String>>() {

        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.foodDao().getAllFoods()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id.toString())
            }
            return listOfIds
        }
    }
}
//        val food = itemList[p1]
//        holder.txtFoodName.text = food.name
//        holder.txtFoodPrice.text = food.price
//        holder.txtFoodRating.text = food.rating
//        holder.imgFoodImage.setBackgroundResource(food.image)

//        Picasso.get().load(food.image).error(R.drawable.foodlogo).into(p1.imgFoodImage)
//    }
//
//    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view){
//
//        val txtFoodName : TextView  = view.findViewById(R.id.txtFoodName)
//        val txtFoodPrice : TextView  = view.findViewById(R.id.txtFoodPrice)
//        val txtFoodRating : TextView  = view.findViewById(R.id.txtFoodRating)
//        val imgFoodImage : TextView  = view.findViewById(R.id.imgFoodImage)
//
//
//    }
//}
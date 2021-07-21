package com.example.androidkickoff.fragment

import android.content.Context
import android.os.Bundle
import android.os.AsyncTask
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.androidkickoff.R
import com.example.androidkickoff.adaptor.HomeRecyclerAdaptor
import com.example.androidkickoff.database.FoodDatabase
import com.example.androidkickoff.database.FoodEntity
import com.example.androidkickoff.model.Food
import com.example.androidkickoff.util.DrawerLocker

class FavouriteFragment : Fragment() {

    private lateinit var recyclerRestaurant: RecyclerView
    private lateinit var homeAdaptor: HomeRecyclerAdaptor
    private var foodList = arrayListOf<Food>()
    private lateinit var rlLoading: RelativeLayout
    private lateinit var rlFav: RelativeLayout
    private lateinit var rlNoFav: RelativeLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.favourite_fragment, container, false)
        (activity as DrawerLocker).setDrawerEnabled(true)
        rlFav = view.findViewById(R.id.rlFavourites)
        rlNoFav = view.findViewById(R.id.rlNoFavourites)
        rlLoading = view.findViewById(R.id.rlLoading)
        rlLoading.visibility = View.VISIBLE

        setUpRecycler(view)

        return view
    }

    private fun setUpRecycler(view: View) {

        recyclerRestaurant = view.findViewById(R.id.recyclerFavourites)

        val backgroundList = FavouritesAsync(activity as Context).execute().get()

        if (backgroundList.isEmpty()) {

            rlLoading.visibility = View.GONE
            rlFav.visibility = View.GONE
            rlNoFav.visibility = View.VISIBLE

        } else {
            rlFav.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
            rlNoFav.visibility = View.GONE

            for (i in backgroundList) {
                foodList.add(
                        Food(
                                i.id,
                                i.name,
                                i.rating,
                                i.costForTwo.toInt(),
                                i.imageUrl
                        )
                )
            }
            homeAdaptor = HomeRecyclerAdaptor( foodList, activity as Context)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerRestaurant.layoutManager = mLayoutManager
            recyclerRestaurant.itemAnimator = DefaultItemAnimator()
            recyclerRestaurant.adapter = homeAdaptor
            recyclerRestaurant.setHasFixedSize(true)

        }

    }

    class FavouritesAsync(context: Context) : AsyncTask<Void, Void, List<FoodEntity>>() {

        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): List<FoodEntity> {

            return db.foodDao().getAllFoods()
        }

    }

}
package com.example.androidkickoff.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidkickoff.R
import com.example.androidkickoff.adaptor.HomeRecyclerAdaptor
import com.example.androidkickoff.model.Food
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androidkickoff.util.ConnectionManager
import com.example.androidkickoff.util.DrawerLocker
import com.example.androidkickoff.util.FETCH_RESTAURANTS
import com.example.androidkickoff.util.Sorter
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    private lateinit var recyclerHome: RecyclerView
//            lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdaptor: HomeRecyclerAdaptor
    private lateinit var rlLoading: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private var foodInfoList = arrayListOf<Food>()
    private var checkedItem: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.home_fragment, container, false)
        (activity as DrawerLocker).setDrawerEnabled(true)

        progressBar = view?.findViewById(R.id.progressBar) as ProgressBar
        rlLoading = view.findViewById(R.id.rlLoading) as RelativeLayout
        rlLoading.visibility = View.VISIBLE
//        layoutManager = LinearLayoutManager(activity)
        setUpRecycler(view)
        setHasOptionsMenu(true)

        return view
    }

        private fun setUpRecycler(view: View) {
            recyclerHome = view.findViewById(R.id.recyclerView) as RecyclerView

            val queue = Volley.newRequestQueue(activity as Context)
//
//        val url = "http://13.235.250.119/v2/restaurants/fetch_result"
//
            if (ConnectionManager().checkConnectivity(activity as Context)) {
//
                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    FETCH_RESTAURANTS, null,
                    Response.Listener<JSONObject> { response ->
//
                        rlLoading.visibility = View.GONE

                        try {
                            val data = response.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {

                                val resArray = data.getJSONArray("data")
                                for (i in 0 until resArray.length()) {

                                    val foodJsonObjects = resArray.getJSONObject(i)
                                    val foodObj = Food(
                                        foodJsonObjects.getString("id").toInt(),
                                        foodJsonObjects.getString("name"),
                                        foodJsonObjects.getString("rating"),
                                        foodJsonObjects.getString("cost_for_one").toInt(),
                                        foodJsonObjects.getString("image_url")
                                    )

                                    foodInfoList.add(foodObj)

                                    if (activity != null) {
                                        recyclerAdaptor =
                                            HomeRecyclerAdaptor(foodInfoList, activity as Context)
                                        val mLayoutManager = LinearLayoutManager(activity)
                                        recyclerHome.adapter = recyclerAdaptor
                                        recyclerHome.itemAnimator = DefaultItemAnimator()
                                        recyclerHome.layoutManager = mLayoutManager
                                        recyclerHome.setHasFixedSize(true)
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
//                    Toast.makeText(activity as Context, "Some error occurred", Toast.LENGTH_SHORT).show()
                        }
                    },
                        Response.ErrorListener { error: VolleyError? ->

//                        if (activity != null) {
                            Toast.makeText(
                                activity as Context,
                                    error?.message,
                                Toast.LENGTH_SHORT
                            ).show()

//                        }

                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "de92a6528d5b09"
                        return headers
                    }
                }
//
                queue.add(jsonObjectRequest)
            } else {
//
                val builder = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                builder.setTitle("Error")
                builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
                builder.setCancelable(false)
                builder.setPositiveButton("Ok") { _, _ ->
                    ActivityCompat.finishAffinity(activity as Activity)

                }

                builder.create()
                builder.show()
            }
        }

        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            activity?.menuInflater?.inflate(R.menu.dashboard_menu, menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_sort -> showDialog(context as Context)
            }
            return super.onOptionsItemSelected(item)
        }

        private fun showDialog(context: Context) {

            val builder: AlertDialog.Builder? = AlertDialog.Builder(context)
            builder?.setTitle("Sort By?")
//            builder?.setSingleChoiceItems(R.array.filters, checkedItem) { _, isChecked ->
//                checkedItem = isChecked
//            }
            builder?.setPositiveButton("Ok") { _, _ ->

                when (checkedItem) {
                    0 -> {
                        Collections.sort(foodInfoList, Sorter.costComparator)
                    }
                    1 -> {
                        Collections.sort(foodInfoList, Sorter.costComparator)
                        foodInfoList.reverse()
                    }
                    2 -> {
                        Collections.sort(foodInfoList, Sorter.ratingComparator)
                        foodInfoList.reverse()
                    }
                }
                recyclerAdaptor.notifyDataSetChanged()
            }
            builder?.setNegativeButton("Cancel") { _, _ ->

            }
            builder?.create()
            builder?.show()
    }

}

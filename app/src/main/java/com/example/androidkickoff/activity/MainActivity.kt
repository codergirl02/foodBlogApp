package com.example.androidkickoff.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.androidkickoff.R
import com.example.androidkickoff.*
import com.google.android.material.navigation.NavigationView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.toolbox.Volley
import com.example.androidkickoff.fragment.*
import com.example.androidkickoff.util.DrawerLocker
import com.example.androidkickoff.util.SessionManager

class MainActivity : AppCompatActivity(), DrawerLocker {

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled)
            DrawerLayout.LOCK_MODE_UNLOCKED
        else
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED

        drawerLayout.setDrawerLockMode(lockMode)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = enabled
    }

//    lateinit var coordinateLayout: CoordinatorLayout
//    lateinit var frameLayout: FrameLayout

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private var previousMenuItem: MenuItem? = null
    private lateinit var sessionManager: SessionManager
    private lateinit var sharedPrefs: SharedPreferences

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
//    (this@MainActivity, drawerLayout,
//            R.string.open_drawer, R.string.close_drawer)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this@MainActivity)
        sharedPrefs = this@MainActivity.getSharedPreferences(
            sessionManager.PREF_NAME,
            sessionManager.PRIVATE_MODE
        )

        init()

        setUpToolbar()

        setUpActionBarToggle()

        openDashboard()

        navigationView.setNavigationItemSelectedListener {item: MenuItem ->

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            item.isCheckable = true
            item.isChecked = true
            previousMenuItem = item

            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 100)

            val fragmentTransaction = supportFragmentManager.beginTransaction()

            when (item.itemId) {

                R.id.home -> {
//                    openDashboard()
                    val homeFragment = HomeFragment()
                    fragmentTransaction.replace(R.id.frame, homeFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "All Restaurants"

                }
                R.id.profile -> {
                    fragmentTransaction.replace(R.id.frame, ProfileFragment())
                    fragmentTransaction.commit()

                    supportActionBar?.title = "My Profile"
//                    drawerLayout.closeDrawers()

                }
                R.id.order_history -> {
                    val orderHistoryFragment = OrderHistoryFagment()
                    fragmentTransaction.replace(R.id.frame, orderHistoryFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "My Previous Orders"
                }

                R.id.favourite -> {
                    fragmentTransaction.replace(R.id.frame, FavouriteFragment())
                    fragmentTransaction.commit()
                    supportActionBar?.title = "Favourite Restaurents"
//                    drawerLayout.closeDrawers()
                }

                R.id.faqs -> {
                    fragmentTransaction.replace(R.id.frame, FaqsFragment())
                    fragmentTransaction.commit()

                    supportActionBar?.title = "FAQs"
//                    drawerLayout.closeDrawers()

                }

                R.id.logOut -> {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want exit?")
                        .setPositiveButton("Yes") { _, _ ->
                            sessionManager.setLogin(false)
                            sharedPrefs.edit().clear().apply()
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                            ActivityCompat.finishAffinity(this)
                        }
                        .setNegativeButton("No") { _, _ ->
                            openDashboard()
                        }
                        .create()
                        .show()

                }
            }
            return@setNavigationItemSelectedListener true
        }


    val convertView = LayoutInflater.from(this@MainActivity).inflate(R.layout.drawer_header, null)
    val userName: TextView = convertView.findViewById(R.id.txtDrawerText)
    val userPhone: TextView = convertView.findViewById(R.id.txtDrawerSecondaryText)
    val appIcon: ImageView = convertView.findViewById(R.id.imgDrawerImage)
    userName.text = sharedPrefs.getString("user_name", null)
    val phoneText = "+91-${sharedPrefs.getString("user_mobile_number", null)}"
    userPhone.text = phoneText
    navigationView.addHeaderView(convertView)


    userName.setOnClickListener{

        val profileFragment = ProfileFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, profileFragment)
        transaction.commit()
        supportActionBar?.title = "My profile"
        val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
        Handler().postDelayed(mPendingRunnable, 50)
    }

    appIcon.setOnClickListener{

        val profileFragment = ProfileFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, profileFragment)
        transaction.commit()
        supportActionBar?.title = "My profile"
        val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
        Handler().postDelayed(mPendingRunnable, 50)
    }
}

    private fun openDashboard() {

        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)

    }

    private fun setUpActionBarToggle(){

        actionBarDrawerToggle = object :
                ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer){

                override fun onDrawerStateChanged(newState: Int) {
                    super.onDrawerStateChanged(newState)
                    val pendingRunnable = Runnable {
                        val inputMethodManager =
                                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    }
                Handler().postDelayed(pendingRunnable, 50)
            }
        }

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    private fun setUpToolbar() {

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
//        toolbar.setTitleTextAppearance(this, R.style.Widget_MaterialComponents_TextView)
        toolbar.setTitleTextAppearance(this, R.style.PoppinsTextAppearance)

    }

    private fun init(){
        drawerLayout = findViewById(R.id.drawerLayout)
//        coordinateLayout = findViewById(R.id.coordinateLayout)
//        frameLayout = findViewById(R.id.frame)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationBar)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        val f = supportFragmentManager.findFragmentById(R.id.frame)
        when (id) {
            android.R.id.home -> {
                if (f is RestaurentsFragment) {
                    onBackPressed()
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }

//        if (id == android.R.id.home) {
//            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

}





package com.example.androidkickoff.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androidkickoff.R
import com.example.androidkickoff.fragment.HomeFragment
import com.example.androidkickoff.util.ConnectionManager
import com.example.androidkickoff.util.LOGIN
import com.example.androidkickoff.util.SessionManager
import com.example.androidkickoff.util.Validation
import org.json.JSONException
import org.json.JSONObject

open class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
//    lateinit var etName: EditText
//    lateinit var txtForgot : TextView
    lateinit var txtForgotPassword: TextView
    lateinit var txtSignup : TextView

    lateinit var sessionManager: SessionManager
    lateinit var sharedPreferences: SharedPreferences

//    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        sharedPreferences = getSharedPreferences("LogIn", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_login)


        etMobileNumber = findViewById(R.id.etMobileNumber)

        etPassword = findViewById(R.id.etPass)
//        etName = findViewById(R.id.etUserName)

        sessionManager = SessionManager(this)
        sharedPreferences =
            this.getSharedPreferences(sessionManager.PREF_NAME, sessionManager.PRIVATE_MODE)


        txtForgotPassword = findViewById(R.id.txtForgotPass)
        txtForgotPassword.setOnClickListener{
            startActivity(Intent(this@LoginActivity, forgotActivity::class.java))
        }

        txtSignup = findViewById(R.id.txtSignup)
        txtSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {

            /*Hide the login button when the process is going on*/
            btnLogin.visibility = View.INVISIBLE

//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            startActivity(intent)

//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.frame, HomeFragment())
//            transaction.commit()

            if (Validation.validateMobile(etMobileNumber.text.toString()) && Validation.validatePasswordLength(etPassword.text.toString())) {
                if (ConnectionManager().checkConnectivity(this@LoginActivity)) {

                    /*Create the queue for the request*/
                    val queue = Volley.newRequestQueue(this@LoginActivity)

                    /*Create the JSON parameters to be sent during the login process*/
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", etMobileNumber.text.toString())
                    jsonParams.put("password", etPassword.text.toString())

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST,LOGIN, jsonParams,
                        Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val response = data.getJSONObject("data")
                                    sharedPreferences.edit()
                                        .putString("user_id", response.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_name", response.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString(
                                            "user_mobile_number",
                                            response.getString("mobile_number")
                                        )
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("user_address", response.getString("address"))
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("user_email", response.getString("email")).apply()
                                    sessionManager.setLogin(true)
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    btnLogin.visibility = View.VISIBLE
                                    txtForgotPassword.visibility = View.VISIBLE
                                    btnLogin.visibility = View.VISIBLE
                                    val errorMessage = data.getString("errorMessage")
                                    Toast.makeText(
                                        this@LoginActivity,
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                btnLogin.visibility = View.VISIBLE
                                txtForgotPassword.visibility = View.VISIBLE
                                txtSignup.visibility = View.VISIBLE
                                e.printStackTrace()
                            }
                        },
                        Response.ErrorListener {
                            btnLogin.visibility = View.VISIBLE
                            txtForgotPassword.visibility = View.VISIBLE
                            txtSignup.visibility = View.VISIBLE
                            Log.e("Error::::", "/post request fail! Error: ${it.message}")
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "de92a6528d5b09"
                            return headers
                        }

                    }
                    queue.add(jsonObjectRequest)

                } else {
                    btnLogin.visibility = View.VISIBLE
                    txtForgotPassword.visibility = View.VISIBLE
                    txtSignup.visibility = View.VISIBLE
                    Toast.makeText(this@LoginActivity, "No internet Connection", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                btnLogin.visibility = View.VISIBLE
                txtForgotPassword.visibility = View.VISIBLE
                txtSignup.visibility = View.VISIBLE
                Toast.makeText(this@LoginActivity, "Invalid Phone or Password", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }


    override fun onPause() {
                    super.onPause()
                    finish()
     }
}

//    fun savePreferences(title: String) {
//                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
//                    sharedPreferences.edit().putString("Title", title).apply()
//     }




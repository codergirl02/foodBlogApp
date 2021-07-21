package com.example.androidkickoff.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androidkickoff.R
import com.example.androidkickoff.fragment.HomeFragment
import com.example.androidkickoff.util.ConnectionManager
import com.example.androidkickoff.util.REGISTER
import com.example.androidkickoff.util.SessionManager
import com.example.androidkickoff.util.Validation
import org.json.JSONObject
import java.lang.Exception

internal class RegisterActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var etPassword: EditText
    lateinit var etconPassword: EditText
    lateinit var btnRegister: Button
    lateinit var etAddress: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etName: EditText
    lateinit var progressBar: ProgressBar
    lateinit var rlRegister: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        sharedPreferences = getSharedPreferences("Registration", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_register)


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextAppearance(this, R.style.PoppinsTextAppearance)
        sessionManager = SessionManager(this@RegisterActivity)
        sharedPreferences = this@RegisterActivity.getSharedPreferences(sessionManager.PREF_NAME, sessionManager.PRIVATE_MODE)
        rlRegister = findViewById(R.id.rlRegister)

        etAddress = findViewById(R.id.etAddress)
        etMobileNumber = findViewById(R.id.etMobile)
        etPassword = findViewById(R.id.etPass)
        etName = findViewById(R.id.etUserName)
        etEmail = findViewById(R.id.etEmail)
        etconPassword = findViewById(R.id.etConformPass)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        rlRegister.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE


        btnRegister.setOnClickListener {

//            val userName = etName.text.toString()
//            val password = etPassword.text.toString()
//            val conPassword = etconPassword.text.toString()
////            val email = etEmail.text.toString()
//            val mobNum = etMobileNumber.text.toString()

//            val intent = Intent(this@RegisterActivity, HomeFragment::class.java)
//            startActivity(intent)

//            if ((userName == validName) && (password == conPassword) && (mobNum == validMobNumber))
//            {



            rlRegister.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            if (Validation.validateNameLength(etName.text.toString())) {
                etName.error = null
                if (Validation.validateEmail(etEmail.text.toString())) {
                    etEmail.error = null
                    if (Validation.validateMobile(etMobileNumber.text.toString())) {
                        etMobileNumber.error = null
                        if (Validation.validatePasswordLength(etPassword.text.toString())) {
                            etPassword.error = null
                            if (Validation.matchPassword(
                                            etPassword.text.toString(),
                                            etconPassword.text.toString()
                                    )
                            ) {
                                etPassword.error = null
                                etconPassword.error = null
                                if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {
                                    sendRegisterRequest(
                                            etName.text.toString(),
                                            etMobileNumber.text.toString(),
                                            etAddress.text.toString(),
                                            etPassword.text.toString(),
                                            etEmail.text.toString()
                                    )
                                } else {
                                    rlRegister.visibility = View.VISIBLE
                                    progressBar.visibility = View.INVISIBLE
                                    Toast.makeText(this@RegisterActivity, "No Internet Connection", Toast.LENGTH_SHORT)
                                            .show()
                                }
                            } else {
                                rlRegister.visibility = View.VISIBLE
                                progressBar.visibility = View.INVISIBLE
                                etPassword.error = "Passwords don't match"
                                etconPassword.error = "Passwords don't match"
                                Toast.makeText(this@RegisterActivity, "Passwords don't match", Toast.LENGTH_SHORT)
                                        .show()
                            }
                        } else {
                            rlRegister.visibility = View.VISIBLE
                            progressBar.visibility = View.INVISIBLE
                            etPassword.error = "Password should be more than or equal 4 digits"
                            Toast.makeText(
                                    this@RegisterActivity,
                                    "Password should be more than or equal 4 digits",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        rlRegister.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                        etMobileNumber.error = "Invalid Mobile number"
                        Toast.makeText(this@RegisterActivity, "Invalid Mobile number", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    rlRegister.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    etEmail.error = "Invalid Email"
                    Toast.makeText(this@RegisterActivity, "Invalid Email", Toast.LENGTH_SHORT).show()
                }
            } else {
                rlRegister.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                etName.error = "Invalid Name"
                Toast.makeText(this@RegisterActivity, "Invalid Name", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendRegisterRequest(name: String, phone: String, address: String, password: String, email: String) {

        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("name", name)
        jsonParams.put("mobile_number", phone)
        jsonParams.put("password", password)
        jsonParams.put("address", address)
        jsonParams.put("email", email)

        val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                REGISTER,
                jsonParams,
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
                                            this@RegisterActivity,
                                            MainActivity::class.java
                                    )
                            )
                            finish()
                        } else {
                            rlRegister.visibility = View.VISIBLE
                            progressBar.visibility = View.INVISIBLE
                            val errorMessage = data.getString("errorMessage")
                            Toast.makeText(
                                    this@RegisterActivity,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception){
                        rlRegister.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_SHORT).show()
                    rlRegister.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "de92a6528d5b09"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    override fun onSupportNavigateUp(): Boolean {
        Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
        onBackPressed()
        return true
    }

//        }
//    }
//    override fun onPause() {
//        super.onPause()
//        finish()
//    }
}
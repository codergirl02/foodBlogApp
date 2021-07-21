package com.example.androidkickoff.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androidkickoff.R
import com.example.androidkickoff.util.ConnectionManager
import com.example.androidkickoff.util.FORGOT_PASSWORD
import com.example.androidkickoff.util.Validation
import org.json.JSONObject

internal class forgotActivity : AppCompatActivity() {

    lateinit var etForgotMobile : EditText
    lateinit var etForgotEmail: EditText
    lateinit var progressBar: ProgressBar
    lateinit var rlContentMain: RelativeLayout
    lateinit var btnNext : Button

//    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        sharedPreferences = getSharedPreferences("Forgot Password", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_forgot)

        etForgotMobile = findViewById(R.id.etPhone)
        etForgotEmail = findViewById(R.id.etEmail)
        rlContentMain = findViewById(R.id.rlContentMain)
        progressBar = findViewById(R.id.progressBar)
        rlContentMain.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener{

//            val intent = Intent(this@forgotActivity, LoginActivity::class.java)
//            startActivity(intent)

            val forgotMobileNumber = etForgotMobile.text.toString()
            if (Validation.validateMobile(forgotMobileNumber)) {
                etForgotMobile.error = null
                if (Validation.validateEmail(etForgotEmail.text.toString())) {
                    if (ConnectionManager().checkConnectivity(this@forgotActivity)) {
                        rlContentMain.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                        sendOTP(etForgotMobile.text.toString(), etForgotEmail.text.toString())
                    } else {
                        rlContentMain.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                                this@forgotActivity,
                                "No Internet Connection!",
                                Toast.LENGTH_SHORT
                        )
                                .show()
                    }
                } else {
                    rlContentMain.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    etForgotEmail.error = "Invalid Email"
                }
            } else {
                rlContentMain.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                etForgotMobile.error = "Invalid Mobile Number"
            }
        }
    }

    private fun sendOTP(mobileNumber: String, email: String) {
        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("email", email)

        val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST, FORGOT_PASSWORD, jsonParams, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val firstTry = data.getBoolean("first_try")
                            if (firstTry) {
                                val builder = AlertDialog.Builder(this@forgotActivity)
                                builder.setTitle("Information")
                                builder.setMessage("Please check your registered Email for the OTP.")
                                builder.setCancelable(false)
                                builder.setPositiveButton("Ok") { _, _ ->
                                    val intent = Intent(
                                            this@forgotActivity,
                                            ResetActivityPass::class.java
                                    )
                                    intent.putExtra("user_mobile", mobileNumber)
                                    startActivity(intent)
                                }
                                builder.create().show()
                            } else {
                                val builder = AlertDialog.Builder(this@forgotActivity)
                                builder.setTitle("Information")
                                builder.setMessage("Please refer to the previous email for the OTP.")
                                builder.setCancelable(false)
                                builder.setPositiveButton("Ok") { _, _ ->
                                    val intent = Intent(
                                            this@forgotActivity,
                                            ResetActivityPass::class.java
                                    )
                                    intent.putExtra("user_mobile", mobileNumber)
                                    startActivity(intent)
                                }
                                builder.create().show()
                            }
                        } else {
                            rlContentMain.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                    this@forgotActivity,
                                    "Mobile number not registered!",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        rlContentMain.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                                this@forgotActivity,
                                "Incorrect response error!!",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    rlContentMain.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    VolleyLog.e("Error::::", "/post request fail! Error: ${it.message}")
                    Toast.makeText(this@forgotActivity, it.message, Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "de92a6528d5b09"
                        return headers
                    }
                }
        queue.add(jsonObjectRequest)

    }
}
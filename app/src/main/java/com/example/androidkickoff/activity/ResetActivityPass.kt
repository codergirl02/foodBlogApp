package com.example.androidkickoff.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androidkickoff.R
import com.example.androidkickoff.util.ConnectionManager
import com.example.androidkickoff.util.RESET_PASSWORD
import com.example.androidkickoff.util.Validation
import org.json.JSONObject

class ResetActivityPass : AppCompatActivity() {


    private lateinit var etOTP: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var btnSubmitOTP: Button
    private lateinit var rlOTP: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var mobileNumber: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)


            etOTP = findViewById(R.id.etOTP)
            etNewPassword = findViewById(R.id.etNewPassword)
            etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
            btnSubmitOTP = findViewById(R.id.btnSubmitOTP)
            rlOTP = findViewById(R.id.rlOTP)
            progressBar = findViewById(R.id.progressBar)

            rlOTP.visibility = View.VISIBLE
            progressBar.visibility = View.GONE

            if (intent != null) {
                mobileNumber = intent.getStringExtra("user_mobile") as String
            }

            btnSubmitOTP.setOnClickListener {
                rlOTP.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                if (ConnectionManager().checkConnectivity(this@ResetActivityPass)) {
                    if (etOTP.text.length == 4) {
                        if (Validation.validatePasswordLength(etNewPassword.text.toString())) {
                            if (Validation.matchPassword(
                                            etNewPassword.text.toString(),
                                            etConfirmNewPassword.text.toString()
                                    )
                            ) {
                                resetPassword(
                                        mobileNumber,
                                        etOTP.text.toString(),
                                        etNewPassword.text.toString()
                                )
                            } else {
                                rlOTP.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                Toast.makeText(
                                        this@ResetActivityPass,
                                        "Passwords do not match",
                                        Toast.LENGTH_SHORT
                                )
                                        .show()
                            }
                        } else {
                            rlOTP.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                    this@ResetActivityPass,
                                    "Invalid Password",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        rlOTP.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@ResetActivityPass, "Incorrect OTP", Toast.LENGTH_SHORT)
                                .show()
                    }
                } else {
                    rlOTP.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                            this@ResetActivityPass,
                            "No Internet Connection!",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        private fun resetPassword(mobileNumber: String, otp: String, password: String) {
            val queue = Volley.newRequestQueue(this)

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobileNumber)
            jsonParams.put("password", password)
            jsonParams.put("otp", otp)

            val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, RESET_PASSWORD, jsonParams, Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                progressBar.visibility = View.INVISIBLE
                                val builder = AlertDialog.Builder(this@ResetActivityPass)
                                builder.setTitle("Confirmation")
                                builder.setMessage("Your password has been successfully changed")
                                builder.setIcon(R.drawable.ic_action_success)
                                builder.setCancelable(false)
                                builder.setPositiveButton("Ok") { _, _ ->
                                    startActivity(
                                            Intent(
                                                    this@ResetActivityPass,
                                                    LoginActivity::class.java
                                            )
                                    )
                                    ActivityCompat.finishAffinity(this@ResetActivityPass)
                                }
                                builder.create().show()
                            } else {
                                rlOTP.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                val error = data.getString("errorMessage")
                                Toast.makeText(
                                        this@ResetActivityPass,
                                        error,
                                        Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            rlOTP.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                    this@ResetActivityPass,
                                    "Incorrect Response!!",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        rlOTP.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        VolleyLog.e("Error::::", "/post request fail! Error: ${it.message}")
                        Toast.makeText(this@ResetActivityPass, it.message, Toast.LENGTH_SHORT).show()
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"

                            /*The below used token will not work, kindly use the token provided to you in the training*/
                            headers["token"] = "de92a6528d5b09"
                            return headers
                        }
                    }
            queue.add(jsonObjectRequest)
        }
    }
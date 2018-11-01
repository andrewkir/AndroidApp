package com.andrewkir.andrewforwork.timem8.Services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

object WebData {

    val BASE_URL = "http://andrewkir.ru/api/schedule"

    fun AddSchedule(context: Context, id: String, text: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("text", text)
        val requestBody = jsonBody.toString()
        val addRequest =object : StringRequest(Request.Method.POST, "$BASE_URL/$id", Response.Listener { response ->
            try {
                Log.d("SUCCESSFUL", "$response")
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC:" + e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not send add req")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        Volley.newRequestQueue(context).add(addRequest)
    }
    fun getSchedule(context: Context, id: String, complete: (Boolean) -> Unit) {
        val stringRequest = StringRequest(Request.Method.GET, "$BASE_URL/$id",
                Response.Listener<String> { response ->
                    var resp = response
                    App.prefs.data = resp.trim()
                    complete(true)
                },
                Response.ErrorListener {
                    complete(false)
                })
        Volley.newRequestQueue(context).add(stringRequest)
    }

}
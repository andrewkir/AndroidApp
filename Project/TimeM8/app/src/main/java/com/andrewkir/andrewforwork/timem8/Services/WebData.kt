package com.andrewkir.andrewforwork.timem8.Services

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

object WebData {

    const val BASE_URL = "http://andrewkir.ru/api/schedule"


    fun addSchedule(context: Context, id: String, text: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("text", text)
        val requestBody = jsonBody.toString()
        val addRequest =object : StringRequest(Request.Method.POST, "$BASE_URL/$id", Response.Listener {
            try {
                complete(true)
            } catch (e: JSONException) {
                complete(false)
            }
        }, Response.ErrorListener {
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
                    App.prefs.data = response.trim()
                    complete(true)
                },
                Response.ErrorListener {
                    complete(false)
                })
        Volley.newRequestQueue(context).add(stringRequest)
    }

}
package com.example.taller3_sophiemejia_estebanblanco.util

import android.content.Context
import com.example.taller3_sophiemejia_estebanblanco.R
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.io.InputStream

data class PointOfInterest(
    val name: String,
    val location: LatLng
)

fun readJsonPos(context: Context): List<PointOfInterest> {
    val pos = mutableListOf<PointOfInterest>()
    try {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.locations)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val jsonObject = JSONObject(jsonString)

        val jsonArray = jsonObject.getJSONArray("locationsArray")

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val name = item.getString("name")
            val lat = item.getDouble("latitude")
            val lng = item.getDouble("longitude")

            pos.add(PointOfInterest(name, LatLng(lat, lng)))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return pos
}
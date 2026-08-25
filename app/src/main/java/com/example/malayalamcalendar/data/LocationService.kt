package com.example.malayalamcalendar.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone

data class LocationProfile(
    val id: String,
    val name: String,
    val nameMl: String,
    val latitude: Double,
    val longitude: Double,
    val tzOffsetHours: Double = 5.5,
    val isGps: Boolean = false
) {
    val displayTitle: String get() = "$nameMl ($name)"
    val coordinatesText: String get() = String.format(Locale.US, "%.4f° N, %.4f° E", latitude, longitude)
}

class LocationService(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("panchangam_location_prefs", Context.MODE_PRIVATE)

    companion object {
        val PRESET_LOCATIONS = listOf(
            LocationProfile("kerala_kochi", "Kochi / Ernakulam", "കൊച്ചി / എറണാകുളം", 9.9312, 76.2673, 5.5),
            LocationProfile("kerala_tvm", "Thiruvananthapuram", "തിരുവനന്തപുരം", 8.5241, 76.9366, 5.5),
            LocationProfile("kerala_clt", "Kozhikode", "കോഴിക്കോട്", 11.2588, 75.7804, 5.5),
            LocationProfile("kerala_tsr", "Thrissur", "തൃശ്ശൂർ", 10.5276, 76.2144, 5.5),
            LocationProfile("kerala_pkd", "Palakkad", "പാലക്കാട്", 10.7867, 76.6548, 5.5),
            LocationProfile("kerala_knr", "Kannur", "കണ്ണൂർ", 11.8745, 75.3704, 5.5),
            LocationProfile("kerala_klm", "Kollam", "കൊല്ലം", 8.8932, 76.6141, 5.5),
            LocationProfile("kerala_ktm", "Kottayam", "കോട്ടയം", 9.5916, 76.5222, 5.5),
            LocationProfile("kerala_mpm", "Malappuram", "മലപ്പുറം", 11.0510, 76.0711, 5.5),
            LocationProfile("kerala_alp", "Alappuzha", "ആലപ്പുഴ", 9.4981, 76.3388, 5.5),
            LocationProfile("kerala_ksd", "Kasaragod", "കാസർഗോഡ്", 12.4996, 74.9869, 5.5),
            LocationProfile("kerala_wyd", "Wayanad (Kalpetta)", "വയനാട് (കൽപ്പറ്റ)", 11.6854, 76.1320, 5.5),
            LocationProfile("kerala_idk", "Idukki (Painavu)", "ഇടുക്കി (പൈനാവ്)", 9.8494, 76.9804, 5.5),
            LocationProfile("kerala_pta", "Pathanamthitta", "പത്തനംതിട്ട", 9.2648, 76.7870, 5.5),
            LocationProfile("india_blr", "Bengaluru", "ബംഗളൂരു", 12.9716, 77.5946, 5.5),
            LocationProfile("india_maa", "Chennai", "ചെന്നൈ", 13.0827, 80.2707, 5.5),
            LocationProfile("india_bom", "Mumbai", "മുംബൈ", 19.0760, 72.8777, 5.5),
            LocationProfile("india_del", "New Delhi", "ന്യൂഡൽഹി", 28.6139, 77.2090, 5.5),
            LocationProfile("gulf_dxb", "Dubai (UAE)", "ദുബായ് (UAE)", 25.2048, 55.2708, 4.0),
            LocationProfile("gulf_auh", "Abu Dhabi (UAE)", "അബുദാബി (UAE)", 24.4539, 54.3773, 4.0),
            LocationProfile("gulf_doh", "Doha (Qatar)", "ദോഹ (ഖത്തർ)", 25.2854, 51.5310, 3.0),
            LocationProfile("gulf_ruh", "Riyadh (Saudi Arabia)", "റിയാദ് (സൗദി)", 24.7136, 46.6753, 3.0),
            LocationProfile("gulf_jed", "Jeddah (Saudi Arabia)", "ജിദ്ദ (സൗദി)", 21.5433, 39.1728, 3.0),
            LocationProfile("gulf_kwi", "Kuwait City", "കുവൈറ്റ് സിറ്റി", 29.3759, 47.9774, 3.0),
            LocationProfile("gulf_mct", "Muscat (Oman)", "മസ്കറ്റ് (ഒമാൻ)", 23.5859, 58.4059, 4.0),
            LocationProfile("gulf_bah", "Manama (Bahrain)", "മനാമ (ബഹ്റൈൻ)", 26.2285, 50.5860, 3.0),
            LocationProfile("world_sgp", "Singapore", "സിംഗപ്പൂർ", 1.3521, 103.8198, 8.0),
            LocationProfile("world_lon", "London (UK)", "ലണ്ടൻ (UK)", 51.5074, -0.1278, 1.0),
            LocationProfile("world_nyc", "New York (USA)", "ന്യൂയോർക്ക് (USA)", 40.7128, -74.0060, -4.0)
        )

        val DEFAULT_LOCATION = PRESET_LOCATIONS[0] // Kochi
    }

    fun getSavedLocation(): LocationProfile {
        val id = prefs.getString("loc_id", DEFAULT_LOCATION.id) ?: DEFAULT_LOCATION.id
        val name = prefs.getString("loc_name", DEFAULT_LOCATION.name) ?: DEFAULT_LOCATION.name
        val nameMl = prefs.getString("loc_name_ml", DEFAULT_LOCATION.nameMl) ?: DEFAULT_LOCATION.nameMl
        val lat = prefs.getString("loc_lat", DEFAULT_LOCATION.latitude.toString())?.toDoubleOrNull() ?: DEFAULT_LOCATION.latitude
        val lon = prefs.getString("loc_lon", DEFAULT_LOCATION.longitude.toString())?.toDoubleOrNull() ?: DEFAULT_LOCATION.longitude
        val tz = prefs.getString("loc_tz", DEFAULT_LOCATION.tzOffsetHours.toString())?.toDoubleOrNull() ?: DEFAULT_LOCATION.tzOffsetHours
        val isGps = prefs.getBoolean("loc_is_gps", false)
        return LocationProfile(id, name, nameMl, lat, lon, tz, isGps)
    }

    fun saveLocation(profile: LocationProfile) {
        prefs.edit()
            .putString("loc_id", profile.id)
            .putString("loc_name", profile.name)
            .putString("loc_name_ml", profile.nameMl)
            .putString("loc_lat", profile.latitude.toString())
            .putString("loc_lon", profile.longitude.toString())
            .putString("loc_tz", profile.tzOffsetHours.toString())
            .putBoolean("loc_is_gps", profile.isGps)
            .apply()
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentGpsLocation(): LocationProfile? = withContext(Dispatchers.IO) {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                ?: return@withContext null

            var bestLocation: Location? = null
            val providers = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER,
                LocationManager.PASSIVE_PROVIDER
            )

            for (provider in providers) {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null) {
                        if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                            bestLocation = loc
                        }
                    }
                }
            }

            if (bestLocation == null) return@withContext null

            val lat = bestLocation.latitude
            val lon = bestLocation.longitude
            val tzOffset = (TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings) / 3600000.0

            var detectedName = "നിലവിലെ GPS ലൊക്കേഷൻ"
            var detectedNameEn = "Current GPS Location"

            try {
                if (Geocoder.isPresent()) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        val locality = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                        val country = addr.countryName
                        if (locality != null) {
                            detectedNameEn = if (country != null) "$locality, $country" else locality
                            detectedName = "GPS: $locality"
                        }
                    }
                }
            } catch (_: Exception) {}

            LocationProfile(
                id = "gps_current",
                name = detectedNameEn,
                nameMl = detectedName,
                latitude = lat,
                longitude = lon,
                tzOffsetHours = tzOffset,
                isGps = true
            )
        } catch (e: Exception) {
            null
        }
    }
}

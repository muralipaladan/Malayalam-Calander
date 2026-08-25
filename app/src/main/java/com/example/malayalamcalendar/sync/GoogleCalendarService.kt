package com.example.malayalamcalendar.sync

import android.accounts.AccountManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.provider.CalendarContract
import android.util.Log
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

data class GoogleCalendarEvent(
    val id: String,
    val summary: String,
    val description: String,
    val startDateTimeIso: String,
    val endDateTimeIso: String,
    val year: Int,
    val month: Int, // 0-based
    val day: Int,
    val timeFormatted: String,
    val startTime: String = timeFormatted,
    val endTime: String = ""
)

data class DeviceAccountInfo(
    val email: String,
    val displayName: String? = null,
    val isDefault: Boolean = false
)

class GoogleCalendarService(private val context: Context) {

    companion object {
        private const val TAG = "GoogleCalendarService"
        private const val PREFS_NAME = "malayalam_calendar_sync_prefs"
        private const val KEY_SAVED_EMAIL = "saved_google_email"
        private const val KEY_SAVED_NAME = "saved_google_name"
        const val CALENDAR_EVENTS_SCOPE = "https://www.googleapis.com/auth/calendar.events"
        const val CALENDAR_READONLY_SCOPE = "https://www.googleapis.com/auth/calendar.readonly"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestScopes(Scope(CALENDAR_EVENTS_SCOPE), Scope(CALENDAR_READONLY_SCOPE))
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun getSignedInAccount(): GoogleSignInAccount? {
        return try {
            GoogleSignIn.getLastSignedInAccount(context)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting signed in account", e)
            null
        }
    }

    /**
     * Finds Google accounts present strictly on this device.
     */
    fun getDeviceGoogleAccounts(): List<DeviceAccountInfo> {
        val result = mutableListOf<DeviceAccountInfo>()

        // 1. Query device's Google Calendar accounts via CalendarContract
        try {
            val uri = CalendarContract.Calendars.CONTENT_URI
            val projection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.IS_PRIMARY
            )
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val accNameIdx = cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)
                val accTypeIdx = cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE)
                val dispNameIdx = cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                val isPrimIdx = cursor.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY)

                while (cursor.moveToNext()) {
                    val accName = if (accNameIdx >= 0) cursor.getString(accNameIdx) else null
                    val accType = if (accTypeIdx >= 0) cursor.getString(accTypeIdx) else null
                    val dispName = if (dispNameIdx >= 0) cursor.getString(dispNameIdx) else null
                    val isPrimary = if (isPrimIdx >= 0) cursor.getInt(isPrimIdx) == 1 else false

                    if (!accName.isNullOrBlank() && (accType == "com.google" || accName.contains("@gmail.com") || accType?.contains("google", ignoreCase = true) == true)) {
                        if (result.none { it.email.equals(accName, ignoreCase = true) }) {
                            result.add(
                                DeviceAccountInfo(
                                    email = accName,
                                    displayName = dispName ?: accName.substringBefore("@"),
                                    isDefault = isPrimary || result.isEmpty()
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "CalendarContract lookup: ${e.message}")
        }

        // 2. Query AccountManager for Google accounts registered on this phone
        try {
            val accountManager = AccountManager.get(context)
            val accounts = accountManager.getAccountsByType("com.google")
            for (acc in accounts) {
                if (result.none { it.email.equals(acc.name, ignoreCase = true) }) {
                    result.add(
                        DeviceAccountInfo(
                            email = acc.name,
                            displayName = acc.name.substringBefore("@"),
                            isDefault = result.isEmpty()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "AccountManager accounts lookup: ${e.message}")
        }

        // 3. Check GoogleSignIn last account on phone
        val signedIn = getSignedInAccount()
        if (signedIn != null && !signedIn.email.isNullOrBlank()) {
            if (result.none { it.email.equals(signedIn.email, ignoreCase = true) }) {
                result.add(
                    DeviceAccountInfo(
                        email = signedIn.email!!,
                        displayName = signedIn.displayName,
                        isDefault = result.isEmpty()
                    )
                )
            }
        }

        // 4. Check locally saved preference account
        val savedEmail = getSavedEmail()
        if (!savedEmail.isNullOrBlank() && result.none { it.email.equals(savedEmail, ignoreCase = true) }) {
            result.add(
                DeviceAccountInfo(
                    email = savedEmail,
                    displayName = getSavedName() ?: savedEmail.substringBefore("@"),
                    isDefault = result.isEmpty()
                )
            )
        }

        return result
    }

    fun saveAccount(email: String, name: String?) {
        prefs.edit()
            .putString(KEY_SAVED_EMAIL, email.trim())
            .putString(KEY_SAVED_NAME, name?.trim())
            .apply()
    }

    fun getSavedEmail(): String? = prefs.getString(KEY_SAVED_EMAIL, null)
    fun getSavedName(): String? = prefs.getString(KEY_SAVED_NAME, null)

    fun clearSavedAccount() {
        prefs.edit().clear().apply()
    }

    suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        val account = getSignedInAccount() ?: return@withContext null
        val androidAccount = account.account ?: return@withContext null
        try {
            val scopeString = "oauth2:$CALENDAR_EVENTS_SCOPE $CALENDAR_READONLY_SCOPE"
            GoogleAuthUtil.getToken(context, androidAccount, scopeString)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Google OAuth access token", e)
            null
        }
    }

    /**
     * Reads calendar events directly from the phone's native Google Calendar via CalendarContract
     */
    fun fetchDeviceCalendarEvents(year: Int, month0Based: Int, targetAccountEmail: String?): List<GoogleCalendarEvent> {
        val events = mutableListOf<GoogleCalendarEvent>()
        try {
            val calStart = Calendar.getInstance().apply {
                set(year, month0Based, 1, 0, 0, 0)
                add(Calendar.DAY_OF_MONTH, -3)
            }
            val calEnd = Calendar.getInstance().apply {
                set(year, month0Based, 1, 23, 59, 59)
                val maxDay = getActualMaximum(Calendar.DAY_OF_MONTH)
                set(Calendar.DAY_OF_MONTH, maxDay)
                add(Calendar.DAY_OF_MONTH, 4)
            }

            val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
            ContentUris.appendId(builder, calStart.timeInMillis)
            ContentUris.appendId(builder, calEnd.timeInMillis)

            val projection = arrayOf(
                CalendarContract.Instances.EVENT_ID,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.DESCRIPTION,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.ALL_DAY
            )

            context.contentResolver.query(builder.build(), projection, null, null, "${CalendarContract.Instances.BEGIN} ASC")?.use { cursor ->
                val idIdx = cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID)
                val titleIdx = cursor.getColumnIndex(CalendarContract.Instances.TITLE)
                val descIdx = cursor.getColumnIndex(CalendarContract.Instances.DESCRIPTION)
                val beginIdx = cursor.getColumnIndex(CalendarContract.Instances.BEGIN)
                val endIdx = cursor.getColumnIndex(CalendarContract.Instances.END)
                val allDayIdx = cursor.getColumnIndex(CalendarContract.Instances.ALL_DAY)

                while (cursor.moveToNext()) {
                    val id = if (idIdx >= 0) cursor.getString(idIdx) ?: "" else ""
                    val title = if (titleIdx >= 0) cursor.getString(titleIdx) ?: "(No Title)" else "(No Title)"
                    val desc = if (descIdx >= 0) cursor.getString(descIdx) ?: "" else ""
                    val begin = if (beginIdx >= 0) cursor.getLong(beginIdx) else 0L
                    val end = if (endIdx >= 0) cursor.getLong(endIdx) else 0L
                    val isAllDay = if (allDayIdx >= 0) cursor.getInt(allDayIdx) == 1 else false

                    val eventCal = Calendar.getInstance().apply { timeInMillis = begin }
                    val evYear = eventCal.get(Calendar.YEAR)
                    val evMonth = eventCal.get(Calendar.MONTH)
                    val evDay = eventCal.get(Calendar.DAY_OF_MONTH)

                    val timeFmt = if (isAllDay) {
                        "All Day"
                    } else {
                        SimpleDateFormat("hh:mm a", Locale.US).format(Date(begin))
                    }

                    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }

                    events.add(
                        GoogleCalendarEvent(
                            id = id.ifBlank { "dev_${evYear}_${evMonth}_${evDay}_${title.hashCode()}" },
                            summary = title,
                            description = desc,
                            startDateTimeIso = isoFormat.format(Date(begin)),
                            endDateTimeIso = isoFormat.format(Date(end)),
                            year = evYear,
                            month = evMonth,
                            day = evDay,
                            timeFormatted = timeFmt
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading device calendar events", e)
        }
        return events
    }

    /**
     * Inserts an event directly into the phone's native Google Calendar
     */
    fun insertEventIntoDeviceGoogleCalendar(
        title: String,
        description: String,
        year: Int,
        month0Based: Int,
        day: Int,
        timeStr: String,
        targetEmail: String?
    ): Long? {
        try {
            var targetCalId: Long? = null
            val calUri = CalendarContract.Calendars.CONTENT_URI
            val calProjection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.Calendars.IS_PRIMARY
            )
            context.contentResolver.query(calUri, calProjection, null, null, null)?.use { cursor ->
                val idIdx = cursor.getColumnIndex(CalendarContract.Calendars._ID)
                val nameIdx = cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)
                val typeIdx = cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIdx)
                    val name = cursor.getString(nameIdx)
                    val type = cursor.getString(typeIdx)

                    if (!targetEmail.isNullOrBlank() && name.equals(targetEmail, ignoreCase = true)) {
                        targetCalId = id
                        break
                    } else if (targetCalId == null && (type == "com.google" || name.contains("@gmail.com"))) {
                        targetCalId = id
                    }
                }
            }

            if (targetCalId == null) {
                targetCalId = 1L
            }

            val calStart = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month0Based)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            var isAllDay = true
            if (timeStr.isNotBlank()) {
                val parsed = parseTime(timeStr)
                if (parsed != null) {
                    calStart.set(Calendar.HOUR_OF_DAY, parsed.first)
                    calStart.set(Calendar.MINUTE, parsed.second)
                    isAllDay = false
                }
            }

            val calEnd = (calStart.clone() as Calendar).apply {
                if (isAllDay) {
                    add(Calendar.DAY_OF_MONTH, 1)
                } else {
                    add(Calendar.HOUR_OF_DAY, 1)
                }
            }

            val values = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID, targetCalId)
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.DTSTART, calStart.timeInMillis)
                put(CalendarContract.Events.DTEND, calEnd.timeInMillis)
                put(CalendarContract.Events.ALL_DAY, if (isAllDay) 1 else 0)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }

            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            if (uri != null) {
                return ContentUris.parseId(uri)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting event into device calendar", e)
        }
        return null
    }

    suspend fun fetchUpcomingEvents(year: Int, month0Based: Int): List<GoogleCalendarEvent> = withContext(Dispatchers.IO) {
        val token = getAccessToken() ?: return@withContext emptyList()
        val events = mutableListOf<GoogleCalendarEvent>()

        try {
            // Fetch for month range with 5 days padding
            val calStart = Calendar.getInstance().apply {
                set(year, month0Based, 1, 0, 0, 0)
                add(Calendar.DAY_OF_MONTH, -3)
            }
            val calEnd = Calendar.getInstance().apply {
                set(year, month0Based, 1, 23, 59, 59)
                val maxDay = getActualMaximum(Calendar.DAY_OF_MONTH)
                set(Calendar.DAY_OF_MONTH, maxDay)
                add(Calendar.DAY_OF_MONTH, 4)
            }

            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val timeMin = isoFormat.format(calStart.time)
            val timeMax = isoFormat.format(calEnd.time)

            val url = "https://www.googleapis.com/calendar/v3/calendars/primary/events" +
                    "?timeMin=$timeMin&timeMax=$timeMax&singleEvents=true&orderBy=startTime&maxResults=100"

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val items = json.optJSONArray("items") ?: JSONArray()

                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val id = item.optString("id")
                    val summary = item.optString("summary", "(No Title)")
                    val description = item.optString("description", "")
                    val startObj = item.optJSONObject("start")
                    val endObj = item.optJSONObject("end")

                    val startDateTimeStr = startObj?.optString("dateTime", startObj.optString("date", "")) ?: ""
                    val endDateTimeStr = endObj?.optString("dateTime", endObj.optString("date", "")) ?: ""

                    val parsedDate = parseIsoDate(startDateTimeStr)
                    if (parsedDate != null) {
                        val parsedCal = Calendar.getInstance().apply { time = parsedDate }
                        val evYear = parsedCal.get(Calendar.YEAR)
                        val evMonth = parsedCal.get(Calendar.MONTH)
                        val evDay = parsedCal.get(Calendar.DAY_OF_MONTH)

                        val timeFmt = if (startDateTimeStr.contains("T")) {
                            SimpleDateFormat("hh:mm a", Locale.US).format(parsedDate)
                        } else {
                            "All Day"
                        }

                        events.add(
                            GoogleCalendarEvent(
                                id = id,
                                summary = summary,
                                description = description,
                                startDateTimeIso = startDateTimeStr,
                                endDateTimeIso = endDateTimeStr,
                                year = evYear,
                                month = evMonth,
                                day = evDay,
                                timeFormatted = timeFmt
                            )
                        )
                    }
                }
            } else {
                Log.e(TAG, "Failed to fetch calendar events: ${response.code} $responseBody")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception fetching calendar events", e)
        }

        events
    }

    suspend fun createCalendarEvent(
        title: String,
        description: String,
        year: Int,
        month0Based: Int,
        day: Int,
        timeStr: String
    ): String? = withContext(Dispatchers.IO) {
        val token = getAccessToken() ?: return@withContext null

        try {
            val calStart = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month0Based)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            var isAllDay = true
            if (timeStr.isNotBlank()) {
                val parsed = parseTime(timeStr)
                if (parsed != null) {
                    calStart.set(Calendar.HOUR_OF_DAY, parsed.first)
                    calStart.set(Calendar.MINUTE, parsed.second)
                    isAllDay = false
                }
            }

            val calEnd = (calStart.clone() as Calendar).apply {
                if (isAllDay) {
                    add(Calendar.DAY_OF_MONTH, 1)
                } else {
                    add(Calendar.HOUR_OF_DAY, 1)
                }
            }

            val isoDateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
            val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            val jsonBody = JSONObject().apply {
                put("summary", title)
                put("description", description)
                if (isAllDay) {
                    put("start", JSONObject().put("date", isoDateFormat.format(calStart.time)))
                    put("end", JSONObject().put("date", isoDateFormat.format(calEnd.time)))
                } else {
                    put("start", JSONObject().put("dateTime", isoDateTimeFormat.format(calStart.time)))
                    put("end", JSONObject().put("dateTime", isoDateTimeFormat.format(calEnd.time)))
                }
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("https://www.googleapis.com/calendar/v3/calendars/primary/events")
                .addHeader("Authorization", "Bearer $token")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val resJson = JSONObject(responseBody)
                return@withContext resJson.optString("id")
            } else {
                Log.e(TAG, "Failed to create Google Calendar event: ${response.code} $responseBody")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception creating calendar event", e)
        }
        null
    }

    suspend fun syncLocalRemindersToGoogleCalendar(
        reminders: List<com.example.malayalamcalendar.data.local.ReminderEntity>,
        targetEmail: String? = null
    ): Int = withContext(Dispatchers.IO) {
        var successCount = 0
        for (rem in reminders) {
            val title = if (rem.subject.isNotBlank()) rem.subject else rem.text
            val desc = "വിവരണം: ${rem.text}\nവിഭാഗം: ${rem.category}\nപ്രാധാന്യം: ${rem.priority}"

            // 1. Insert into device native Google calendar
            val deviceEventId = insertEventIntoDeviceGoogleCalendar(
                title = title,
                description = desc,
                year = rem.year,
                month0Based = rem.month,
                day = rem.day,
                timeStr = rem.time,
                targetEmail = targetEmail
            )

            // 2. Also push via API if online session is active
            val apiEventId = createCalendarEvent(
                title = title,
                description = desc,
                year = rem.year,
                month0Based = rem.month,
                day = rem.day,
                timeStr = rem.time
            )

            if (deviceEventId != null || apiEventId != null) {
                successCount++
            }
        }
        successCount
    }

    private fun parseIsoDate(isoStr: String): Date? {
        if (isoStr.isBlank()) return null
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
        )
        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                val date = sdf.parse(isoStr)
                if (date != null) return date
            } catch (_: Exception) {}
        }
        return null
    }

    private fun parseTime(timeStr: String): Pair<Int, Int>? {
        val formats = listOf("hh:mm a", "h:mm a", "hh:mma", "h:mma", "HH:mm", "H:mm")
        for (f in formats) {
            try {
                val sdf = SimpleDateFormat(f, Locale.US)
                val d = sdf.parse(timeStr.trim())
                if (d != null) {
                    val c = Calendar.getInstance().apply { time = d }
                    return Pair(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
                }
            } catch (_: Exception) {}
        }
        return null
    }
}

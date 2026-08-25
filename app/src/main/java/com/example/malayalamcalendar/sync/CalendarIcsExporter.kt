package com.example.malayalamcalendar.sync

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.example.malayalamcalendar.data.DayPanchangamData
import com.example.malayalamcalendar.data.PanchangamCalculator
import com.example.malayalamcalendar.data.local.ReminderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class IcsExportConfig(
    val year: Int,
    val includePanchangamDaily: Boolean = true,
    val includeFestivals: Boolean = true,
    val includeReminders: Boolean = true
)

data class IcsExportResult(
    val file: File,
    val uri: Uri,
    val totalEventsCount: Int,
    val panchangamCount: Int,
    val festivalCount: Int,
    val remindersCount: Int,
    val fileSizeFormatted: String
)

object CalendarIcsExporter {

    private const val TAG = "CalendarIcsExporter"

    /**
     * Identifies special festival/vratam/holiday name if any for a given date & Panchangam.
     */
    fun getSpecialFestivalForDay(
        year: Int,
        month0Based: Int,
        day: Int,
        panchangam: DayPanchangamData
    ): String? {
        val mlMonth = panchangam.mlMonth
        val mlDate = panchangam.mlDate
        val nak = panchangam.nakshatra
        val tithi = panchangam.tithi
        val paksha = panchangam.paksha
        val isShukla = paksha.contains("ശുക്ല")
        val isKrishna = paksha.contains("കൃഷ്ണ")

        // 1. Fixed Gregorian Holidays
        if (month0Based == 0 && day == 26) return "റിപ്പബ്ലിക് ദിനം (Republic Day)"
        if (month0Based == 4 && day == 1) return "തൊഴിലാളി ദിനം (May Day)"
        if (month0Based == 7 && day == 15) return "സ്വാതന്ത്ര്യദിനം (Independence Day)"
        if (month0Based == 9 && day == 2) return "ഗാന്ധി ജയന്തി (Gandhi Jayanti)"
        if (month0Based == 11 && day == 25) return "ക്രിസ്മസ് (Christmas)"

        // 2. Fixed Malayalam Month Dates
        if (mlMonth == "ചിങ്ങം" && mlDate == 1) return "ചിങ്ങം 1 - കർഷക ദിനം (Kollavarsham New Year)"
        if (mlMonth == "ചിങ്ങം" && mlDate == 28) return "അയ്യങ്കാളി ജയന്തി"
        if (mlMonth == "കന്നി" && mlDate == 5) return "ശ്രീനാരായണ ഗുരു സമാധി"
        if (mlMonth == "വൃശ്ചികം" && mlDate == 1) return "മണ്ഡലകാലാരംഭം (Mandala Vratham Start)"
        if (mlMonth == "ധനു" && mlDate == 1) return "ധനു 1"
        if (mlMonth == "മകരം" && mlDate == 1) return "മകരവിളക്ക് / മകരസംക്രാന്തി (Makaravilakku)"
        if (mlMonth == "കുംഭം" && mlDate == 1) return "കുംഭം 1"
        if (mlMonth == "മീനം" && mlDate == 1) return "മീനം 1"
        if (mlMonth == "മേടം" && mlDate == 1) return "വിഷു (Vishu - Medam 1)"
        if (mlMonth == "ഇടവം" && mlDate == 1) return "ഇടവം 1"
        if (mlMonth == "മിഥുനം" && mlDate == 1) return "മിഥുനം 1"
        if (mlMonth == "കർക്കിടകം" && mlDate == 1) return "കർക്കിടകം 1 - രാമായണ മാസാചരണം (Ramayana Month Start)"

        // 3. Onam festival days (Chingam Atham to Chathayam)
        if (mlMonth == "ചിങ്ങം") {
            if (nak == "അത്തം") return "അത്തം നാൾ (Atham - Onam Start)"
            if (nak == "ചിത്തിര") return "ചിത്തിര നാൾ (Chithira)"
            if (nak == "ചോതി") return "ചോതി നാൾ (Chothi)"
            if (nak == "വിശാഖം") return "വിശാഖം നാൾ (Visakham)"
            if (nak == "അനിഴം") return "അനിഴം നാൾ (Anizham)"
            if (nak == "തൃക്കേട്ട") return "തൃക്കേട്ട നാൾ (Thrikketta)"
            if (nak == "മൂലം") return "മൂലം നാൾ (Moolam)"
            if (nak == "പൂരാടം") return "പൂരാടം നാൾ (Pooradam)"
            if (nak == "ഉത്രാടം") return "ഒന്നാം ഓണം - ഉത്രാടം (First Onam - Uthradam)"
            if (nak == "തിരുവോണം") return "തിരുവോണം (Thiruvonam - Main Onam Day)"
            if (nak == "അവിട്ടം") return "മൂന്നാം ഓണം - അവിട്ടം (3rd Onam - Avittom)"
            if (nak == "ചതയം") return "നാലാം ഓണം - ചതയം / ശ്രീനാരായണ ഗുരു ജയന്തി (Guru Jayanthi)"
        }

        // 4. Other key Kerala festivals based on Malayalam Month + Nakshatra / Tithi
        if (mlMonth == "ചിങ്ങം") {
            if (nak == "രോഹിണി" || (tithi == "അഷ്ടമി" && isKrishna)) return "ശ്രീകൃഷ്ണ ജയന്തി / അഷ്ടമി രോഹിണി (Sri Krishna Jayanti)"
            if (tithi == "ചതുർഥി" && isShukla) return "വിനായക ചതുർഥി (Vinayaka Chaturthi)"
        }

        if (mlMonth == "കന്നി") {
            if (tithi == "അഷ്ടമി" && isShukla) return "ദുർഗ്ഗാഷ്ടമി (Durgashtami / Pooja Vayppu)"
            if (tithi == "നവമി" && isShukla) return "മഹാനവമി / ആയുധപൂജ (Mahanavami)"
            if (tithi == "ദശമി" && isShukla) return "വിജയദശമി - വിദ്യാരംഭം (Vijayadashami / Vidyarambham)"
        }

        if (mlMonth == "തുലാം") {
            if ((tithi == "ചതുർദശി" && isKrishna) || (tithi == "അമാവാസി")) return "ദീപാവലി (Deepavali)"
            if (tithi == "ഷഷ്ഠി" && isShukla) return "സ്കന്ദ ഷഷ്ഠി (Skanda Sashti)"
        }

        if (mlMonth == "വൃശ്ചികം") {
            if (nak == "കാർത്തിക") return "തൃക്കാർത്തിക (Thrikkarthika Deepam)"
            if (tithi == "ഏകാദശി" && isShukla) return "ഗുരുവായൂർ ഏകാദശി (Guruvayur Ekadashi)"
        }

        if (mlMonth == "ധനു") {
            if (nak == "തിരുവാതിര") return "ധനു തിരുവാതിര (Thiruvathira Vratham)"
            if (tithi == "ഏകാദശി" && isShukla) return "വൈകുണ്ഠ ഏകാദശി (Vaikunta Ekadashi)"
        }

        if (mlMonth == "കുംഭം") {
            if (tithi == "ചതുർദശി" && isKrishna) return "മഹാ ശിവരാത്രി (Maha Shivaratri)"
            if (nak == "പൂരം") return "ആറ്റുകാൽ പൊങ്കാല (Attukal Pongala)"
        }

        if (mlMonth == "മീനം") {
            if (nak == "ഉത്രം") return "ആറാട്ടുപുഴ പൂരം (Arattupuzha Pooram)"
            if (nak == "ഭരണി") return "കൊടുങ്ങല്ലൂർ ഭരണി (Kodungallur Bharani)"
        }

        if (mlMonth == "മേടം") {
            if (nak == "പൂരം") return "തൃശ്ശൂർ പൂരം (Thrissur Pooram)"
        }

        if (mlMonth == "കർക്കിടകം") {
            if (tithi == "അമാവാസി") return "കർക്കിടക വാവ് ബലി (Karkidaka Vavu)"
        }

        // 5. General Vedic Vratam Days
        if (tithi == "ഏകാദശി") {
            return if (isShukla) "ശുക്ല ഏകാദശി വ്രതം (Shukla Ekadashi)" else "കൃഷ്ണ ഏകാദശി വ്രതം (Krishna Ekadashi)"
        }
        if (tithi == "ത്രയോദശി") {
            return if (isShukla) "ശുക്ല പ്രദോഷ വ്രതം (Pradosha Vratham)" else "കൃഷ്ണ പ്രദോഷ വ്രതം (Pradosha Vratham)"
        }
        if (tithi == "പൗർണ്ണമി") {
            return "പൗർണ്ണമി വ്രതം (Pournami / Full Moon)"
        }
        if (tithi == "അമാവാസി") {
            return "അമാവാസി (Amavasi / New Moon)"
        }
        if (tithi == "ഷഷ്ഠി" && isShukla) {
            return "ഷഷ്ഠി വ്രതം (Sashti Vratham)"
        }

        return null
    }

    /**
     * Generates a standard RFC 5545 .ics calendar string.
     */
    suspend fun generateIcsString(
        config: IcsExportConfig,
        userReminders: List<ReminderEntity> = emptyList()
    ): String = withContext(Dispatchers.Default) {
        val sb = StringBuilder()
        val utcFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val dtStamp = utcFormat.format(Date())

        sb.appendLine("BEGIN:VCALENDAR")
        sb.appendLine("VERSION:2.0")
        sb.appendLine("PRODID:-//Malayalam Calendar//Panchangam Kerala//ML")
        sb.appendLine("CALSCALE:GREGORIAN")
        sb.appendLine("METHOD:PUBLISH")
        sb.appendLine("X-WR-CALNAME:മലയാള പഞ്ചാംഗം കലണ്ടർ (${config.year})")
        sb.appendLine("X-WR-CALDESC:Malayalam Panchangam Calendar with Nakshathram, Tithi, Festivals, Vratams and Reminders")
        sb.appendLine("X-WR-TIMEZONE:Asia/Kolkata")

        // 1. Generate full year Panchangam & Festival events
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, config.year)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val totalDays = if (isLeapYear(config.year)) 366 else 365

        for (i in 0 until totalDays) {
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val d = cal.get(Calendar.DAY_OF_MONTH)

            val panchangam = PanchangamCalculator.computeDayData(y, m, d)
            val dateStr = String.format(Locale.US, "%04d%02d%02d", y, m + 1, d)

            val nextCal = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
            val nextDateStr = String.format(Locale.US, "%04d%02d%02d", nextCal.get(Calendar.YEAR), nextCal.get(Calendar.MONTH) + 1, nextCal.get(Calendar.DAY_OF_MONTH))

            val festival = getSpecialFestivalForDay(y, m, d, panchangam)

            // A. Special Festival / Vratam Event
            if (config.includeFestivals && festival != null) {
                val festUid = "festival-$y-$m-$d-${festival.hashCode()}@malayalamcalendar"
                sb.appendLine("BEGIN:VEVENT")
                sb.appendLine("UID:$festUid")
                sb.appendLine("DTSTAMP:$dtStamp")
                sb.appendLine("DTSTART;VALUE=DATE:$dateStr")
                sb.appendLine("DTEND;VALUE=DATE:$nextDateStr")
                sb.appendLine("SUMMARY:🌟 $festival")
                sb.appendLine("DESCRIPTION:മലയാള തീയതി: ${panchangam.mlMonth} ${panchangam.mlDate} (കൊ.വ. ${panchangam.kollaVarsham})\\nനക്ഷത്രം: ${panchangam.nakshatra} (${panchangam.nakEnd} വരെ)\\nതിഥി: ${panchangam.tithi} · ${panchangam.paksha}\\nശകവർഷം: ${panchangam.sakaDay} ${panchangam.sakaMonth}\\nഹിജ്റ: ${panchangam.hijriDay} ${panchangam.hijriMonth}")
                sb.appendLine("CATEGORIES:വിശേഷദിവസങ്ങൾ,വ്രതം,Kerala Festivals")
                sb.appendLine("LOCATION:Kerala, India")
                sb.appendLine("STATUS:CONFIRMED")
                sb.appendLine("TRANSP:TRANSPARENT")
                sb.appendLine("END:VEVENT")
            }

            // B. Daily Panchangam Details Event
            if (config.includePanchangamDaily) {
                val panUid = "panchangam-$y-$m-$d@malayalamcalendar"
                val summary = "${panchangam.mlMonth} ${panchangam.mlDate} (${panchangam.kollaVarsham}) · ${panchangam.nakshatra} · ${panchangam.tithi} · ${panchangam.weekdayMl}"

                val desc = "മലയാള തീയതി: ${panchangam.mlMonth} ${panchangam.mlDate}, കൊല്ലവർഷം ${panchangam.kollaVarsham}\\n" +
                        "നക്ഷത്രം: ${panchangam.nakshatra} (അവസാനം: ${panchangam.nakEnd}, അടുത്തത്: ${panchangam.nextNakshatra})\\n" +
                        "തിഥി: ${panchangam.tithi} (${panchangam.paksha})\\n" +
                        "ശകവർഷം: ${panchangam.sakaYear} ${panchangam.sakaMonth} ${panchangam.sakaDay}\\n" +
                        "ഹിജ്റ: ${panchangam.hijriYear} ${panchangam.hijriMonth} ${panchangam.hijriDay}\\n" +
                        "സൂര്യൻ: ${panchangam.sunNir}° · ചന്ദ്രൻ: ${panchangam.moonNir}°"

                sb.appendLine("BEGIN:VEVENT")
                sb.appendLine("UID:$panUid")
                sb.appendLine("DTSTAMP:$dtStamp")
                sb.appendLine("DTSTART;VALUE=DATE:$dateStr")
                sb.appendLine("DTEND;VALUE=DATE:$nextDateStr")
                sb.appendLine("SUMMARY:$summary")
                sb.appendLine("DESCRIPTION:$desc")
                sb.appendLine("CATEGORIES:മലയാള പഞ്ചാംഗം,Panchangam")
                sb.appendLine("STATUS:CONFIRMED")
                sb.appendLine("TRANSP:TRANSPARENT")
                sb.appendLine("END:VEVENT")
            }

            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 2. User Reminders & Notes
        if (config.includeReminders) {
            userReminders.forEach { rem ->
                val remUid = "reminder-${rem.id}-${rem.year}-${rem.month}-${rem.day}@malayalamcalendar"
                val dateStr = String.format(Locale.US, "%04d%02d%02d", rem.year, rem.month + 1, rem.day)
                val summary = if (rem.subject.isNotBlank()) "📌 ${rem.subject}" else "📌 ${rem.text}"

                sb.appendLine("BEGIN:VEVENT")
                sb.appendLine("UID:$remUid")
                sb.appendLine("DTSTAMP:$dtStamp")

                if (rem.time.isNotBlank()) {
                    val timePair = parseTime(rem.time)
                    if (timePair != null) {
                        val startCal = Calendar.getInstance().apply {
                            set(rem.year, rem.month, rem.day, timePair.first, timePair.second, 0)
                        }
                        val endCal = (startCal.clone() as Calendar).apply {
                            add(Calendar.HOUR_OF_DAY, 1)
                        }
                        val dtStartFmt = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.US).format(startCal.time)
                        val dtEndFmt = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.US).format(endCal.time)
                        sb.appendLine("DTSTART;TZID=Asia/Kolkata:$dtStartFmt")
                        sb.appendLine("DTEND;TZID=Asia/Kolkata:$dtEndFmt")
                    } else {
                        sb.appendLine("DTSTART;VALUE=DATE:$dateStr")
                        val nextDateStr = getNextDateString(rem.year, rem.month, rem.day)
                        sb.appendLine("DTEND;VALUE=DATE:$nextDateStr")
                    }
                } else {
                    sb.appendLine("DTSTART;VALUE=DATE:$dateStr")
                    val nextDateStr = getNextDateString(rem.year, rem.month, rem.day)
                    sb.appendLine("DTEND;VALUE=DATE:$nextDateStr")
                }

                // Recurrence Rule (RRULE)
                when (rem.repeatType) {
                    ReminderEntity.REPEAT_DAILY -> sb.appendLine("RRULE:FREQ=DAILY")
                    ReminderEntity.REPEAT_MONTHLY -> sb.appendLine("RRULE:FREQ=MONTHLY")
                    ReminderEntity.REPEAT_YEARLY -> sb.appendLine("RRULE:FREQ=YEARLY")
                }

                val desc = "വിഷയം: ${rem.subject}\\nവിവരണം: ${rem.text}\\nവിഭാഗം: ${rem.category}\\nപ്രാധാന്യം: ${rem.priority}"
                sb.appendLine("SUMMARY:$summary")
                sb.appendLine("DESCRIPTION:$desc")
                sb.appendLine("CATEGORIES:${rem.category},Reminders,Notes")
                sb.appendLine("STATUS:${if (rem.isDone) "COMPLETED" else "CONFIRMED"}")

                // Alarm / Notification
                if (rem.isNotif) {
                    sb.appendLine("BEGIN:VALARM")
                    sb.appendLine("ACTION:DISPLAY")
                    sb.appendLine("DESCRIPTION:$summary")
                    sb.appendLine("TRIGGER:-PT15M")
                    sb.appendLine("END:VALARM")
                }

                sb.appendLine("END:VEVENT")
            }
        }

        sb.appendLine("END:VCALENDAR")
        sb.toString()
    }

    /**
     * Exports the calendar to an .ics file in the app cache and returns the file and content URI.
     */
    suspend fun exportToFile(
        context: Context,
        config: IcsExportConfig,
        userReminders: List<ReminderEntity> = emptyList()
    ): IcsExportResult = withContext(Dispatchers.IO) {
        val icsContent = generateIcsString(config, userReminders)
        val fileName = "Malayalam_Calendar_${config.year}.ics"

        val exportDir = File(context.cacheDir, "calendar_exports").apply {
            if (!exists()) mkdirs()
        }
        val file = File(exportDir, fileName)

        FileOutputStream(file).use { fos ->
            fos.write(icsContent.toByteArray(Charsets.UTF_8))
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // Count totals
        val totalDays = if (isLeapYear(config.year)) 366 else 365
        var festCount = 0
        if (config.includeFestivals) {
            val cal = Calendar.getInstance().apply { set(config.year, 0, 1) }
            for (i in 0 until totalDays) {
                val pan = PanchangamCalculator.computeDayData(config.year, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                if (getSpecialFestivalForDay(config.year, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), pan) != null) {
                    festCount++
                }
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        val panCount = if (config.includePanchangamDaily) totalDays else 0
        val remCount = if (config.includeReminders) userReminders.size else 0
        val totalEvents = panCount + festCount + remCount

        val bytes = file.length()
        val formattedSize = if (bytes < 1024) "$bytes B" else if (bytes < 1024 * 1024) "${bytes / 1024} KB" else String.format(Locale.US, "%.1f MB", bytes.toDouble() / (1024 * 1024))

        IcsExportResult(
            file = file,
            uri = uri,
            totalEventsCount = totalEvents,
            panchangamCount = panCount,
            festivalCount = festCount,
            remindersCount = remCount,
            fileSizeFormatted = formattedSize
        )
    }

    /**
     * Saves the .ics file to public Downloads folder so the user can easily find it or import via browser.
     */
    suspend fun saveToDownloads(
        context: Context,
        config: IcsExportConfig,
        userReminders: List<ReminderEntity> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val icsContent = generateIcsString(config, userReminders)
        val fileName = "Malayalam_Calendar_${config.year}.ics"

        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (downloadsDir != null && downloadsDir.exists()) {
                val outFile = File(downloadsDir, fileName)
                FileOutputStream(outFile).use { it.write(icsContent.toByteArray(Charsets.UTF_8)) }
                return@withContext outFile.absolutePath
            }
        } catch (e: Exception) {
            Log.w(TAG, "Public downloads folder not directly writable, saving to app external storage", e)
        }

        val appDownloads = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        val outFile = File(appDownloads, fileName)
        FileOutputStream(outFile).use { it.write(icsContent.toByteArray(Charsets.UTF_8)) }
        outFile.absolutePath
    }

    /**
     * Triggers Intent to open/import .ics into Google Calendar or any Calendar app installed on device.
     */
    fun openIcsWithCalendarApp(context: Context, uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/calendar")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(intent, "Open with Google Calendar"))
        } catch (e: Exception) {
            Log.e(TAG, "Error opening calendar intent", e)
            shareIcsFile(context, uri)
        }
    }

    /**
     * Shares the .ics file via standard Android share sheet (WhatsApp, Email, Drive, etc.).
     */
    fun shareIcsFile(context: Context, uri: Uri) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/calendar"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "മലയാളം കലണ്ടർ .ics ഫയൽ")
                putExtra(Intent.EXTRA_TEXT, "മലയാള പഞ്ചാംഗം കലണ്ടർ ഡാറ്റയും വിശേഷദിവസങ്ങളും കുറിപ്പുകളും (.ics ഫോർമാറ്റ്). Google Calendar അല്ലെങ്കിൽ മറ്റ് കലണ്ടറുകളിൽ ഇമ്പോർട്ട് ചെയ്യാം.")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share .ics Calendar File"))
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing calendar file", e)
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun getNextDateString(year: Int, month0Based: Int, day: Int): String {
        val cal = Calendar.getInstance().apply {
            set(year, month0Based, day)
            add(Calendar.DAY_OF_MONTH, 1)
        }
        return String.format(Locale.US, "%04d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
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

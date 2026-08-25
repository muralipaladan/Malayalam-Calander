package com.example.malayalamcalendar.data

import java.util.Calendar
import java.util.Locale
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

data class NakshatraInfo(
    val index: Int,
    val name: String
)

data class MalayalamMonthInfo(
    val index: Int,
    val name: String
)

data class DayPanchangamData(
    val year: Int,
    val month: Int, // 0-based
    val day: Int,
    val sunNir: Double,
    val moonNir: Double,
    val nakshatra: String,
    val nakshatraIdx: Int,
    val nakEnd: String,
    val nextNakshatra: String,
    val tithi: String,
    val tithiIdx: Int,
    val tithiEnd: String = "",
    val nextTithi: String = "",
    val mlMonth: String,
    val mlDate: Int,
    val kollaVarsham: Int,
    val paksha: String,
    val weekdayMl: String,
    val nakNazhika: String = "",
    val tithiNazhika: String = "",
    val sakaDay: Int = 1,
    val sakaMonth: String = "ചൈത്രം",
    val sakaYear: Int = 1946,
    val hijriDay: Int = 1,
    val hijriMonth: String = "റമദാൻ",
    val hijriYear: Int = 1446,
    // Location & Solar details
    val sunriseTime: String = "06:15 AM",
    val sunsetTime: String = "06:35 PM",
    val sunriseHour: Double = 6.25,
    val sunsetHour: Double = 18.58,
    val rahuKalam: String = "04:30 PM - 06:00 PM",
    val gulikaKalam: String = "03:00 PM - 04:30 PM",
    val yamagandam: String = "12:00 PM - 01:30 PM",
    val abhijithMuhurtham: String = "12:05 PM - 12:53 PM",
    val dinamanam: String = "12 മണിക്കൂർ 20 മിനിറ്റ്",
    val locationName: String = "കൊച്ചി",
    val latitude: Double = 9.9312,
    val longitude: Double = 76.2673,
    val tzOffsetHours: Double = 5.5
)

object PanchangamCalculator {

    val NAKSHATRA_NAMES = listOf(
        "അശ്വതി", "ഭരണി", "കാർത്തിക", "രോഹിണി", "മകയിരം", "തിരുവാതിര",
        "പുണർതം", "പൂയം", "ആയില്യം", "മകം", "പൂരം", "ഉത്രം",
        "അത്തം", "ചിത്തിര", "ചോതി", "വിശാഖം", "അനിഴം", "തൃക്കേട്ട",
        "മൂലം", "പൂരാടം", "ഉത്രാടം", "തിരുവോണം", "അവിട്ടം", "ചതയം",
        "പൂരൂരുട്ടാതി", "ഉത്തൃട്ടാതി", "രേവതി"
    )

    val TITHI_NAMES = listOf(
        "പ്രഥമ", "ദ്വിതീയ", "തൃതീയ", "ചതുർഥി", "പഞ്ചമി",
        "ഷഷ്ഠി", "സപ്തമി", "അഷ്ടമി", "നവമി", "ദശമി",
        "ഏകാദശി", "ദ്വാദശി", "ത്രയോദശി", "ചതുർദശി", "പൗർണ്ണമി",
        "പ്രഥമ(K)", "ദ്വിതീയ(K)", "തൃതീയ(K)", "ചതുർഥി(K)", "പഞ്ചമി(K)",
        "ഷഷ്ഠി(K)", "സപ്തമി(K)", "അഷ്ടമി(K)", "നവമി(K)", "ദശമി(K)",
        "ഏകാദശി(K)", "ദ്വാദശി(K)", "ത്രയോദശി(K)", "ചതുർദശി(K)", "അമാവാസി"
    )

    val ML_MONTHS = listOf(
        "ചിങ്ങം", "കന്നി", "തുലാം", "വൃശ്ചികം", "ധനു", "മകരം",
        "കുംഭം", "മീനം", "മേടം", "ഇടവം", "മിഥുനം", "കർക്കിടകം"
    )

    val GREG_MONTHS_ML = listOf(
        "ജനുവരി", "ഫെബ്രുവരി", "മാർച്ച്", "ഏപ്രിൽ", "മേയ്", "ജൂൺ",
        "ജൂലൈ", "ഓഗസ്റ്റ്", "സെപ്റ്റംബർ", "ഒക്ടോബർ", "നവംബർ", "ഡിസംബർ"
    )

    val GREG_MONTHS_EN = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val WEEKDAYS_ML = listOf(
        "ഞായർ", "തിങ്കൾ", "ചൊവ്വ", "ബുധൻ", "വ്യാഴം", "വെള്ളി", "ശനി"
    )

    private val astroCache = mutableMapOf<String, DayPanchangamData>()

    fun kollaVarsham(year: Int, month: Int, day: Int): Int {
        val isAfterChingam1 = if (month > 7) true
        else if (month == 7) day >= 17
        else false
        return if (isAfterChingam1) year - 824 else year - 825
    }

    fun julianDay(year: Int, month1Based: Int, day: Int, hour: Double = 0.0): Double {
        var y = year
        var m = month1Based
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5 + (hour / 24.0)
    }

    private fun tValue(jd: Double): Double = (jd - 2451545.0) / 36525.0

    private fun norm(deg: Double): Double = ((deg % 360.0) + 360.0) % 360.0

    fun sunLongitude(jd: Double): Double {
        val t = tValue(jd)
        val l0 = norm(280.46646 + 36000.76983 * t + 0.0003032 * t * t)
        val m = norm(357.52911 + 35999.05029 * t - 0.0001537 * t * t) * PI / 180.0
        val c = (1.9146 - 0.004817 * t - 0.000014 * t * t) * sin(m) +
                (0.019993 - 0.000101 * t) * sin(2 * m) +
                0.00029 * sin(3 * m)
        val sunLon = norm(l0 + c)
        val omega = norm(125.04 - 1934.136 * t)
        return norm(sunLon - 0.00569 - 0.00478 * sin(omega * PI / 180.0))
    }

    fun moonLongitude(jd: Double): Double {
        val t = tValue(jd)
        val l1 = norm(218.3164477 + 481267.88123421 * t)
        val d = norm(297.8501921 + 445267.1114034 * t) * PI / 180.0
        val m = norm(357.5291092 + 35999.0502909 * t) * PI / 180.0
        val mp = norm(134.9633964 + 477198.8675055 * t) * PI / 180.0
        val f = norm(93.2720950 + 483202.0175233 * t) * PI / 180.0

        var lon = l1
        lon += 6.288774 * sin(mp)
        lon += 1.274027 * sin(2 * d - mp)
        lon += 0.658314 * sin(2 * d)
        lon += 0.213618 * sin(2 * mp)
        lon -= 0.185116 * sin(m)
        lon -= 0.114332 * sin(2 * f)
        lon += 0.058793 * sin(2 * d - 2 * mp)
        lon += 0.057066 * sin(2 * d - m - mp)
        lon += 0.053322 * sin(2 * d + mp)
        lon += 0.045758 * sin(2 * d - m)
        lon -= 0.040923 * sin(m - mp)
        lon -= 0.034720 * sin(d)
        lon -= 0.030383 * sin(m + mp)
        lon += 0.015327 * sin(2 * d - 2 * f)
        lon += 0.010980 * sin(mp - 2 * f)
        lon += 0.010675 * sin(4 * d - mp)
        lon += 0.010034 * sin(3 * mp)
        lon += 0.008548 * sin(4 * d - 2 * mp)
        lon -= 0.007888 * sin(2 * d + m - mp)
        lon -= 0.006766 * sin(2 * d + m)
        lon += 0.005185 * sin(mp - d)
        lon += 0.004877 * sin(4 * d)
        lon += 0.004297 * sin(2 * d - m - 2 * mp)
        lon += 0.003102 * sin(2 * d + 2 * mp)

        return norm(lon)
    }

    fun lahiriAyanamsa(jd: Double): Double = 23.85 + 0.013611 * tValue(jd)

    fun nirayana(lon: Double, jd: Double): Double = norm(lon - lahiriAyanamsa(jd))

    fun getNakshatra(moonNir: Double): NakshatraInfo {
        val idx = (floor(moonNir / (360.0 / 27.0)).toInt()) % 27
        val safeIdx = if (idx < 0) (idx + 27) % 27 else idx
        return NakshatraInfo(safeIdx, NAKSHATRA_NAMES[safeIdx])
    }

    fun findNakshatraEndJD(startJD: Double, currentNakIdx: Int): Double {
        var lo = startJD
        var hi = startJD + 2.0
        val boundary = (currentNakIdx + 1) * (360.0 / 27.0)
        for (i in 0 until 45) {
            val mid = (lo + hi) / 2.0
            val moonNir = nirayana(moonLongitude(mid), mid)
            val diff = norm(moonNir - boundary)
            if (diff > 180.0) hi = mid else lo = mid
        }
        return (lo + hi) / 2.0
    }

    fun getTithi(sunNir: Double, moonNir: Double): Int {
        val diff = norm(moonNir - sunNir)
        val idx = floor(diff / 12.0).toInt() % 30
        return if (idx < 0) (idx + 30) % 30 else idx
    }

    fun findTithiEndJD(startJD: Double, currentTithiIdx: Int): Double {
        var lo = startJD
        var hi = startJD + 2.0
        val boundary = (currentTithiIdx + 1) * 12.0
        for (i in 0 until 45) {
            val mid = (lo + hi) / 2.0
            val sNir = nirayana(sunLongitude(mid), mid)
            val mNir = nirayana(moonLongitude(mid), mid)
            val diff = norm(mNir - sNir)
            val delta = norm(diff - boundary)
            if (delta > 180.0) hi = mid else lo = mid
        }
        return (lo + hi) / 2.0
    }

    fun getMalayalamMonth(sunNir: Double): MalayalamMonthInfo {
        val mlIdx = (floor(sunNir / 30.0).toInt() + 8) % 12
        val safeIdx = if (mlIdx < 0) (mlIdx + 12) % 12 else mlIdx
        return MalayalamMonthInfo(safeIdx, ML_MONTHS[safeIdx])
    }

    fun getMalayalamDate(jd: Double): Int {
        val sunNir = nirayana(sunLongitude(jd), jd)
        val curRashi = floor(sunNir / 30.0).toInt()
        var sankramanJD = jd
        for (step in 1..35) {
            val prevJD = jd - step
            val prevSunNir = nirayana(sunLongitude(prevJD), prevJD)
            if (floor(prevSunNir / 30.0).toInt() != curRashi) {
                var lo = prevJD
                var hi = prevJD + 1.0
                for (i in 0 until 30) {
                    val mid = (lo + hi) / 2.0
                    if (floor(nirayana(sunLongitude(mid), mid) / 30.0).toInt() == curRashi) {
                        hi = mid
                    } else {
                        lo = mid
                    }
                }
                sankramanJD = (lo + hi) / 2.0
                break
            }
        }
        val diffDays = floor(jd - sankramanJD).toInt() + 1
        return if (diffDays <= 0) 1 else diffDays
    }

    data class CalendarDate(val year: Int, val month1Based: Int, val day: Int, val hour: Double)

    fun jdToDate(jd: Double): CalendarDate {
        val z = floor(jd + 0.5).toLong()
        val f = jd + 0.5 - z
        val a = if (z < 2299161) {
            z
        } else {
            val alpha = floor((z - 1867216.25) / 36524.25).toLong()
            z + 1 + alpha - floor(alpha / 4.0).toLong()
        }
        val b = a + 1524
        val c = floor((b - 122.1) / 365.25).toLong()
        val d = floor(365.25 * c).toLong()
        val e = floor((b - d) / 30.6001).toLong()
        val day = (b - d - floor(30.6001 * e)).toInt()
        val month = if (e < 14) (e - 1).toInt() else (e - 13).toInt()
        val year = if (month > 2) (c - 4716).toInt() else (c - 4715).toInt()
        return CalendarDate(year, month, day, f * 24.0)
    }

    fun formatLocalTime(hourOfDay: Double): String {
        var h = hourOfDay
        if (h < 0.0) h += 24.0
        if (h >= 24.0) h -= 24.0
        val hourInt = floor(h).toInt()
        val minuteInt = floor((h - hourInt) * 60.0).toInt()
        val displayHour = if (hourInt % 12 == 0) 12 else hourInt % 12
        val amPm = if (hourInt >= 12) "PM" else "AM"
        return String.format(Locale.US, "%02d:%02d %s", displayHour, minuteInt, amPm)
    }

    fun formatTimeRange(startHour: Double, endHour: Double): String {
        return "${formatLocalTime(startHour)} - ${formatLocalTime(endHour)}"
    }

    /**
     * Calculates high-precision Sunrise & Sunset for a given date, latitude, longitude and timezone.
     * Returns Pair(sunriseHourLocal, sunsetHourLocal).
     */
    fun calculateSunTimes(
        year: Int,
        month0Based: Int,
        day: Int,
        latitude: Double,
        longitude: Double,
        tzOffsetHours: Double
    ): Pair<Double, Double> {
        val approxJd = julianDay(year, month0Based + 1, day, 12.0 - tzOffsetHours - (longitude / 15.0))
        val t = tValue(approxJd)

        val l0 = norm(280.46646 + 36000.76983 * t)
        val m = norm(357.52911 + 35999.05029 * t) * PI / 180.0
        val eotC = (1.9146 - 0.004817 * t) * sin(m) + (0.019993 - 0.000101 * t) * sin(2 * m)
        val sunTrueLon = norm(l0 + eotC) * PI / 180.0
        val eps = (23.439291 - 0.0130042 * t) * PI / 180.0

        val sinDec = sin(eps) * sin(sunTrueLon)
        val dec = asin(sinDec)
        val cosDec = cos(dec)

        // Equation of Time in minutes
        val y = tan(eps / 2.0) * tan(eps / 2.0)
        val sin2L0 = sin(2 * (l0 * PI / 180.0))
        val sinM = sin(m)
        val cos2L0 = cos(2 * (l0 * PI / 180.0))
        val sin4L0 = sin(4 * (l0 * PI / 180.0))
        val eotMin = 4.0 * (y * sin2L0 - 2 * 0.0167 * sinM + 4 * 0.0167 * y * sinM * cos2L0 - 0.5 * y * y * sin4L0) * (180.0 / PI)

        val solarNoonLocal = 12.0 + (4.0 * (tzOffsetHours * 15.0 - longitude) - eotMin) / 60.0

        // Standard refraction zenith: 90° 50' = 90.83333°
        val zenithRad = 90.83333 * PI / 180.0
        val latRad = latitude * PI / 180.0

        val cosH0 = (cos(zenithRad) - sin(latRad) * sinDec) / (cos(latRad) * cosDec)
        val clampedCosH0 = cosH0.coerceIn(-1.0, 1.0)
        val hourAngleDeg = acos(clampedCosH0) * 180.0 / PI
        val halfDayHours = hourAngleDeg / 15.0

        val sunrise = solarNoonLocal - halfDayHours
        val sunset = solarNoonLocal + halfDayHours

        return Pair(sunrise, sunset)
    }

    /**
     * Converts hours from sunrise into traditional Malayalam Nazhika and Vinazhika.
     * (1 Nazhika = 24 minutes, 1 Vinazhika = 24 seconds = 1/60 Nazhika).
     */
    fun formatNazhikaVinazhika(hoursFromSunrise: Double): String {
        val safeHours = hoursFromSunrise.coerceAtLeast(0.0)
        val totalNazhika = safeHours * 2.5
        val nazh = totalNazhika.toInt()
        val vinazh = ((totalNazhika - nazh) * 60.0).toInt().coerceIn(0, 59)
        return if (vinazh > 0) {
            "$nazh നാഴിക $vinazh വിനാഴിക"
        } else {
            "$nazh നാഴിക"
        }
    }

    /**
     * Computes the complete Panchangam data for a given day and geographical coordinates.
     */
    fun computeDayData(
        year: Int,
        month0Based: Int,
        day: Int,
        latitude: Double = 9.9312,
        longitude: Double = 76.2673,
        tzOffsetHours: Double = 5.5,
        locationName: String = "കൊച്ചി"
    ): DayPanchangamData {
        val key = String.format(Locale.US, "%d-%d-%d_%.2f_%.2f_%.1f", year, month0Based, day, latitude, longitude, tzOffsetHours)
        astroCache[key]?.let { return it }

        // 1. Calculate Sun times
        val (riseHour, setHour) = calculateSunTimes(year, month0Based, day, latitude, longitude, tzOffsetHours)
        val daylightDurationHours = (setHour - riseHour).coerceAtLeast(1.0)
        val dayDurationMin = (daylightDurationHours * 60.0).toInt()
        val dayDurationNazh = daylightDurationHours * 2.5
        val dinamanamText = "${dayDurationMin / 60} മണിക്കൂർ ${dayDurationMin % 60} മിനിറ്റ് (${String.format(Locale.US, "%.1f", dayDurationNazh)} നാഴിക)"

        val sunriseStr = formatLocalTime(riseHour)
        val sunsetStr = formatLocalTime(setHour)

        // 2. Julian Day at local sunrise
        val sunriseUtcHour = riseHour - tzOffsetHours
        val jdSunrise = julianDay(year, month0Based + 1, day, sunriseUtcHour)

        val sunNir = nirayana(sunLongitude(jdSunrise), jdSunrise)
        val moonNir = nirayana(moonLongitude(jdSunrise), jdSunrise)
        val nakData = getNakshatra(moonNir)
        val tithiIdx = getTithi(sunNir, moonNir)
        val mlMonth = getMalayalamMonth(sunNir)
        val mlDate = getMalayalamDate(jdSunrise)

        // 3. Nakshatra Ending Time & Nazhika
        val nakEndJD = findNakshatraEndJD(jdSunrise - 0.2, nakData.index)
        val nakEndLocal = jdToDate(nakEndJD + (tzOffsetHours / 24.0))
        val isNakSameDay = (nakEndLocal.day == day && nakEndLocal.month1Based == (month0Based + 1) && nakEndLocal.year == year)
        val isNakNextDay = !isNakSameDay

        val nakEndStr = if (isNakSameDay) {
            "${formatLocalTime(nakEndLocal.hour)} വരെ"
        } else {
            "നാളെ ${formatLocalTime(nakEndLocal.hour)} വരെ"
        }

        val hoursFromRiseToNakEnd = if (isNakSameDay) {
            (nakEndLocal.hour - riseHour).coerceAtLeast(0.0)
        } else {
            (24.0 - riseHour + nakEndLocal.hour).coerceAtLeast(0.0)
        }
        val nakNazhikaVal = formatNazhikaVinazhika(hoursFromRiseToNakEnd)

        // 4. Thithi Ending Time & Nazhika
        val tithiEndJD = findTithiEndJD(jdSunrise - 0.2, tithiIdx)
        val tithiEndLocal = jdToDate(tithiEndJD + (tzOffsetHours / 24.0))
        val isTithiSameDay = (tithiEndLocal.day == day && tithiEndLocal.month1Based == (month0Based + 1) && tithiEndLocal.year == year)
        val tithiEndStr = if (isTithiSameDay) {
            "${formatLocalTime(tithiEndLocal.hour)} വരെ"
        } else {
            "നാളെ ${formatLocalTime(tithiEndLocal.hour)} വരെ"
        }

        val hoursFromRiseToTithiEnd = if (isTithiSameDay) {
            (tithiEndLocal.hour - riseHour).coerceAtLeast(0.0)
        } else {
            (24.0 - riseHour + tithiEndLocal.hour).coerceAtLeast(0.0)
        }
        val tithiNazhikaVal = formatNazhikaVinazhika(hoursFromRiseToTithiEnd)

        // 5. Day of week calculations
        val cal = Calendar.getInstance().apply {
            set(year, month0Based, day)
        }
        val dayOfWeekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0: Sun, 1: Mon, ... 6: Sat
        val weekdayMl = WEEKDAYS_ML.getOrElse(dayOfWeekIndex) { "" }

        // 6. Vedic Segments (8 parts of daytime)
        val segmentDuration = daylightDurationHours / 8.0

        // Rahu Kalam part index (0-7)
        val rahuIndex = when (dayOfWeekIndex) {
            0 -> 7 // Sunday: 8th part (4:30 - 6:00)
            1 -> 1 // Monday: 2nd part (7:30 - 9:00)
            2 -> 6 // Tuesday: 7th part (3:00 - 4:30)
            3 -> 4 // Wednesday: 5th part (12:00 - 1:30)
            4 -> 5 // Thursday: 6th part (1:30 - 3:00)
            5 -> 3 // Friday: 4th part (10:30 - 12:00)
            6 -> 2 // Saturday: 3rd part (9:00 - 10:30)
            else -> 7
        }
        val rahuStart = riseHour + rahuIndex * segmentDuration
        val rahuEnd = rahuStart + segmentDuration
        val rahuKalamStr = formatTimeRange(rahuStart, rahuEnd)

        // Gulika Kalam part index
        val gulikaIndex = when (dayOfWeekIndex) {
            0 -> 6 // Sunday: 7th part
            1 -> 5 // Monday: 6th part
            2 -> 4 // Tuesday: 5th part
            3 -> 3 // Wednesday: 4th part
            4 -> 2 // Thursday: 3rd part
            5 -> 1 // Friday: 2nd part
            6 -> 0 // Saturday: 1st part
            else -> 6
        }
        val gulikaStart = riseHour + gulikaIndex * segmentDuration
        val gulikaEnd = gulikaStart + segmentDuration
        val gulikaKalamStr = formatTimeRange(gulikaStart, gulikaEnd)

        // Yamagandam part index
        val yamaIndex = when (dayOfWeekIndex) {
            0 -> 4 // Sunday: 5th part
            1 -> 3 // Monday: 4th part
            2 -> 2 // Tuesday: 3rd part
            3 -> 1 // Wednesday: 2nd part
            4 -> 0 // Thursday: 1st part
            5 -> 6 // Friday: 7th part
            6 -> 5 // Saturday: 6th part
            else -> 4
        }
        val yamaStart = riseHour + yamaIndex * segmentDuration
        val yamaEnd = yamaStart + segmentDuration
        val yamagandamStr = formatTimeRange(yamaStart, yamaEnd)

        // Abhijith Muhurtham (midday)
        val solarNoon = (riseHour + setHour) / 2.0
        val abhijithStart = solarNoon - (segmentDuration * 0.4)
        val abhijithEnd = solarNoon + (segmentDuration * 0.4)
        val abhijithStr = formatTimeRange(abhijithStart, abhijithEnd)

        // 7. Saka Era calculations
        val sakaMonths = listOf("ചൈത്രം", "വൈശാഖം", "ജ്യേഷ്ഠം", "ആഷാഢം", "ശ്രാവണം", "ഭാദ്രപദം", "ആശ്വിനം", "കാർത്തികം", "മാർഗ്ഗശീർഷം", "പൗഷം", "മാഘം", "ഫാൽഗുനം")
        val sakaMonthIdx = (month0Based + if (day >= 22) 0 else 11) % 12
        val sakaMonthName = sakaMonths.getOrElse(sakaMonthIdx) { "ചൈത്രം" }
        val sakaDay = ((day + 9) % 30) + 1
        val sakaYear = year - 78

        // 8. Hijri calculations
        val hijriMonths = listOf("മുഹറം", "സഫർ", "റബീഉൽ അവ്വൽ", "റബീഉസ്സാനി", "ജമാദുൽ അവ്വൽ", "ജമാദുസ്സാനി", "റജബ്", "ശഅ്ബാൻ", "റമദാൻ", "ശവ്വാൽ", "ദുൽഖഅദ്", "ദുൽഹിജ്ജ")
        val islamicEpoch = 1948439.5
        val hijriDays = (jdSunrise - islamicEpoch).toInt()
        val hijriDay = ((hijriDays % 30) + 1).coerceIn(1, 30)
        val hijriMonthIdx = ((hijriDays / 30) % 12)
        val hijriMonthName = hijriMonths.getOrElse(hijriMonthIdx) { "റമദാൻ" }
        val hijriYear = ((jdSunrise - islamicEpoch) / 354.367).toInt() + 1

        val data = DayPanchangamData(
            year = year,
            month = month0Based,
            day = day,
            sunNir = Math.round(sunNir * 100.0) / 100.0,
            moonNir = Math.round(moonNir * 100.0) / 100.0,
            nakshatra = nakData.name,
            nakshatraIdx = nakData.index,
            nakEnd = nakEndStr,
            nextNakshatra = NAKSHATRA_NAMES[(nakData.index + 1) % 27],
            tithi = TITHI_NAMES.getOrElse(tithiIdx) { "" },
            tithiIdx = tithiIdx,
            tithiEnd = tithiEndStr,
            nextTithi = TITHI_NAMES.getOrElse((tithiIdx + 1) % 30) { "" },
            mlMonth = mlMonth.name,
            mlDate = mlDate,
            kollaVarsham = kollaVarsham(year, month0Based, day),
            paksha = if (tithiIdx < 15) "ശുക്ല പക്ഷം" else "കൃഷ്ണ പക്ഷം",
            weekdayMl = weekdayMl,
            nakNazhika = nakNazhikaVal,
            tithiNazhika = tithiNazhikaVal,
            sakaDay = sakaDay,
            sakaMonth = sakaMonthName,
            sakaYear = sakaYear,
            hijriDay = hijriDay,
            hijriMonth = hijriMonthName,
            hijriYear = hijriYear,
            sunriseTime = sunriseStr,
            sunsetTime = sunsetStr,
            sunriseHour = riseHour,
            sunsetHour = setHour,
            rahuKalam = rahuKalamStr,
            gulikaKalam = gulikaKalamStr,
            yamagandam = yamagandamStr,
            abhijithMuhurtham = abhijithStr,
            dinamanam = dinamanamText,
            locationName = locationName,
            latitude = latitude,
            longitude = longitude,
            tzOffsetHours = tzOffsetHours
        )
        astroCache[key] = data
        return data
    }

    /**
     * Computes the Malayalam month span (e.g. "കർക്കിടകം - ചിങ്ങം") and Kollavarsham span for a Gregorian month.
     */
    fun getMalayalamMonthSpanForGregMonth(year: Int, month0Based: Int, latitude: Double = 9.9312, longitude: Double = 76.2673): String {
        val cal = Calendar.getInstance().apply {
            set(year, month0Based, 1)
        }
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDay = computeDayData(year, month0Based, 1, latitude, longitude)
        val lastDay = computeDayData(year, month0Based, maxDays, latitude, longitude)

        val m1 = firstDay.mlMonth
        val m2 = lastDay.mlMonth
        val kv1 = firstDay.kollaVarsham
        val kv2 = lastDay.kollaVarsham

        val monthText = if (m1 == m2) m1 else "$m1 - $m2"
        val kvText = if (kv1 == kv2) "$kv1" else "$kv1 - $kv2"
        return "$monthText (കൊ.വ. $kvText)"
    }
}

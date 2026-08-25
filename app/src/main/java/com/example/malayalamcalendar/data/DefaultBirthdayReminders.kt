package com.example.malayalamcalendar.data

import android.content.Context
import com.example.malayalamcalendar.data.local.ReminderDao
import com.example.malayalamcalendar.data.local.ReminderEntity
import com.example.malayalamcalendar.notification.AlarmScheduler

/**
 * Pre-configured default family birthday reminders with yearly repetition.
 *
 * 1. Muraleedharan: 01/04/1968 (April 1, 1968)
 * 2. Sini: 12/08/1972 (August 12, 1972)
 * 3. Sivashri: 10/09/1998 (September 10, 1998)
 * 4. Abijith: 30/05/2008 (May 30, 2008)
 * 5. Aiswarya: 11/09/1996 (September 11, 1996)
 * 6. Vishnu: 17/07/1995 (July 17, 1995)
 * 7. Sivashree: 21/10/2022 (October 21, 2022)
 */
object DefaultBirthdayReminders {

    val INITIAL_BIRTHDAYS = listOf(
        ReminderEntity(
            year = 1968,
            month = 3, // April (0-indexed: Jan=0, Feb=1, Mar=2, Apr=3)
            day = 1,
            subject = "മുരളീധരൻ (Muraleedharan) - ജന്മദിനം 🎂",
            text = "മുരളീധരന്റെ ജന്മദിനം (ജനനം: 01/04/1968, ഏപ്രിൽ 1)",
            time = "08:00 AM",
            category = "ജന്മദിനം / വാർഷികം",
            priority = "പ്രധാനം",
            repeatType = ReminderEntity.REPEAT_YEARLY,
            isNotif = true,
            isDone = false
        ),
        ReminderEntity(
            year = 1972,
            month = 7, // August (0-indexed: Aug=7)
            day = 12,
            subject = "സിനി (Sini) - ജന്മദിനം 🎂",
            text = "സിനിയുടെ ജന്മദിനം (ജനനം: 12/08/1972, ഓഗസ്റ്റ് 12)",
            time = "08:00 AM",
            category = "ജന്മദിനം / വാർഷികം",
            priority = "പ്രധാനം",
            repeatType = ReminderEntity.REPEAT_YEARLY,
            isNotif = true,
            isDone = false
        ),
        ReminderEntity(
            year = 1998,
            month = 8, // September (0-indexed: Sep=8)
            day = 10,
            subject = "ശിവശ്രീ (Sivashri) - ജന്മദിനം 🎂",
            text = "ശിവശ്രീയുടെ ജന്മദിനം (ജനനം: 10/09/1998, സെപ്റ്റംബർ 10)",
            time = "08:00 AM",
            category = "ജന്മദിനം / വാർഷികം",
            priority = "പ്രധാനം",
            repeatType = ReminderEntity.REPEAT_YEARLY,
            isNotif = true,
            isDone = false
        ),
        ReminderEntity(
            year = 2008,
            month = 4, // May (0-indexed: May=4)
            day = 30,
            subject = "അഭിജിത്ത് (Abijith) - ജന്മദിനം 🎂",
            text = "അഭിജിത്തിന്റെ ജന്മദിനം (ജനനം: 30/05/2008, മെയ് 30)",
            time = "08:00 AM",
            category = "ജന്മദിനം / വാർഷികം",
            priority = "പ്രധാനം",
            repeatType = ReminderEntity.REPEAT_YEARLY,
            isNotif = true,
            isDone = false
        ),
        ReminderEntity(
            year = 1996,
            month = 8, // September (0-indexed: Sep=8)
            day = 11,
            subject = "ഐശ്വര്യ (Aiswarya) - ജന്മദിനം 🎂",
            text = "ഐശ്വര്യയുടെ ജന്മദിനം (ജനനം: 11/09/1996, സെപ്റ്റംബർ 11)",
            time = "08:00 AM",
            category = "ജന്മദിനം / വാർഷികം",
            priority = "പ്രധാനം",
            repeatType = ReminderEntity.REPEAT_YEARLY,
            isNotif = true,
            isDone = false
        ),
        ReminderEntity(
            year = 1995,
            month = 6, // July (0-indexed: Jul=6)
            day = 17,
            subject = "വിഷ്ണു (Vishnu) - ജന്മദിനം 🎂",
            text = "വിഷ്ണുവിന്റെ ജന്മദിനം (ജനനം: 17/07/1995, ജൂലൈ 17)",
            time = "08:00 AM",
            category = "ജന്മദിനം / വാർഷികം",
            priority = "പ്രധാനം",
            repeatType = ReminderEntity.REPEAT_YEARLY,
            isNotif = true,
            isDone = false
        ),
        ReminderEntity(
            year = 2022,
            month = 9, // October (0-indexed: Oct=9)
            day = 21,
            subject = "ശിവശ്രീ (Sivashree) - ജന്മദിനം 🎂",
            text = "ശിവശ്രീയുടെ ജന്മദിനം (ജനനം: 21/10/2022, ഒക്ടോബർ 21)",
            time = "08:00 AM",
            category = "ജന്മദിനം / വാർഷികം",
            priority = "പ്രധാനം",
            repeatType = ReminderEntity.REPEAT_YEARLY,
            isNotif = true,
            isDone = false
        )
    )

    /**
     * Seeds initial birthday reminders into database if they do not already exist.
     */
    suspend fun seedDefaultBirthdayReminders(context: Context, dao: ReminderDao) {
        try {
            val existing = dao.getAllRemindersList()
            for (bday in INITIAL_BIRTHDAYS) {
                val alreadyExists = existing.any { item ->
                    (item.month == bday.month && item.day == bday.day && item.repeatType == ReminderEntity.REPEAT_YEARLY) ||
                    (item.subject.contains(bday.subject.substringBefore(" ("), ignoreCase = true) && item.month == bday.month && item.day == bday.day)
                }
                if (!alreadyExists) {
                    val insertedId = dao.insertReminder(bday)
                    if (insertedId > 0) {
                        val entityWithId = bday.copy(id = insertedId)
                        try {
                            AlarmScheduler.scheduleReminderAlarm(context, entityWithId)
                        } catch (e: Exception) {
                            android.util.Log.e("DefaultBirthdays", "Failed to schedule alarm for seed reminder", e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DefaultBirthdays", "Error seeding default birthdays", e)
        }
    }
}

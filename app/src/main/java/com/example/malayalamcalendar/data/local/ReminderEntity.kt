package com.example.malayalamcalendar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val year: Int,
    val month: Int, // 0-based (0: Jan .. 11: Dec)
    val day: Int,
    val text: String, // Note description / കുറിപ്പ്
    val time: String = "", // e.g. "09:30 AM" or "14:30"
    val fileName: String = "", // backwards-compatibility
    val subject: String = "", // Subject / വിഷയം
    val category: String = "കുറിപ്പ്", // "കുറിപ്പ്", "ഓർമ്മപ്പെടുത്തൽ", "മീറ്റിംഗ്", "ജന്മദിനം / വാർഷികം", "വ്യക്തിഗതം"
    val priority: String = "സാധാരണ", // "അടിയന്തിരം", "പ്രധാനം", "സാധാരണ"
    val isNotif: Boolean = true,
    val isDone: Boolean = false,
    val repeatType: String = REPEAT_ONCE, // "ONCE", "DAILY", "MONTHLY", "YEARLY"
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val REPEAT_ONCE = "ONCE"
        const val REPEAT_DAILY = "DAILY"
        const val REPEAT_MONTHLY = "MONTHLY"
        const val REPEAT_YEARLY = "YEARLY"

        fun getRepeatLabel(repeatType: String): String {
            return when (repeatType) {
                REPEAT_DAILY -> "എല്ലാ ദിവസവും"
                REPEAT_MONTHLY -> "എല്ലാ മാസവും"
                REPEAT_YEARLY -> "എല്ലാ വർഷവും"
                else -> "ഒറ്റത്തവണ"
            }
        }
    }
}


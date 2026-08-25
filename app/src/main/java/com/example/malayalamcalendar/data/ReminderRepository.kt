package com.example.malayalamcalendar.data

import com.example.malayalamcalendar.data.local.ReminderDao
import com.example.malayalamcalendar.data.local.ReminderEntity
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {

    val allReminders: Flow<List<ReminderEntity>> = reminderDao.getAllReminders()

    fun getRemindersForDate(year: Int, month: Int, day: Int): Flow<List<ReminderEntity>> {
        return reminderDao.getRemindersForDate(year, month, day)
    }

    suspend fun insert(reminder: ReminderEntity): Long {
        return reminderDao.insertReminder(reminder)
    }

    suspend fun update(reminder: ReminderEntity) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun delete(reminder: ReminderEntity) {
        reminderDao.deleteReminder(reminder)
    }

    suspend fun deleteById(id: Long) {
        reminderDao.deleteById(id)
    }
}

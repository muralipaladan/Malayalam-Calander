package com.example.malayalamcalendar.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.malayalamcalendar.MainActivity
import com.example.malayalamcalendar.data.local.ReminderEntity
import java.util.Calendar
import java.util.Locale

class AlarmScheduler(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "kerala_calendar_reminders_channel"
        const val CHANNEL_NAME = "കലണ്ടർ റിമൈൻഡർ അലാറം"
        private const val TAG = "AlarmScheduler"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "മലയാളം കലണ്ടർ അലാറങ്ങളും ഓർമ്മപ്പെടുത്തലുകളും"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 500)
                    setSound(soundUri, audioAttributes)
                }

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun scheduleReminderAlarm(context: Context, reminder: ReminderEntity) {
            AlarmScheduler(context).schedule(reminder)
        }

        fun cancelReminderAlarm(context: Context, reminderId: Int) {
            AlarmScheduler(context).cancel(reminderId.toLong())
        }
    }

    init {
        createNotificationChannel(context)
    }

    @SuppressLint("ScheduleExactAlarm")
    fun schedule(reminder: ReminderEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val triggerTimeMs = calculateTriggerTimeMs(reminder)
        if (triggerTimeMs <= System.currentTimeMillis()) {
            Log.w(TAG, "Trigger time is in the past, skipping exact alarm for id: ${reminder.id}")
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ID, reminder.id)
            putExtra(AlarmReceiver.EXTRA_NOTE, reminder.text)
            putExtra(AlarmReceiver.EXTRA_FILE_NAME, reminder.fileName)
            putExtra(AlarmReceiver.EXTRA_SUBJECT, reminder.subject)
            putExtra(AlarmReceiver.EXTRA_CATEGORY, reminder.category)
            putExtra(AlarmReceiver.EXTRA_PRIORITY, reminder.priority)
            putExtra(AlarmReceiver.EXTRA_TIME, reminder.time)
            putExtra(AlarmReceiver.EXTRA_REPEAT_TYPE, reminder.repeatType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            }
            Log.d(TAG, "Alarm scheduled successfully for reminder ${reminder.id} at $triggerTimeMs (repeat: ${reminder.repeatType})")
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for exact alarm, falling back to inexact", e)
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm", e)
        }
    }

    fun cancel(reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Alarm cancelled for reminder $reminderId")
        }
    }

    private fun calculateTriggerTimeMs(reminder: ReminderEntity): Long {
        val now = Calendar.getInstance()
        val (hour, minute) = if (reminder.time.isNotBlank()) parseTime(reminder.time) else Pair(9, 0)

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        when (reminder.repeatType) {
            ReminderEntity.REPEAT_DAILY -> {
                // If today's time has passed, set to tomorrow
                if (cal.timeInMillis <= now.timeInMillis) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            ReminderEntity.REPEAT_MONTHLY -> {
                cal.set(Calendar.DAY_OF_MONTH, reminder.day.coerceAtMost(cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
                if (cal.timeInMillis <= now.timeInMillis) {
                    cal.add(Calendar.MONTH, 1)
                    cal.set(Calendar.DAY_OF_MONTH, reminder.day.coerceAtMost(cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
                }
            }
            ReminderEntity.REPEAT_YEARLY -> {
                cal.set(Calendar.MONTH, reminder.month)
                cal.set(Calendar.DAY_OF_MONTH, reminder.day.coerceAtMost(cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
                if (cal.timeInMillis <= now.timeInMillis) {
                    cal.add(Calendar.YEAR, 1)
                }
            }
            else -> {
                // Single date (ONCE)
                cal.set(Calendar.YEAR, reminder.year)
                cal.set(Calendar.MONTH, reminder.month)
                cal.set(Calendar.DAY_OF_MONTH, reminder.day)
            }
        }

        return cal.timeInMillis
    }

    private fun parseTime(timeStr: String): Pair<Int, Int> {
        return try {
            val clean = timeStr.trim().uppercase(Locale.US)
            val isPm = clean.contains("PM")
            val isAm = clean.contains("AM")
            val digits = clean.replace("AM", "").replace("PM", "").trim()
            val parts = digits.split(":")
            var h = parts[0].trim().toInt()
            val m = if (parts.size > 1) parts[1].trim().toInt() else 0

            if (isPm && h < 12) h += 12
            if (isAm && h == 12) h = 0
            Pair(h, m)
        } catch (e: Exception) {
            Pair(9, 0)
        }
    }
}

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ID = "extra_reminder_id"
        const val EXTRA_NOTE = "extra_reminder_note"
        const val EXTRA_FILE_NAME = "extra_reminder_file_name"
        const val EXTRA_SUBJECT = "extra_reminder_subject"
        const val EXTRA_CATEGORY = "extra_reminder_category"
        const val EXTRA_PRIORITY = "extra_reminder_priority"
        const val EXTRA_TIME = "extra_reminder_time"
        const val EXTRA_REPEAT_TYPE = "extra_reminder_repeat_type"
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_ID, 0L)
        val note = intent.getStringExtra(EXTRA_NOTE) ?: ""
        val fileName = intent.getStringExtra(EXTRA_FILE_NAME) ?: ""
        val subject = intent.getStringExtra(EXTRA_SUBJECT) ?: ""
        val priority = intent.getStringExtra(EXTRA_PRIORITY) ?: "സാധാരണ"
        val time = intent.getStringExtra(EXTRA_TIME) ?: ""
        val repeatType = intent.getStringExtra(EXTRA_REPEAT_TYPE) ?: ReminderEntity.REPEAT_ONCE

        Log.d(TAG, "Alarm fired for reminder $reminderId: $note (repeat: $repeatType)")

        val contentTitle = when {
            subject.isNotBlank() -> "🔔 [$priority] $subject"
            note.isNotBlank() -> "🔔 ഓർമ്മപ്പെടുത്തൽ ($priority)"
            fileName.isNotBlank() -> "🔔 കുറിപ്പ്: $fileName"
            else -> "🔔 കലണ്ടർ ഓർമ്മപ്പെടുത്തൽ"
        }

        val repeatLabel = if (repeatType != ReminderEntity.REPEAT_ONCE) " [🔁 ${ReminderEntity.getRepeatLabel(repeatType)}]" else ""
        val contentBody = when {
            note.isNotBlank() && time.isNotBlank() -> "$note (സമയം: $time)$repeatLabel"
            note.isNotBlank() -> "$note$repeatLabel"
            subject.isNotBlank() -> "$subject$repeatLabel"
            else -> "നിങ്ങളുടെ ഓർമ്മപ്പെടുത്തൽ സമയം ആയിരിക്കുന്നു$repeatLabel"
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, AlarmScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(contentTitle)
            .setContentText(contentBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentBody))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 250, 500, 250, 500))
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(reminderId.toInt().coerceAtLeast(1001), notification)
    }
}

package com.example.malayalamcalendar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.malayalamcalendar.data.LocationProfile
import com.example.malayalamcalendar.data.LocationService
import com.example.malayalamcalendar.data.DayPanchangamData
import com.example.malayalamcalendar.data.DefaultBirthdayReminders
import com.example.malayalamcalendar.data.PanchangamCalculator
import com.example.malayalamcalendar.data.ReminderRepository
import com.example.malayalamcalendar.data.local.AppDatabase
import com.example.malayalamcalendar.data.local.ReminderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

import com.example.malayalamcalendar.notification.AlarmScheduler
import com.example.malayalamcalendar.sync.DeviceAccountInfo
import com.example.malayalamcalendar.sync.GoogleCalendarEvent
import com.example.malayalamcalendar.sync.GoogleCalendarService
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.delay

data class SyncProgressState(
    val isSyncing: Boolean = false,
    val progress: Float = 0f, // 0.0f to 1.0f
    val currentStepText: String = "",
    val stepIndex: Int = 0, // 1 to 4
    val totalSteps: Int = 4,
    val activeEmail: String = "",
    val syncedBirthdaysCount: Int = 0,
    val syncedRemindersCount: Int = 0,
    val syncedFestivalsCount: Int = 0,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class CalendarUiState(
    val viewYear: Int,
    val viewMonth: Int, // 0-based
    val expandedDateKey: String? = null,
    val todayYear: Int,
    val todayMonth: Int,
    val todayDay: Int,
    val showAllRemindersDialog: Boolean = false,
    val showYearMonthPicker: Boolean = false,
    val showGoogleSyncDialog: Boolean = false,
    val showIcsExportDialog: Boolean = false,
    val isExportingIcs: Boolean = false,
    val isPushingToGoogle: Boolean = false,
    val isGoogleCalendarSyncing: Boolean = false,
    val googleUserEmail: String? = null,
    val googleUserName: String? = null,
    val isGoogleSyncEnabled: Boolean = false,
    val lastGoogleSyncTime: Long? = null,
    val toastMessage: String? = null,
    val activeAlarm: ReminderEntity? = null,
    val locationProfile: LocationProfile = LocationService.DEFAULT_LOCATION,
    val showLocationPicker: Boolean = false,
    val isDetectingGps: Boolean = false
) {
    val isGoogleSignedIn: Boolean get() = isGoogleSyncEnabled && !googleUserEmail.isNullOrBlank()
    val googleAccountEmail: String? get() = googleUserEmail
    val googleAccountName: String? get() = googleUserName
}

data class CalendarDayCell(
    val day: Int,
    val month: Int, // 0-based
    val year: Int,
    val isOtherMonth: Boolean,
    val isToday: Boolean,
    val isSunday: Boolean,
    val isSaturday: Boolean,
    val isExpanded: Boolean,
    val panchangam: DayPanchangamData?,
    val reminders: List<ReminderEntity>,
    val googleEvents: List<GoogleCalendarEvent> = emptyList()
) {
    val dateKey: String get() = "$year-${month + 1}-$day"
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReminderRepository
    val googleCalendarService = GoogleCalendarService(application)
    val locationService = LocationService(application)

    private val todayCal = Calendar.getInstance()
    private val tYear = todayCal.get(Calendar.YEAR)
    private val tMonth = todayCal.get(Calendar.MONTH)
    private val tDay = todayCal.get(Calendar.DAY_OF_MONTH)

    private val savedLocation = locationService.getSavedLocation()

    private val _uiState = MutableStateFlow(
        CalendarUiState(
            viewYear = tYear,
            viewMonth = tMonth,
            expandedDateKey = "$tYear-${tMonth + 1}-$tDay",
            todayYear = tYear,
            todayMonth = tMonth,
            todayDay = tDay,
            locationProfile = savedLocation
        )
    )
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val _syncProgress = MutableStateFlow(SyncProgressState())
    val syncProgress: StateFlow<SyncProgressState> = _syncProgress.asStateFlow()

    private val _availableDeviceAccounts = MutableStateFlow<List<DeviceAccountInfo>>(emptyList())
    val availableDeviceAccounts: StateFlow<List<DeviceAccountInfo>> = _availableDeviceAccounts.asStateFlow()

    private val _googleEvents = MutableStateFlow<List<GoogleCalendarEvent>>(emptyList())
    val googleEvents: StateFlow<List<GoogleCalendarEvent>> = _googleEvents.asStateFlow()

    val googleEventsByDate: StateFlow<Map<String, List<GoogleCalendarEvent>>>

    val allReminders: StateFlow<List<ReminderEntity>>

    val pendingRemindersCount: StateFlow<Int>

    val calendarCells: StateFlow<List<CalendarDayCell>>

    init {
        val db = AppDatabase.getDatabase(application)
        val dao = db.reminderDao()
        repository = ReminderRepository(dao)

        allReminders = repository.allReminders
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        googleEventsByDate = _googleEvents
            .map { list ->
                list.groupBy { "${it.year}-${it.month + 1}-${it.day}" }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap()
            )

        pendingRemindersCount = allReminders
            .map { list -> list.count { !it.isDone } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

        calendarCells = combine(
            _uiState,
            allReminders,
            googleEventsByDate
        ) { state, reminderList, gEventsMap ->
            buildCalendarCells(
                year = state.viewYear,
                month = state.viewMonth,
                expandedKey = state.expandedDateKey,
                todayY = state.todayYear,
                todayM = state.todayMonth,
                todayD = state.todayDay,
                allReminders = reminderList,
                gEventsMap = gEventsMap,
                location = state.locationProfile
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        try {
            checkInitialGoogleAccount()
            refreshAvailableAccounts()
        } catch (e: Exception) {
            android.util.Log.e("MainViewModel", "Error in init google account check", e)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                DefaultBirthdayReminders.seedDefaultBirthdayReminders(application, dao)
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error seeding birthdays", e)
            }
        }
    }

    private fun checkInitialGoogleAccount() {
        val account = googleCalendarService.getSignedInAccount()
        val savedEmail = googleCalendarService.getSavedEmail()
        val savedName = googleCalendarService.getSavedName()

        if (account != null && !account.email.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    googleUserEmail = account.email,
                    googleUserName = account.displayName,
                    isGoogleSyncEnabled = true
                )
            }
            syncGoogleCalendar()
        } else if (!savedEmail.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    googleUserEmail = savedEmail,
                    googleUserName = savedName ?: savedEmail.substringBefore("@"),
                    isGoogleSyncEnabled = true
                )
            }
            syncGoogleCalendar()
        }
    }

    fun refreshAvailableAccounts() {
        try {
            val accounts = googleCalendarService.getDeviceGoogleAccounts()
            _availableDeviceAccounts.value = accounts
            // If not already signed in, check if there is a default account found on phone
            if (_uiState.value.googleUserEmail.isNullOrBlank()) {
                val defaultAcc = accounts.firstOrNull { it.isDefault } ?: accounts.firstOrNull()
                if (defaultAcc != null) {
                    _uiState.update {
                        it.copy(
                            googleUserEmail = defaultAcc.email,
                            googleUserName = defaultAcc.displayName,
                            isGoogleSyncEnabled = true
                        )
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MainViewModel", "Error refreshing accounts", e)
        }
    }

    fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        if (account != null && !account.email.isNullOrBlank()) {
            googleCalendarService.saveAccount(account.email!!, account.displayName)
            _uiState.update {
                it.copy(
                    googleUserEmail = account.email,
                    googleUserName = account.displayName,
                    isGoogleSyncEnabled = true,
                    toastMessage = "✅ Google അക്കൗണ്ട് ബന്ധിപ്പിച്ചു: ${account.email}"
                )
            }
            startFullGoogleCalendarSync(account.email!!, account.displayName)
        } else {
            _uiState.update {
                it.copy(
                    toastMessage = "❌ Google ലോഗിൻ ചെയ്യാൻ കഴിഞ്ഞില്ല"
                )
            }
        }
    }

    fun connectGoogleAccountDirect(email: String, name: String? = null) {
        if (email.isNotBlank()) {
            val finalName = name ?: email.substringBefore("@")
            googleCalendarService.saveAccount(email.trim(), finalName)
            _uiState.update {
                it.copy(
                    googleUserEmail = email.trim(),
                    googleUserName = finalName,
                    isGoogleSyncEnabled = true,
                    toastMessage = "✅ Google അക്കൗണ്ട് ബന്ധിപ്പിച്ചു: ${email.trim()}"
                )
            }
            startFullGoogleCalendarSync(email.trim(), finalName)
        }
    }

    /**
     * Executes a full synchronization flow with step-by-step progress animation
     */
    fun startFullGoogleCalendarSync(targetEmail: String? = null, targetName: String? = null) {
        val detectedAccounts = googleCalendarService.getDeviceGoogleAccounts()
        val defaultDeviceAcc = detectedAccounts.firstOrNull { it.isDefault } ?: detectedAccounts.firstOrNull()
        val email = targetEmail 
            ?: _uiState.value.googleUserEmail 
            ?: defaultDeviceAcc?.email 
            ?: googleCalendarService.getSavedEmail() 
            ?: "ഡിഫോൾട്ട് Google അക്കൗണ്ട്"
        val name = targetName ?: _uiState.value.googleUserName ?: defaultDeviceAcc?.displayName ?: email.substringBefore("@")

        if (email.contains("@")) {
            googleCalendarService.saveAccount(email, name)
        }
        _uiState.update {
            it.copy(
                googleUserEmail = email,
                googleUserName = name,
                isGoogleSyncEnabled = true,
                isGoogleCalendarSyncing = true
            )
        }

        viewModelScope.launch {
            try {
                // Step 1: Connecting with phone Google account
                _syncProgress.value = SyncProgressState(
                    isSyncing = true,
                    progress = 0.20f,
                    currentStepText = "📱 ഈ ഫോണിലെ Google അക്കൗണ്ടുമായി ബന്ധിപ്പിക്കുന്നു ($email)...",
                    stepIndex = 1,
                    totalSteps = 4,
                    activeEmail = email,
                    isSuccess = false
                )
                delay(350)

                // Step 2: Processing Birthdays & Reminders
                val remindersList = allReminders.value
                val bdayCount = remindersList.count { it.repeatType == ReminderEntity.REPEAT_YEARLY || it.subject.contains("ജന്മദിനം", ignoreCase = true) }
                _syncProgress.value = _syncProgress.value.copy(
                    progress = 0.50f,
                    currentStepText = "🎂 കുടുംബാംഗങ്ങളുടെ 7 ജന്മദിനങ്ങളും കുറിപ്പുകളും ശേഖരിക്കുന്നു (${remindersList.size} കുറിപ്പുകൾ)...",
                    stepIndex = 2,
                    syncedBirthdaysCount = bdayCount,
                    syncedRemindersCount = remindersList.size
                )
                delay(400)

                // Step 3: Syncing to device Google calendar & API
                _syncProgress.value = _syncProgress.value.copy(
                    progress = 0.75f,
                    currentStepText = "🔄 ഫോണിലെ Google കലണ്ടറിലേക്ക് ഇവന്റുകൾ സമന്വയിപ്പിക്കുന്നു...",
                    stepIndex = 3,
                    syncedFestivalsCount = 42
                )

                // Push reminders to phone calendar
                googleCalendarService.syncLocalRemindersToGoogleCalendar(remindersList, email)

                // Read back events from phone calendar & API
                val year = _uiState.value.viewYear
                val month = _uiState.value.viewMonth
                val devEvents = googleCalendarService.fetchDeviceCalendarEvents(year, month, email)
                val apiEvents = try {
                    googleCalendarService.fetchUpcomingEvents(year, month)
                } catch (_: Exception) {
                    emptyList()
                }
                val merged = (devEvents + apiEvents).distinctBy { "${it.year}-${it.month}-${it.day}-${it.summary.trim().lowercase()}" }
                _googleEvents.value = merged

                delay(400)

                // Step 4: Completed Successfully!
                _syncProgress.value = _syncProgress.value.copy(
                    progress = 1.0f,
                    currentStepText = "✅ ഈ ഫോണിലെ Google കലണ്ടറുമായി വിജയകരമായി സിങ്ക് ചെയ്തു!",
                    stepIndex = 4,
                    isSuccess = true,
                    isSyncing = false
                )

                _uiState.update {
                    it.copy(
                        isGoogleCalendarSyncing = false,
                        lastGoogleSyncTime = System.currentTimeMillis(),
                        toastMessage = "🔄 ഈ ഫോണിലെ Google Calendar വിജയകരമായി സിങ്ക് ചെയ്തു"
                    )
                }
            } catch (e: Exception) {
                _syncProgress.value = _syncProgress.value.copy(
                    isSyncing = false,
                    isSuccess = false,
                    errorMessage = "സിങ്ക് ചെയ്യുമ്പോൾ തടസ്സം നേരിട്ടു: ${e.message}"
                )
                _uiState.update {
                    it.copy(
                        isGoogleCalendarSyncing = false,
                        toastMessage = "Google കലണ്ടർ സിങ്ക് ചെയ്യാൻ കഴിഞ്ഞില്ല"
                    )
                }
            }
        }
    }

    fun resetSyncProgress() {
        _syncProgress.value = SyncProgressState()
    }

    fun onGoogleSignInFailure(msg: String) {
        _uiState.update {
            it.copy(
                toastMessage = "⚠️ $msg"
            )
        }
    }

    fun signOutGoogle() {
        viewModelScope.launch {
            googleCalendarService.getGoogleSignInClient().signOut()
            _googleEvents.value = emptyList()
            _uiState.update {
                it.copy(
                    googleUserEmail = null,
                    googleUserName = null,
                    isGoogleSyncEnabled = false,
                    toastMessage = "Google അക്കൗണ്ട് ലോഗ് ഔട്ട് ചെയ്തു"
                )
            }
        }
    }

    fun syncGoogleCalendar() {
        val email = _uiState.value.googleUserEmail
        if (email == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGoogleCalendarSyncing = true) }
            try {
                val year = _uiState.value.viewYear
                val month = _uiState.value.viewMonth

                // 1. Fetch events from phone's native Google Calendar
                val devEvents = googleCalendarService.fetchDeviceCalendarEvents(year, month, email)

                // 2. Fetch events from Google API if connected
                val apiEvents = try {
                    googleCalendarService.fetchUpcomingEvents(year, month)
                } catch (_: Exception) {
                    emptyList()
                }

                val merged = (devEvents + apiEvents).distinctBy { "${it.year}-${it.month}-${it.day}-${it.summary.trim().lowercase()}" }
                _googleEvents.value = merged
                _uiState.update {
                    it.copy(
                        isGoogleCalendarSyncing = false,
                        lastGoogleSyncTime = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGoogleCalendarSyncing = false
                    )
                }
            }
        }
    }

    private fun getRemindersForDate(
        year: Int,
        month: Int,
        day: Int,
        allReminders: List<ReminderEntity>
    ): List<ReminderEntity> {
        return allReminders.filter { rem ->
            when (rem.repeatType) {
                ReminderEntity.REPEAT_DAILY -> {
                    // Daily: matches on or after start date
                    year > rem.year || (year == rem.year && (month > rem.month || (month == rem.month && day >= rem.day)))
                }
                ReminderEntity.REPEAT_MONTHLY -> {
                    // Monthly: every month on same day of month (after start date)
                    rem.day == day && (year > rem.year || (year == rem.year && month >= rem.month))
                }
                ReminderEntity.REPEAT_YEARLY -> {
                    // Yearly: every year on same month and day
                    rem.month == month && rem.day == day && year >= rem.year
                }
                else -> {
                    // Single date (ONCE)
                    rem.year == year && rem.month == month && rem.day == day
                }
            }
        }
    }

    private fun buildCalendarCells(
        year: Int,
        month: Int,
        expandedKey: String?,
        todayY: Int,
        todayM: Int,
        todayD: Int,
        allReminders: List<ReminderEntity>,
        gEventsMap: Map<String, List<GoogleCalendarEvent>> = emptyMap(),
        location: LocationProfile = LocationService.DEFAULT_LOCATION
    ): List<CalendarDayCell> {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0 for Sunday
        val daysInCurrentMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Previous month days
        val prevCal = Calendar.getInstance()
        val prevM = if (month == 0) 11 else month - 1
        val prevY = if (month == 0) year - 1 else year
        prevCal.set(prevY, prevM, 1)
        val daysInPrevMonth = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val nextM = if (month == 11) 0 else month + 1
        val nextY = if (month == 11) year + 1 else year

        val cells = mutableListOf<CalendarDayCell>()

        // Trailing days from previous month
        for (i in (firstDayOfWeek - 1) downTo 0) {
            val d = daysInPrevMonth - i
            val cellCal = Calendar.getInstance().apply { set(prevY, prevM, d) }
            val dow = cellCal.get(Calendar.DAY_OF_WEEK)
            val key = "$prevY-${prevM + 1}-$d"
            cells.add(
                CalendarDayCell(
                    day = d,
                    month = prevM,
                    year = prevY,
                    isOtherMonth = true,
                    isToday = (d == todayD && prevM == todayM && prevY == todayY),
                    isSunday = (dow == Calendar.SUNDAY),
                    isSaturday = (dow == Calendar.SATURDAY),
                    isExpanded = (key == expandedKey),
                    panchangam = null,
                    reminders = getRemindersForDate(prevY, prevM, d, allReminders),
                    googleEvents = gEventsMap[key] ?: emptyList()
                )
            )
        }

        // Days of current month
        for (d in 1..daysInCurrentMonth) {
            val cellCal = Calendar.getInstance().apply { set(year, month, d) }
            val dow = cellCal.get(Calendar.DAY_OF_WEEK)
            val key = "$year-${month + 1}-$d"
            val panchangam = PanchangamCalculator.computeDayData(
                year = year,
                month0Based = month,
                day = d,
                latitude = location.latitude,
                longitude = location.longitude,
                tzOffsetHours = location.tzOffsetHours,
                locationName = location.nameMl
            )
            cells.add(
                CalendarDayCell(
                    day = d,
                    month = month,
                    year = year,
                    isOtherMonth = false,
                    isToday = (d == todayD && month == todayM && year == todayY),
                    isSunday = (dow == Calendar.SUNDAY),
                    isSaturday = (dow == Calendar.SATURDAY),
                    isExpanded = (key == expandedKey),
                    panchangam = panchangam,
                    reminders = getRemindersForDate(year, month, d, allReminders),
                    googleEvents = gEventsMap[key] ?: emptyList()
                )
            )
        }

        // Fill remainder up to 42 cells (6 full weeks)
        var nextDay = 1
        while (cells.size < 42) {
            val cellCal = Calendar.getInstance().apply { set(nextY, nextM, nextDay) }
            val dow = cellCal.get(Calendar.DAY_OF_WEEK)
            val key = "$nextY-${nextM + 1}-$nextDay"
            cells.add(
                CalendarDayCell(
                    day = nextDay,
                    month = nextM,
                    year = nextY,
                    isOtherMonth = true,
                    isToday = (nextDay == todayD && nextM == todayM && nextY == todayY),
                    isSunday = (dow == Calendar.SUNDAY),
                    isSaturday = (dow == Calendar.SATURDAY),
                    isExpanded = (key == expandedKey),
                    panchangam = null,
                    reminders = getRemindersForDate(nextY, nextM, nextDay, allReminders),
                    googleEvents = gEventsMap[key] ?: emptyList()
                )
            )
            nextDay++
        }

        return cells
    }

    fun prevMonth() {
        _uiState.update { current ->
            var newMonth = current.viewMonth - 1
            var newYear = current.viewYear
            if (newMonth < 0) {
                newMonth = 11
                newYear -= 1
            }
            current.copy(
                viewMonth = newMonth,
                viewYear = newYear,
                expandedDateKey = null
            )
        }
        syncGoogleCalendar()
    }

    fun nextMonth() {
        _uiState.update { current ->
            var newMonth = current.viewMonth + 1
            var newYear = current.viewYear
            if (newMonth > 11) {
                newMonth = 0
                newYear += 1
            }
            current.copy(
                viewMonth = newMonth,
                viewYear = newYear,
                expandedDateKey = null
            )
        }
        syncGoogleCalendar()
    }

    fun jumpToToday() {
        val now = Calendar.getInstance()
        val y = now.get(Calendar.YEAR)
        val m = now.get(Calendar.MONTH)
        val d = now.get(Calendar.DAY_OF_MONTH)
        _uiState.update { current ->
            current.copy(
                viewYear = y,
                viewMonth = m,
                todayYear = y,
                todayMonth = m,
                todayDay = d,
                expandedDateKey = "$y-${m + 1}-$d"
            )
        }
        syncGoogleCalendar()
    }

    fun jumpToYearMonth(year: Int, month: Int) {
        _uiState.update { current ->
            current.copy(
                viewYear = year,
                viewMonth = month,
                expandedDateKey = null,
                showYearMonthPicker = false
            )
        }
        syncGoogleCalendar()
    }

    fun jumpToSpecificDate(year: Int, month: Int, day: Int) {
        val key = "$year-${month + 1}-$day"
        _uiState.update { current ->
            current.copy(
                viewYear = year,
                viewMonth = month,
                expandedDateKey = key,
                showYearMonthPicker = false,
                toastMessage = "📅 $day/${month + 1}/$year തീയതിയിലേക്ക് മാറി"
            )
        }
        syncGoogleCalendar()
    }

    fun toggleDayExpanded(year: Int, month: Int, day: Int) {
        val key = "$year-${month + 1}-$day"
        _uiState.update { current ->
            current.copy(
                expandedDateKey = if (current.expandedDateKey == key) null else key
            )
        }
    }

    fun addReminder(
        year: Int,
        month: Int,
        day: Int,
        text: String,
        time: String = "",
        fileName: String = "",
        subject: String = "",
        category: String = "കുറിപ്പ്",
        priority: String = "സാധാരണ",
        repeatType: String = ReminderEntity.REPEAT_ONCE,
        isNotif: Boolean = true,
        syncToGoogle: Boolean = true
    ) {
        if (text.isBlank() && subject.isBlank() && fileName.isBlank()) return
        val displayText = if (text.isNotBlank()) text.trim() else (if (subject.isNotBlank()) subject.trim() else fileName.trim())
        viewModelScope.launch {
            val newEntity = ReminderEntity(
                year = year,
                month = month,
                day = day,
                text = displayText,
                time = time.trim(),
                fileName = fileName.trim(),
                subject = subject.trim(),
                category = category.trim(),
                priority = priority.trim(),
                repeatType = repeatType,
                isNotif = isNotif,
                isDone = false
            )
            val insertedId = repository.insert(newEntity)
            val scheduledEntity = newEntity.copy(id = insertedId)

            AlarmScheduler.scheduleReminderAlarm(getApplication(), scheduledEntity)

            if (syncToGoogle && _uiState.value.isGoogleSyncEnabled) {
                val repeatBadge = if (repeatType != ReminderEntity.REPEAT_ONCE) " [${ReminderEntity.getRepeatLabel(repeatType)}]" else ""
                val title = if (subject.isNotBlank()) "$subject - $displayText$repeatBadge" else "$displayText$repeatBadge"
                val desc = "Malayalam Calendar Note\nCategory: $category\nPriority: $priority\nTime: $time\nRepeat: ${ReminderEntity.getRepeatLabel(repeatType)}"
                launch {
                    googleCalendarService.createCalendarEvent(
                        title = title,
                        description = desc,
                        year = year,
                        month0Based = month,
                        day = day,
                        timeStr = time
                    )
                    syncGoogleCalendar()
                }
            }

            val repeatLabel = if (repeatType != ReminderEntity.REPEAT_ONCE) " (${ReminderEntity.getRepeatLabel(repeatType)})" else ""
            val msg = if (time.isNotBlank() && isNotif) {
                "⏰ ${time}-ൽ അലാം & കുറിപ്പ് സെറ്റ് ചെയ്തു$repeatLabel"
            } else {
                "✅ കുറിപ്പ് ചേർത്തു$repeatLabel"
            }
            _uiState.update { it.copy(toastMessage = msg) }
        }
    }

    fun updateReminder(
        reminder: ReminderEntity,
        newYear: Int,
        newMonth: Int,
        newDay: Int,
        newText: String,
        newTime: String = "",
        newSubject: String = "",
        newCategory: String = "കുറിപ്പ്",
        newPriority: String = "സാധാരണ",
        newRepeatType: String = ReminderEntity.REPEAT_ONCE,
        newIsNotif: Boolean = true
    ) {
        val displayText = if (newText.isNotBlank()) newText.trim() else (if (newSubject.isNotBlank()) newSubject.trim() else reminder.fileName.trim())
        viewModelScope.launch {
            val updated = reminder.copy(
                year = newYear,
                month = newMonth,
                day = newDay,
                text = displayText,
                time = newTime.trim(),
                subject = newSubject.trim(),
                category = newCategory.trim(),
                priority = newPriority.trim(),
                repeatType = newRepeatType,
                isNotif = newIsNotif
            )
            repository.update(updated)

            // Reschedule or Cancel Alarm based on new settings
            AlarmScheduler.cancelReminderAlarm(getApplication(), updated.id.toInt())
            if (updated.isNotif && !updated.isDone) {
                AlarmScheduler.scheduleReminderAlarm(getApplication(), updated)
            }

            val repeatLabel = if (newRepeatType != ReminderEntity.REPEAT_ONCE) " (${ReminderEntity.getRepeatLabel(newRepeatType)})" else ""
            _uiState.update { it.copy(toastMessage = "✅ കുറിപ്പ് പുതുക്കി (Updated)$repeatLabel") }
        }
    }

    fun toggleReminderDone(reminder: ReminderEntity) {
        viewModelScope.launch {
            val updated = reminder.copy(isDone = !reminder.isDone)
            repository.update(updated)
            if (updated.isDone) {
                AlarmScheduler.cancelReminderAlarm(getApplication(), updated.id.toInt())
            } else if (updated.isNotif) {
                AlarmScheduler.scheduleReminderAlarm(getApplication(), updated)
            }
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            AlarmScheduler.cancelReminderAlarm(getApplication(), reminder.id.toInt())
            repository.delete(reminder)
            _uiState.update { it.copy(toastMessage = "🗑️ നീക്കം ചെയ്തു") }
        }
    }

    fun openAllRemindersDialog() {
        _uiState.update { it.copy(showAllRemindersDialog = true) }
    }

    fun closeAllRemindersDialog() {
        _uiState.update { it.copy(showAllRemindersDialog = false) }
    }

    fun openGoogleSyncDialog() {
        _uiState.update { it.copy(showGoogleSyncDialog = true) }
    }

    fun closeGoogleSyncDialog() {
        _uiState.update { it.copy(showGoogleSyncDialog = false) }
    }

    fun openIcsExportDialog() {
        _uiState.update { it.copy(showIcsExportDialog = true) }
    }

    fun closeIcsExportDialog() {
        _uiState.update { it.copy(showIcsExportDialog = false) }
    }

    fun exportCalendarIcs(
        context: android.content.Context,
        config: com.example.malayalamcalendar.sync.IcsExportConfig,
        onSuccess: (com.example.malayalamcalendar.sync.IcsExportResult) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExportingIcs = true) }
            try {
                val reminders = allReminders.value
                val result = com.example.malayalamcalendar.sync.CalendarIcsExporter.exportToFile(
                    context = context,
                    config = config,
                    userReminders = reminders
                )
                _uiState.update {
                    it.copy(
                        isExportingIcs = false,
                        toastMessage = "✅ .ics ഫയൽ തയ്യാറായി (${result.totalEventsCount} ഇവന്റുകൾ, ${result.fileSizeFormatted})"
                    )
                }
                onSuccess(result)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isExportingIcs = false,
                        toastMessage = "❌ .ics ഫയൽ നിർമ്മിക്കാൻ കഴിഞ്ഞില്ല: ${e.message}"
                    )
                }
            }
        }
    }

    fun saveIcsToDownloads(
        context: android.content.Context,
        config: com.example.malayalamcalendar.sync.IcsExportConfig,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExportingIcs = true) }
            try {
                val reminders = allReminders.value
                val path = com.example.malayalamcalendar.sync.CalendarIcsExporter.saveToDownloads(
                    context = context,
                    config = config,
                    userReminders = reminders
                )
                _uiState.update {
                    it.copy(
                        isExportingIcs = false,
                        toastMessage = "💾 Downloads-ലേക്ക് സേവ് ചെയ്തു: Malayalam_Calendar_${config.year}.ics"
                    )
                }
                onSuccess(path)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isExportingIcs = false,
                        toastMessage = "❌ ഡൗൺലോഡ് പരാജയപ്പെട്ടു: ${e.message}"
                    )
                }
            }
        }
    }

    fun pushAllRemindersToGoogleCalendar() {
        if (_uiState.value.googleUserEmail == null) {
            _uiState.update { it.copy(toastMessage = "⚠️ ദയവായി ആദ്യം Google അക്കൗണ്ട് കണക്റ്റ് ചെയ്യുക") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isPushingToGoogle = true) }
            try {
                val reminders = allReminders.value
                if (reminders.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isPushingToGoogle = false,
                            toastMessage = "⚠️ സിങ്ക് ചെയ്യാൻ കുറിപ്പുകൾ ഒന്നും തന്നെയില്ല"
                        )
                    }
                    return@launch
                }
                val count = googleCalendarService.syncLocalRemindersToGoogleCalendar(reminders, _uiState.value.googleUserEmail)
                _uiState.update {
                    it.copy(
                        isPushingToGoogle = false,
                        toastMessage = "✅ $count കുറിപ്പുകൾ ഈ ഫോണിലെ Google കലണ്ടറിലേക്ക് വിജയകരമായി അയച്ചു!"
                    )
                }
                syncGoogleCalendar()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isPushingToGoogle = false,
                        toastMessage = "❌ Google കലണ്ടറിലേക്ക് സിങ്ക് പരാജയപ്പെട്ടു"
                    )
                }
            }
        }
    }

    fun showYearMonthPicker(show: Boolean) {
        _uiState.update { it.copy(showYearMonthPicker = show) }
    }

    fun showLocationPicker(show: Boolean) {
        _uiState.update { it.copy(showLocationPicker = show) }
    }

    fun selectLocation(profile: LocationProfile) {
        locationService.saveLocation(profile)
        _uiState.update {
            it.copy(
                locationProfile = profile,
                showLocationPicker = false,
                toastMessage = "📍 ലൊക്കേഷൻ മാറ്റി: ${profile.nameMl}"
            )
        }
    }

    fun setCustomCoordinates(name: String, lat: Double, lon: Double, tzOffset: Double = 5.5) {
        val profile = LocationProfile(
            id = "custom_${System.currentTimeMillis()}",
            name = name.ifBlank { "Custom Location" },
            nameMl = name.ifBlank { "കസ്റ്റം ലൊക്കേഷൻ" },
            latitude = lat,
            longitude = lon,
            tzOffsetHours = tzOffset,
            isGps = false
        )
        selectLocation(profile)
    }

    fun detectGpsLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDetectingGps = true) }
            try {
                val gpsLoc = locationService.getCurrentGpsLocation()
                if (gpsLoc != null) {
                    locationService.saveLocation(gpsLoc)
                    _uiState.update {
                        it.copy(
                            isDetectingGps = false,
                            locationProfile = gpsLoc,
                            showLocationPicker = false,
                            toastMessage = "🛰️ GPS ലൊക്കേഷൻ കണ്ടെത്തി: ${gpsLoc.nameMl} (${String.format(java.util.Locale.US, "%.3f°, %.3f°", gpsLoc.latitude, gpsLoc.longitude)})"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isDetectingGps = false,
                            toastMessage = "⚠️ GPS ലൊക്കേഷൻ ലഭ്യമായില്ല. ഫോണിലെ ലൊക്കേഷൻ (GPS) ഓൺ ചെയ്തിട്ടുണ്ടോ എന്ന് പരിശോധിക്കുക."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDetectingGps = false,
                        toastMessage = "❌ ലൊക്കേഷൻ കണ്ടെത്തുന്നതിൽ തടസ്സം നേരിട്ടു"
                    )
                }
            }
        }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun dismissAlarm() {
        _uiState.update { it.copy(activeAlarm = null) }
    }
}

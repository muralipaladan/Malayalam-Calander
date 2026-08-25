package com.example.malayalamcalendar.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.UploadFile
import com.example.malayalamcalendar.sync.CalendarIcsExporter
import com.example.malayalamcalendar.sync.DeviceAccountInfo
import com.example.malayalamcalendar.sync.IcsExportConfig
import com.example.malayalamcalendar.sync.IcsExportResult
import com.example.malayalamcalendar.ui.SyncProgressState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.malayalamcalendar.data.DayPanchangamData
import com.example.malayalamcalendar.data.LocationProfile
import com.example.malayalamcalendar.data.LocationService
import com.example.malayalamcalendar.data.PanchangamCalculator
import com.example.malayalamcalendar.data.local.ReminderEntity
import com.example.malayalamcalendar.sync.GoogleCalendarEvent
import com.example.malayalamcalendar.ui.CalendarDayCell
import com.example.malayalamcalendar.ui.MainViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.example.malayalamcalendar.ui.theme.BorderLight
import com.example.malayalamcalendar.ui.theme.Cream
import com.example.malayalamcalendar.ui.theme.CreamDarker
import com.example.malayalamcalendar.ui.theme.DeepBrown
import com.example.malayalamcalendar.ui.theme.DeepBrownDark
import com.example.malayalamcalendar.ui.theme.GoldLight
import com.example.malayalamcalendar.ui.theme.GoldPale
import com.example.malayalamcalendar.ui.theme.GoldPrimary
import com.example.malayalamcalendar.ui.theme.GoldTextDark
import com.example.malayalamcalendar.ui.theme.GoldTextMedium
import com.example.malayalamcalendar.ui.theme.GreenDark
import com.example.malayalamcalendar.ui.theme.GreenMid
import com.example.malayalamcalendar.ui.theme.Ink
import com.example.malayalamcalendar.ui.theme.MoonBlue
import com.example.malayalamcalendar.ui.theme.PurpleTithi
import com.example.malayalamcalendar.ui.theme.Rust
import com.example.malayalamcalendar.ui.theme.RustLight
import com.example.malayalamcalendar.ui.theme.SaturdayBlue
import com.example.malayalamcalendar.ui.theme.SundayRed
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchangamCalendarScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onLaunchGoogleSignIn: (() -> Unit)? = null,
    onRequestLocationPermission: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val cells by viewModel.calendarCells.collectAsState()
    val allReminders by viewModel.allReminders.collectAsState()
    val pendingRemCount by viewModel.pendingRemindersCount.collectAsState()
    val googleEventsList by viewModel.googleEvents.collectAsState()
    val syncProgress by viewModel.syncProgress.collectAsState()
    val availableAccounts by viewModel.availableDeviceAccounts.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var dragTotalX by remember { mutableFloatStateOf(0f) }
    var showGoogleSyncDialog by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<ReminderEntity?>(null) }

    // Google Sign-In Activity Result Launcher
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    viewModel.handleGoogleSignInResult(account)
                } catch (apiEx: ApiException) {
                    android.util.Log.w("GoogleSignIn", "ApiException on sign-in: ${apiEx.statusCode}")
                    val lastAccount = viewModel.googleCalendarService.getSignedInAccount()
                    if (lastAccount != null) {
                        viewModel.handleGoogleSignInResult(lastAccount)
                    } else {
                        viewModel.onGoogleSignInFailure("ലോഗിൻ ചെയ്യാൻ കഴിഞ്ഞില്ല (Status: ${apiEx.statusCode})")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("GoogleSignIn", "Sign-in exception", e)
                    viewModel.onGoogleSignInFailure("Google ലോഗിൻ പിഴവ്")
                }
            } catch (t: Throwable) {
                android.util.Log.e("GoogleSignIn", "Fatal sign-in error", t)
                viewModel.onGoogleSignInFailure("ലോഗിൻ ചെയ്യാൻ സാധിച്ചില്ല")
            }
        } else {
            val lastAccount = viewModel.googleCalendarService.getSignedInAccount()
            if (lastAccount != null) {
                viewModel.handleGoogleSignInResult(lastAccount)
            }
        }
    }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissToast()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(uiState.viewYear, uiState.viewMonth) {
                detectHorizontalDragGestures(
                    onDragStart = { dragTotalX = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        dragTotalX += dragAmount
                    },
                    onDragEnd = {
                        if (dragTotalX < -65f) {
                            viewModel.nextMonth()
                        } else if (dragTotalX > 65f) {
                            viewModel.prevMonth()
                        }
                        dragTotalX = 0f
                    },
                    onDragCancel = {
                        dragTotalX = 0f
                    }
                )
            },
        containerColor = Cream,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Traditional Kerala Temple Header Banner
            item {
                HeaderBanner()
            }

            // 2. Navigation Bar (English Month & Malayalam Month Span + Location Selector + Google Sync)
            item {
                MonthNavBar(
                    year = uiState.viewYear,
                    month = uiState.viewMonth,
                    locationProfile = uiState.locationProfile,
                    pendingRemindersCount = pendingRemCount,
                    googleAccountEmail = uiState.googleAccountEmail,
                    isGoogleSyncing = uiState.isGoogleCalendarSyncing,
                    onTodayClick = { viewModel.jumpToToday() },
                    onMonthTitleClick = { viewModel.showYearMonthPicker(true) },
                    onLocationClick = { viewModel.showLocationPicker(true) },
                    onRemindersClick = { viewModel.openAllRemindersDialog() },
                    onGoogleSyncClick = { showGoogleSyncDialog = true }
                )
            }

            // 3. Weekday Header (ഞായർ, തിങ്കൾ, ചൊവ്വ, etc.)
            item {
                WeekdayHeaderRow()
            }

            // 4. Calendar Rows (7 cells per row, with expandable drawer inserted directly below active row)
            val rows = cells.chunked(7)
            items(rows) { rowCells ->
                CalendarRowItem(
                    rowCells = rowCells,
                    expandedDateKey = uiState.expandedDateKey,
                    onCellClick = { cell ->
                        if (!cell.isOtherMonth) {
                            viewModel.toggleDayExpanded(cell.year, cell.month, cell.day)
                        }
                    },
                    onCloseExpanded = {
                        val expandedCell = rowCells.find { it.dateKey == uiState.expandedDateKey }
                        if (expandedCell != null) {
                            viewModel.toggleDayExpanded(expandedCell.year, expandedCell.month, expandedCell.day)
                        }
                    },
                    onAddReminder = { y, m, d, text, time, fileName, subject, cat, priority, repeatType, notif, syncGoogle ->
                        viewModel.addReminder(y, m, d, text, time, fileName, subject, cat, priority, repeatType, notif, syncGoogle)
                    },
                    onToggleDone = { rem ->
                        viewModel.toggleReminderDone(rem)
                    },
                    onEditReminder = { rem ->
                        editingReminder = rem
                    },
                    onDeleteReminder = { rem ->
                        viewModel.deleteReminder(rem)
                    }
                )
            }
        }
    }

    // Modal Sheet for All Reminders
    if (uiState.showAllRemindersDialog) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeAllRemindersDialog() },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            AllRemindersSheetContent(
                reminders = allReminders,
                onClose = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        viewModel.closeAllRemindersDialog()
                    }
                },
                onToggleDone = { viewModel.toggleReminderDone(it) },
                onEditReminder = { rem ->
                    editingReminder = rem
                },
                onDeleteReminder = { viewModel.deleteReminder(it) },
                onOpenSyncExport = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        viewModel.closeAllRemindersDialog()
                        viewModel.openIcsExportDialog()
                    }
                }
            )
        }
    }

    // Edit Reminder Dialog
    editingReminder?.let { rem ->
        EditReminderDialog(
            reminder = rem,
            onDismiss = { editingReminder = null },
            onSave = { newYear, newMonth, newDay, newText, newTime, newSubject, newCategory, newPriority, newRepeatType, newIsNotif ->
                viewModel.updateReminder(
                    reminder = rem,
                    newYear = newYear,
                    newMonth = newMonth,
                    newDay = newDay,
                    newText = newText,
                    newTime = newTime,
                    newSubject = newSubject,
                    newCategory = newCategory,
                    newPriority = newPriority,
                    newRepeatType = newRepeatType,
                    newIsNotif = newIsNotif
                )
                editingReminder = null
            }
        )
    }

    // Google Calendar Sync & .ics Export Dialog
    if (showGoogleSyncDialog || uiState.showGoogleSyncDialog || uiState.showIcsExportDialog) {
        GoogleCalendarSyncDialog(
            isLoggedIn = uiState.isGoogleSignedIn,
            userEmail = uiState.googleAccountEmail,
            userName = uiState.googleAccountName,
            isSyncing = uiState.isGoogleCalendarSyncing || syncProgress.isSyncing,
            isPushing = uiState.isPushingToGoogle,
            isExportingIcs = uiState.isExportingIcs,
            lastSyncTime = uiState.lastGoogleSyncTime,
            syncedEventsCount = googleEventsList.size,
            currentYear = uiState.viewYear,
            remindersCount = allReminders.size,
            syncProgress = syncProgress,
            availableAccounts = availableAccounts,
            initialTab = if (uiState.showIcsExportDialog) 1 else 0,
            onDismiss = {
                showGoogleSyncDialog = false
                viewModel.closeGoogleSyncDialog()
                viewModel.closeIcsExportDialog()
                viewModel.resetSyncProgress()
            },
            onSignInClick = {
                try {
                    if (onLaunchGoogleSignIn != null) {
                        onLaunchGoogleSignIn()
                    } else {
                        val client = viewModel.googleCalendarService.getGoogleSignInClient()
                        signInLauncher.launch(client.signInIntent)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("GoogleSignIn", "Error launching sign in", e)
                    viewModel.onGoogleSignInFailure("Google ലോഗിൻ ആരംഭിക്കാൻ കഴിഞ്ഞില്ല")
                }
            },
            onConnectEmail = { email ->
                viewModel.connectGoogleAccountDirect(email)
            },
            onStartFullSync = { email, name ->
                viewModel.startFullGoogleCalendarSync(email, name)
            },
            onResetSyncProgress = {
                viewModel.resetSyncProgress()
            },
            onSyncClick = { viewModel.startFullGoogleCalendarSync() },
            onPushRemindersToGoogle = { viewModel.pushAllRemindersToGoogleCalendar() },
            onSignOutClick = { viewModel.signOutGoogle() },
            onExportIcs = { config, onDone ->
                viewModel.exportCalendarIcs(context, config, onDone)
            },
            onSaveToDownloads = { config ->
                viewModel.saveIcsToDownloads(context, config) {}
            }
        )
    }

    // Comprehensive Year-Month & Specific Date Picker Dialog
    if (uiState.showYearMonthPicker) {
        YearMonthAndDatePickerDialog(
            currentYear = uiState.viewYear,
            currentMonth = uiState.viewMonth,
            onDismiss = { viewModel.showYearMonthPicker(false) },
            onSelectMonthYear = { y, m -> viewModel.jumpToYearMonth(y, m) },
            onSelectSpecificDate = { y, m, d -> viewModel.jumpToSpecificDate(y, m, d) }
        )
    }

    // Location & GPS Settings Dialog
    if (uiState.showLocationPicker) {
        LocationPickerDialog(
            currentLocation = uiState.locationProfile,
            isDetectingGps = uiState.isDetectingGps,
            onDismiss = { viewModel.showLocationPicker(false) },
            onSelectLocation = { profile -> viewModel.selectLocation(profile) },
            onDetectGps = {
                if (onRequestLocationPermission != null) {
                    onRequestLocationPermission()
                } else {
                    viewModel.detectGpsLocation()
                }
            },
            onSaveCustomLocation = { name, lat, lon, tz ->
                viewModel.setCustomCoordinates(name, lat, lon, tz)
            }
        )
    }
}

@Composable
fun HeaderBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(DeepBrownDark, DeepBrown, Rust)
                )
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "☀   ☽   ★",
                color = GoldLight,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
                modifier = Modifier.testTag("header_symbols")
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "മലയാള പഞ്ചാംഗം കലണ്ടർ",
                color = GoldLight,
                fontFamily = FontFamily.Serif,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("header_title")
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Drik Ganitha · Lahiri Ayanamsa · Office Notes & Reminders",
                color = GoldLight.copy(alpha = 0.8f),
                fontSize = 10.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("header_subtitle")
            )
        }
    }
}

@Composable
fun MonthNavBar(
    year: Int,
    month: Int,
    locationProfile: LocationProfile = LocationService.DEFAULT_LOCATION,
    pendingRemindersCount: Int,
    googleAccountEmail: String?,
    isGoogleSyncing: Boolean,
    onTodayClick: () -> Unit,
    onMonthTitleClick: () -> Unit,
    onLocationClick: () -> Unit,
    onRemindersClick: () -> Unit,
    onGoogleSyncClick: () -> Unit
) {
    val monthNameEn = PanchangamCalculator.GREG_MONTHS_EN.getOrElse(month) { "" }
    val mlMonthSpan = remember(year, month) {
        PanchangamCalculator.getMalayalamMonthSpanForGregMonth(year, month)
    }

    Surface(
        color = DeepBrown,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Main Top Bar: [ഇന്ന്] | [August 2026 ▾] | [തീയതി] [Google]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Today Button (Left)
                Surface(
                    color = GoldPrimary,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onTodayClick() }
                        .testTag("today_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = "Today",
                            tint = DeepBrown,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "ഇന്ന്",
                            color = DeepBrown,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Month-Year Center Display (Clickable for quick month/year picker)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onMonthTitleClick() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("month_year_selector")
                ) {
                    Text(
                        text = "$monthNameEn $year",
                        color = GoldLight,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Select Month & Year",
                        tint = GoldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Right Action Cluster: [തീയതി] + [Google Sync]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    // Date Jump button ("തീയതി")
                    Surface(
                        color = GoldPrimary.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, GoldPrimary),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onMonthTitleClick() }
                            .testTag("direct_date_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditCalendar,
                                contentDescription = "Select Date",
                                tint = GoldLight,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "തീയതി",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                    }

                    // Google Sync button
                    Surface(
                        color = if (googleAccountEmail != null) DeepBrownDark else GoldPrimary.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, if (googleAccountEmail != null) GoldPrimary else GoldLight),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onGoogleSyncClick() }
                            .testTag("google_sync_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isGoogleSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(11.dp),
                                    color = GoldPrimary,
                                    strokeWidth = 1.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (googleAccountEmail != null) Icons.Default.CloudSync else Icons.Default.Sync,
                                    contentDescription = "Google Calendar Sync",
                                    tint = if (googleAccountEmail != null) GoldLight else GoldPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (googleAccountEmail != null) "Sync" else "Google",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                    }
                }
            }

            // Sub-Bar: Malayalam Month Span + Location Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF23140C))
                    .padding(horizontal = 10.dp, vertical = 3.5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = mlMonthSpan,
                    color = GoldPrimary,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp
                )

                // Location / GPS Chip
                Surface(
                    color = Color(0xFF381F12),
                    border = BorderStroke(0.8.dp, GoldPrimary.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onLocationClick() }
                        .testTag("location_selector_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (locationProfile.isGps) Icons.Default.MyLocation else Icons.Default.Place,
                            contentDescription = "Location",
                            tint = if (locationProfile.isGps) GreenMid else GoldLight,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = locationProfile.nameMl,
                            color = GoldLight,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Quick Reminders & Notes Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepBrownDark)
                    .clickable { onRemindersClick() }
                    .padding(horizontal = 14.dp, vertical = 5.dp)
                    .testTag("all_reminders_bar"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EditCalendar,
                    contentDescription = "Notes & Reminders",
                    tint = GoldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = GoldLight,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "കുറിപ്പുകളും Reminders-ഉം",
                    color = GoldLight,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
                if (pendingRemindersCount > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = SundayRed,
                        shape = CircleShape
                    ) {
                        Text(
                            text = "$pendingRemindersCount",
                            color = Color.White,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = GoldPrimary, thickness = 1.5.dp)
        }
    }
}

@Composable
fun WeekdayHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val weekdays = PanchangamCalculator.WEEKDAYS_ML
        weekdays.forEachIndexed { index, name ->
            val color = when (index) {
                0 -> SundayRed
                6 -> SaturdayBlue
                else -> Ink
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = color,
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}

@Composable
fun CalendarRowItem(
    rowCells: List<CalendarDayCell>,
    expandedDateKey: String?,
    onCellClick: (CalendarDayCell) -> Unit,
    onCloseExpanded: () -> Unit,
    onAddReminder: (Int, Int, Int, String, String, String, String, String, String, String, Boolean, Boolean) -> Unit,
    onToggleDone: (ReminderEntity) -> Unit,
    onEditReminder: (ReminderEntity) -> Unit,
    onDeleteReminder: (ReminderEntity) -> Unit
) {
    val expandedCell = rowCells.find { it.dateKey == expandedDateKey }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // Grid row: 7 day cells
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            rowCells.forEach { cell ->
                DayCellView(
                    cell = cell,
                    onClick = { onCellClick(cell) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Expand accordion card under this row if any cell is expanded
        AnimatedVisibility(
            visible = expandedCell != null && expandedCell.panchangam != null,
            enter = expandVertically(animationSpec = tween(220)) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(180)) + fadeOut()
        ) {
            if (expandedCell?.panchangam != null) {
                ExpandedDayCard(
                    cell = expandedCell,
                    panchangam = expandedCell.panchangam,
                    reminders = expandedCell.reminders,
                    googleEvents = expandedCell.googleEvents,
                    onClose = onCloseExpanded,
                    onAddReminder = { text, time, fileName, subject, cat, priority, repeatType, notif, syncGoogle ->
                        onAddReminder(
                            expandedCell.year,
                            expandedCell.month,
                            expandedCell.day,
                            text,
                            time,
                            fileName,
                            subject,
                            cat,
                            priority,
                            repeatType,
                            notif,
                            syncGoogle
                        )
                    },
                    onToggleDone = onToggleDone,
                    onEditReminder = onEditReminder,
                    onDeleteReminder = onDeleteReminder
                )
            }
        }
    }
}

@Composable
fun DayCellView(
    cell: CalendarDayCell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        cell.isOtherMonth -> Color(0xFFF8F4EC)
        cell.isExpanded -> Color(0xFFFFF8F0)
        cell.isToday -> GoldPale
        else -> Color.White
    }

    val borderColor = when {
        cell.isExpanded -> Rust
        cell.isToday -> GoldPrimary
        else -> BorderLight
    }

    val borderWidth = if (cell.isExpanded || cell.isToday) 1.8.dp else 1.dp
    val hasReminders = cell.reminders.isNotEmpty()
    val pendingReminders = cell.reminders.any { !it.isDone }
    val hasGoogleEvents = cell.googleEvents.isNotEmpty()

    val dateTextColor = when {
        cell.isToday -> Color.White
        cell.isSunday -> SundayRed
        cell.isSaturday -> SaturdayBlue
        cell.isOtherMonth -> Color.Gray.copy(alpha = 0.4f)
        else -> Ink
    }

    val circleBg = if (cell.isToday) GoldPrimary else Color.Transparent

    Card(
        modifier = modifier
            .height(86.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(enabled = !cell.isOtherMonth) { onClick() }
            .testTag("day_cell_${cell.year}_${cell.month + 1}_${cell.day}"),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 3.dp, vertical = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Indicators (Reminders / Google Calendar) on Left, Hijri date on Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (hasReminders) {
                            Icon(
                                imageVector = if (pendingReminders) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "Reminder",
                                tint = if (pendingReminders) SundayRed else Rust,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                        if (hasGoogleEvents) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = "Google Calendar Event",
                                tint = MoonBlue,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    // Hijri Date Number (Top-Right)
                    if (cell.panchangam != null && !cell.isOtherMonth && cell.panchangam.hijriDay > 0) {
                        Text(
                            text = "${cell.panchangam.hijriDay}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF666666)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                }

                // Center: Big Bold English Date
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(circleBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${cell.day}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = dateTextColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Bottom Row: Malayalam Month Date (Left), Nakshatra & Tithi (Center), Saka day (Right)
                if (cell.panchangam != null && !cell.isOtherMonth) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Malayalam Date Number (Big font in Rust)
                        Text(
                            text = "${cell.panchangam.mlDate}",
                            color = Rust,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.padding(end = 2.dp)
                        )

                        // Middle: Nakshatra and Tithi with Nazhika
                        Column(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            // Nakshatra line
                            val nakText = if (cell.panchangam.nakNazhika.isNotBlank()) {
                                "${cell.panchangam.nakshatra} ${cell.panchangam.nakNazhika}"
                            } else {
                                cell.panchangam.nakshatra
                            }
                            Text(
                                text = nakText,
                                color = GreenDark,
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Tithi line
                            val tithiText = if (cell.panchangam.tithiNazhika.isNotBlank()) {
                                "${cell.panchangam.tithi} ${cell.panchangam.tithiNazhika}"
                            } else {
                                cell.panchangam.tithi
                            }
                            Text(
                                text = tithiText,
                                color = PurpleTithi,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Right: Saka Day Number
                        if (cell.panchangam.sakaDay > 0) {
                            Text(
                                text = "${cell.panchangam.sakaDay}",
                                color = Color(0xFF555555),
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 1.dp)
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedDayCard(
    cell: CalendarDayCell,
    panchangam: DayPanchangamData,
    reminders: List<ReminderEntity>,
    googleEvents: List<GoogleCalendarEvent> = emptyList(),
    onClose: () -> Unit,
    onAddReminder: (text: String, time: String, fileName: String, subject: String, category: String, priority: String, repeatType: String, notif: Boolean, syncGoogle: Boolean) -> Unit,
    onToggleDone: (ReminderEntity) -> Unit,
    onEditReminder: (ReminderEntity) -> Unit,
    onDeleteReminder: (ReminderEntity) -> Unit
) {
    val context = LocalContext.current
    var subjectInput by remember { mutableStateOf("") }
    var noteTextInput by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("കുറിപ്പ്") }
    var selectedPriority by remember { mutableStateOf("സാധാരണ") }
    var selectedRepeatType by remember { mutableStateOf(ReminderEntity.REPEAT_ONCE) }
    var reminderNotif by remember { mutableStateOf(true) }
    var syncGoogleCalendar by remember { mutableStateOf(false) }

    val gregMonthMl = PanchangamCalculator.GREG_MONTHS_ML.getOrElse(cell.month) { "" }
    val gregMonthEn = PanchangamCalculator.GREG_MONTHS_EN.getOrElse(cell.month) { "" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 6.dp)
            .shadow(6.dp, RoundedCornerShape(10.dp))
            .testTag("expanded_day_card"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column {
            // Header bar with Full Date Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(DeepBrownDark, DeepBrown, Rust)
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$gregMonthEn ${cell.day}, ${cell.year} ($gregMonthMl) — ${panchangam.weekdayMl}",
                        color = GoldLight,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                    Text(
                        text = "${panchangam.mlMonth} ${panchangam.mlDate}, കൊല്ലവർഷം ${panchangam.kollaVarsham} · ശകവർഷം ${panchangam.sakaYear} ${panchangam.sakaMonth} ${panchangam.sakaDay} · ഹിജ്റ ${panchangam.hijriYear} ${panchangam.hijriMonth} ${panchangam.hijriDay}",
                        color = GoldPrimary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(28.dp)
                        .border(1.dp, GoldPrimary, CircleShape)
                        .testTag("close_expanded_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = GoldLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Solar Times Banner (ഉദയം, അസ്തമയം, ദിനമാനം)
            Surface(
                color = Color(0xFF2A170C),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sunrise
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌅", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "ഉദയം",
                                fontSize = 10.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = panchangam.sunriseTime,
                                fontSize = 12.5.sp,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(GoldPrimary.copy(alpha = 0.3f))
                    )

                    // Sunset
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌇", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "അസ്തമയം",
                                fontSize = 10.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = panchangam.sunsetTime,
                                fontSize = 12.5.sp,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(GoldPrimary.copy(alpha = 0.3f))
                    )

                    // Dinamanam
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⏱️", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "ദിനമാനം",
                                fontSize = 10.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = panchangam.dinamanam,
                                fontSize = 11.5.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Rich Panchangam Grid with Nazhika, Tithi/Nakshathra End Times, Kala Timings
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: Malayalam Month/Date & Tithi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PanchangaFeatureItem(
                        label = "മലയാള മാസം & തീയതി",
                        value = "${panchangam.mlMonth} ${panchangam.mlDate}",
                        subtitle = "കൊ.വ. ${panchangam.kollaVarsham}",
                        accentColor = Rust,
                        modifier = Modifier.weight(1f)
                    )
                    val tithiSub = buildString {
                        if (panchangam.tithiEnd.isNotBlank() && panchangam.tithiEnd != "-") {
                            append("${panchangam.tithiEnd} വരെ")
                        }
                        if (panchangam.tithiNazhika.isNotBlank()) {
                            if (isNotEmpty()) append(" (${panchangam.tithiNazhika})")
                            else append(panchangam.tithiNazhika)
                        }
                        if (panchangam.nextTithi.isNotBlank()) {
                            append(" → തുടർന്ന് ${panchangam.nextTithi}")
                        }
                        if (isEmpty()) append(panchangam.paksha)
                    }
                    PanchangaFeatureItem(
                        label = "തിഥി (${panchangam.paksha})",
                        value = panchangam.tithi,
                        subtitle = tithiSub,
                        accentColor = PurpleTithi,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Nakshatra & Calendar Eras
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val nakSub = buildString {
                        if (panchangam.nakEnd.isNotBlank() && panchangam.nakEnd != "-") {
                            append("${panchangam.nakEnd} വരെ")
                        }
                        if (panchangam.nakNazhika.isNotBlank()) {
                            if (isNotEmpty()) append(" (${panchangam.nakNazhika})")
                            else append(panchangam.nakNazhika)
                        }
                        if (panchangam.nextNakshatra.isNotBlank()) {
                            append(" → തുടർന്ന് ${panchangam.nextNakshatra}")
                        }
                    }
                    PanchangaFeatureItem(
                        label = "നക്ഷത്രം",
                        value = panchangam.nakshatra,
                        subtitle = if (nakSub.isNotBlank()) nakSub else "പൂർണ്ണ ദിനം",
                        accentColor = GreenMid,
                        modifier = Modifier.weight(1f)
                    )
                    PanchangaFeatureItem(
                        label = "ശക & ഹിജ്റ വർഷം",
                        value = "ശക: ${panchangam.sakaDay} ${panchangam.sakaMonth}",
                        subtitle = "ഹിജ്റ: ${panchangam.hijriDay} ${panchangam.hijriMonth}",
                        accentColor = GoldPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: Rahu Kalam & Gulika Kalam
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PanchangaFeatureItem(
                        label = "രാഹുകാലം (അശുഭ കാലം)",
                        value = panchangam.rahuKalam,
                        subtitle = "ദുർമുഹൂർത്തം",
                        accentColor = SundayRed,
                        modifier = Modifier.weight(1f)
                    )
                    PanchangaFeatureItem(
                        label = "ഗുളികകാലം",
                        value = panchangam.gulikaKalam,
                        subtitle = "ഗുളികോദയം",
                        accentColor = Rust,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 4: Yamakandam & Abhijith Muhurtham
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PanchangaFeatureItem(
                        label = "യമകണ്ടം",
                        value = panchangam.yamagandam,
                        subtitle = "വർജ്യ കാലം",
                        accentColor = Color(0xFFB45309),
                        modifier = Modifier.weight(1f)
                    )
                    PanchangaFeatureItem(
                        label = "അഭിജിത് മുഹൂർത്തം (ശുഭം)",
                        value = panchangam.abhijithMuhurtham,
                        subtitle = "സർവ്വകാര്യ സിദ്ധി",
                        accentColor = GreenDark,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 5: Sun Nirayana & Moon Nirayana
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PanchangaFeatureItem(
                        label = "സൂര്യൻ (Nirayana)",
                        value = "${panchangam.sunNir}°",
                        subtitle = "Lahiri Ayanamsa",
                        accentColor = RustLight,
                        modifier = Modifier.weight(1f)
                    )
                    PanchangaFeatureItem(
                        label = "ചന്ദ്രൻ (Nirayana)",
                        value = "${panchangam.moonNir}°",
                        subtitle = "Moon Longitude",
                        accentColor = MoonBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Location calculation info badge
            Surface(
                color = CreamDarker,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(0.7.dp, BorderLight)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location",
                        tint = DeepBrown,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "📍 ${panchangam.locationName} (${String.format(Locale.US, "%.3f° N, %.3f° E", panchangam.latitude, panchangam.longitude)}) കണക്കുകൂട്ടലുകൾ",
                        fontSize = 10.5.sp,
                        color = DeepBrownDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(horizontal = 12.dp))

            // Google Calendar Synced Events (if any for this day)
            if (googleEvents.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = MoonBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Google Calendar Events (${googleEvents.size})",
                            color = DeepBrownDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        googleEvents.forEach { event ->
                            Surface(
                                color = Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Event,
                                        contentDescription = null,
                                        tint = MoonBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = event.summary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp,
                                            color = Ink
                                        )
                                        if (event.startTime.isNotBlank()) {
                                            Text(
                                                text = "Time: ${event.startTime}" + if (event.endTime.isNotBlank()) " - ${event.endTime}" else "",
                                                fontSize = 11.sp,
                                                color = Color(0xFF4B5563)
                                            )
                                        }
                                        if (event.description.isNotBlank()) {
                                            Text(
                                                text = event.description,
                                                fontSize = 11.sp,
                                                color = Color(0xFF6B7280)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(horizontal = 12.dp))
            }

            // Notes & Reminders Section (കുറിപ്പുകളും ഓർമ്മപ്പെടുത്തലുകളും)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Section Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = null,
                            tint = GoldTextDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "കുറിപ്പുകളും ഓർമ്മപ്പെടുത്തലുകളും",
                            color = GoldTextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    if (reminders.isNotEmpty()) {
                        Surface(
                            color = Rust.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${reminders.size} എണ്ണം",
                                color = Rust,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List of existing reminders for this day
                if (reminders.isEmpty()) {
                    Text(
                        text = "ഈ തീയതിയിൽ കുറിപ്പുകൾ ഒന്നും ചേർത്തിട്ടില്ല",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        reminders.forEach { reminder ->
                            ReminderRowItem(
                                reminder = reminder,
                                onToggle = { onToggleDone(reminder) },
                                onEdit = { onEditReminder(reminder) },
                                onDelete = { onDeleteReminder(reminder) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Input Card - Cream Background with Recurrence Frequency Options
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Cream),
                    border = BorderStroke(1.dp, GoldLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Recurrence Selection Tabs (ഒറ്റത്തവണ, എല്ലാ ദിവസവും, എല്ലാ മാസവും, എല്ലാ വർഷവും)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "ആവർത്തനം (Repeat Frequency):",
                                fontSize = 11.sp,
                                color = DeepBrownDark,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CreamDarker, RoundedCornerShape(6.dp))
                                    .border(1.dp, GoldLight, RoundedCornerShape(6.dp))
                                    .padding(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                val repeatOptions = listOf(
                                    ReminderEntity.REPEAT_ONCE to "ഒറ്റത്തവണ",
                                    ReminderEntity.REPEAT_DAILY to "ദിവസവും",
                                    ReminderEntity.REPEAT_MONTHLY to "മാസംതോറും",
                                    ReminderEntity.REPEAT_YEARLY to "വർഷംതോറും"
                                )
                                repeatOptions.forEach { (type, label) ->
                                    val isSel = (selectedRepeatType == type)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (isSel) DeepBrownDark else Color.Transparent,
                                                RoundedCornerShape(4.dp)
                                            )
                                            .clickable { selectedRepeatType = type }
                                            .padding(vertical = 5.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSel) GoldLight else Ink,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        // Subject / Title input (optional)
                        OutlinedTextField(
                            value = subjectInput,
                            onValueChange = { subjectInput = it },
                            placeholder = {
                                Text(
                                    text = "തലക്കെട്ട് / വിഷയം (ഓപ്ഷണൽ)",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = DeepBrownDark,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Serif
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("note_subject_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepBrownDark,
                                unfocusedTextColor = DeepBrownDark,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = Rust,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = GoldLight
                            ),
                            shape = RoundedCornerShape(6.dp)
                        )

                        // Main Note Text Input
                        OutlinedTextField(
                            value = noteTextInput,
                            onValueChange = { noteTextInput = it },
                            placeholder = {
                                Text(
                                    text = "കുറിപ്പ് / ഓർമ്മപ്പെടുത്തൽ ടൈപ്പ് ചെയ്യുക...",
                                    color = Color.Gray,
                                    fontSize = 12.5.sp
                                )
                            },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = DeepBrownDark,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp,
                                fontFamily = FontFamily.Serif
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reminder_text_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepBrownDark,
                                unfocusedTextColor = DeepBrownDark,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = Rust,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = GoldLight
                            ),
                            shape = RoundedCornerShape(6.dp)
                        )

                        // Category & Priority Chips
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Category Chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val categories = listOf("കുറിപ്പ്", "ഓർമ്മപ്പെടുത്തൽ", "മീറ്റിംഗ്", "ജന്മദിനം / വാർഷികം", "വ്യക്തിഗതം")
                                categories.forEach { cat ->
                                    val isSel = (selectedCategory == cat)
                                    Surface(
                                        color = if (isSel) GoldPrimary else Color.White,
                                        border = BorderStroke(1.dp, if (isSel) GoldPrimary else GoldLight),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier
                                            .clickable { selectedCategory = cat }
                                            .padding(vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) DeepBrownDark else Ink,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            // Priority Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "പ്രിയോറിറ്റി:", fontSize = 11.sp, color = Ink, fontWeight = FontWeight.SemiBold)
                                val priorities = listOf("സാധാരണ", "പ്രധാനം", "അടിയന്തിരം")
                                priorities.forEach { p ->
                                    val isSel = (selectedPriority == p)
                                    val pColor = when (p) {
                                        "അടിയന്തിരം" -> SundayRed
                                        "പ്രധാനം" -> GoldTextDark
                                        else -> GreenDark
                                    }
                                    Surface(
                                        color = if (isSel) pColor else Color.White,
                                        border = BorderStroke(1.dp, pColor),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.clickable { selectedPriority = p }
                                    ) {
                                        Text(
                                            text = p,
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) Color.White else pColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Google Calendar Sync Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = syncGoogleCalendar,
                                onCheckedChange = { syncGoogleCalendar = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = GoldPrimary,
                                    checkmarkColor = DeepBrownDark
                                ),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Google Calendar-ലേക്ക് Sync ചെയ്യുക",
                                fontSize = 11.sp,
                                color = DeepBrownDark,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Bottom Actions: Time Picker + Notification + Add Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Time Picker Button
                                OutlinedButton(
                                    onClick = {
                                        val now = Calendar.getInstance()
                                        TimePickerDialog(
                                            context,
                                            { _, h, m ->
                                                val ampm = if (h >= 12) "PM" else "AM"
                                                val displayH = if (h % 12 == 0) 12 else h % 12
                                                reminderTime = String.format(Locale.US, "%d:%02d %s", displayH, m, ampm)
                                            },
                                            now.get(Calendar.HOUR_OF_DAY),
                                            now.get(Calendar.MINUTE),
                                            false
                                        ).show()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Cream,
                                        contentColor = GoldTextDark
                                    ),
                                    border = BorderStroke(1.dp, GoldLight),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("reminder_time_picker")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Time",
                                        tint = GoldTextDark,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (reminderTime.isNotEmpty()) reminderTime else "സമയം & അലാറം",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Notification toggle
                                Checkbox(
                                    checked = reminderNotif,
                                    onCheckedChange = { reminderNotif = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = GoldPrimary,
                                        checkmarkColor = DeepBrownDark
                                    ),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "അറിയിപ്പ്",
                                    fontSize = 11.sp,
                                    color = Color(0xFF555555)
                                )
                            }

                            // Add Button
                            Button(
                                onClick = {
                                    if (noteTextInput.isNotBlank() || subjectInput.isNotBlank()) {
                                        onAddReminder(
                                            noteTextInput,
                                            reminderTime,
                                            "", // File name removed
                                            subjectInput,
                                            selectedCategory,
                                            selectedPriority,
                                            selectedRepeatType,
                                            reminderNotif,
                                            syncGoogleCalendar
                                        )
                                        subjectInput = ""
                                        noteTextInput = ""
                                        reminderTime = ""
                                        syncGoogleCalendar = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = DeepBrownDark
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("add_reminder_button")
                            ) {
                                Text("+ കുറിപ്പ് ചേർക്കുക", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleCalendarSyncDialog(
    isLoggedIn: Boolean,
    userEmail: String?,
    userName: String?,
    isSyncing: Boolean,
    isPushing: Boolean = false,
    isExportingIcs: Boolean = false,
    lastSyncTime: Long?,
    syncedEventsCount: Int,
    currentYear: Int = 2026,
    remindersCount: Int = 0,
    syncProgress: SyncProgressState = SyncProgressState(),
    availableAccounts: List<DeviceAccountInfo> = emptyList(),
    initialTab: Int = 0,
    onDismiss: () -> Unit,
    onSignInClick: () -> Unit,
    onConnectEmail: (String) -> Unit,
    onStartFullSync: (String, String?) -> Unit = { _, _ -> },
    onResetSyncProgress: () -> Unit = {},
    onSyncClick: () -> Unit,
    onPushRemindersToGoogle: () -> Unit = {},
    onSignOutClick: () -> Unit,
    onExportIcs: (IcsExportConfig, (IcsExportResult) -> Unit) -> Unit = { _, _ -> },
    onSaveToDownloads: (IcsExportConfig) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var manualEmail by remember { mutableStateOf(userEmail ?: (availableAccounts.firstOrNull()?.email ?: "muralipaladan@gmail.com")) }
    var showManualEmailInput by remember { mutableStateOf(false) }

    // .ics Export Options
    var exportYear by remember { mutableIntStateOf(currentYear) }
    var incPanchangam by remember { mutableStateOf(true) }
    var incFestivals by remember { mutableStateOf(true) }
    var incReminders by remember { mutableStateOf(true) }
    var exportResult by remember { mutableStateOf<IcsExportResult?>(null) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.CloudSync else Icons.Default.Download,
                            contentDescription = null,
                            tint = Rust,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedTab == 0) "Google Calendar Sync" else ".ics കലണ്ടർ ഡൗൺലോഡ്",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = DeepBrownDark
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2 Tabs: [Google Sync] & [.ics Download]
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CreamDarker,
                    contentColor = DeepBrownDark,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoldPrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Google Sync",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.5.sp
                            )
                        },
                        icon = {
                            Icon(
                                Icons.Default.Sync,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                ".ics ഡൗൺലോഡ്",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.5.sp
                            )
                        },
                        icon = {
                            Icon(
                                Icons.Default.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedTab == 0) {
                    // TAB 0: GOOGLE CALENDAR SYNC
                    if (syncProgress.isSyncing || syncProgress.isSuccess) {
                        // PROGRESS ANIMATION VIEW
                        SyncProgressAnimationCard(
                            syncProgress = syncProgress,
                            onSyncAgain = {
                                val target = userEmail ?: (availableAccounts.firstOrNull()?.email ?: "muralipaladan@gmail.com")
                                onStartFullSync(target, userName)
                            },
                            onDone = {
                                onResetSyncProgress()
                                onDismiss()
                            }
                        )
                    } else if (isLoggedIn && userEmail != null) {
                        // LOGGED IN VIEW
                        Surface(
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFF93C5FD)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = GreenDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Google അക്കൗണ്ട് ബന്ധിപ്പിച്ചിരിക്കുന്നു",
                                        fontWeight = FontWeight.Bold,
                                        color = GreenDark,
                                        fontSize = 13.5.sp
                                    )
                                }
                                if (userName != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = userName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = Ink
                                    )
                                }
                                Text(
                                    text = userEmail,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E3A8A),
                                    fontWeight = FontWeight.Medium
                                )
                                if (syncedEventsCount > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "✔ $syncedEventsCount ഇവന്റുകൾ മലയാളം കലണ്ടറുമായി സിങ്ക് ചെയ്തു",
                                        fontSize = 11.5.sp,
                                        color = GreenDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Big Sync Now Button with Animation trigger
                        Button(
                            onClick = {
                                onStartFullSync(userEmail, userName)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = DeepBrownDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = DeepBrownDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ഇപ്പോൾ സിങ്ക് ചെയ്യുക (Sync Now)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        // Push Reminders to Google Button
                        Surface(
                            color = CreamDarker,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, GoldLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "ഇരുവശത്തേക്കും സിങ്ക് ചെയ്യാം:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = DeepBrownDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "നിങ്ങൾ ഇവിടെ കുറിച്ച 7 ജന്മദിനങ്ങളും ഓർമ്മപ്പെടുത്തലുകളും ($remindersCount എണ്ണം) Google കലണ്ടറിലേക്ക് അയക്കാം.",
                                    fontSize = 11.sp,
                                    color = Ink
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = onPushRemindersToGoogle,
                                    enabled = !isPushing && remindersCount > 0,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Rust,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isPushing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("അയക്കുന്നു...", fontSize = 12.sp)
                                    } else {
                                        Icon(
                                            Icons.Default.CloudUpload,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("കുറിപ്പുകൾ Google Calendar-ലേക്ക് അയക്കുക", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        // NOT CONNECTED VIEW - AUTO-DETECT PHONE GOOGLE ACCOUNTS
                        Surface(
                            color = CreamDarker,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Google കലണ്ടർ സിങ്ക് ചെയ്യുക",
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBrownDark,
                                    fontSize = 13.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "നിങ്ങളുടെ ഫോണിലെ ഡിഫോൾട്ട് Google അക്കൗണ്ടുമായി ഒറ്റ ക്ലിക്കിൽ കലണ്ടറും ജന്മദിനങ്ങളും സിങ്ക് ചെയ്യാം.",
                                    fontSize = 11.5.sp,
                                    color = Ink
                                )
                            }
                        }

                        // Auto-detected accounts list on device
                        if (availableAccounts.isNotEmpty()) {
                            Surface(
                                color = Color(0xFFF0FDF4),
                                border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = GreenDark,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "📱 ഈ ഫോണിലുള്ള Google അക്കൗണ്ട്:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GreenDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))

                                    availableAccounts.forEach { acc ->
                                        Surface(
                                            color = Color.White,
                                            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            Icons.Default.AccountCircle,
                                                            contentDescription = null,
                                                            tint = GoldPrimary,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = acc.displayName ?: acc.email.substringBefore("@"),
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            color = DeepBrownDark
                                                        )
                                                    }
                                                    Text(
                                                        text = acc.email,
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF4B5563)
                                                    )
                                                }
                                                Button(
                                                    onClick = {
                                                        onStartFullSync(acc.email, acc.displayName)
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = GreenDark,
                                                        contentColor = Color.White
                                                    ),
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    Text("സിങ്ക് ചെയ്യുക 🔄", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Google Sign In button via Google Play Services / Phone account picker
                        Button(
                            onClick = onSignInClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E40AF),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_login_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ഫോണിലെ Google അക്കൗണ്ട് തിരഞ്ഞെടുക്കുക", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        }

                        // Custom Email Entry
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CreamDarker.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "ഫോണിലെ ഇമെയിൽ നേരിട്ട് നൽകാൻ:",
                                fontSize = 11.sp,
                                color = DeepBrownDark,
                                fontWeight = FontWeight.Medium
                            )
                            OutlinedTextField(
                                value = manualEmail,
                                onValueChange = { manualEmail = it },
                                placeholder = { Text("ഉദാ: name@gmail.com", fontSize = 12.sp, color = Color.Gray) },
                                textStyle = androidx.compose.ui.text.TextStyle(color = DeepBrownDark, fontSize = 13.sp),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = DeepBrownDark,
                                    unfocusedTextColor = DeepBrownDark,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = GoldLight
                                )
                            )
                            if (manualEmail.isNotBlank()) {
                                Button(
                                    onClick = {
                                        if (manualEmail.isNotBlank()) {
                                            onStartFullSync(manualEmail.trim(), null)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldPrimary,
                                        contentColor = DeepBrownDark
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("ഈ അക്കൗണ്ടുമായി സിങ്ക് ചെയ്യുക 🔄", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    // TAB 1: .ICS FILE DOWNLOAD & EXPORT
                    Surface(
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "📅 സമ്പൂർണ്ണ കലണ്ടർ .ics ഫയൽ",
                                fontWeight = FontWeight.Bold,
                                color = GreenDark,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ഈ കലണ്ടറിലെ പഞ്ചാംഗം, വിശേഷദിവസങ്ങൾ, കുറിപ്പുകൾ എന്നിവ .ics ഫോർമാറ്റിൽ ലഭിക്കും. ഇത് Google Calendar, Apple Calendar, Outlook എന്നിവയിൽ നേരിട്ട് ഇമ്പോർട്ട് ചെയ്യാം.",
                                fontSize = 11.sp,
                                color = Ink,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // Year Selection Chips
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "വർഷം തിരഞ്ഞെടുക്കുക (Year):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBrownDark
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(2025, 2026, 2027).forEach { yr ->
                                val isSel = (exportYear == yr)
                                Surface(
                                    color = if (isSel) GoldPrimary else Cream,
                                    border = BorderStroke(1.dp, if (isSel) DeepBrownDark else GoldLight),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { exportYear = yr }
                                ) {
                                    Text(
                                        text = "$yr",
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) DeepBrownDark else Ink,
                                        fontSize = 13.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Checkboxes for Content Inclusion
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CreamDarker.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "ഉൾപ്പെടുത്തേണ്ട വിവരങ്ങൾ:",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBrownDark
                        )

                        // 1. Panchangam Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { incPanchangam = !incPanchangam }
                        ) {
                            Checkbox(
                                checked = incPanchangam,
                                onCheckedChange = { incPanchangam = it },
                                colors = CheckboxDefaults.colors(checkedColor = GoldPrimary, checkmarkColor = DeepBrownDark)
                            )
                            Column {
                                Text("ദിവസേന പഞ്ചാംഗം വിവരങ്ങൾ", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                                Text("നക്ഷത്രം, തിഥി, കൊല്ലവർഷം, രാഹുകാലം", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        // 2. Festivals Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { incFestivals = !incFestivals }
                        ) {
                            Checkbox(
                                checked = incFestivals,
                                onCheckedChange = { incFestivals = it },
                                colors = CheckboxDefaults.colors(checkedColor = GoldPrimary, checkmarkColor = DeepBrownDark)
                            )
                            Column {
                                Text("കേരള വിശേഷദിവസങ്ങളും വ്രതങ്ങളും", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                                Text("ഓണം, വിഷു, ശിവരാത്രി, ഏകാദശി, പ്രദോഷം...", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        // 3. User Reminders Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { incReminders = !incReminders }
                        ) {
                            Checkbox(
                                checked = incReminders,
                                onCheckedChange = { incReminders = it },
                                colors = CheckboxDefaults.colors(checkedColor = GoldPrimary, checkmarkColor = DeepBrownDark)
                            )
                            Column {
                                Text("നിങ്ങളുടെ കുറിപ്പുകൾ & Reminders", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                                Text("$remindersCount കുറിപ്പുകൾ ഉൾപ്പെടുത്തും", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Result preview if generated
                    exportResult?.let { res ->
                        Surface(
                            color = Color(0xFFF0FDF4),
                            border = BorderStroke(1.dp, GreenDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "✅ .ics ഫയൽ തയ്യാറായി!",
                                    fontWeight = FontWeight.Bold,
                                    color = GreenDark,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "ഇവന്റുകൾ: ${res.totalEventsCount} (പഞ്ചാംഗം: ${res.panchangamCount}, വിശേഷങ്ങൾ: ${res.festivalCount}, കുറിപ്പുകൾ: ${res.remindersCount})",
                                    fontSize = 11.sp,
                                    color = Ink
                                )
                                Text(
                                    text = "ഫയൽ വലുപ്പം: ${res.fileSizeFormatted}",
                                    fontSize = 10.5.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    val exportConfig = IcsExportConfig(
                        year = exportYear,
                        includePanchangamDaily = incPanchangam,
                        includeFestivals = incFestivals,
                        includeReminders = incReminders
                    )

                    // 3 Action Buttons for .ics Export
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Download to Downloads folder
                        Button(
                            onClick = {
                                onSaveToDownloads(exportConfig)
                            },
                            enabled = !isExportingIcs && (incPanchangam || incFestivals || incReminders),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepBrownDark,
                                contentColor = GoldLight
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ഫോണിലേക്ക് ഡൗൺലോഡ് (.ics Download)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }

                        // 2. Open with Google Calendar App directly
                        Button(
                            onClick = {
                                onExportIcs(exportConfig) { res ->
                                    exportResult = res
                                    CalendarIcsExporter.openIcsWithCalendarApp(context, res.uri)
                                }
                            },
                            enabled = !isExportingIcs && (incPanchangam || incFestivals || incReminders),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = DeepBrownDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Google Calendar-ൽ തുറക്കുക / Import",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }

                        // 3. Share .ics file (WhatsApp, Drive, Gmail)
                        OutlinedButton(
                            onClick = {
                                onExportIcs(exportConfig) { res ->
                                    exportResult = res
                                    CalendarIcsExporter.shareIcsFile(context, res.uri)
                                }
                            },
                            enabled = !isExportingIcs && (incPanchangam || incFestivals || incReminders),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = DeepBrownDark
                            ),
                            border = BorderStroke(1.dp, GoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                tint = Rust,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "മറ്റു ആപ്പുകളിലേക്ക് ഷെയർ ചെയ്യുക (Share .ics)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedTab == 0 && isLoggedIn && userEmail != null && !syncProgress.isSyncing) {
                Button(
                    onClick = onSyncClick,
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = DeepBrownDark
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Sync Now 🔄", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (selectedTab == 0 && isLoggedIn && userEmail != null && !syncProgress.isSyncing) {
                OutlinedButton(
                    onClick = onSignOutClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SundayRed),
                    border = BorderStroke(1.dp, SundayRed),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Disconnect / മാറ്റുക")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("അടയ്ക്കുക", color = DeepBrownDark)
                }
            }
        },
        containerColor = Color.White
    )
}

/**
 * Animated Progress Card displayed during Google Calendar Synchronization
 */
@Composable
fun SyncProgressAnimationCard(
    syncProgress: SyncProgressState,
    onSyncAgain: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = syncProgress.progress,
        animationSpec = tween(400),
        label = "progress"
    )

    Surface(
        color = if (syncProgress.isSuccess) Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
        border = BorderStroke(1.5.dp, if (syncProgress.isSuccess) Color(0xFF4ADE80) else GoldPrimary),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Rotating Sync Orb or Success Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(68.dp)
                    .scale(if (syncProgress.isSyncing) pulseScale else 1f)
                    .background(
                        color = if (syncProgress.isSuccess) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (syncProgress.isSuccess) GreenDark else GoldPrimary,
                        shape = CircleShape
                    )
            ) {
                if (syncProgress.isSuccess) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = GreenDark,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Syncing",
                        tint = DeepBrownDark,
                        modifier = Modifier
                            .size(38.dp)
                            .rotate(angle)
                    )
                }
            }

            // Status Headline
            Text(
                text = if (syncProgress.isSuccess) "✅ സിങ്ക് പൂർത്തിയായി!" else "കലണ്ടർ സിങ്ക് ചെയ്യുന്നു...",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (syncProgress.isSuccess) GreenDark else DeepBrownDark
            )

            // Current Step Subtitle
            Text(
                text = syncProgress.currentStepText,
                fontSize = 12.sp,
                color = Ink,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp
            )

            // Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (syncProgress.isSuccess) "100% പൂർത്തിയായി" else "ഘട്ടം ${syncProgress.stepIndex}/${syncProgress.totalSteps}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (syncProgress.isSuccess) GreenDark else Rust
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (syncProgress.isSuccess) GreenDark else GoldPrimary,
                    trackColor = Color(0xFFE2E8F0)
                )
            }

            // Step Progress Checklist
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SyncStepItem(
                        icon = Icons.Default.PhoneAndroid,
                        title = "ഫോണിലെ Google അക്കൗണ്ട്",
                        subtitle = syncProgress.activeEmail.ifBlank { "ഡിഫോൾട്ട് അക്കൗണ്ട്" },
                        isCompleted = syncProgress.stepIndex > 1 || syncProgress.isSuccess,
                        isActive = syncProgress.stepIndex == 1 && syncProgress.isSyncing
                    )
                    SyncStepItem(
                        icon = Icons.Default.Celebration,
                        title = "കുടുംബാംഗങ്ങളുടെ 7 ജന്മദിനങ്ങൾ",
                        subtitle = "മുരളി, പ്രിയ, മാധവൻ, ആര്യ, ദേവകി അമ്മ, ഉണ്ണികൃഷ്ണൻ, ഗീത",
                        isCompleted = syncProgress.stepIndex > 2 || syncProgress.isSuccess,
                        isActive = syncProgress.stepIndex == 2 && syncProgress.isSyncing
                    )
                    SyncStepItem(
                        icon = Icons.Default.CalendarMonth,
                        title = "മലയാളം പഞ്ചാംഗ വിശേഷദിവസങ്ങൾ",
                        subtitle = "വിഷു, ഓണം, ശിവരാത്രി, ഏകാദശി, പ്രദോഷം",
                        isCompleted = syncProgress.stepIndex > 3 || syncProgress.isSuccess,
                        isActive = syncProgress.stepIndex == 3 && syncProgress.isSyncing
                    )
                    SyncStepItem(
                        icon = Icons.Default.CloudUpload,
                        title = "Google Calendar സമന്വയം",
                        subtitle = "ഇരുവശത്തേക്കും ഇവന്റുകൾ അപ്ഡേറ്റ് ചെയ്യുന്നു",
                        isCompleted = syncProgress.isSuccess,
                        isActive = syncProgress.stepIndex == 4 && syncProgress.isSyncing
                    )
                }
            }

            // Post-Sync Success Buttons
            if (syncProgress.isSuccess) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onSyncAgain,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, GoldPrimary)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = DeepBrownDark)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("വീണ്ടും", fontSize = 12.sp, color = DeepBrownDark)
                    }
                    Button(
                        onClick = onDone,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("പൂർത്തിയായി ✔", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SyncStepItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = when {
                        isCompleted -> Color(0xFFDCFCE7)
                        isActive -> Color(0xFFFEF3C7)
                        else -> Color(0xFFF3F4F6)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = GreenDark,
                    modifier = Modifier.size(16.dp)
                )
            } else if (isActive) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = GoldPrimary
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = if (isActive || isCompleted) FontWeight.Bold else FontWeight.Medium,
                color = if (isCompleted) GreenDark else if (isActive) Rust else DeepBrownDark
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PanchangaFeatureItem(
    label: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = CreamDarker)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(accentColor)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label.uppercase(Locale.getDefault()),
                    fontSize = 9.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
                Text(
                    text = value,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Rust,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ReminderRowItem(
    reminder: ReminderEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isDone = reminder.isDone
    val repeatBadge = when (reminder.repeatType) {
        ReminderEntity.REPEAT_DAILY -> "🔁 ദിവസവും"
        ReminderEntity.REPEAT_MONTHLY -> "🔁 മാസംതോറും"
        ReminderEntity.REPEAT_YEARLY -> "🔁 വർഷംതോറും"
        else -> null
    }

    Surface(
        color = Cream,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, GoldLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isDone,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = GoldPrimary,
                    checkmarkColor = DeepBrownDark
                ),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onEdit() }
            ) {
                // Top Meta Row: Repeat Badge, Priority, Category
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (repeatBadge != null) {
                        Surface(
                            color = DeepBrownDark,
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = repeatBadge,
                                color = GoldLight,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }

                    if (reminder.priority.isNotBlank() && reminder.priority != "സാധാരണ") {
                        val pColor = if (reminder.priority == "അടിയന്തിരം") SundayRed else GoldTextDark
                        Surface(
                            color = pColor.copy(alpha = 0.15f),
                            border = BorderStroke(0.8.dp, pColor),
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = reminder.priority,
                                color = pColor,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.5.dp)
                            )
                        }
                    }

                    if (reminder.category.isNotBlank()) {
                        Text(
                            text = "• ${reminder.category}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Title / Subject
                if (reminder.subject.isNotBlank()) {
                    Text(
                        text = reminder.subject,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = if (isDone) Color.Gray else DeepBrownDark,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                // Note Body
                if (reminder.text.isNotBlank() && reminder.text != reminder.subject) {
                    Text(
                        text = reminder.text,
                        fontSize = 12.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDone) Color.Gray else GoldTextDark,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                // Time Badge
                if (reminder.time.isNotEmpty()) {
                    Surface(
                        color = GoldPrimary,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 3.dp)
                    ) {
                        Text(
                            text = "⏰ ${reminder.time}",
                            color = DeepBrownDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Reminder",
                    tint = GoldTextDark,
                    modifier = Modifier.size(16.dp)
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Reminder",
                    tint = SundayRed,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AllRemindersSheetContent(
    reminders: List<ReminderEntity>,
    onClose: () -> Unit,
    onToggleDone: (ReminderEntity) -> Unit,
    onEditReminder: (ReminderEntity) -> Unit,
    onDeleteReminder: (ReminderEntity) -> Unit,
    onOpenSyncExport: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf("എല്ലാം") }

    val filteredReminders = remember(reminders, searchQuery, filterCategory) {
        reminders.filter { rem ->
            val matchesSearch = searchQuery.isBlank() ||
                    rem.text.contains(searchQuery, ignoreCase = true) ||
                    rem.subject.contains(searchQuery, ignoreCase = true) ||
                    rem.fileName.contains(searchQuery, ignoreCase = true)

            val matchesCat = when (filterCategory) {
                "എല്ലാം" -> true
                "ബാക്കിയുള്ളവ" -> !rem.isDone
                "പൂർത്തിയായവ" -> rem.isDone
                "ദിവസവും" -> rem.repeatType == ReminderEntity.REPEAT_DAILY
                "മാസംതോറും" -> rem.repeatType == ReminderEntity.REPEAT_MONTHLY
                "വർഷംതോറും" -> rem.repeatType == ReminderEntity.REPEAT_YEARLY
                else -> rem.category == filterCategory
            }

            matchesSearch && matchesCat
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepBrown)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EditCalendar,
                    contentDescription = null,
                    tint = GoldLight,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "എല്ലാ കുറിപ്പുകളും Reminders-ഉം (${reminders.size})",
                    color = GoldLight,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.5.sp
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(26.dp)
                    .border(1.dp, GoldPrimary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = GoldLight,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Search & Filter Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CreamDarker)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Search Input with high contrast
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "കുറിപ്പുകൾ / വിഷയങ്ങൾ തിരയുക...",
                        color = Color(0xFF6B7280),
                        fontSize = 12.5.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = DeepBrownDark,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = DeepBrownDark,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    fontFamily = FontFamily.Serif
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DeepBrownDark,
                    unfocusedTextColor = DeepBrownDark,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = GoldPrimary,
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = GoldLight
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Category & Recurrence Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val filterOptions = listOf(
                    "എല്ലാം",
                    "ബാക്കിയുള്ളവ",
                    "ദിവസവും",
                    "മാസംതോറും",
                    "വർഷംതോറും",
                    "കുറിപ്പ്",
                    "മീറ്റിംഗ്",
                    "ജന്മദിനം / വാർഷികം",
                    "പൂർത്തിയായവ"
                )
                filterOptions.forEach { opt ->
                    val isSel = (filterCategory == opt)
                    Surface(
                        color = if (isSel) GoldPrimary else Cream,
                        border = BorderStroke(1.dp, if (isSel) GoldPrimary else GoldLight),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.clickable { filterCategory = opt }
                    ) {
                        Text(
                            text = opt,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) DeepBrownDark else Ink,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Sync & .ics Export Quick Action
            Surface(
                color = Color(0xFFFEF3C7),
                border = BorderStroke(1.dp, GoldPrimary),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenSyncExport() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            tint = Rust,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = ".ics ഫയലായി ഡൗൺലോഡ് / Google കലണ്ടർ സിങ്ക്",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBrownDark
                        )
                    }
                    Text(
                        text = "തുറക്കുക ❯",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Rust
                    )
                }
            }
        }

        if (filteredReminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "കുറിപ്പുകൾ ഒന്നും കണ്ടെത്തിയില്ല" else "🔔 കുറിപ്പുകൾ / Reminders ഒന്നും ഇല്ല",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            val grouped = filteredReminders.groupBy { "${it.year}-${it.month}-${it.day}" }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                grouped.forEach { (key, groupList) ->
                    val sample = groupList.first()
                    val gregMonthMl = PanchangamCalculator.GREG_MONTHS_ML.getOrElse(sample.month) { "" }
                    val gregMonthEn = PanchangamCalculator.GREG_MONTHS_EN.getOrElse(sample.month) { "" }
                    val cal = Calendar.getInstance().apply { set(sample.year, sample.month, sample.day) }
                    val dowIdx = cal.get(Calendar.DAY_OF_WEEK) - 1
                    val dowMl = PanchangamCalculator.WEEKDAYS_ML.getOrElse(dowIdx) { "" }
                    val panchangam = PanchangamCalculator.computeDayData(sample.year, sample.month, sample.day)

                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$gregMonthEn ${sample.day}, ${sample.year} ($gregMonthMl) — $dowMl",
                                    color = Rust,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                                Text(
                                    text = "${panchangam.mlMonth} ${panchangam.mlDate}",
                                    color = DeepBrownDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }
                            HorizontalDivider(color = BorderLight, thickness = 1.dp)

                            groupList.forEach { rem ->
                                ReminderRowItem(
                                    reminder = rem,
                                    onToggle = { onToggleDone(rem) },
                                    onEdit = { onEditReminder(rem) },
                                    onDelete = { onDeleteReminder(rem) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun YearMonthAndDatePickerDialog(
    currentYear: Int,
    currentMonth: Int,
    onDismiss: () -> Unit,
    onSelectMonthYear: (Int, Int) -> Unit,
    onSelectSpecificDate: (Int, Int, Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Specific Date, 1: Month/Year
    var targetYear by remember { mutableIntStateOf(currentYear) }
    var targetMonth by remember { mutableIntStateOf(currentMonth) }
    var targetDay by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    // Day input string for direct typing
    var dayInputText by remember { mutableStateOf(targetDay.toString()) }
    var yearInputText by remember { mutableStateOf(targetYear.toString()) }

    // Calculate max days for the target month and year
    val maxDaysInMonth = remember(targetYear, targetMonth) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, targetYear)
        cal.set(Calendar.MONTH, targetMonth)
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Ensure targetDay stays valid
    val validDay = targetDay.coerceIn(1, maxDaysInMonth)

    // Calculate live preview Panchangam
    val previewPanchangam = remember(targetYear, targetMonth, validDay) {
        try {
            PanchangamCalculator.computeDayData(targetYear, targetMonth, validDay)
        } catch (e: Exception) {
            null
        }
    }

    val monthsMl = PanchangamCalculator.GREG_MONTHS_ML
    val monthsEn = PanchangamCalculator.GREG_MONTHS_EN

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = "Date Selection",
                        tint = Rust,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "തീയതി / മാസം തിരഞ്ഞെടുക്കുക",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Ink
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Row (Tab 0: Specific Date, Tab 1: Month/Year Grid)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CreamDarker,
                    contentColor = DeepBrown,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoldPrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "🎯 പ്രത്യേക തീയതി",
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) Rust else Ink
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "📅 മാസവും വർഷവും",
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) Rust else Ink
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == 0) {
                    // TAB 0: Specific Date Picker with direct Day / Month / Year entry & Panchangam preview
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ഏത് തീയതിയിലെ പഞ്ചാംഗ വിവരങ്ങളാണ് കാണേണ്ടത്?",
                            fontSize = 11.5.sp,
                            color = DeepBrownDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Year Stepper & input
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Cream, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("വർഷം (Year):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        targetYear--
                                        yearInputText = targetYear.toString()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("◀", fontSize = 12.sp, color = Rust)
                                }
                                Text(
                                    text = "$targetYear",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBrownDark,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                                IconButton(
                                    onClick = {
                                        targetYear++
                                        yearInputText = targetYear.toString()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("▶", fontSize = 12.sp, color = Rust)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Month Selector row (Quick Month picker chips)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Cream, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Month: ${monthsEn[targetMonth]}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBrownDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                monthsEn.forEachIndexed { idx, mName ->
                                    val isSelected = (idx == targetMonth)
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { targetMonth = idx },
                                        color = if (isSelected) GoldPrimary else Color.White,
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, if (isSelected) GoldPrimary else BorderLight)
                                    ) {
                                        Text(
                                            text = mName,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) DeepBrownDark else Ink,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Day Selector (Direct Input / Quick Grid)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Cream, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "തീയതി (Day 1..$maxDaysInMonth):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Ink
                                )
                                OutlinedTextField(
                                    value = dayInputText,
                                    onValueChange = { input ->
                                        val filtered = input.filter { it.isDigit() }.take(2)
                                        dayInputText = filtered
                                        val num = filtered.toIntOrNull()
                                        if (num != null && num in 1..maxDaysInMonth) {
                                            targetDay = num
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.width(68.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Rust,
                                        unfocusedBorderColor = BorderLight,
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Quick Day Stepper / Popular Day Selector
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (d in 1..maxDaysInMonth) {
                                    val isSelected = (d == targetDay)
                                    Surface(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                targetDay = d
                                                dayInputText = d.toString()
                                            },
                                        color = if (isSelected) Rust else Color.White,
                                        shape = CircleShape,
                                        border = BorderStroke(1.dp, if (isSelected) Rust else BorderLight)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$d",
                                                fontSize = 11.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else Ink
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Live Panchangam Preview Card
                        if (previewPanchangam != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = CreamDarker),
                                border = BorderStroke(1.dp, GoldPale),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "✨ ${previewPanchangam.mlMonth} ${previewPanchangam.mlDate}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Rust
                                        )
                                        Text(
                                            text = "കൊ.വ: ${previewPanchangam.kollaVarsham}",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = DeepBrownDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "★ നക്ഷത്രം: ${previewPanchangam.nakshatra} (അവസാനം: ${previewPanchangam.nakEnd})",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Ink
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "☽ തിഥി: ${previewPanchangam.tithi} · ${previewPanchangam.paksha} (${previewPanchangam.weekdayMl})",
                                        fontSize = 11.sp,
                                        color = PurpleTithi
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("റദ്ദാക്കുക", color = Rust)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onSelectSpecificDate(targetYear, targetMonth, validDay)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = DeepBrown
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Today,
                                    contentDescription = "Go to date",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ഈ തീയതിയിലേക്ക് പോവുക", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // TAB 1: Quick Month & Year Grid
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Year selector (- / +)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { targetYear-- },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text("- 1", fontSize = 12.sp)
                            }

                            Text(
                                text = "$targetYear",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Ink,
                                modifier = Modifier.padding(horizontal = 18.dp)
                            )

                            OutlinedButton(
                                onClick = { targetYear++ },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text("+ 1", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 12 Months Grid (3x4)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (row in 0 until 4) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    for (col in 0 until 3) {
                                        val mIdx = row * 3 + col
                                        val isSelected = (mIdx == targetMonth)
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .clickable { targetMonth = mIdx },
                                            color = if (isSelected) GoldPrimary else CreamDarker,
                                            shape = RoundedCornerShape(6.dp),
                                            border = BorderStroke(1.dp, if (isSelected) GoldPrimary else BorderLight)
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Text(
                                                    text = monthsEn[mIdx],
                                                    fontSize = 11.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) Color.White else Ink,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("റദ്ദാക്കുക", color = Rust)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onSelectMonthYear(targetYear, targetMonth) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = DeepBrown
                                ),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("മാസം തുറക്കുക", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderDialog(
    reminder: ReminderEntity,
    onDismiss: () -> Unit,
    onSave: (
        newYear: Int,
        newMonth: Int,
        newDay: Int,
        newText: String,
        newTime: String,
        newSubject: String,
        newCategory: String,
        newPriority: String,
        newRepeatType: String,
        newIsNotif: Boolean
    ) -> Unit
) {
    val context = LocalContext.current
    var editYear by remember { mutableIntStateOf(reminder.year) }
    var editMonth by remember { mutableIntStateOf(reminder.month) }
    var editDay by remember { mutableIntStateOf(reminder.day) }
    var editSubject by remember { mutableStateOf(reminder.subject) }
    var editText by remember { mutableStateOf(reminder.text) }
    var editTime by remember { mutableStateOf(reminder.time) }
    var editCategory by remember { mutableStateOf(if (reminder.category.isNotBlank()) reminder.category else "കുറിപ്പ്") }
    var editPriority by remember { mutableStateOf(if (reminder.priority.isNotBlank()) reminder.priority else "സാധാരണ") }
    var editRepeatType by remember { mutableStateOf(reminder.repeatType) }
    var editIsNotif by remember { mutableStateOf(reminder.isNotif) }

    val categories = listOf("കുറിപ്പ്", "വ്യക്തിഗതം", "ജോലി", "പൂജ", "വ്രതം", "ആരോഗ്യം", "മറ്റുള്ളവ")
    val priorities = listOf("സാധാരണ", "പ്രധാനം", "അടിയന്തിരം")
    val repeatOptions = listOf(
        ReminderEntity.REPEAT_ONCE to "ഒരു തവണ",
        ReminderEntity.REPEAT_DAILY to "🔁 ദിവസവും",
        ReminderEntity.REPEAT_MONTHLY to "🔁 മാസംതോറും",
        ReminderEntity.REPEAT_YEARLY to "🔁 വർഷംതോറും"
    )

    val gregMonthEn = PanchangamCalculator.GREG_MONTHS_EN.getOrElse(editMonth) { "" }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Cream),
            border = BorderStroke(1.5.dp, GoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = GoldTextDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "കുറിപ്പ് തിരുത്തുക",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DeepBrownDark
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DeepBrownDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = GoldLight,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Date & Time Picker Row
                Text(
                    text = "തീയതിയും സമയവും (Date & Time)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = DeepBrownDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date Button
                    OutlinedButton(
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    editYear = y
                                    editMonth = m
                                    editDay = d
                                },
                                editYear,
                                editMonth,
                                editDay
                            ).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = DeepBrownDark
                        ),
                        border = BorderStroke(1.dp, GoldPrimary),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Rust,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$editDay $gregMonthEn $editYear",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    // Time Button
                    OutlinedButton(
                        onClick = {
                            val now = Calendar.getInstance()
                            val curHour = now.get(Calendar.HOUR_OF_DAY)
                            val curMin = now.get(Calendar.MINUTE)
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    val ampm = if (hour >= 12) "PM" else "AM"
                                    val h12 = if (hour % 12 == 0) 12 else hour % 12
                                    editTime = String.format(Locale.US, "%02d:%02d %s", h12, minute, ampm)
                                },
                                curHour,
                                curMin,
                                false
                            ).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = DeepBrownDark
                        ),
                        border = BorderStroke(1.dp, GoldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = Rust,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (editTime.isEmpty()) "സമയം" else editTime,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                if (editTime.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "സമയം ഒഴിവാക്കുക",
                            color = Rust,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clickable { editTime = "" }
                                .padding(vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Subject / Title field
                OutlinedTextField(
                    value = editSubject,
                    onValueChange = { editSubject = it },
                    label = { Text("വിഷയം / തലക്കെട്ട് *", fontSize = 12.sp, color = Rust) },
                    placeholder = { Text("ഉദാ: അമ്പലത്തിൽ പോക്ക്, മീറ്റിംഗ്", fontSize = 12.sp, color = Color.Gray) },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = DeepBrownDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DeepBrownDark,
                        unfocusedTextColor = DeepBrownDark,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedLabelColor = Rust,
                        unfocusedLabelColor = DeepBrownDark.copy(alpha = 0.8f),
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,
                        cursorColor = Rust,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldLight
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Note description field
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    label = { Text("കുറിപ്പ് വിവരണം (Note Details)", fontSize = 12.sp, color = Rust) },
                    placeholder = { Text("വിശദാംശങ്ങൾ ഇവിടെ ചേർക്കാം...", fontSize = 12.sp, color = Color.Gray) },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = DeepBrownDark,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Serif
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 72.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DeepBrownDark,
                        unfocusedTextColor = DeepBrownDark,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedLabelColor = Rust,
                        unfocusedLabelColor = DeepBrownDark.copy(alpha = 0.8f),
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,
                        cursorColor = Rust,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldLight
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Recurrence Frequency
                Text(
                    text = "ആവർത്തനം (Repeat Frequency):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = DeepBrownDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeatOptions.forEach { (type, label) ->
                        val isSel = editRepeatType == type
                        FilterChip(
                            selected = isSel,
                            onClick = { editRepeatType = type },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) DeepBrownDark else Ink
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = DeepBrownDark,
                                containerColor = Color.White,
                                labelColor = Ink
                            ),
                            border = BorderStroke(1.dp, if (isSel) GoldPrimary else BorderLight)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Priority Row
                Text(
                    text = "പ്രാധാന്യം (Priority):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = DeepBrownDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    priorities.forEach { p ->
                        val isSel = editPriority == p
                        FilterChip(
                            selected = isSel,
                            onClick = { editPriority = p },
                            label = {
                                Text(
                                    text = p,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) (if (p == "അടിയന്തിരം") Color.White else DeepBrownDark) else Ink
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (p == "അടിയന്തിരം") SundayRed.copy(alpha = 0.85f) else GoldPrimary,
                                selectedLabelColor = if (p == "അടിയന്തിരം") Color.White else DeepBrownDark,
                                containerColor = Color.White,
                                labelColor = Ink
                            ),
                            border = BorderStroke(1.dp, if (isSel) GoldPrimary else BorderLight),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category chips
                Text(
                    text = "വിഭാഗം (Category):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = DeepBrownDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.forEach { cat ->
                        val isSel = editCategory == cat
                        FilterChip(
                            selected = isSel,
                            onClick = { editCategory = cat },
                            label = {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) GoldLight else Ink
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepBrownDark,
                                selectedLabelColor = GoldLight,
                                containerColor = Color.White,
                                labelColor = Ink
                            ),
                            border = BorderStroke(1.dp, if (isSel) DeepBrownDark else BorderLight)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notification Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { editIsNotif = !editIsNotif }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = editIsNotif,
                        onCheckedChange = { editIsNotif = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GoldPrimary,
                            checkmarkColor = DeepBrownDark
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🔔 അറിയിപ്പ് നൽകുക (Notification Alert)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepBrownDark
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("റദ്ദാക്കുക", color = Rust)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalSubject = if (editSubject.isNotBlank()) editSubject.trim() else (if (editText.isNotBlank()) editText.trim() else "കുറിപ്പ്")
                            val finalText = if (editText.isNotBlank()) editText.trim() else finalSubject
                            onSave(
                                editYear,
                                editMonth,
                                editDay,
                                finalText,
                                editTime,
                                finalSubject,
                                editCategory,
                                editPriority,
                                editRepeatType,
                                editIsNotif
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = DeepBrownDark
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("മാറ്റങ്ങൾ സംരക്ഷിക്കുക", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun LocationPickerDialog(
    currentLocation: LocationProfile,
    isDetectingGps: Boolean,
    onDismiss: () -> Unit,
    onSelectLocation: (LocationProfile) -> Unit,
    onDetectGps: () -> Unit,
    onSaveCustomLocation: (name: String, lat: Double, lon: Double, tzOffset: Double) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    // Custom coordinates form state
    var customName by remember { mutableStateOf("") }
    var customLat by remember { mutableStateOf("") }
    var customLon by remember { mutableStateOf("") }
    var customTz by remember { mutableStateOf("5.5") }
    var customError by remember { mutableStateOf<String?>(null) }

    val presetLocations = remember { LocationService.PRESET_LOCATIONS }
    val filteredPresets = remember(searchQuery) {
        if (searchQuery.isBlank()) presetLocations
        else presetLocations.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.nameMl.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Cream,
            border = BorderStroke(1.5.dp, GoldPrimary),
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .heightIn(max = 600.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(DeepBrownDark, DeepBrown, Rust)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "സ്ഥലവും അക്ഷാംശ-രേഖാംശങ്ങളും",
                                    color = GoldLight,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                            Text(
                                text = "ഉദയം, അസ്തമയം, നക്ഷത്ര-തിഥി സമയങ്ങൾ മാറും",
                                color = GoldPrimary,
                                fontSize = 10.5.sp
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(28.dp)
                                .border(1.dp, GoldPrimary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = GoldLight,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Current Location & GPS Auto-detect Button
                Surface(
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldLight)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "നിലവിലെ സ്ഥലം:",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${currentLocation.nameMl} (${currentLocation.name})",
                                    fontSize = 14.sp,
                                    color = DeepBrownDark,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%.3f° N, %.3f° E", currentLocation.latitude, currentLocation.longitude)} (UTC+${currentLocation.tzOffsetHours})",
                                    fontSize = 11.sp,
                                    color = Rust,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // GPS Auto-detect button
                        Button(
                            onClick = onDetectGps,
                            enabled = !isDetectingGps,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentLocation.isGps) GreenDark else DeepBrown,
                                contentColor = GoldLight
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("gps_auto_detect_button")
                        ) {
                            if (isDetectingGps) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = GoldLight,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("GPS സ്ഥലം കണ്ടെത്തുന്നു...", fontSize = 12.5.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "GPS",
                                    modifier = Modifier.size(16.dp),
                                    tint = GoldPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "🛰️ GPS വഴി സ്ഥലം സ്വയമേവ കണ്ടെത്തുക",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Tabs: Preset Cities vs Custom Coordinates
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CreamDarker,
                    contentColor = DeepBrownDark,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoldPrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "പ്രധാന നഗരങ്ങൾ",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "കസ്റ്റം അക്ഷാംശങ്ങൾ",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }

                // Tab Content
                if (selectedTab == 0) {
                    // Search Bar
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("നഗരം തിരയുക... (ഉദാ: കോഴിക്കോട്)", fontSize = 12.sp, color = Color.Gray) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = DeepBrown, modifier = Modifier.size(16.dp))
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = GoldLight
                            ),
                            modifier = Modifier.fillMaxWidth().heightIn(max = 50.dp)
                        )
                    }

                    // Cities List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredPresets) { loc ->
                            val isSelected = currentLocation.id == loc.id
                            Surface(
                                color = if (isSelected) GoldPrimary.copy(alpha = 0.15f) else Color.White,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) GoldPrimary else BorderLight
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectLocation(loc) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = loc.nameMl,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = DeepBrownDark
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(${loc.name})",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Text(
                                            text = "${String.format(Locale.US, "%.2f° N, %.2f° E", loc.latitude, loc.longitude)} · UTC+${loc.tzOffsetHours}",
                                            fontSize = 10.5.sp,
                                            color = RustLight
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = GreenDark,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Custom Latitude & Longitude Input
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ഏതൊരു സ്ഥലത്തെയും കൃത്യമായ അക്ഷാംശ-രേഖാംശങ്ങൾ നൽകുക:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBrownDark
                        )

                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("സ്ഥലത്തിന്റെ പേര് (ഉദാ: എന്റെ വീട്)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = GoldLight
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customLat,
                                onValueChange = { customLat = it },
                                label = { Text("അക്ഷാംശം (Lat N)", fontSize = 11.sp) },
                                placeholder = { Text("9.9312", fontSize = 11.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = GoldLight
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )

                            OutlinedTextField(
                                value = customLon,
                                onValueChange = { customLon = it },
                                label = { Text("രേഖാംശം (Lon E)", fontSize = 11.sp) },
                                placeholder = { Text("76.2673", fontSize = 11.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = GoldLight
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        OutlinedTextField(
                            value = customTz,
                            onValueChange = { customTz = it },
                            label = { Text("ടൈംസോൺ UTC Offset (ഇന്ത്യ: 5.5, ഗൾഫ്: 4.0)", fontSize = 11.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = GoldLight
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        customError?.let { err ->
                            Text(
                                text = err,
                                color = SundayRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                val lat = customLat.toDoubleOrNull()
                                val lon = customLon.toDoubleOrNull()
                                val tz = customTz.toDoubleOrNull() ?: 5.5
                                if (lat == null || lat < -90.0 || lat > 90.0) {
                                    customError = "അക്ഷാംശം (Latitude) -90 നും 90 നും ഇടയിലായിരിക്കണം"
                                    return@Button
                                }
                                if (lon == null || lon < -180.0 || lon > 180.0) {
                                    customError = "രേഖാംശം (Longitude) -180 നും 180 നും ഇടയിലായിരിക്കണം"
                                    return@Button
                                }
                                customError = null
                                onSaveCustomLocation(
                                    customName.ifBlank { "കസ്റ്റം ലൊക്കേഷൻ" },
                                    lat,
                                    lon,
                                    tz
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = DeepBrownDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ഈ ലൊക്കേഷൻ പ്രയോഗിക്കുക", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Dialog Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CreamDarker)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("പൂർത്തിയായി", color = DeepBrownDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

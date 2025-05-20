package com.example.consecutivepractice.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import com.example.consecutivepractice.notifications.NotificationHelper
import com.example.consecutivepractice.viewmodels.ProfileEditViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    viewModel: ProfileEditViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onSaveComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()    // Все состояния теперь управляются через ViewModel
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onAvatarChanged(it.toString())
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            uiState.tempCameraUri?.let {
                viewModel.onAvatarChanged(it)
            }
        }
    }

    //Тут вроде обязательно нужен контекст для проверки разрешений
    //И я не смог вынести. Но вроде порефакторил
    //Чтобы мы не делали работы с данными и только вызывали метод из viewmodel
    LaunchedEffect(Unit) {
        viewModel.checkAllPermissions(context)
    }

    DisposableEffect(Unit) {
        val activity = context as? ComponentActivity
        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.checkAllPermissions(context)
                }
            }
        }

        activity?.lifecycle?.addObserver(observer)

        onDispose {
            activity?.lifecycle?.removeObserver(observer)
        }
    }


// запрос разрешения камеры
    val requestCameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateCameraPermission(isGranted)
        if (isGranted) {
            openCamera(context) { uri ->
                viewModel.setTempCameraUri(uri.toString())
                cameraLauncher.launch(uri)
            }
        } else {
            viewModel.showPermissionDeniedDialog()
        }
    }
// запрос разрешения хранилища

    val requestStoragePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateStoragePermission(isGranted)
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            viewModel.showPermissionDeniedDialog()
        }
    }

// запрос разрешения на уведомления
    val requestNotificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateNotificationPermission(isGranted)
    }

// окно при отказе
    if (uiState.showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hidePermissionDeniedDialog() },
            title = { Text("Требуется разрешение") },
            text = { Text("Для выбора изображения необходимо предоставить соответствующие разрешения.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.hidePermissionDeniedDialog()
                        openAppSettings(context)
                    }
                ) {
                    Text("Открыть настройки")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hidePermissionDeniedDialog() }) {
                    Text("Отмена")
                }
            }
        )
    }
    if (uiState.showAlarmPermissionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAlarmPermissionDialog() },
            title = { Text("Требуется разрешение") },
            text = { Text("Для настройки уведомления о начале любимой пары необходимо разрешить точные будильники.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.hideAlarmPermissionDialog()
                        val notificationHelper = NotificationHelper(context)
                        context.startActivity(notificationHelper.getExactAlarmPermissionIntent())
                    }
                ) {
                    Text("Открыть настройки")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.hideAlarmPermissionDialog()
                    viewModel.saveProfile(onSuccess = onSaveComplete)
                }) {
                    Text("Сохранить без уведомлений")
                }
            }
        )
    }

// выбор источника
    if (uiState.showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideImageSourceDialog() },
            title = { Text("Выберите источник") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Откуда вы хотите выбрать изображение?")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Опция Камера
                    TextButton(
                        onClick = {
                            viewModel.hideImageSourceDialog()
                            if (uiState.cameraPermissionGranted) {
                                openCamera(context) { uri ->
                                    viewModel.setTempCameraUri(uri.toString())
                                    cameraLauncher.launch(uri)
                                }
                            } else {
                                requestCameraPermission.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Камера")
                        }
                    }

                    // Опция Галерея
                    TextButton(
                        onClick = {
                            viewModel.hideImageSourceDialog()

                            val storagePermission = if (Build.VERSION.SDK_INT >= 33) {
                                Manifest.permission.READ_MEDIA_IMAGES
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }

                            if (uiState.storagePermissionGranted) {
                                galleryLauncher.launch("image/*")
                            } else {
                                requestStoragePermission.launch(storagePermission)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Галерея")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.hideImageSourceDialog() }) {
                    Text("Отмена")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактировать профиль") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }, floatingActionButton = {
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        uiState.favoriteClassTime.isNotBlank() &&
                        !uiState.exactAlarmPermissionGranted
                    ) {
                        viewModel.showAlarmPermissionDialog()
                    } else {
                        viewModel.saveProfile(onSuccess = onSaveComplete)
                    }
                },
                enabled = !uiState.isSaving && (uiState.favoriteClassTime.isBlank() || uiState.favoriteClassTimeError == null)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Сохранить"
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Выбор аватарки
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { viewModel.showImageSourceDialog() },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.avatarUri.isNotBlank()) {
                    AsyncImage(
                        model = uiState.avatarUri,
                        contentDescription = "Фото профиля",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить фото",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = { viewModel.onFullNameChanged(it) },
                label = { Text("ФИО") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.jobTitle,
                onValueChange = { viewModel.onJobTitleChanged(it) },
                label = { Text("Должность") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.resumeUrl,
                onValueChange = { viewModel.onResumeUrlChanged(it) },
                label = { Text("URL резюме (PDF)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.favoriteClassTime,
                onValueChange = { viewModel.onFavoriteClassTimeChanged(it) },
                label = { Text("Время любимой пары") },
                placeholder = { Text("HH:MM") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.favoriteClassTimeError != null,
                supportingText = if (uiState.favoriteClassTimeError != null) {
                    { Text(uiState.favoriteClassTimeError!!) }
                } else {
                    { Text("Формат: HH:MM, например: 14:30") }
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.showTimePicker() }) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Выбрать время"
                        )
                    }
                }
            )// Проверка разрешений при запуске
            if (uiState.favoriteClassTime.isNotBlank() && uiState.favoriteClassTimeError == null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val notificationHelper = NotificationHelper(context)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !uiState.exactAlarmPermissionGranted) {
                            viewModel.showAlarmPermissionDialog()
                        } else {
                            notificationHelper.showDebugNotification(uiState.fullName)
                        }
                    }
                ) {
                    Text("Проверить уведомление")
                }
            }
            if (uiState.showTimePicker) {
                val initialHour =
                    uiState.favoriteClassTime.split(":").getOrNull(0)?.toIntOrNull() ?: 12
                val initialMinute =
                    uiState.favoriteClassTime.split(":").getOrNull(1)?.toIntOrNull() ?: 0

                if (Build.VERSION.SDK_INT >= 33 && !uiState.notificationPermissionGranted) {
                    requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                val timePickerState = rememberTimePickerState(
                    initialHour = initialHour,
                    initialMinute = initialMinute,
                    is24Hour = true
                )

                AlertDialog(
                    onDismissRequest = { viewModel.hideTimePicker() },
                    title = { Text("Выберите время пары") },
                    text = {
                        TimePicker(
                            state = timePickerState
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.setTimeFromPicker(
                                    timePickerState.hour,
                                    timePickerState.minute
                                )
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.hideTimePicker() }) {
                            Text("Отмена")
                        }
                    }
                )
            }
        }
    }
}


private fun openCamera(context: Context, onUriCreated: (Uri) -> Unit) {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = context.getExternalFilesDir("Camera")
    val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
    val imageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
    onUriCreated(imageUri)
}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
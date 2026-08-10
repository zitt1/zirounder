package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SettingsRepository
import com.example.model.CornerShapeType
import com.example.model.OverlaySettings
import com.example.service.CornerOverlayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    val settingsState: StateFlow<OverlaySettings> = repository.settingsState

    private val _hasOverlayPermission = MutableStateFlow(false)
    val hasOverlayPermission: StateFlow<Boolean> = _hasOverlayPermission.asStateFlow()

    private val _isBatteryOptimizationIgnored = MutableStateFlow(true)
    val isBatteryOptimizationIgnored: StateFlow<Boolean> = _isBatteryOptimizationIgnored.asStateFlow()

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        val context = getApplication<Application>()
        val canDraw = Settings.canDrawOverlays(context)
        _hasOverlayPermission.value = canDraw

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        _isBatteryOptimizationIgnored.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pm.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }

    fun toggleService(enabled: Boolean, context: Context) {
        val updated = settingsState.value.copy(isEnabled = enabled)
        repository.updateSettings(updated)

        if (enabled && Settings.canDrawOverlays(context)) {
            startService(context)
        } else {
            stopService(context)
        }
    }

    fun startService(context: Context) {
        if (!Settings.canDrawOverlays(context)) return
        val intent = Intent(context, CornerOverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopService(context: Context) {
        val intent = Intent(context, CornerOverlayService::class.java)
        context.stopService(intent)
    }

    fun setUnifiedRadius(radiusDp: Int) {
        val updated = settingsState.value.copy(
            unifiedRadiusDp = radiusDp,
            topLeftRadiusDp = radiusDp,
            topRightRadiusDp = radiusDp,
            bottomLeftRadiusDp = radiusDp,
            bottomRightRadiusDp = radiusDp
        )
        repository.updateSettings(updated)
    }

    fun setIndividualCorners(enabled: Boolean) {
        val current = settingsState.value
        val updated = current.copy(
            isIndividualCorners = enabled,
            topLeftRadiusDp = current.unifiedRadiusDp,
            topRightRadiusDp = current.unifiedRadiusDp,
            bottomLeftRadiusDp = current.unifiedRadiusDp,
            bottomRightRadiusDp = current.unifiedRadiusDp
        )
        repository.updateSettings(updated)
    }

    fun setCornerRadii(tl: Int, tr: Int, bl: Int, br: Int) {
        val updated = settingsState.value.copy(
            topLeftRadiusDp = tl,
            topRightRadiusDp = tr,
            bottomLeftRadiusDp = bl,
            bottomRightRadiusDp = br
        )
        repository.updateSettings(updated)
    }

    fun setShapeType(shape: CornerShapeType) {
        val updated = settingsState.value.copy(shapeType = shape)
        repository.updateSettings(updated)
    }

    fun setColorHex(colorHex: String) {
        val updated = settingsState.value.copy(colorHex = colorHex)
        repository.updateSettings(updated)
    }

    fun setOpacity(alpha: Float) {
        val updated = settingsState.value.copy(opacityAlpha = alpha)
        repository.updateSettings(updated)
    }

    fun setTopOffset(offsetDp: Int) {
        val updated = settingsState.value.copy(topOffsetDp = offsetDp)
        repository.updateSettings(updated)
    }

    fun setBottomOffset(offsetDp: Int) {
        val updated = settingsState.value.copy(bottomOffsetDp = offsetDp)
        repository.updateSettings(updated)
    }

    fun toggleTopEnabled(enabled: Boolean) {
        val updated = settingsState.value.copy(isTopEnabled = enabled)
        repository.updateSettings(updated)
    }

    fun toggleBottomEnabled(enabled: Boolean) {
        val updated = settingsState.value.copy(isBottomEnabled = enabled)
        repository.updateSettings(updated)
    }

    fun setAutoStart(enabled: Boolean) {
        val updated = settingsState.value.copy(autoStartOnBoot = enabled)
        repository.updateSettings(updated)
    }

    fun setPauseScreenOff(enabled: Boolean) {
        val updated = settingsState.value.copy(pauseOnScreenOff = enabled)
        repository.updateSettings(updated)
    }

    fun setAmoledSaver(enabled: Boolean) {
        val colorHex = if (enabled) "#000000" else settingsState.value.colorHex
        val updated = settingsState.value.copy(
            amoledSaverMode = enabled,
            colorHex = colorHex
        )
        repository.updateSettings(updated)
    }

    fun setLanguage(lang: String) {
        val updated = settingsState.value.copy(language = lang)
        repository.updateSettings(updated)
    }

    fun completeOnboarding() {
        val updated = settingsState.value.copy(isOnboardingCompleted = true)
        repository.updateSettings(updated)
    }

    fun applyPreset(radiusDp: Int, shape: CornerShapeType) {
        val updated = settingsState.value.copy(
            unifiedRadiusDp = radiusDp,
            topLeftRadiusDp = radiusDp,
            topRightRadiusDp = radiusDp,
            bottomLeftRadiusDp = radiusDp,
            bottomRightRadiusDp = radiusDp,
            isIndividualCorners = false,
            shapeType = shape
        )
        repository.updateSettings(updated)
    }

    fun requestOverlayPermission(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun requestIgnoreBatteryOptimizations(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                val altIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(altIntent)
            }
        }
    }
}

package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.CornerShapeType
import com.example.model.OverlaySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settingsState = MutableStateFlow(loadSettings())
    val settingsState: StateFlow<OverlaySettings> = _settingsState.asStateFlow()

    fun loadSettings(): OverlaySettings {
        return OverlaySettings(
            isEnabled = prefs.getBoolean(KEY_ENABLED, false),
            unifiedRadiusDp = prefs.getInt(KEY_UNIFIED_RADIUS, 15),
            isIndividualCorners = prefs.getBoolean(KEY_INDIVIDUAL_CORNERS, false),
            topLeftRadiusDp = prefs.getInt(KEY_TOP_LEFT_RADIUS, 15),
            topRightRadiusDp = prefs.getInt(KEY_TOP_RIGHT_RADIUS, 15),
            bottomLeftRadiusDp = prefs.getInt(KEY_BOTTOM_LEFT_RADIUS, 15),
            bottomRightRadiusDp = prefs.getInt(KEY_BOTTOM_RIGHT_RADIUS, 15),
            shapeType = try {
                CornerShapeType.valueOf(prefs.getString(KEY_SHAPE_TYPE, CornerShapeType.ROUND.name) ?: CornerShapeType.ROUND.name)
            } catch (e: Exception) {
                CornerShapeType.ROUND
            },
            colorHex = prefs.getString(KEY_COLOR_HEX, "#000000") ?: "#000000",
            opacityAlpha = prefs.getFloat(KEY_OPACITY_ALPHA, 1.0f),
            topOffsetDp = prefs.getInt(KEY_TOP_OFFSET, 0),
            bottomOffsetDp = prefs.getInt(KEY_BOTTOM_OFFSET, 0),
            isTopEnabled = prefs.getBoolean(KEY_TOP_ENABLED, true),
            isBottomEnabled = prefs.getBoolean(KEY_BOTTOM_ENABLED, true),
            autoStartOnBoot = prefs.getBoolean(KEY_AUTO_START, true),
            pauseOnScreenOff = prefs.getBoolean(KEY_PAUSE_SCREEN_OFF, true),
            amoledSaverMode = prefs.getBoolean(KEY_AMOLED_SAVER, true),
            showNotification = prefs.getBoolean(KEY_SHOW_NOTIFICATION, false),
            language = prefs.getString(KEY_LANGUAGE, "ru") ?: "ru",
            isOnboardingCompleted = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        )
    }

    fun updateSettings(newSettings: OverlaySettings) {
        prefs.edit().apply {
            putBoolean(KEY_ENABLED, newSettings.isEnabled)
            putInt(KEY_UNIFIED_RADIUS, newSettings.unifiedRadiusDp)
            putBoolean(KEY_INDIVIDUAL_CORNERS, newSettings.isIndividualCorners)
            putInt(KEY_TOP_LEFT_RADIUS, newSettings.topLeftRadiusDp)
            putInt(KEY_TOP_RIGHT_RADIUS, newSettings.topRightRadiusDp)
            putInt(KEY_BOTTOM_LEFT_RADIUS, newSettings.bottomLeftRadiusDp)
            putInt(KEY_BOTTOM_RIGHT_RADIUS, newSettings.bottomRightRadiusDp)
            putString(KEY_SHAPE_TYPE, newSettings.shapeType.name)
            putString(KEY_COLOR_HEX, newSettings.colorHex)
            putFloat(KEY_OPACITY_ALPHA, newSettings.opacityAlpha)
            putInt(KEY_TOP_OFFSET, newSettings.topOffsetDp)
            putInt(KEY_BOTTOM_OFFSET, newSettings.bottomOffsetDp)
            putBoolean(KEY_TOP_ENABLED, newSettings.isTopEnabled)
            putBoolean(KEY_BOTTOM_ENABLED, newSettings.isBottomEnabled)
            putBoolean(KEY_AUTO_START, newSettings.autoStartOnBoot)
            putBoolean(KEY_PAUSE_SCREEN_OFF, newSettings.pauseOnScreenOff)
            putBoolean(KEY_AMOLED_SAVER, newSettings.amoledSaverMode)
            putBoolean(KEY_SHOW_NOTIFICATION, newSettings.showNotification)
            putString(KEY_LANGUAGE, newSettings.language)
            putBoolean(KEY_ONBOARDING_COMPLETED, newSettings.isOnboardingCompleted)
            apply()
        }
        _settingsState.value = newSettings
    }

    companion object {
        private const val PREFS_NAME = "corner_rounder_prefs"
        private const val KEY_ENABLED = "key_enabled"
        private const val KEY_UNIFIED_RADIUS = "key_unified_radius"
        private const val KEY_INDIVIDUAL_CORNERS = "key_individual_corners"
        private const val KEY_TOP_LEFT_RADIUS = "key_top_left_radius"
        private const val KEY_TOP_RIGHT_RADIUS = "key_top_right_radius"
        private const val KEY_BOTTOM_LEFT_RADIUS = "key_bottom_left_radius"
        private const val KEY_BOTTOM_RIGHT_RADIUS = "key_bottom_right_radius"
        private const val KEY_SHAPE_TYPE = "key_shape_type"
        private const val KEY_COLOR_HEX = "key_color_hex"
        private const val KEY_OPACITY_ALPHA = "key_opacity_alpha"
        private const val KEY_TOP_OFFSET = "key_top_offset"
        private const val KEY_BOTTOM_OFFSET = "key_bottom_offset"
        private const val KEY_TOP_ENABLED = "key_top_enabled"
        private const val KEY_BOTTOM_ENABLED = "key_bottom_enabled"
        private const val KEY_AUTO_START = "key_auto_start"
        private const val KEY_PAUSE_SCREEN_OFF = "key_pause_screen_off"
        private const val KEY_AMOLED_SAVER = "key_amoled_saver"
        private const val KEY_SHOW_NOTIFICATION = "key_show_notification"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
    }
}

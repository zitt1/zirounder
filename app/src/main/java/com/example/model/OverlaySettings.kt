package com.example.model

enum class CornerShapeType {
    ROUND,     // Classic circular arc arc
    SQUIRCLE,  // Smooth superellipse (iOS / Pixel style)
    NOTCH      // Inverted straight chamfer / angled
}

data class OverlaySettings(
    val isEnabled: Boolean = true,
    val unifiedRadiusDp: Int = 15,
    val isIndividualCorners: Boolean = false,
    val topLeftRadiusDp: Int = 15,
    val topRightRadiusDp: Int = 15,
    val bottomLeftRadiusDp: Int = 15,
    val bottomRightRadiusDp: Int = 15,
    val shapeType: CornerShapeType = CornerShapeType.ROUND,
    val colorHex: String = "#000000",
    val opacityAlpha: Float = 1.0f,
    val topOffsetDp: Int = 0,
    val bottomOffsetDp: Int = 0,
    val isTopEnabled: Boolean = true,
    val isBottomEnabled: Boolean = true,
    val autoStartOnBoot: Boolean = true,
    val pauseOnScreenOff: Boolean = true,
    val amoledSaverMode: Boolean = true,
    val showNotification: Boolean = false,
    val language: String = "ru", // "ru" or "en"
    val isOnboardingCompleted: Boolean = false
)

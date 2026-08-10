package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CornerShapeType
import com.example.model.OverlaySettings

@Composable
fun CornerPreviewCard(
    settings: OverlaySettings,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(28.dp))
            .padding(18.dp)
            .testTag("corner_preview_card"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    val isRu = settings.language != "en"
                    Text(
                        text = if (isRu) "Экранный Оверлей" else "Screen Overlay",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = if (isRu) "Предпросмотр на экране" else "Screen preview",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            if (settings.isEnabled) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    val isRu = settings.language != "en"
                    Text(
                        text = if (settings.isEnabled) (if (isRu) "Вкл" else "On") else (if (isRu) "Выкл" else "Off"),
                        fontSize = 11.sp,
                        color = if (settings.isEnabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Simulated Device Screen Container
            Box(
                modifier = Modifier
                    .width(230.dp)
                    .height(290.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A),
                                Color(0xFF181825)
                            )
                        )
                    )
                    .border(2.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
            ) {

                // Mock Screen Contents
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Status bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "09:41",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White.copy(alpha = 0.3f))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(10.dp)
                        )
                    }

                    // Content preview widgets
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Скругление: ${if (settings.isIndividualCorners) "Индивид." else "${settings.unifiedRadiusDp}dp"}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(65.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("OLED Saver", fontSize = 10.sp, color = Color(0xFF00E5FF))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(65.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF00E676).copy(alpha = 0.15f))
                                    .border(1.dp, Color(0xFF00E676).copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("0% АКБ", fontSize = 10.sp, color = Color(0xFF00E676))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Gesture Home Bar
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                            .width(70.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                }

                // Render Canvas Rounded Corners Overlay over simulated screen
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (!settings.isEnabled) return@Canvas

                    val scale = size.width / 220f
                    val tlRad = (if (settings.isIndividualCorners) settings.topLeftRadiusDp else settings.unifiedRadiusDp) * scale * 1.8f
                    val trRad = (if (settings.isIndividualCorners) settings.topRightRadiusDp else settings.unifiedRadiusDp) * scale * 1.8f
                    val blRad = (if (settings.isIndividualCorners) settings.bottomLeftRadiusDp else settings.unifiedRadiusDp) * scale * 1.8f
                    val brRad = (if (settings.isIndividualCorners) settings.bottomRightRadiusDp else settings.unifiedRadiusDp) * scale * 1.8f

                    val color = try {
                        val parsed = android.graphics.Color.parseColor(settings.colorHex)
                        Color(parsed).copy(alpha = settings.opacityAlpha)
                    } catch (e: Exception) {
                        Color.Black.copy(alpha = settings.opacityAlpha)
                    }

                    val shape = settings.shapeType

                    // Top Left Corner
                    if (settings.isTopEnabled && tlRad > 0f) {
                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(tlRad, 0f)
                            when (shape) {
                                CornerShapeType.ROUND -> {
                                    arcTo(Rect(0f, 0f, 2 * tlRad, 2 * tlRad), 270f, -90f, false)
                                }
                                CornerShapeType.SQUIRCLE -> {
                                    cubicTo(tlRad * 0.4f, 0f, 0f, tlRad * 0.4f, 0f, tlRad)
                                }
                                CornerShapeType.NOTCH -> {
                                    lineTo(0f, tlRad)
                                }
                            }
                            lineTo(0f, 0f)
                            close()
                        }
                        drawPath(path, color)
                    }

                    // Top Right Corner
                    if (settings.isTopEnabled && trRad > 0f) {
                        val w = size.width
                        val path = Path().apply {
                            moveTo(w, 0f)
                            lineTo(w - trRad, 0f)
                            when (shape) {
                                CornerShapeType.ROUND -> {
                                    arcTo(Rect(w - 2 * trRad, 0f, w, 2 * trRad), 270f, 90f, false)
                                }
                                CornerShapeType.SQUIRCLE -> {
                                    cubicTo(w - trRad * 0.4f, 0f, w, trRad * 0.4f, w, trRad)
                                }
                                CornerShapeType.NOTCH -> {
                                    lineTo(w, trRad)
                                }
                            }
                            lineTo(w, 0f)
                            close()
                        }
                        drawPath(path, color)
                    }

                    // Bottom Left Corner
                    if (settings.isBottomEnabled && blRad > 0f) {
                        val h = size.height
                        val path = Path().apply {
                            moveTo(0f, h)
                            lineTo(0f, h - blRad)
                            when (shape) {
                                CornerShapeType.ROUND -> {
                                    arcTo(Rect(0f, h - 2 * blRad, 2 * blRad, h), 180f, -90f, false)
                                }
                                CornerShapeType.SQUIRCLE -> {
                                    cubicTo(0f, h - blRad * 0.4f, blRad * 0.4f, h, blRad, h)
                                }
                                CornerShapeType.NOTCH -> {
                                    lineTo(blRad, h)
                                }
                            }
                            lineTo(0f, h)
                            close()
                        }
                        drawPath(path, color)
                    }

                    // Bottom Right Corner
                    if (settings.isBottomEnabled && brRad > 0f) {
                        val w = size.width
                        val h = size.height
                        val path = Path().apply {
                            moveTo(w, h)
                            lineTo(w, h - brRad)
                            when (shape) {
                                CornerShapeType.ROUND -> {
                                    arcTo(Rect(w - 2 * brRad, h - 2 * brRad, w, h), 0f, 90f, false)
                                }
                                CornerShapeType.SQUIRCLE -> {
                                    cubicTo(w, h - brRad * 0.4f, w - brRad * 0.4f, h, w - brRad, h)
                                }
                                CornerShapeType.NOTCH -> {
                                    lineTo(w - brRad, h)
                                }
                            }
                            lineTo(w, h)
                            close()
                        }
                        drawPath(path, color)
                    }
                }
            }
        }
    }
}

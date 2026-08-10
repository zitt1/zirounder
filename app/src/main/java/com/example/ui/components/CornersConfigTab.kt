package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.RoundedCorner
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CornerShapeType
import com.example.model.OverlaySettings

@Composable
fun CornersConfigTab(
    settings: OverlaySettings,
    onSetUnifiedRadius: (Int) -> Unit,
    onSetIndividualCorners: (Boolean) -> Unit,
    onSetCornerRadii: (Int, Int, Int, Int) -> Unit,
    onSetShapeType: (CornerShapeType) -> Unit,
    onSetTopOffset: (Int) -> Unit,
    onSetBottomOffset: (Int) -> Unit,
    onToggleTop: (Boolean) -> Unit,
    onToggleBottom: (Boolean) -> Unit,
    onApplyPreset: (Int, CornerShapeType) -> Unit,
    modifier: Modifier = Modifier
) {
    val isRu = settings.language != "en"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Bento Card: Main Corner Radius
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .border(
                                    2.5.dp,
                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                    RoundedCornerShape(topStart = 10.dp)
                                )
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${settings.unifiedRadiusDp}px",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 38.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (isRu) "Радиус Скругления" else "Corner Radius",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = settings.unifiedRadiusDp.toFloat(),
                    onValueChange = { onSetUnifiedRadius(it.toInt()) },
                    valueRange = 0f..60f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        activeTrackColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        inactiveTrackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("radius_slider")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Individual Corners Toggle inside Hero
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isRu) "Раздельная настройка 4 углов" else "Individual 4 corner setup",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.isIndividualCorners,
                        onCheckedChange = onSetIndividualCorners,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.surface,
                            checkedTrackColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                // Individual Corner Sliders
                AnimatedVisibility(visible = settings.isIndividualCorners) {
                    Column(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                    ) {
                        IndividualCornerSlider(
                            label = if (isRu) "Верхний Левый (TL)" else "Top Left (TL)",
                            value = settings.topLeftRadiusDp,
                            onValueChange = { tl ->
                                onSetCornerRadii(tl, settings.topRightRadiusDp, settings.bottomLeftRadiusDp, settings.bottomRightRadiusDp)
                            }
                        )
                        IndividualCornerSlider(
                            label = if (isRu) "Верхний Правый (TR)" else "Top Right (TR)",
                            value = settings.topRightRadiusDp,
                            onValueChange = { tr ->
                                onSetCornerRadii(settings.topLeftRadiusDp, tr, settings.bottomLeftRadiusDp, settings.bottomRightRadiusDp)
                            }
                        )
                        IndividualCornerSlider(
                            label = if (isRu) "Нижний Левый (BL)" else "Bottom Left (BL)",
                            value = settings.bottomLeftRadiusDp,
                            onValueChange = { bl ->
                                onSetCornerRadii(settings.topLeftRadiusDp, settings.topRightRadiusDp, bl, settings.bottomRightRadiusDp)
                            }
                        )
                        IndividualCornerSlider(
                            label = if (isRu) "Нижний Правый (BR)" else "Bottom Right (BR)",
                            value = settings.bottomRightRadiusDp,
                            onValueChange = { br ->
                                onSetCornerRadii(settings.topLeftRadiusDp, settings.topRightRadiusDp, settings.bottomLeftRadiusDp, br)
                            }
                        )
                    }
                }
            }
        }

        // Quick Presets Bento Section
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (isRu) "Быстрые Пресеты" else "Quick Presets",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetChip(
                        label = "15px",
                        subtitle = if (isRu) "Стандарт" else "Standard",
                        isSelected = settings.unifiedRadiusDp == 15 && !settings.isIndividualCorners && settings.shapeType == CornerShapeType.ROUND,
                        onClick = { onApplyPreset(15, CornerShapeType.ROUND) },
                        modifier = Modifier.weight(1f)
                    )
                    PresetChip(
                        label = "20px",
                        subtitle = "Medium",
                        isSelected = settings.unifiedRadiusDp == 20 && !settings.isIndividualCorners && settings.shapeType == CornerShapeType.ROUND,
                        onClick = { onApplyPreset(20, CornerShapeType.ROUND) },
                        modifier = Modifier.weight(1f)
                    )
                    PresetChip(
                        label = "28px",
                        subtitle = "Large",
                        isSelected = settings.unifiedRadiusDp == 28 && !settings.isIndividualCorners && settings.shapeType == CornerShapeType.ROUND,
                        onClick = { onApplyPreset(28, CornerShapeType.ROUND) },
                        modifier = Modifier.weight(1f)
                    )
                    PresetChip(
                        label = "36px",
                        subtitle = "Squircle",
                        isSelected = settings.unifiedRadiusDp == 36 && !settings.isIndividualCorners && settings.shapeType == CornerShapeType.SQUIRCLE,
                        onClick = { onApplyPreset(36, CornerShapeType.SQUIRCLE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Shape Type Selector Card
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (isRu) "Форма Скругления" else "Corner Shape",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShapeTypeCard(
                        title = if (isRu) "Круглая" else "Round",
                        description = if (isRu) "Классика" else "Classic",
                        isSelected = settings.shapeType == CornerShapeType.ROUND,
                        onClick = { onSetShapeType(CornerShapeType.ROUND) },
                        modifier = Modifier.weight(1f)
                    )
                    ShapeTypeCard(
                        title = "Squircle",
                        description = if (isRu) "Суперэллипс" else "Superellipse",
                        isSelected = settings.shapeType == CornerShapeType.SQUIRCLE,
                        onClick = { onSetShapeType(CornerShapeType.SQUIRCLE) },
                        modifier = Modifier.weight(1f)
                    )
                    ShapeTypeCard(
                        title = "Notch",
                        description = if (isRu) "Фасетный" else "Chamfer",
                        isSelected = settings.shapeType == CornerShapeType.NOTCH,
                        onClick = { onSetShapeType(CornerShapeType.NOTCH) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Top & Bottom Toggles and Offsets Card
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (isRu) "Позиционирование и Отступы" else "Position & Offsets",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Top Toggle & Offset
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isRu) "Верхние Углы" else "Top Corners",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.isTopEnabled,
                        onCheckedChange = onToggleTop,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.surface,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                if (settings.isTopEnabled) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isRu) "Смещение сверху:" else "Top offset:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${settings.topOffsetDp} dp", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = settings.topOffsetDp.toFloat(),
                        onValueChange = { onSetTopOffset(it.toInt()) },
                        valueRange = 0f..40f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Toggle & Offset
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isRu) "Нижние Углы" else "Bottom Corners",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.isBottomEnabled,
                        onCheckedChange = onToggleBottom,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.surface,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                if (settings.isBottomEnabled) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isRu) "Смещение снизу:" else "Bottom offset:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${settings.bottomOffsetDp} dp", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = settings.bottomOffsetDp.toFloat(),
                        onValueChange = { onSetBottomOffset(it.toInt()) },
                        valueRange = 0f..40f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}


@Composable
private fun PresetChip(
    label: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IndividualCornerSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "$value dp", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..60f,
            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun ShapeTypeCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (isSelected) Icons.Default.RoundedCorner else Icons.Default.CropSquare,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

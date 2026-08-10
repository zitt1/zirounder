package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.RoundedCorner
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.AppearanceTab
import com.example.ui.components.AutomationTab
import com.example.ui.components.BatteryAuditTab
import com.example.ui.components.CornerPreviewCard
import com.example.ui.components.CornersConfigTab
import com.example.ui.components.MasterControlCard
import com.example.ui.theme.CornerRounderTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CornerRounderTheme {
                CornerRounderApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkPermissions()
    }
}

@Composable
fun CornerRounderApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()
    val hasOverlayPermission by viewModel.hasOverlayPermission.collectAsStateWithLifecycle()
    val isBatteryOptimizationIgnored by viewModel.isBatteryOptimizationIgnored.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    val isRu = settings.language != "en"

    if (!settings.isOnboardingCompleted) {
        OnboardingScreen(
            settings = settings,
            hasOverlayPermission = hasOverlayPermission,
            isBatteryOptimizationIgnored = isBatteryOptimizationIgnored,
            onSelectLanguage = { viewModel.setLanguage(it) },
            onRequestOverlayPermission = { viewModel.requestOverlayPermission(context) },
            onRequestBatteryExemption = { viewModel.requestIgnoreBatteryOptimizations(context) },
            onCompleteOnboarding = {
                viewModel.completeOnboarding()
                viewModel.toggleService(true, context)
            }
        )
        return
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Square,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "zirounder",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = if (isRu) "Легкое скругление углов" else "Lightweight corner rounding",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Quick Language Switcher Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .clickable { viewModel.setLanguage(if (isRu) "en" else "ru") }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRu) "RU" else "EN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Device Preview Box
                CornerPreviewCard(settings = settings)

                // Master Toggle Control Card
                MasterControlCard(
                    settings = settings,
                    hasOverlayPermission = hasOverlayPermission,
                    onToggleService = { enabled -> viewModel.toggleService(enabled, context) },
                    onRequestPermission = { viewModel.requestOverlayPermission(context) }
                )

                // Bento Pill Tab Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 6.dp,
                    indicator = { },
                    divider = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(28.dp))
                        .padding(4.dp)
                        .testTag("tab_row")
                ) {
                    TabItem(if (isRu) "Углы" else "Corners", Icons.Default.RoundedCorner, selectedTabIndex == 0) { selectedTabIndex = 0 }
                    TabItem(if (isRu) "Вид" else "Style", Icons.Default.ColorLens, selectedTabIndex == 1) { selectedTabIndex = 1 }
                    TabItem(if (isRu) "Батарея" else "Battery", Icons.Default.BatterySaver, selectedTabIndex == 2) { selectedTabIndex = 2 }
                    TabItem(if (isRu) "Авто" else "Auto", Icons.Default.Autorenew, selectedTabIndex == 3) { selectedTabIndex = 3 }
                }

                // Active Tab Content
                when (selectedTabIndex) {
                    0 -> CornersConfigTab(
                        settings = settings,
                        onSetUnifiedRadius = { viewModel.setUnifiedRadius(it) },
                        onSetIndividualCorners = { viewModel.setIndividualCorners(it) },
                        onSetCornerRadii = { tl, tr, bl, br -> viewModel.setCornerRadii(tl, tr, bl, br) },
                        onSetShapeType = { viewModel.setShapeType(it) },
                        onSetTopOffset = { viewModel.setTopOffset(it) },
                        onSetBottomOffset = { viewModel.setBottomOffset(it) },
                        onToggleTop = { viewModel.toggleTopEnabled(it) },
                        onToggleBottom = { viewModel.toggleBottomEnabled(it) },
                        onApplyPreset = { rad, shape -> viewModel.applyPreset(rad, shape) }
                    )
                    1 -> AppearanceTab(
                        settings = settings,
                        onSetColorHex = { viewModel.setColorHex(it) },
                        onSetOpacity = { viewModel.setOpacity(it) },
                        onSetAmoledSaver = { viewModel.setAmoledSaver(it) }
                    )
                    2 -> BatteryAuditTab(
                        settings = settings,
                        isBatteryOptimizationIgnored = isBatteryOptimizationIgnored,
                        onSetPauseScreenOff = { viewModel.setPauseScreenOff(it) },
                        onRequestIgnoreBatteryOptimization = { viewModel.requestIgnoreBatteryOptimizations(context) }
                    )
                    3 -> AutomationTab(
                        settings = settings,
                        onSetAutoStart = { viewModel.setAutoStart(it) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun OnboardingScreen(
    settings: com.example.model.OverlaySettings,
    hasOverlayPermission: Boolean,
    isBatteryOptimizationIgnored: Boolean,
    onSelectLanguage: (String) -> Unit,
    onRequestOverlayPermission: () -> Unit,
    onRequestBatteryExemption: () -> Unit,
    onCompleteOnboarding: () -> Unit
) {
    val isRu = settings.language != "en"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo / App Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Square,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "zirounder",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Text(
                text = if (isRu) "Настройка в 2 шага" else "2-Step Setup",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Step 1: Language Choice Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRu) "1. Выберите язык / Select language" else "1. Select language",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSelectLanguage("ru") },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                if (isRu) 2.dp else 1.dp,
                                if (isRu) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isRu) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Русский 🇷🇺",
                                fontWeight = if (isRu) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        OutlinedButton(
                            onClick = { onSelectLanguage("en") },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                if (!isRu) 2.dp else 1.dp,
                                if (!isRu) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (!isRu) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "English 🇬🇧",
                                fontWeight = if (!isRu) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 2: Permissions Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRu) "2. Все необходимые разрешения" else "2. Required Permissions",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Permission 1: Overlay
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isRu) "Поверх всех окон" else "Display over apps",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = if (hasOverlayPermission) (if (isRu) "✓ Разрешено" else "✓ Granted") else (if (isRu) "Обязательно для углов" else "Required for corners"),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (hasOverlayPermission) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                                )
                            )
                        }
                        Button(
                            onClick = onRequestOverlayPermission,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasOverlayPermission) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (hasOverlayPermission) (if (isRu) "Готово" else "Granted") else (if (isRu) "Разрешить" else "Grant"),
                                color = if (hasOverlayPermission) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Permission 2: Battery exemption
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isRu) "Оптимизация батареи" else "Battery optimization",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = if (isBatteryOptimizationIgnored) (if (isRu) "✓ Исключено" else "✓ Exemption active") else (if (isRu) "Для работы в фоне" else "For background stability"),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isBatteryOptimizationIgnored) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Button(
                            onClick = onRequestBatteryExemption,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBatteryOptimizationIgnored) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isBatteryOptimizationIgnored) (if (isRu) "Готово" else "Granted") else (if (isRu) "Разрешить" else "Grant"),
                                color = if (isBatteryOptimizationIgnored) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Launch Button
            Button(
                onClick = onCompleteOnboarding,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isRu) "Запустить zirounder" else "Start zirounder",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
            )
            .padding(vertical = 10.dp, horizontal = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


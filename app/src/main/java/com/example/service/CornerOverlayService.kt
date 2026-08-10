package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.SettingsRepository
import com.example.model.OverlaySettings
import com.example.ui.components.CornerPosition
import com.example.ui.components.CornerShapeView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CornerOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var repository: SettingsRepository

    private var topLeftView: CornerShapeView? = null
    private var topRightView: CornerShapeView? = null
    private var bottomLeftView: CornerShapeView? = null
    private var bottomRightView: CornerShapeView? = null

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var currentSettings: OverlaySettings? = null
    private var isViewsAttached = false

    // Screen State Receiver for Zero Power Consumption when locked
    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (currentSettings?.pauseOnScreenOff == true) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_OFF -> setViewsVisibility(View.GONE)
                    Intent.ACTION_SCREEN_ON -> setViewsVisibility(View.VISIBLE)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        repository = SettingsRepository(this)

        startForegroundServiceWithNotification()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(screenReceiver, filter)

        observeSettings()
    }

    private fun startForegroundServiceWithNotification() {
        val channelId = "corner_rounder_overlay_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Corner Overlay Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Runs the ultra battery optimized screen corner rounder overlay"
                setSound(null, null)
                enableVibration(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.overlay_service_title))
            .setContentText(getString(R.string.overlay_service_desc))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        startForeground(1001, notification)
    }

    private fun observeSettings() {
        serviceScope.launch {
            repository.settingsState.collectLatest { settings ->
                currentSettings = settings
                if (settings.isEnabled && Settings.canDrawOverlays(this@CornerOverlayService)) {
                    applySettings(settings)
                } else {
                    removeOverlayViews()
                    stopSelf()
                }
            }
        }
    }

    private fun applySettings(settings: OverlaySettings) {
        val density = resources.displayMetrics.density

        val tlRadius = if (settings.isIndividualCorners) settings.topLeftRadiusDp else settings.unifiedRadiusDp
        val trRadius = if (settings.isIndividualCorners) settings.topRightRadiusDp else settings.unifiedRadiusDp
        val blRadius = if (settings.isIndividualCorners) settings.bottomLeftRadiusDp else settings.unifiedRadiusDp
        val brRadius = if (settings.isIndividualCorners) settings.bottomRightRadiusDp else settings.unifiedRadiusDp

        val topOffsetPx = (settings.topOffsetDp * density).toInt()
        val bottomOffsetPx = (settings.bottomOffsetDp * density).toInt()

        if (!isViewsAttached) {
            createAndAttachViews(settings)
        }

        // Update Top Left
        if (settings.isTopEnabled && tlRadius > 0) {
            topLeftView?.let { view ->
                view.visibility = View.VISIBLE
                view.updateConfig(tlRadius * density, settings.colorHex, settings.opacityAlpha, settings.shapeType)
                updateViewLayoutParams(view, Gravity.TOP or Gravity.LEFT, (tlRadius * density).toInt(), topOffsetPx)
            }
        } else {
            topLeftView?.visibility = View.GONE
        }

        // Update Top Right
        if (settings.isTopEnabled && trRadius > 0) {
            topRightView?.let { view ->
                view.visibility = View.VISIBLE
                view.updateConfig(trRadius * density, settings.colorHex, settings.opacityAlpha, settings.shapeType)
                updateViewLayoutParams(view, Gravity.TOP or Gravity.RIGHT, (trRadius * density).toInt(), topOffsetPx)
            }
        } else {
            topRightView?.visibility = View.GONE
        }

        // Update Bottom Left
        if (settings.isBottomEnabled && blRadius > 0) {
            bottomLeftView?.let { view ->
                view.visibility = View.VISIBLE
                view.updateConfig(blRadius * density, settings.colorHex, settings.opacityAlpha, settings.shapeType)
                updateViewLayoutParams(view, Gravity.BOTTOM or Gravity.LEFT, (blRadius * density).toInt(), bottomOffsetPx)
            }
        } else {
            bottomLeftView?.visibility = View.GONE
        }

        // Update Bottom Right
        if (settings.isBottomEnabled && brRadius > 0) {
            bottomRightView?.let { view ->
                view.visibility = View.VISIBLE
                view.updateConfig(brRadius * density, settings.colorHex, settings.opacityAlpha, settings.shapeType)
                updateViewLayoutParams(view, Gravity.BOTTOM or Gravity.RIGHT, (brRadius * density).toInt(), bottomOffsetPx)
            }
        } else {
            bottomRightView?.visibility = View.GONE
        }
    }

    private fun createAndAttachViews(settings: OverlaySettings) {
        if (isViewsAttached) return

        topLeftView = CornerShapeView(this, CornerPosition.TOP_LEFT)
        topRightView = CornerShapeView(this, CornerPosition.TOP_RIGHT)
        bottomLeftView = CornerShapeView(this, CornerPosition.BOTTOM_LEFT)
        bottomRightView = CornerShapeView(this, CornerPosition.BOTTOM_RIGHT)

        val views: List<Pair<View, Int>> = listOfNotNull(
            topLeftView?.let { it to (Gravity.TOP or Gravity.LEFT) },
            topRightView?.let { it to (Gravity.TOP or Gravity.RIGHT) },
            bottomLeftView?.let { it to (Gravity.BOTTOM or Gravity.LEFT) },
            bottomRightView?.let { it to (Gravity.BOTTOM or Gravity.RIGHT) }
        )

        for (pair in views) {
            val view = pair.first
            val gravity = pair.second
            val params = createOverlayLayoutParams(gravity, 100, 0)
            try {
                windowManager.addView(view, params)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        isViewsAttached = true
    }

    private fun createOverlayLayoutParams(gravity: Int, sizePx: Int, offsetPx: Int): WindowManager.LayoutParams {
        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }

        val flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        return WindowManager.LayoutParams(
            sizePx.coerceAtLeast(1),
            sizePx.coerceAtLeast(1),
            windowType,
            flags,
            PixelFormat.TRANSLUCENT
        ).apply {
            this.gravity = gravity
            this.y = offsetPx
            this.x = 0
        }
    }

    private fun updateViewLayoutParams(view: View, gravity: Int, sizePx: Int, offsetPx: Int) {
        try {
            val params = view.layoutParams as WindowManager.LayoutParams
            if (params.width != sizePx || params.height != sizePx || params.y != offsetPx || params.gravity != gravity) {
                params.width = sizePx.coerceAtLeast(1)
                params.height = sizePx.coerceAtLeast(1)
                params.gravity = gravity
                params.y = offsetPx
                windowManager.updateViewLayout(view, params)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setViewsVisibility(visibility: Int) {
        topLeftView?.visibility = if (currentSettings?.isTopEnabled == true) visibility else View.GONE
        topRightView?.visibility = if (currentSettings?.isTopEnabled == true) visibility else View.GONE
        bottomLeftView?.visibility = if (currentSettings?.isBottomEnabled == true) visibility else View.GONE
        bottomRightView?.visibility = if (currentSettings?.isBottomEnabled == true) visibility else View.GONE
    }

    private fun removeOverlayViews() {
        if (!isViewsAttached) return

        val views = listOf(topLeftView, topRightView, bottomLeftView, bottomRightView)
        for (v in views) {
            if (v != null) {
                try {
                    windowManager.removeViewImmediate(v)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        topLeftView = null
        topRightView = null
        bottomLeftView = null
        bottomRightView = null
        isViewsAttached = false
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(screenReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        serviceJob.cancel()
        removeOverlayViews()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

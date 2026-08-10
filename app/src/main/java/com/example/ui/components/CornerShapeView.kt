package com.example.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import com.example.model.CornerShapeType

enum class CornerPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}

class CornerShapeView(
    context: Context,
    private val position: CornerPosition
) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    private val path = Path()
    private val rectF = RectF()

    private var radiusPx: Float = 0f
    private var shapeType: CornerShapeType = CornerShapeType.ROUND

    fun updateConfig(radiusPx: Float, colorHex: String, opacityAlpha: Float, shape: CornerShapeType) {
        val newRadius = radiusPx.coerceAtLeast(0f)
        var parsedColor = try {
            Color.parseColor(colorHex)
        } catch (e: Exception) {
            Color.BLACK
        }

        val alphaInt = (opacityAlpha.coerceIn(0f, 1f) * 255).toInt()
        parsedColor = Color.argb(
            alphaInt,
            Color.red(parsedColor),
            Color.green(parsedColor),
            Color.blue(parsedColor)
        )

        if (this.radiusPx != newRadius || paint.color != parsedColor || this.shapeType != shape) {
            this.radiusPx = newRadius
            this.paint.color = parsedColor
            this.shapeType = shape

            // Request layout & draw only when config actually changes
            requestLayout()
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = radiusPx.toInt()
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (radiusPx <= 0f) return

        path.reset()
        val w = width.toFloat()
        val h = height.toFloat()

        when (position) {
            CornerPosition.TOP_LEFT -> {
                path.moveTo(0f, 0f)
                path.lineTo(w, 0f)
                when (shapeType) {
                    CornerShapeType.ROUND -> {
                        rectF.set(0f, 0f, 2 * w, 2 * h)
                        path.arcTo(rectF, 270f, -90f, false)
                    }
                    CornerShapeType.SQUIRCLE -> {
                        path.cubicTo(w * 0.4f, 0f, 0f, h * 0.4f, 0f, h)
                    }
                    CornerShapeType.NOTCH -> {
                        path.lineTo(0f, h)
                    }
                }
                path.lineTo(0f, 0f)
            }
            CornerPosition.TOP_RIGHT -> {
                path.moveTo(w, 0f)
                path.lineTo(0f, 0f)
                when (shapeType) {
                    CornerShapeType.ROUND -> {
                        rectF.set(-w, 0f, w, 2 * h)
                        path.arcTo(rectF, 270f, 90f, false)
                    }
                    CornerShapeType.SQUIRCLE -> {
                        path.cubicTo(w * 0.6f, 0f, w, h * 0.4f, w, h)
                    }
                    CornerShapeType.NOTCH -> {
                        path.lineTo(w, h)
                    }
                }
                path.lineTo(w, 0f)
            }
            CornerPosition.BOTTOM_LEFT -> {
                path.moveTo(0f, h)
                path.lineTo(0f, 0f)
                when (shapeType) {
                    CornerShapeType.ROUND -> {
                        rectF.set(0f, -h, 2 * w, h)
                        path.arcTo(rectF, 180f, -90f, false)
                    }
                    CornerShapeType.SQUIRCLE -> {
                        path.cubicTo(0f, h * 0.6f, w * 0.4f, h, w, h)
                    }
                    CornerShapeType.NOTCH -> {
                        path.lineTo(w, h)
                    }
                }
                path.lineTo(0f, h)
            }
            CornerPosition.BOTTOM_RIGHT -> {
                path.moveTo(w, h)
                path.lineTo(w, 0f)
                when (shapeType) {
                    CornerShapeType.ROUND -> {
                        rectF.set(-w, -h, w, h)
                        path.arcTo(rectF, 0f, 90f, false)
                    }
                    CornerShapeType.SQUIRCLE -> {
                        path.cubicTo(w, h * 0.6f, w * 0.6f, h, 0f, h)
                    }
                    CornerShapeType.NOTCH -> {
                        path.lineTo(0f, h)
                    }
                }
                path.lineTo(w, h)
            }
        }
        path.close()
        canvas.drawPath(path, paint)
    }
}

package com.example.client.gui.elements

import com.example.client.gui.GuiElement
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import kotlin.math.*

class CircleElement(
    centerX: Float = 0f,
    centerY: Float = 0f,
    var radius: Float = 20f,
    var fillColor: Int = 0xFFFFFFFF.toInt(),
    var outlineColor: Int? = null,
    var outlineWidth: Float = 2f,
    var startAngle: Float = 0f,
    var sweepAngle: Float = 360f,
    var gradientStart: Int? = null,
    var gradientEnd: Int? = null,
    var segments: Int = 32,
    zIndex: Int = 0
) : GuiElement(centerX - radius, centerY - radius, radius * 2, radius * 2, zIndex = zIndex) {
    
    init {
        updateBounds()
    }
    
    private fun updateBounds() {
        // x и y - это верхний левый угол bounding box
        // centerX и centerY переданы в конструктор
    }
    
    override fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        if (!visible) return
        
        val cx = x + radius
        val cy = y + radius
        
        // Рисуем заполненный сектор
        if (fillColor != 0x00000000 && sweepAngle > 0) {
            drawSector(context, cx, cy, radius, startAngle, sweepAngle, fillColor, gradientStart, gradientEnd)
        }
        
        // Рисуем обводку
        outlineColor?.let { color ->
            if (color != 0x00000000) {
                drawSectorOutline(context, cx, cy, radius, startAngle, sweepAngle, color, outlineWidth)
            }
        }
    }
    
    private fun drawSector(
        context: DrawContext,
        cx: Float, cy: Float,
        radius: Float,
        start: Float,
        sweep: Float,
        color: Int,
        gradStart: Int? = null,
        gradEnd: Int? = null
    ) {
        if (sweep <= 0) return
        
        val startRad = Math.toRadians(start.toDouble())
        val sweepRad = Math.toRadians(sweep.toDouble())
        val step = (sweepRad / segments).toFloat()
        
        var angle = startRad.toFloat()
        val endAngle = startRad.toFloat() + sweepRad.toFloat()
        
        // Рисуем треугольники от центра к окружности
        while (angle < endAngle) {
            val nextAngle = min(angle + step, endAngle)
            
            val x1 = cx + radius * cos(angle.toDouble()).toFloat()
            val y1 = cy + radius * sin(angle.toDouble()).toFloat()
            val x2 = cx + radius * cos(nextAngle.toDouble()).toFloat()
            val y2 = cy + radius * sin(nextAngle.toDouble()).toFloat()
            
            // Вычисляем цвет для градиента
            val finalColor = when {
                gradStart != null && gradEnd != null -> {
                    val progress = (angle - startRad.toFloat()) / sweepRad.toFloat()
                    interpolateColor(gradStart, gradEnd, progress)
                }
                else -> color
            }
            
            // Рисуем залитый треугольник
            drawFilledTriangle(context, cx.toInt(), cy.toInt(), x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt(), finalColor)
            
            angle += step
        }
    }
    
    private fun drawSectorOutline(
        context: DrawContext,
        cx: Float, cy: Float,
        radius: Float,
        start: Float,
        sweep: Float,
        color: Int,
        width: Float
    ) {
        if (sweep <= 0) return
        
        val startRad = Math.toRadians(start.toDouble())
        val sweepRad = Math.toRadians(sweep.toDouble())
        
        val step = (sweepRad / segments).toFloat()
        var angle = startRad.toFloat()
        val endAngle = startRad.toFloat() + sweepRad.toFloat()
        
        var lastX = cx + radius * cos(angle.toDouble()).toFloat()
        var lastY = cy + radius * sin(angle.toDouble()).toFloat()
        
        angle += step
        
        while (angle <= endAngle) {
            val x = cx + radius * cos(angle.toDouble()).toFloat()
            val y = cy + radius * sin(angle.toDouble()).toFloat()
            
            drawThickLine(context, lastX, lastY, x, y, width, color)
            
            lastX = x
            lastY = y
            angle += step
        }
        
        // Замыкаем сектор линиями к центру
        if (sweep < 360f) {
            val startX = cx + radius * cos(startRad).toFloat()
            val startY = cy + radius * sin(startRad).toFloat()
            val endX = cx + radius * cos(startRad + sweepRad).toFloat()
            val endY = cy + radius * sin(startRad + sweepRad).toFloat()
            
            drawThickLine(context, cx, cy, startX, startY, width, color)
            drawThickLine(context, cx, cy, endX, endY, width, color)
        }
    }
    
    private fun drawFilledTriangle(context: DrawContext, x1: Int, y1: Int, x2: Int, y2: Int, x3: Int, y3: Int, color: Int) {
        val minX = minOf(x1, x2, x3)
        val maxX = maxOf(x1, x2, x3)
        val minY = minOf(y1, y2, y3)
        val maxY = maxOf(y1, y2, y3)
        
        for (y in minY..maxY) {
            val intersections = mutableListOf<Int>()
            
            checkLineIntersection(x1, y1, x2, y2, y, intersections)
            checkLineIntersection(x2, y2, x3, y3, y, intersections)
            checkLineIntersection(x3, y3, x1, y1, y, intersections)
            
            intersections.sort()
            
            for (i in 0 until intersections.size step 2) {
                if (i + 1 < intersections.size) {
                    context.fill(intersections[i], y, intersections[i + 1], y + 1, color)
                }
            }
        }
    }
    
    private fun checkLineIntersection(x1: Int, y1: Int, x2: Int, y2: Int, y: Int, intersections: MutableList<Int>) {
        if ((y1 > y) != (y2 > y)) {
            val x = x1 + (x2 - x1) * (y - y1) / (y2 - y1)
            intersections.add(x)
        }
    }
    
    private fun drawThickLine(context: DrawContext, x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Int) {
        // Простая линия через заливку прямоугольника
        val dx = x2 - x1
        val dy = y2 - y1
        val length = sqrt(dx * dx + dy * dy)
        
        if (length == 0f) return
        
        val perpX = -dy / length * thickness / 2
        val perpY = dx / length * thickness / 2
        
        val points = listOf(
            x1 + perpX to y1 + perpY,
            x1 - perpX to y1 - perpY,
            x2 - perpX to y2 - perpY,
            x2 + perpX to y2 + perpY
        )
        
        val minX = points.minOf { it.first.toInt() }
        val maxX = points.maxOf { it.first.toInt() }
        val minY = points.minOf { it.second.toInt() }
        val maxY = points.maxOf { it.second.toInt() }
        
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                if (isPointInPolygon(x.toFloat(), y.toFloat(), points)) {
                    context.fill(x, y, x + 1, y + 1, color)
                }
            }
        }
    }
    
    private fun isPointInPolygon(x: Float, y: Float, polygon: List<Pair<Float, Float>>): Boolean {
        var inside = false
        var j = polygon.size - 1
        
        for (i in polygon.indices) {
            val xi = polygon[i].first
            val yi = polygon[i].second
            val xj = polygon[j].first
            val yj = polygon[j].second
            
            if ((yi > y) != (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
                inside = !inside
            }
            j = i
        }
        
        return inside
    }
    
    private fun interpolateColor(start: Int, end: Int, progress: Float): Int {
        val startA = (start shr 24) and 0xFF
        val startR = (start shr 16) and 0xFF
        val startG = (start shr 8) and 0xFF
        val startB = start and 0xFF
        
        val endA = (end shr 24) and 0xFF
        val endR = (end shr 16) and 0xFF
        val endG = (end shr 8) and 0xFF
        val endB = end and 0xFF
        
        val a = (startA + (endA - startA) * progress).toInt()
        val r = (startR + (endR - startR) * progress).toInt()
        val g = (startG + (endG - startG) * progress).toInt()
        val b = (startB + (endB - startB) * progress).toInt()
        
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
    
    fun setCenter(x: Float, y: Float) {
        this.x = x - radius
        this.y = y - radius
    }
    
    fun setRadius(newRadius: Float) {
        this.radius = newRadius
        this.width = radius * 2
        this.height = radius * 2
    }
}

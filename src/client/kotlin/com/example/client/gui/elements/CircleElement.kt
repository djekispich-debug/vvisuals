
package com.example.client.gui.elements

import com.example.client.gui.GuiElement
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import kotlin.math.*

/**
 * Элемент для отрисовки кругов, секторов и дуг
 * Поддерживает градиенты, обводку и частичную заливку
 */
class CircleElement(
    centerX: Float = 0f,
    centerY: Float = 0f,
    var radius: Float = 20f,
    var fillColor: Int = 0xFFFFFFFF,
    var outlineColor: Int? = null,
    var outlineWidth: Float = 2f,
    var startAngle: Float = 0f,      // Начальный угол в градусах (0 = вверх)
    var sweepAngle: Float = 360f,     // Сколько градусов заливать (360 = полный круг)
    var gradientStart: Int? = null,   // Цвет градиента (если не null)
    var gradientEnd: Int? = null,     // Конечный цвет градиента
    var segments: Int = 32,           // Количество сегментов (чем больше, тем плавнее)
    zIndex: Int = 0
) : GuiElement(centerX - radius, centerY - radius, radius * 2, radius * 2, zIndex = zIndex) {
    
    // Обновляем bounds при изменении радиуса или позиции
    private fun updateBounds() {
        this.x = centerX - radius
        this.y = centerY - radius
        this.width = radius * 2
        this.height = radius * 2
    }
    
    init {
        updateBounds()
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
    
    /**
     * Рисует сектор круга
     */
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
        
        // Используем треугольники для построения сектора
        val step = (sweepRad / segments).toFloat()
        
        var angle = startRad.toFloat()
        val endAngle = startRad.toFloat() + sweepRad.toFloat()
        
        // Первая вершина - центр
        val centerX = cx
        val centerY = cy
        
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
            
            // Рисуем треугольник через заливку (используем много маленьких линий)
            drawFilledTriangle(context, centerX.toInt(), centerY.toInt(), x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt(), finalColor)
            
            angle += step
        }
    }
    
    /**
     * Рисует обводку сектора
     */
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
            
            // Рисуем линию между точками
            drawThickLine(context, lastX, lastY, x, y, width, color)
            
            lastX = x
            lastY = y
            angle += step
        }
        
        // Замыкаем сектор линиями к центру если нужно
        if (sweep < 360f) {
            val startX = cx + radius * cos(startRad).toFloat()
            val startY = cy + radius * sin(startRad).toFloat()
            val endX = cx + radius * cos(startRad + sweepRad).toFloat()
            val endY = cy + radius * sin(startRad + sweepRad).toFloat()
            
            drawThickLine(context, cx, cy, startX, startY, width, color)
            drawThickLine(context, cx, cy, endX, endY, width, color)
        }
    }
    
    /**
     * Рисует залитый треугольник (упрощённая версия через линии)
     */
    private fun drawFilledTriangle(context: DrawContext, x1: Int, y1: Int, x2: Int, y2: Int, x3: Int, y3: Int, color: Int) {
        // Находим границы треугольника
        val minX = minOf(x1, x2, x3)
        val maxX = maxOf(x1, x2, x3)
        val minY = minOf(y1, y2, y3)
        val maxY = maxOf(y1, y2, y3)
        
        // Простая заливка через горизонтальные линии (барицентрическая)
        for (y in minY..maxY) {
            var intersections = mutableListOf<Int>()
            
            // Проверяем пересечения с каждой стороной
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
    
    /**
     * Рисует линию с толщиной
     */
    private fun drawThickLine(context: DrawContext, x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Int) {
        val angle = atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())
        val dx = (sin(angle) * thickness / 2).toFloat()
        val dy = (-cos(angle) * thickness / 2).toFloat()
        
        val x1i = x1.toInt()
        val y1i = y1.toInt()
        val x2i = x2.toInt()
        val y2i = y2.toInt()
        
        // Рисуем как четырёхугольник
        context.fill(x1i, y1i, x2i, y2i, color)
        
        // Дорисовываем кружки на концах для плавности
        val radius = (thickness / 2).toInt()
        drawFilledCircle(context, (x1i - radius).toFloat(), (y1i - radius).toFloat(), radius.toFloat(), color)
        drawFilledCircle(context, (x2i - radius).toFloat(), (y2i - radius).toFloat(), radius.toFloat(), color)
    }
    
    /**
     * Рисует залитый круг (упрощённо)
     */
    private fun drawFilledCircle(context: DrawContext, cx: Float, cy: Float, r: Float, color: Int) {
        val ix = cx.toInt()
        val iy = cy.toInt()
        val ir = r.toInt()
        
        for (y in -ir..ir) {
            val dy = y.toFloat()
            val dx = sqrt(r * r - dy * dy).toInt()
            context.fill(ix - dx, iy + y, ix + dx, iy + y + 1, color)
        }
    }
    
    /**
     * Интерполяция цветов для градиента
     */
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
    
    /**
     * Обновляет позицию центра
     */
    fun setCenter(x: Float, y: Float) {
        this.x = x - radius
        this.y = y - radius
    }
    
    /**
     * Обновляет радиус
     */
    fun setRadius(newRadius: Float) {
        this.radius = newRadius
        updateBounds()
    }
}

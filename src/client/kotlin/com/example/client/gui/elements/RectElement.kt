package com.example.client.gui.elements

import com.example.client.gui.GuiElement
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

class RectElement(
    x: Float = 0f,
    y: Float = 0f,
    width: Float = 100f,
    height: Float = 100f,
    var fillColor: Int = 0xCC000000.toInt(),
    var outlineColor: Int? = null,
    var roundedRadius: Float = 0f,
    zIndex: Int = 0
) : GuiElement(x, y, width, height, zIndex = zIndex) {
    
    override fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        if (!visible) return
        
        if (roundedRadius > 0) {
            // Рисуем скруглённый прямоугольник
            drawRoundedRect(context, x, y, x + width, y + height, roundedRadius, fillColor)
            outlineColor?.let { drawRoundedRectOutline(context, x, y, x + width, y + height, roundedRadius, it) }
        } else {
            // Обычный прямоугольник
            context.fill(x.toInt(), y.toInt(), (x + width).toInt(), (y + height).toInt(), fillColor)
            outlineColor?.let { drawOutline(context, x, y, x + width, y + height, it) }
        }
    }
    
    private fun drawOutline(context: DrawContext, x: Float, y: Float, x2: Float, y2: Float, color: Int) {
        // Верхняя линия
        context.fill(x.toInt(), y.toInt(), x2.toInt(), (y + 1).toInt(), color)
        // Нижняя линия
        context.fill(x.toInt(), (y2 - 1).toInt(), x2.toInt(), y2.toInt(), color)
        // Левая линия
        context.fill(x.toInt(), y.toInt(), (x + 1).toInt(), y2.toInt(), color)
        // Правая линия
        context.fill((x2 - 1).toInt(), y.toInt(), x2.toInt(), y2.toInt(), color)
    }
    
    private fun drawRoundedRect(context: DrawContext, x1: Float, y1: Float, x2: Float, y2: Float, radius: Float, color: Int) {
        val ix1 = x1.toInt()
        val iy1 = y1.toInt()
        val ix2 = x2.toInt()
        val iy2 = y2.toInt()
        val ir = radius.toInt()
        
        // Центральная часть
        context.fill(ix1 + ir, iy1, ix2 - ir, iy2, color)
        context.fill(ix1, iy1 + ir, ix2, iy2 - ir, color)
        
        // Углы (аппроксимация через маленькие прямоугольники)
        for (y in -ir..ir) {
            val dy = Math.abs(y.toFloat())
            val dx = Math.sqrt((ir * ir - dy * dy).toDouble()).toInt()
            
            // Верхний левый
            context.fill(ix1 + ir - dx, iy1 + ir + y, ix1 + ir, iy1 + ir + y + 1, color)
            // Верхний правый
            context.fill(ix2 - ir, iy1 + ir + y, ix2 - ir + dx, iy1 + ir + y + 1, color)
            // Нижний левый
            context.fill(ix1 + ir - dx, iy2 - ir + y, ix1 + ir, iy2 - ir + y + 1, color)
            // Нижний правый
            context.fill(ix2 - ir, iy2 - ir + y, ix2 - ir + dx, iy2 - ir + y + 1, color)
        }
    }
    
    private fun drawRoundedRectOutline(context: DrawContext, x1: Float, y1: Float, x2: Float, y2: Float, radius: Float, color: Int) {
        val ix1 = x1.toInt()
        val iy1 = y1.toInt()
        val ix2 = x2.toInt()
        val iy2 = y2.toInt()
        val ir = radius.toInt()
        
        // Горизонтальные линии
        context.fill(ix1 + ir, iy1, ix2 - ir, iy1 + 1, color) // верх
        context.fill(ix1 + ir, iy2 - 1, ix2 - ir, iy2, color) // низ
        
        // Вертикальные линии
        context.fill(ix1, iy1 + ir, ix1 + 1, iy2 - ir, color) // лево
        context.fill(ix2 - 1, iy1 + ir, ix2, iy2 - ir, color) // право
        
        // Углы (окружности)
        for (y in -ir..ir) {
            val dy = Math.abs(y.toFloat())
            val dx = Math.sqrt((ir * ir - dy * dy).toDouble()).toInt()
            
            // Верхний левый
            context.fill(ix1 + ir - dx, iy1 + ir + y, ix1 + ir - dx + 1, iy1 + ir + y + 1, color)
            // Верхний правый
            context.fill(ix2 - ir + dx - 1, iy1 + ir + y, ix2 - ir + dx, iy1 + ir + y + 1, color)
            // Нижний левый
            context.fill(ix1 + ir - dx, iy2 - ir + y, ix1 + ir - dx + 1, iy2 - ir + y + 1, color)
            // Нижний правый
            context.fill(ix2 - ir + dx - 1, iy2 - ir + y, ix2 - ir + dx, iy2 - ir + y + 1, color)
        }
    }
}

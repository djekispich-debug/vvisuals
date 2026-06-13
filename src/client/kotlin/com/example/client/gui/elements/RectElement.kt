package com.example.client.gui.elements

import com.example.client.gui.GuiElement
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

class RectElement(
    x: Float = 0f,
    y: Float = 0f,
    width: Float = 100f,
    height: Float = 100f,
    var fillColor: Int = 0xCC000000,
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
            outlineColor?.let { drawOutline(context, x, y, width, height, it) }
        }
    }
    
    private fun drawOutline(context: DrawContext, x: Float, y: Float, w: Float, h: Float, color: Int) {
        context.drawBorder(x.toInt(), y.toInt(), w.toInt(), h.toInt(), color)
    }
    
    private fun drawRoundedRect(context: DrawContext, x1: Float, y1: Float, x2: Float, y2: Float, radius: Float, color: Int) {
        // Упрощённая версия скруглённого прямоугольника
        val ix1 = x1.toInt()
        val iy1 = y1.toInt()
        val ix2 = x2.toInt()
        val iy2 = y2.toInt()
        val ir = radius.toInt()
        
        // Центральная часть
        context.fill(ix1 + ir, iy1, ix2 - ir, iy2, color)
        context.fill(ix1, iy1 + ir, ix2, iy2 - ir, color)
        
        // Углы (можно улучшить)
        // Здесь можно добавить отрисовку закруглений через круги
    }
    
    private fun drawRoundedRectOutline(context: DrawContext, x1: Float, y1: Float, x2: Float, y2: Float, radius: Float, color: Int) {
        // Аналогично с обводкой
        drawRoundedRect(context, x1, y1, x2, y2, radius, color)
    }
}

package com.example.client.gui

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

/**
 * Базовый класс для всех GUI элементов
 * Аналог элементов в LiquidBounce
 */
abstract class GuiElement(
    var x: Float = 0f,
    var y: Float = 0f,
    var width: Float = 100f,
    var height: Float = 20f,
    var visible: Boolean = true,
    var zIndex: Int = 0  // Слой отрисовки (чем больше, тем выше)
) {
    
    // Границы элемента (для проверки пересечений)
    val bounds: Bounds
        get() = Bounds(x, y, x + width, y + height)
    
    abstract fun render(context: DrawContext, tickCounter: RenderTickCounter)
    
    // Проверка, находится ли мышь внутри элемента
    fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return visible && bounds.contains(mouseX, mouseY)
    }
    
    // Вызывается при клике
    open fun onClick(mouseX: Double, mouseY: Double, button: Int): Boolean = false
    
    // Вызывается при наведении
    open fun onHover(mouseX: Double, mouseY: Double) {}
    
    data class Bounds(
        val x1: Float, val y1: Float,
        val x2: Float, val y2: Float
    ) {
        fun contains(x: Double, y: Double): Boolean =
            x in x1..x2 && y in y1..y2
    }
}

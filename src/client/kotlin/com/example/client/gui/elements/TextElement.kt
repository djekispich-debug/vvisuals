package com.example.client.gui.elements

import com.example.client.gui.GuiElement
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

class TextElement(
    var text: String = "",
    x: Float = 0f,
    y: Float = 0f,
    var color: Int = 0xFFFFFF,
    var shadow: Boolean = true,
    var scale: Float = 1.0f,
    xOffset: Float = 0f,
    yOffset: Float = 0f,
    zIndex: Int = 0
) : GuiElement(x, y, 0f, 0f, zIndex = zIndex) {
    
    private val client = MinecraftClient.getInstance()
    private val textRenderer = client.textRenderer
    
    override fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        if (!visible) return
        
        val textToDraw = text
        val width = textRenderer.getWidth(textToDraw)
        val height = textRenderer.fontHeight
        
        // Сохраняем матрицу для масштабирования
        val matrices = context.matrices
        matrices.push()
        
        matrices.translate(x, y, 0f)
        matrices.scale(scale, scale, 1f)
        
        // Рисуем текст
        context.drawText(
            textRenderer,
            textToDraw,
            0,
            0,
            color,
            shadow
        )
        
        matrices.pop()
        
        // Обновляем размеры элемента для корректной обработки кликов
        this.width = width * scale
        this.height = height * scale
    }
}

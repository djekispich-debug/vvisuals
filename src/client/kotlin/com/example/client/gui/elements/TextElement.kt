package com.example.client.gui.elements

import com.example.client.gui.GuiElement
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import org.joml.Matrix4f

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
        val textWidth = textRenderer.getWidth(textToDraw)
        val textHeight = textRenderer.fontHeight
        
        if (scale != 1.0f) {
            // Если есть масштабирование, используем матрицы
            val matrices = context.matrices
            val positionMatrix = matrices.peek().positionMatrix
            positionMatrix.pushMatrix()
            positionMatrix.translate(x, y, 0f)
            positionMatrix.scale(scale, scale, 1f)
            
            context.drawText(
                textRenderer,
                textToDraw,
                0,
                0,
                color,
                shadow
            )
            
            positionMatrix.popMatrix()
        } else {
            // Без масштабирования - проще и быстрее
            context.drawText(
                textRenderer,
                textToDraw,
                x.toInt(),
                y.toInt(),
                color,
                shadow
            )
        }
        
        // Обновляем размеры элемента для корректной обработки кликов
        this.width = textWidth * scale
        this.height = textHeight * scale
    }
}

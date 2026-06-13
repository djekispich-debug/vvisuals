}package com.example.client.gui.elements

import com.example.client.gui.GuiElement
import com.example.client.gui.GuiRenderer
import net.minecraft.client.MinecraftClient

/**
 * Управляет HUD элементами (FPS, координаты и т.д.)
 * Аналог того, что мы видели в LiquidBounce
 */
object HUDManager {
    
    private val elements = mutableListOf<GuiElement>()
    private var dirty = false
    
    fun register(element: GuiElement) {
        elements.add(element)
        GuiRenderer.register(element)
        dirty = true
    }
    
    fun unregister(element: GuiElement) {
        elements.remove(element)
        GuiRenderer.unregister(element)
        dirty = true
    }
    
    fun rearrange() {
        if (!dirty) return
        
        // Сортируем по вертикали и горизонтали (как в LiquidBounce)
        elements.sortWith(compareBy(
            { it.bounds.y1 },
            { it.bounds.x1 }
        ))
        
        // Простое "расталкивание" чтобы не пересекались
        for (i in elements.indices) {
            for (j in i + 1 until elements.size) {
                val a = elements[i]
                val b = elements[j]
                
                if (a.bounds.x2 > b.bounds.x1 && a.bounds.y2 > b.bounds.y1) {
                    // Пересекаются - сдвигаем второй элемент вниз
                    b.y = b.y + (a.bounds.y2 - b.bounds.y1) + 2
                    dirty = true
                }
            }
        }
        
        dirty = false
    }
    
    fun createFpsCounter(): TextElement {
        val fpsText = TextElement("FPS: 0", 5f, 5f, color = 0x00FF00, zIndex = 100)
        
        // Обновляем FPS каждый кадр
        object : Thread() {
            init {
                isDaemon = true
                start()
            }
            override fun run() {
                while (true) {
                    val fps = MinecraftClient.getInstance().currentFps
                    fpsText.text = "FPS: $fps"
                    sleep(500) // Обновляем каждые 500ms
                }
            }
        }
        
        return fpsText
    }
    
    fun createCoordinates(): TextElement {
        val coordsText = TextElement("X: 0 Y: 0 Z: 0", 5f, 25f, color = 0xFFFFFF, zIndex = 100)
        
        thread {
            while (true) {
                val player = MinecraftClient.getInstance().player
                if (player != null) {
                    coordsText.text = "X: ${player.x.toInt()} Y: ${player.y.toInt()} Z: ${player.z.toInt()}"
                }
                Thread.sleep(100)
            }
        }
        
        return coordsText
    }
}

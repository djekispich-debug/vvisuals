package com.example.client.gui

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import org.slf4j.LoggerFactory

/**
 * Главный рендерер GUI - обрабатывает все отрисовки
 * Упрощённая версия того, что мы видели в LiquidBounce
 */
object GuiRenderer {
    
    private val logger = LoggerFactory.getLogger("GuiRenderer")
    private val elements = mutableListOf<GuiElement>()
    private var initialized = false
    
    fun initialize() {
        if (initialized) return
        initialized = true
        logger.info("GUI Renderer инициализирован")
    }
    
    fun register(element: GuiElement) {
        elements.add(element)
        logger.debug("Зарегистрирован GUI элемент: ${element::class.simpleName}")
    }
    
    fun unregister(element: GuiElement) {
        elements.remove(element)
    }
    
    fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        if (!initialized) return
        
        // Сортируем по слою (z-index)
        val sorted = elements.sortedBy { it.zIndex }
        
        // Отрисовываем каждый элемент
        sorted.forEach { element ->
            if (element.visible) {
                try {
                    element.render(context, tickCounter)
                } catch (e: Exception) {
                    logger.error("Ошибка при отрисовке элемента: ${element::class.simpleName}", e)
                }
            }
        }
    }
    
    fun clear() {
        elements.clear()
    }
}

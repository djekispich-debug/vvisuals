package com.example.client

import com.example.client.gui.GuiRenderer
import com.example.client.gui.elements.HUDManager
import com.example.client.gui.elements.RectElement
import com.example.client.gui.elements.TextElement
import com.example.client.gui.elements.CircleElement
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import org.slf4j.LoggerFactory

class VVisualsClient : ClientModInitializer {
    
    private val logger = LoggerFactory.getLogger("VVisuals")
    
    override fun onInitializeClient() {
        logger.info("VVisuals Client инициализирован!")
        
        // Инициализируем систему GUI
        GuiRenderer.initialize()
        
        // Создаём и регистрируем HUD элементы
        HUDManager.register(HUDManager.createFpsCounter())
        HUDManager.register(HUDManager.createCoordinates())
        
        // Добавляем демо-панель
        val demoPanel = RectElement(
            x = 5f, y = 45f,
            width = 120f, height = 80f,
            fillColor = 0xCC000000.toInt(),
            outlineColor = 0xFF00FF00.toInt(),
            roundedRadius = 8f,
            zIndex = 50
        )
        HUDManager.register(demoPanel)
        
        // Добавляем круг
        val circle = CircleElement(
            centerX = 150f,
            centerY = 80f,
            radius = 25f,
            fillColor = 0x88FF0000.toInt(),
            outlineColor = 0xFFFF0000.toInt(),
            outlineWidth = 2f,
            zIndex = 55
        )
        HUDManager.register(circle)
        
        // Регистрируем основной рендерер
        HudRenderCallback.EVENT.register { context, tickCounter ->
            // Перестраиваем HUD если нужно
            HUDManager.rearrange()
            
            // Отрисовываем все элементы
            GuiRenderer.render(context, tickCounter)
        }
        
        logger.info("GUI система готова!")
    }
}

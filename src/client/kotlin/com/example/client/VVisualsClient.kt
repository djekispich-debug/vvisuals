package com.example.client

import com.example.client.gui.GuiRenderer
import com.example.client.gui.elements.HUDManager
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
            fillColor = 0xCC000000,
            outlineColor = 0xFF00FF00,
            roundedRadius = 8f,
            zIndex = 50
        )
        HUDManager.register(demoPanel)
        
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

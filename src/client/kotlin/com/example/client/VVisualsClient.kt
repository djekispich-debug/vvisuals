package com.example.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import org.slf4j.LoggerFactory

class VVisualsClient : ClientModInitializer {
    
    private val logger = LoggerFactory.getLogger("VVisuals")
    
    override fun onInitializeClient() {
        logger.info("VVisuals Client инициализирован! Мод работает!")
        
        // Используем LAST слот, чтобы рисовать ПОСЛЕ всего
        HudRenderCallback.EVENT.register { context, tickDelta ->
            renderFpsHud(context)
        }
    }
    
    private fun renderFpsHud(context: DrawContext) {
        val client = MinecraftClient.getInstance()
        val textRenderer = client.textRenderer ?: return
        
        val fps = client.currentFps
        
        // Рисуем с черной подложкой (лучше видно на любом фоне)
        val fpsText = "$fps FPS"
        
        // Черная подложка для читаемости
        context.fill(2, 2, 80, 18, 0x80000000)
        
        // Белый текст
        context.drawText(
            textRenderer,
            fpsText,
            5,
            5,
            0xFFFFFF,
            false
        )
    }
}

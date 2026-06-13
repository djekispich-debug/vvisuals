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
        
        HudRenderCallback.EVENT.register { context, tickDelta ->
            renderFpsHud(context)
        }
    }
    
    private fun renderFpsHud(context: DrawContext) {
        val client = MinecraftClient.getInstance()
        val textRenderer = client.textRenderer ?: return
        
        val fps = client.currentFps
        val fpsText = "§aFPS: §f$fps"
        
        context.drawText(
            textRenderer,
            fpsText,
            5,
            5,
            0xFFFFFF,
            true
        )
    }
}

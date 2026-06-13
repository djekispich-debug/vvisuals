package com.example.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext

class VVisualsClient : ClientModInitializer {
    
    override fun onInitializeClient() {
        // Регистрируем кастомный HUD
        HudRenderCallback.EVENT.register { drawContext, tickDelta ->
            renderFpsHud(drawContext)
        }
    }
    
    private fun renderFpsHud(context: DrawContext) {
        val client = MinecraftClient.getInstance()
        val textRenderer = client.textRenderer
        
        val fps = client.currentFps
        val fpsText = "FPS: $fps"
        
        // Рисуем текст в левом верхнем углу
        context.drawText(
            textRenderer,
            fpsText,
            5,  // x
            5,  // y
            0xFFFFFF, // белый цвет
            true // shadow
        )
    }
}

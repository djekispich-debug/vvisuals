package com.example.client.gui.elements

import com.example.client.gui.GuiElement
import com.example.client.gui.GuiRenderer
import net.minecraft.client.MinecraftClient
import kotlin.concurrent.thread

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
        
        // Сортируем по вертикали
        elements.sortWith(compareBy({ it.bounds.y1 }, { it.bounds.x1 }))
        
        // Простое "расталкивание" чтобы не пересекались
        for (i in elements.indices) {
            for (j in i + 1 until elements.size) {
                val a = elements[i]
                val b = elements[j]
                
                if (a.bounds.x2 > b.bounds.x1 && a.bounds.y2 > b.bounds.y1) {
                    b.y = b.y + (a.bounds.y2 - b.bounds.y1) + 2
                    dirty = true
                }
            }
        }
        
        dirty = false
    }
    
    fun createFpsCounter(): TextElement {
        val fpsText = TextElement("FPS: 0", 5f, 5f, color = 0x00FF00, zIndex = 100)
        
        thread {
            while (true) {
                try {
                    val fps = MinecraftClient.getInstance().currentFps
                    fpsText.text = "FPS: $fps"
                    Thread.sleep(500)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        return fpsText
    }
    
    fun createCoordinates(): TextElement {
        val coordsText = TextElement("X: 0 Y: 0 Z: 0", 5f, 25f, color = 0xFFFFFF, zIndex = 100)
        
        thread {
            while (true) {
                try {
                    val player = MinecraftClient.getInstance().player
                    if (player != null) {
                        coordsText.text = "X: ${player.x.toInt()} Y: ${player.y.toInt()} Z: ${player.z.toInt()}"
                    }
                    Thread.sleep(100)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        return coordsText
    }
    
    fun createPingDisplay(): TextElement {
        val pingText = TextElement("Ping: 0ms", 5f, 45f, color = 0xFFFF00, zIndex = 100)
        
        thread {
            while (true) {
                try {
                    val player = MinecraftClient.getInstance().player
                    val networkHandler = MinecraftClient.getInstance().networkHandler
                    if (player != null && networkHandler != null) {
                        val entry = networkHandler.getPlayerListEntry(player.gameProfile.id)
                        if (entry != null) {
                            pingText.text = "Ping: ${entry.latency}ms"
                        }
                    }
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        return pingText
    }
    
    fun createMemoryUsage(): TextElement {
        val memoryText = TextElement("Mem: 0%", 5f, 65f, color = 0xFF00FF, zIndex = 100)
        
        thread {
            while (true) {
                try {
                    val runtime = Runtime.getRuntime()
                    val usedMemory = runtime.totalMemory() - runtime.freeMemory()
                    val maxMemory = runtime.maxMemory()
                    val usagePercent = (usedMemory * 100 / maxMemory).toInt()
                    memoryText.text = "Mem: $usagePercent%"
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        return memoryText
    }
    
    fun createGameTime(): TextElement {
        val timeText = TextElement("Time: 00:00", 5f, 85f, color = 0x00FFFF, zIndex = 100)
        
        thread {
            while (true) {
                try {
                    val world = MinecraftClient.getInstance().world
                    if (world != null) {
                        val time = world.timeOfDay % 24000
                        val hours = ((time + 6000) % 24000) / 1000
                        val minutes = ((time % 1000) * 60 / 1000).toInt()
                        timeText.text = "Time: ${hours.toInt().toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
                    }
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        return timeText
    }
    
    fun createDirectionDisplay(): TextElement {
        val directionText = TextElement("Dir: N", 5f, 105f, color = 0xFFAA00, zIndex = 100)
        
        thread {
            while (true) {
                try {
                    val player = MinecraftClient.getInstance().player
                    if (player != null) {
                        val yaw = (player.yaw % 360 + 360) % 360
                        val direction = when {
                            yaw in 337.5..360.0 || yaw in 0.0..22.5 -> "N"
                            yaw in 22.5..67.5 -> "NE"
                            yaw in 67.5..112.5 -> "E"
                            yaw in 112.5..157.5 -> "SE"
                            yaw in 157.5..202.5 -> "S"
                            yaw in 202.5..247.5 -> "SW"
                            yaw in 247.5..292.5 -> "W"
                            yaw in 292.5..337.5 -> "NW"
                            else -> "N"
                        }
                        directionText.text = "Dir: $direction (${yaw.toInt()}°)"
                    }
                    Thread.sleep(50)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        return directionText
    }
    
    fun createBiomeDisplay(): TextElement {
        val biomeText = TextElement("Biome: ...", 5f, 125f, color = 0x88FF88, zIndex = 100)
        
        thread {
            while (true) {
                try {
                    val player = MinecraftClient.getInstance().player
                    val world = MinecraftClient.getInstance().world
                    if (player != null && world != null) {
                        val blockPos = player.blockPos
                        val biome = world.getBiome(blockPos)
                        val biomeKey = biome.key.value.path
                        val biomeName = biomeKey.split("_").joinToString(" ") { 
                            it.replaceFirstChar { c -> c.uppercaseChar() }
                        }
                        biomeText.text = "Biome: $biomeName"
                    }
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        return biomeText
    }
    
    fun clearAll() {
        elements.forEach { GuiRenderer.unregister(it) }
        elements.clear()
        dirty = false
    }
    
    fun getAllElements(): List<GuiElement> = elements.toList()
}

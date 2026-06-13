package com.example.client.gui.elements

import com.example.client.gui.GuiElement
import com.example.client.gui.GuiRenderer
import net.minecraft.client.MinecraftClient
import net.minecraft.registry.RegistryKeys
import kotlin.concurrent.thread

/**
 * Управляет HUD элементами (FPS, координаты и т.д.)
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
        
        elements.sortWith(compareBy({ it.y }, { it.x }))
        
        for (i in elements.indices) {
            for (j in i + 1 until elements.size) {
                val a = elements[i]
                val b = elements[j]
                
                if (a.bounds.x2 > b.bounds.x1 && a.bounds.y2 > b.bounds.y1) {
                    b.y = a.bounds.y2 + 2
                    dirty = true
                }
            }
        }
        
        dirty = false
    }
    
    fun createFpsCounter(): TextElement {
        val fpsText = TextElement(
            text = "FPS: 0",
            x = 5f,
            y = 5f,
            color = 0x00FF00,
            zIndex = 100
        )
        
        thread(name = "FPS-Updater", isDaemon = true) {
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
        val coordsText = TextElement(
            text = "X: 0 Y: 0 Z: 0",
            x = 5f,
            y = 25f,
            color = 0xFFFFFF,
            zIndex = 100
        )
        
        thread(name = "Coords-Updater", isDaemon = true) {
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
        val pingText = TextElement(
            text = "Ping: 0ms",
            x = 5f,
            y = 45f,
            color = 0xFFFF00,
            zIndex = 100
        )
        
        thread(name = "Ping-Updater", isDaemon = true) {
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
        val memoryText = TextElement(
            text = "Mem: 0%",
            x = 5f,
            y = 65f,
            color = 0xFF00FF,
            zIndex = 100
        )
        
        thread(name = "Memory-Updater", isDaemon = true) {
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
        val timeText = TextElement(
            text = "Time: 00:00",
            x = 5f,
            y = 85f,
            color = 0x00FFFF,
            zIndex = 100
        )
        
        thread(name = "Time-Updater", isDaemon = true) {
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
        val directionText = TextElement(
            text = "Dir: N",
            x = 5f,
            y = 105f,
            color = 0xFFAA00,
            zIndex = 100
        )
        
        thread(name = "Direction-Updater", isDaemon = true) {
            while (true) {
                try {
                    val player = MinecraftClient.getInstance().player
                    if (player != null) {
                        val yaw = (player.yaw % 360 + 360) % 360
                        val direction = when {
                            yaw >= 337.5 || yaw < 22.5 -> "N"
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
        val biomeText = TextElement(
            text = "Biome: ...",
            x = 5f,
            y = 125f,
            color = 0x88FF88,
            zIndex = 100
        )
        
        thread(name = "Biome-Updater", isDaemon = true) {
            while (true) {
                try {
                    val player = MinecraftClient.getInstance().player
                    val world = MinecraftClient.getInstance().world
                    if (player != null && world != null) {
                        val biome = world.getBiome(player.blockPos)
                        // Исправленный доступ к ключу биома без Optional
                        val biomeId = biome.key.value.toString()
                        val biomeName = if (biomeId.contains(":")) {
                            biomeId.split(":")[1]
                                .split("_")
                                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
                        } else {
                            biomeId.split("_")
                                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
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
    
    fun createBackground(
        x: Float = 0f,
        y: Float = 0f,
        width: Float = 100f,
        height: Float = 100f,
        fillColor: Int = 0x80000000.toInt(),
        outlineColor: Int? = null,
        roundedRadius: Float = 0f,
        zIndex: Int = 99
    ): RectElement {
        return RectElement(
            x = x,
            y = y,
            width = width,
            height = height,
            fillColor = fillColor,
            outlineColor = outlineColor,
            roundedRadius = roundedRadius,
            zIndex = zIndex
        )
    }
    
    fun createCircleIndicator(
        centerX: Float = 50f,
        centerY: Float = 50f,
        radius: Float = 20f,
        fillColor: Int = 0xFFFF0000.toInt(),
        outlineColor: Int? = 0xFFFFFFFF.toInt(),
        startAngle: Float = 0f,
        sweepAngle: Float = 360f,
        zIndex: Int = 100
    ): CircleElement {
        return CircleElement(
            centerX = centerX,
            centerY = centerY,
            radius = radius,
            fillColor = fillColor,
            outlineColor = outlineColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            zIndex = zIndex
        )
    }
    
    fun createMiniMap(
        x: Float = 5f,
        y: Float = 150f,
        size: Float = 100f
    ): RectElement {
        return RectElement(
            x = x,
            y = y,
            width = size,
            height = size,
            fillColor = 0x80000000.toInt(),
            outlineColor = 0xFFFFFFFF.toInt(),
            roundedRadius = 4f,
            zIndex = 100
        )
    }
    
    fun clearAll() {
        elements.forEach { GuiRenderer.unregister(it) }
        elements.clear()
        dirty = false
    }
    
    fun getAllElements(): List<GuiElement> = elements.toList()
    
    fun getElementAt(mouseX: Double, mouseY: Double): GuiElement? {
        return elements.findLast { it.isMouseOver(mouseX, mouseY) }
    }
    
    fun markDirty() {
        dirty = true
    }
}

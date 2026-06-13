package com.example.client.gui.elements

import com.example.client.gui.GuiElement
import com.example.client.gui.GuiRenderer
import net.minecraft.client.MinecraftClient
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
        
        // Сортируем по вертикали (по Y, затем по X)
        elements.sortWith(compareBy({ it.y }, { it.x }))
        
        // Простое "расталкивание" чтобы элементы не пересекались
        for (i in elements.indices) {
            for (j in i + 1 until elements.size) {
                val a = elements[i]
                val b = elements[j]
                
                // Проверяем пересечение
                if (a.x + a.width > b.x && a.y + a.height > b.y) {
                    // Сдвигаем элемент b вниз
                    b.y = a.y + a.height + 2
                    dirty = true
                }
            }
        }
        
        dirty = false
    }
    
    /**
     * Создаёт счётчик FPS
     */
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
    
    /**
     * Создаёт отображение координат игрока
     */
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
    
    /**
     * Создаёт отображение пинга
     */
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
    
    /**
     * Создаёт отображение использования памяти
     */
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
    
    /**
     * Создаёт отображение игрового времени
     */
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
    
    /**
     * Создаёт отображение направления взгляда
     */
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
                            yaw >= 22.5 && yaw < 67.5 -> "NE"
                            yaw >= 67.5 && yaw < 112.5 -> "E"
                            yaw >= 112.5 && yaw < 157.5 -> "SE"
                            yaw >= 157.5 && yaw < 202.5 -> "S"
                            yaw >= 202.5 && yaw < 247.5 -> "SW"
                            yaw >= 247.5 && yaw < 292.5 -> "W"
                            yaw >= 292.5 && yaw < 337.5 -> "NW"
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
    
    /**
     * Создаёт отображение биома
     */
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
                        val biomeName = biome.key.value.path
                            .split("_")
                            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
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
    
    /**
     * Создаёт фоновый прямоугольник для группы элементов
     * Полезно для визуального объединения HUD-элементов
     */
    fun createBackground(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Int = 0x80000000.toInt(),
        zIndex: Int = 99
    ): RectElement {
        return RectElement(
            x = x,
            y = y,
            width = width,
            height = height,
            color = color,
            zIndex = zIndex
        )
    }
    
    /**
     * Создаёт индикатор в виде круга (например, для здоровья)
     */
    fun createCircleIndicator(
        x: Float,
        y: Float,
        radius: Float,
        color: Int = 0xFF0000,
        zIndex: Int = 100
    ): CircleElement {
        return CircleElement(
            x = x,
            y = y,
            radius = radius,
            color = color,
            zIndex = zIndex
        )
    }
    
    /**
     * Удаляет все элементы
     */
    fun clearAll() {
        elements.forEach { GuiRenderer.unregister(it) }
        elements.clear()
        dirty = false
    }
    
    /**
     * Возвращает копию списка всех элементов
     */
    fun getAllElements(): List<GuiElement> = elements.toList()
    
    /**
     * Возвращает элемент по координатам (для кликов)
     */
    fun getElementAt(x: Float, y: Float): GuiElement? {
        return elements.findLast { element ->
            element.visible && 
            x >= element.x && 
            x <= element.x + element.width &&
            y >= element.y && 
            y <= element.y + element.height
        }
    }
    
    /**
     * Принудительно помечает компоновку как требующую обновления
     */
    fun markDirty() {
        dirty = true
    }
}

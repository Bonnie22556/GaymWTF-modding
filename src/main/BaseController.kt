package com.example.main.controller

import java.io.File
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import javafx.stage.Window
import main.GetterFromMenu

abstract class BaseController {
    @FXML protected lateinit var root: AnchorPane
    @FXML protected var path_text: TextField? = null
    @FXML protected var texture_preview: ImageView? = null
    protected var fullTexturePath: String? = null
    protected val getter = GetterFromMenu()

    @FXML
    open fun initialize() {
        setTextureByPath(path_text?.text)
    }

    protected fun setTextureByPath(path: String?) {
        if (path.isNullOrBlank()) {
            setEmptyTexture()
            return
        }

        // Используем полный путь для загрузки текстуры
        val resourcePath = fullTexturePath ?: path
        try {
            val file = File(resourcePath)
            if (file.exists()) {
                val img = Image(file.toURI().toString())
                val scale = 4
                val scaled = scaleImageNearest(img, scale)
                texture_preview?.isSmooth = false
                texture_preview?.fitWidth = img.width * scale
                texture_preview?.fitHeight = img.height * scale
                texture_preview?.image = scaled
            } else {
                setEmptyTexture()
            }
        } catch (e: Exception) {
            println("Ошибка загрузки текстуры: ${e.message}")
            setEmptyTexture()
        }
    }

    protected fun setEmptyTexture() {
        if (texture_preview != null) {
            val emptyStream = javaClass.getResourceAsStream("/empty.png")
            if (emptyStream != null) {
                val img = Image(emptyStream)
                val scale = 4
                val scaled = scaleImageNearest(img, scale)
                texture_preview?.isSmooth = false
                texture_preview?.fitWidth = img.width * scale
                texture_preview?.fitHeight = img.height * scale
                texture_preview?.image = scaled
            } else {
                texture_preview?.image = null
            }
        }
    }

    protected fun scaleImageNearest(img: Image, scale: Int): Image {
        val w = img.width.toInt()
        val h = img.height.toInt()
        val sw = w * scale
        val sh = h * scale
        val reader: PixelReader = img.pixelReader
        val out = WritableImage(sw, sh)
        val writer: PixelWriter = out.pixelWriter
        for (y in 0 until sh) {
            for (x in 0 until sw) {
                val argb = reader.getArgb(x / scale, y / scale)
                writer.setArgb(x, y, argb)
            }
        }
        return out
    }


    @FXML
    fun onPathClick() {
        val chooser = FileChooser()
        chooser.title = "Выберите PNG текстуру"
        chooser.extensionFilters.add(FileChooser.ExtensionFilter("PNG files", "*.png"))
        val window: Window? = path_text?.scene?.window
        val file = chooser.showOpenDialog(window)

        if (file != null) {
            // Сохраняем полный путь
            fullTexturePath = file.absolutePath

            val fullPath = file.absolutePath.replace('\\', '/')
            val srcPath = getter.getGameSRC().replace('\\', '/')

            val relativePath = if (srcPath.isNotEmpty()) {
                val parentDir = srcPath.substringBeforeLast("/src")
                if (fullPath.startsWith(parentDir)) {
                    fullPath.substring(parentDir.length)
                } else {
                    when {
                        fullPath.contains("/assets/") -> fullPath.substring(fullPath.indexOf("/assets/"))
                        fullPath.contains("/modded/") -> fullPath.substring(fullPath.indexOf("/modded/"))
                        else -> "/${file.name}"
                    }
                }
            } else {
                when {
                    fullPath.contains("/assets/") -> fullPath.substring(fullPath.indexOf("/assets/"))
                    fullPath.contains("/modded/") -> fullPath.substring(fullPath.indexOf("/modded/"))
                    else -> "/${file.name}"
                }
            }

            // Сохраняем относительный путь с прямыми слешами
            path_text?.text = relativePath
            setTextureByPath(fullTexturePath)
        } else {
            fullTexturePath = null
            path_text?.text = ""
            setEmptyTexture()
        }
    }



    protected fun loadContent(fxml: String) {
        val loader = FXMLLoader(javaClass.getResource(fxml))
        val view: Parent = loader.load()
        val controller = loader.getController<Any>()

        // Устанавливаем контроллер в GetterFromMenu
        val getter = GetterFromMenu()
        when (controller) {
            is TileController -> getter.setTileController(controller)
            is BiomeController -> getter.setBiomeController(controller)
            is ObjectController -> getter.setObjectController(controller)
        }

        root.children.clear()
        root.children.add(view)
        AnchorPane.setTopAnchor(view, 0.0)
        AnchorPane.setBottomAnchor(view, 0.0)
        AnchorPane.setLeftAnchor(view, 0.0)
        AnchorPane.setRightAnchor(view, 0.0)
    }


    @FXML fun loadTileMenu() = loadContent("/FXMLmenu/tile.fxml")
    @FXML fun loadBiomeMenu() = loadContent("/FXMLmenu/biome.fxml")
    @FXML fun loadObjMenu() = loadContent("/FXMLmenu/object.fxml")
}



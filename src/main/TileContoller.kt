package com.example.main.controller

import javafx.fxml.FXML
import javafx.scene.control.TextField
import main.Create
import main.reload

class TileController : BaseController() {
    @FXML private lateinit var tileName: TextField
    @FXML private lateinit var gameSRC: TextField
    @FXML private lateinit var windowType: TextField

    @FXML
    override fun initialize() {
        super.initialize()
    }

    @FXML
    fun inject() {
        val gameSrcText = gameSRC.text
        if (gameSrcText.isNotEmpty()) {
            val create = Create(gameSrcText)
            create.Creator("tiles", tileName.text)
                .tileAdd(tileName.text, path_text?.text ?: "")
            create.Updater().tiles(tileName.text)
        } else {
            println("SRC is empty")
        }
    }

    @FXML
    fun reloadInMenu() {
        reload(gameSRC.text)
    }

    // Геттеры для доступа к полям
    fun getTileName(): String = tileName.text
    fun getGameSRC(): String = gameSRC.text
    fun getWindowType(): String = windowType.text
    fun getTexturePath(): String = path_text?.text ?: ""
}

package com.example.main.controller

import javafx.fxml.FXML
import javafx.scene.control.TextField
import main.Create
import main.reload

class ObjectController : BaseController() {
    @FXML private lateinit var objName: TextField
    @FXML private lateinit var gameSRC: TextField
    @FXML private lateinit var windowType: TextField
    @FXML private lateinit var xSize: TextField
    @FXML private lateinit var ySize: TextField

    @FXML
    override fun initialize() {
        super.initialize()
    }

    @FXML
    fun inject() {
        val gameSrcText = gameSRC.text
        if (gameSrcText.isNotEmpty()) {
            val create = Create(gameSrcText)
            create.Creator("objects", objName.text)
                .objectAdd(
                    objName.text,
                    path_text?.text ?: "",
                    xSize.text.toDoubleOrNull() ?: 0.0,
                    ySize.text.toDoubleOrNull() ?: 0.0
                )
            create.Updater().objects(objName.text)
        } else {
            println("SRC is empty")
        }
    }

    @FXML
    fun reloadInMenu() {
        reload(gameSRC.text)
    }

    fun getObjectName(): String = objName.text
    fun getGameSRC(): String = gameSRC.text
    fun getWindowType(): String = windowType.text
    fun getTexturePath(): String = path_text?.text ?: ""
    fun getSizeX(): Double = xSize.text.toDoubleOrNull() ?: 0.0
    fun getSizeY(): Double = ySize.text.toDoubleOrNull() ?: 0.0
}
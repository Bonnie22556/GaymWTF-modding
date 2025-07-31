package com.example.main.controller

import javafx.fxml.FXML
import javafx.scene.layout.StackPane

class MenuController : BaseController() {
    @FXML
    private lateinit var contentArea: StackPane

    @FXML
    override fun initialize() {
        super.initialize()
    }

    @FXML
    fun reloadMODS() {
        // Реализация перезагрузки модов
    }
}
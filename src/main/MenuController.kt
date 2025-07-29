package com.example.main.controller

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane
import javafx.scene.Parent

class MainController {
    @FXML lateinit var root: AnchorPane

    private fun loadContent(fxml: String) {
        val loader = FXMLLoader(javaClass.getResource(fxml))
        val view: Parent = loader.load()
        root.children.clear() // удаляем всё содержимое AnchorPane
        root.children.add(view) // добавляем новый контент
        AnchorPane.setTopAnchor(view, 0.0)
        AnchorPane.setBottomAnchor(view, 0.0)
        AnchorPane.setLeftAnchor(view, 0.0)
        AnchorPane.setRightAnchor(view, 0.0)
    }

    @FXML fun loadTileMenu() = loadContent("/FXMLmenu/tile.fxml")
    @FXML fun loadBiomeMenu() = loadContent("/FXMLmenu/biome.fxml")
    @FXML fun loadObjMenu() = loadContent("/FXMLmenu/object.fxml")
}
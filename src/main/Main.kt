package main

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.text.Text
import javafx.scene.layout.VBox

class MainApp : Application() {
    override fun start(primaryStage: Stage) {
        val resource = javaClass.getResource("/FXMLmenu/menu.fxml")
        println("FXML resource: $resource") // Диагностика

        if (resource == null) {
            showErrorScreen(
                primaryStage,
                """
                FXML file not found: /FXMLmenu/menu.fxml

                Проверьте:
                1. Файл должен лежать по пути src/main/resources/FXMLmenu/menu.fxml
                2. В IntelliJ IDEA папка src/main/resources должна быть помечена как Resources Root:
                   ПКМ по папке → Mark Directory as → Resources Root
                3. После сборки файл должен быть внутри jar или build/classes/resources/FXMLmenu/menu.fxml
                """.trimIndent()
            )
            return
        }

        val loader = FXMLLoader(resource)
        val root: Parent = loader.load()
        val scene = Scene(root, 600.0, 400.0)

        primaryStage.title = "GaymWTF Mod generator by bon_26"
        primaryStage.scene = scene
        primaryStage.isResizable = false
        primaryStage.show()
    }

    fun showErrorScreen(stage: Stage, message: String) {
        stage.scene = Scene(VBox(Text(message)), 400.0, 200.0)
        stage.title = "Error"
        stage.show()
    }
}

fun main() {
    var create = Create("")
    println(MainApp::class.java.getResource("/FXMLmenu/menu.fxml")) // Диагностика
    Application.launch(MainApp::class.java)
}
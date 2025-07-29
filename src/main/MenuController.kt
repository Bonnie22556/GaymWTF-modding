package com.example.main.controller

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.AnchorPane
import javafx.scene.Parent
import javafx.stage.FileChooser
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.control.TextField
import javafx.scene.control.Button
import javafx.stage.Window
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter

class MainController {
    @FXML lateinit var root: AnchorPane
    @FXML var myTableView: TableView<MyRowData>? = null
    @FXML var nameColumn: TableColumn<MyRowData, String>? = null
    @FXML var chanceColumn: TableColumn<MyRowData, String>? = null
    val tableData: ObservableList<MyRowData> = FXCollections.observableArrayList()

    @FXML var path_text: TextField? = null
    @FXML var texture_preview: ImageView? = null
    @FXML var path: Button? = null

    @FXML
    fun initialize() {
        myTableView?.items = tableData
        nameColumn?.cellValueFactory = javafx.util.Callback { cellData -> cellData.value.column1Property }
        nameColumn?.cellFactory = TextFieldTableCell.forTableColumn()
        nameColumn?.setOnEditCommit {
            it.rowValue.column1 = it.newValue
        }
        chanceColumn?.cellValueFactory = javafx.util.Callback { cellData -> cellData.value.column2Property }
        chanceColumn?.cellFactory = TextFieldTableCell.forTableColumn()
        chanceColumn?.setOnEditCommit {
            it.rowValue.column2 = it.newValue
        }
        setTextureByPath(path_text?.text)
    }

    private fun setEmptyTexture() {
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

    private fun setTextureByPath(path: String?) {
        if (path.isNullOrBlank() || path == "/assets/") {
            setEmptyTexture()
            return
        }
        // Попробуем загрузить файл из ресурсов assets, иначе empty
        val resourceStream = javaClass.getResourceAsStream(path)
        if (resourceStream != null) {
            val img = Image(resourceStream)
            val scale = 4
            val scaled = scaleImageNearest(img, scale)
            texture_preview?.isSmooth = false
            texture_preview?.fitWidth = img.width * scale
            texture_preview?.fitHeight = img.height * scale
            texture_preview?.image = scaled
        } else {
            setEmptyTexture()
        }
    }

    @FXML
    fun onAddRow() {
        // Добавляет строку с тестовыми данными, можно заменить на ввод из полей
        tableData.add(MyRowData("новый объект", "0.5"))
    }

    fun addRow(row: MyRowData) {
        tableData.add(row)
    }

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

    @FXML
    fun onPathClick() {
        val chooser = FileChooser()
        chooser.title = "Выберите PNG текстуру"
        chooser.extensionFilters.add(FileChooser.ExtensionFilter("PNG files", "*.png"))
        val window: Window? = path_text?.scene?.window
        val file = chooser.showOpenDialog(window)
        if (file != null) {
            val assetPath = "/assets/${file.name}"
            path_text?.text = assetPath
            setTextureByPath(assetPath)
        } else {
            // Если файл не выбран — показать пустую текстуру
            path_text?.text = ""
            setEmptyTexture()
        }
    }

    // Масштабирование с фильтром NEAREST
    private fun scaleImageNearest(img: Image, scale: Int): Image {
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
}

// --- Класс данных для строки таблицы с поддержкой редактирования ---
data class MyRowData(
    private var _column1: String,
    private var _column2: String
) {
    val column1Property: StringProperty = SimpleStringProperty(_column1)
    var column1: String
        get() = column1Property.get()
        set(value) = column1Property.set(value)

    val column2Property: StringProperty = SimpleStringProperty(_column2)
    var column2: String
        get() = column2Property.get()
        set(value) = column2Property.set(value)
}

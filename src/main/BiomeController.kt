package com.example.main.controller

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.control.TableView
import javafx.scene.control.TableColumn
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Slider
import javafx.scene.control.cell.TextFieldTableCell
import main.Create
import main.reload

class BiomeController : BaseController() {
    @FXML private lateinit var biomeName: TextField
    @FXML private lateinit var gameSRC: TextField
    @FXML private lateinit var windowType: TextField
    @FXML private lateinit var tileID: TextField
    @FXML private lateinit var minTemp: Slider
    @FXML private lateinit var maxTemp: Slider
    @FXML private lateinit var minHeight: Slider
    @FXML private lateinit var maxHeight: Slider
    @FXML private lateinit var minMoisture: Slider
    @FXML private lateinit var maxMoisture: Slider

    @FXML var myTableView: TableView<MyRowData>? = null
    @FXML var nameColumn: TableColumn<MyRowData, String>? = null
    @FXML var chanceColumn: TableColumn<MyRowData, String>? = null
    val tableData: ObservableList<MyRowData> = FXCollections.observableArrayList()

    @FXML
    override fun initialize() {
        super.initialize()

        myTableView?.items = tableData
        nameColumn?.cellValueFactory = javafx.util.Callback { cellData -> cellData.value.column1Property }
        nameColumn?.cellFactory = TextFieldTableCell.forTableColumn()
        nameColumn?.setOnEditCommit { it.rowValue.column1 = it.newValue }

        chanceColumn?.cellValueFactory = javafx.util.Callback { cellData -> cellData.value.column2Property }
        chanceColumn?.cellFactory = TextFieldTableCell.forTableColumn()
        chanceColumn?.setOnEditCommit { it.rowValue.column2 = it.newValue }
    }

    @FXML
    fun onAddRow() {
        tableData.add(MyRowData("новый объект", "0.5"))
    }

    @FXML
    fun inject() {
        val gameSrcText = gameSRC.text
        if (gameSrcText.isNotEmpty()) {
            val create = Create(gameSrcText)
            val objects = tableData.map { it.column1 to it.column2.toFloatOrNull() ?: 0f }
            create.Creator("biomes", biomeName.text)
                .biomeAdd(
                    tileType = tileID.text,
                    spawnableObjects = objects as List<Pair<String, Float>>,
                    minHeight = minHeight.value,
                    maxHeight = maxHeight.value,
                    minMoisture = minMoisture.value,
                    maxMoisture = maxMoisture.value,
                    minTemperature = minTemp.value,
                    maxTemperature = maxTemp.value)
            create.Updater().biomes(biomeName.text)
        } else {
            println("SRC is empty")
        }
    }

    @FXML
    fun reloadInMenu() {
        reload(gameSRC.text)
    }

    fun getMinTemp(): Double = minTemp.value
    fun getMaxTemp(): Double = maxTemp.value
    fun getMinHeight(): Double = minHeight.value
    fun getMaxHeight(): Double = maxHeight.value
    fun getMinMoisture(): Double = minMoisture.value
    fun getMaxMoisture(): Double = maxMoisture.value
    fun getBiomeName(): String = biomeName.text
    fun getGameSRC(): String = gameSRC.text
    fun getWindowType(): String = windowType.text
    fun getTileId(): String = tileID.text
    fun getTableRows(): List<MyRowData> = tableData.toList()
}


// --- Класс данных для строки таблицы с поддержкой редактирования ---
data class MyRowData(
    private var _column1: String,
    private var _column2: String
)
{
    val column1Property: StringProperty = SimpleStringProperty(_column1)
    var column1: String
        get() = column1Property.get()
        set(value) = column1Property.set(value)

    val column2Property: StringProperty = SimpleStringProperty(_column2)
    var column2: String
        get() = column2Property.get()
        set(value) = column2Property.set(value)
}
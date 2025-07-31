package main

import com.example.main.controller.TileController
import com.example.main.controller.BiomeController
import com.example.main.controller.ObjectController
import com.example.main.controller.MyRowData

class GetterFromMenu {
    private var tileController: TileController? = null
    private var biomeController: BiomeController? = null
    private var objectController: ObjectController? = null

    // Сеттеры для установки контроллеров
    fun setTileController(controller: TileController) {
        tileController = controller
    }

    fun setBiomeController(controller: BiomeController) {
        biomeController = controller
    }

    fun setObjectController(controller: ObjectController) {
        objectController = controller
    }

    // Методы для получения данных тайлов
    fun getTileData(): TileData? {
        return tileController?.let {
            TileData(
                tileName = it.getTileName(),
                texturePath = it.getTexturePath()
            )
        }
    }

    // Методы для получения данных биомов
    fun getBiomeData(): BiomeData? {
        return biomeController?.let {
            BiomeData(
                biomeName = it.getBiomeName(),
                tileId = it.getTileId(),
                objects = it.getTableRows()
                    .map { row -> row.column1 to row.column2.toFloatOrNull() ?: 0f }
                    .toList() as List<Pair<String, Float>>
            )
        }
    }

    // Методы для получения данных объектов
    fun getObjectData(): ObjectData? {
        return objectController?.let {
            ObjectData(
                objectName = it.getObjectName(),
                texturePath = it.getTexturePath(),
                sizeX = it.getSizeX(),
                sizeY = it.getSizeY()
            )
        }
    }

    // Общие методы для всех контроллеров
    fun getGameSRC(): String {
        return tileController?.getGameSRC()
            ?: biomeController?.getGameSRC()
            ?: objectController?.getGameSRC()
            ?: ""
    }

    fun getWindowName(): String {
        return tileController?.getWindowType()
            ?: biomeController?.getWindowType()
            ?: objectController?.getWindowType()
            ?: ""
    }

    // Структуры данных
    data class TileData(
        val tileName: String,
        val texturePath: String
    )

    data class BiomeData(
        val biomeName: String,
        val tileId: String,
        val objects: List<Pair<String, Float>>
    )

    data class ObjectData(
        val objectName: String,
        val texturePath: String,
        val sizeX: Double,
        val sizeY: Double
    )
}

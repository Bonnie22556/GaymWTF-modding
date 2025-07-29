package main

import java.io.File

class Create(val srcPath: String) {

    inner class Updater(mainRSPath: String = "${this@Create.srcPath}\\main.main.rs") {
        private val file = File(mainRSPath)

        private fun updMAIN(newCont: String, endMarker: String) {
            var freshText = file.readText()
            val endIndex = freshText.indexOf(endMarker).takeIf { it >= 0 } ?: throw RuntimeException("End marker not found: $endMarker")

            val formattedContent = "    $newCont\n"
            if (freshText.contains(newCont)) {throw RuntimeException("$newCont already exist")}
            val newText = freshText.substring(0, endIndex) +
                    formattedContent +
                    freshText.substring(endIndex)

            file.writeText(newText)
        }

        fun biomes (
            biomeName: String,
            endMarker: String = "    // {{END_GEN_BIOMES}}",
            newCont: String = "registry.register(crate::biomes::${biomeName.lowercase()}::${biomeName}Biome);"
        ) = updMAIN(newCont, endMarker)

        fun objects (
            objectName: String,
            endMarker: String = "    // {{END_GEN_OBJECTS}}",
            newCont: String = "registry.register(crate::objects::${objectName.lowercase()}::${objectName}::new(Vec2::ZERO));"
        ) = updMAIN(newCont, endMarker)

        fun tiles(
            tileName: String,
            endMarker: String = "    // {{END_GEN_TILES}}",
            newCont: String = "registry.register(crate::tiles::${tileName.lowercase()}::${tileName}Tile::new(Vec2::ZERO));"
        ) = updMAIN(newCont, endMarker)
    }

    inner class Creator(contentType: String, val contentName: String, filePath: String = "${this@Create.srcPath}\\${contentType}\\${contentName.lowercase()}.rs") {
        private val file = File(filePath)
        private var mod = File("${this@Create.srcPath}\\${contentType}\\mod.rs")

        fun biomeAdd(biomeName: String = contentName,
                     minHeight: Double = 0.0,
                     maxHeight: Double = 1.0,
                     minMoisture: Double = 0.0,
                     maxMoisture: Double = 1.0,
                     minTemperature: Double = 0.0,
                     maxTemperature: Double = 1.0,
                     tileType: String = "grass",
                     spawnableObjects: List<Pair<String, Float>> = emptyList()) {
            if (!file.exists()) {
                file.createNewFile()
            }

            val objectsString = if (spawnableObjects.isEmpty()) {
                "vec![]"
            } else {
                "vec![\n" +
                        spawnableObjects.joinToString(",\n") { (objName, chance) ->
                            "        (\"$objName\", ${chance})"
                        } + "\n    ]"
            }

            val biomeFile: String = """
use gaymwtf_core::Biome;

#[derive(Clone)]
pub struct ${biomeName}Biome;

impl Biome for ${biomeName}Biome {
    fn get_type_tag(&self) -> &'static str {
        "$biomeName"
    }

    fn is_suitable(&self, height: f64, moisture: f64, temperature: f64) -> bool {
        height >= $minHeight && height <= $maxHeight &&
        moisture >= $minMoisture && moisture <= $maxMoisture &&
        temperature >= $minTemperature && temperature <= $maxTemperature
    }

    fn get_ground_tile_type(&self) -> &'static str {
        "$tileType"
    }

    fn get_spawnable_objects(&self) -> Vec<(&'static str, f32)> {
        $objectsString
    }

    fn clone_box(&self) -> Box<dyn Biome> {
        Box::new(self.clone())
    }
}
        """.trimIndent()
            file.writeText(biomeFile)

            val modCont = mod.readText()
            if (!modCont.contains("pub mod ${contentName.lowercase()};\n")) {
                mod.appendText("pub mod ${contentName.lowercase()};\n")
            }
        }

        fun tileAdd(tileName: String,
                    texturePath: String,
        ) {
            if (!file.exists()) {
                file.createNewFile()
            }
            val tileFile: String = """use gaymwtf_core::{load_texture_sync, DrawBatch, Tile, TILE_SIZE};
use macroquad::prelude::*;
use once_cell::sync::Lazy;

static ${tileName.uppercase()}_TEXTURE: Lazy<Texture2D> = Lazy::new(|| {
    load_texture_sync("${texturePath}").expect("Failed to load ${tileName.lowercase()} texture")
});

#[derive(Clone, Debug)]
pub struct ${tileName}Tile {
    pos: Vec2,
}

impl ${tileName}Tile {
    pub fn new(pos: Vec2) -> Self {
        Self { pos }
    }

    pub fn get_texture(&self) -> Texture2D {
        ${tileName.uppercase()}_TEXTURE.clone()
    }
}

impl Tile for ${tileName}Tile {
    fn get_type_tag(&self) -> &'static str { "${tileName.lowercase()}" }
    fn get_pos(&self) -> Vec2 { self.pos }
    fn set_pos(&mut self, pos: Vec2) { self.pos = pos; }
    fn get_size(&self) -> Vec2 { vec2(TILE_SIZE, TILE_SIZE) }
    fn clone_box(&self) -> Box<dyn Tile> { Box::new(self.clone()) }

    fn draw(&self, batch: &mut DrawBatch, pos: Vec2) {
        batch.add(self.get_texture(), pos, 0.0, None);
    }
}
""".trimIndent()
            file.writeText(tileFile)

            val modCont = mod.readText()
            if (!modCont.contains("pub mod ${contentName.lowercase()};\n")) {
                mod.appendText("pub mod ${contentName.lowercase()};\n")
            }
        }

        fun objectAdd(objectName: String,
                      texturePath: String,
                      sizeX: Double,
                      sizeY: Double
        ) {
            if (!file.exists()) {
                file.createNewFile()
            }
            val objFile: String = """use gaymwtf_core::{load_texture_sync, DrawBatch, Object};
use macroquad::prelude::*;
use once_cell::sync::Lazy;

static ${objectName.uppercase()}_TEXTURE: Lazy<Texture2D> = Lazy::new(|| {
    load_texture_sync("${texturePath}").expect("Failed to load ${objectName.lowercase()} texture")
});

#[derive(Clone, Debug)]
pub struct ${objectName} {
    pos: Vec2,
    size: Vec2,
    velocity: Vec2,
}

impl ${objectName} {
    pub fn new(pos: Vec2) -> Self {
        Self { pos, size: vec2(${sizeX}, ${sizeY}), velocity: Vec2::ZERO }
    }
}

impl ${objectName} {
    pub fn get_texture(&self) -> Texture2D {
        ${objectName.uppercase()}_TEXTURE.clone()
    }
}

impl Object for ${objectName} {
    fn get_type_tag(&self) -> &'static str { "${objectName.lowercase()}" }
    fn get_pos(&self) -> Vec2 { self.pos }
    fn get_size(&self) -> Vec2 { self.size }
    fn get_velocity(&self) -> Vec2 { self.velocity }

    fn tick(&mut self, _dt: f32, _world: &mut gaymwtf_core::World) { }
    fn clone_box(&self) -> Box<dyn Object> { Box::new(self.clone()) }

    fn set_pos(&mut self, pos: Vec2) { self.pos = pos; }
    fn set_size(&mut self, size: Vec2) { self.size = size; }
    fn set_velocity(&mut self, velocity: Vec2) { self.velocity = velocity }

    fn draw(&self, batch: &mut DrawBatch) {
        batch.add(self.get_texture(), self.pos, 1.0, Some(self.size));
    }
}
            """.trimIndent()
            file.writeText(objFile)

            val modCont = mod.readText()
            if (!modCont.contains("pub mod ${contentName.lowercase()};\n")) {
                mod.appendText("pub mod ${contentName.lowercase()};\n")
            }
        }
    }
}


fun Reload(srcPath: String) {
    val main = File("${srcPath}\\main.main.rs")
    val biomeMod = File("${srcPath}\\biomes\\mod.rs")
    val objectMod = File("${srcPath}\\objects\\mod.rs")
    val tileMod = File("${srcPath}\\tiles\\mod.rs")
    main.writeText("""pub mod biomes;
pub mod objects;
pub mod player;
pub mod tiles;
pub mod worldgen;
pub mod menus;
pub mod utils;

use gaymwtf_core::{
    BiomeRegistry, DrawBatch, ObjectRegistry, TileRegistry, World, TILE_SIZE,
};
use macroquad::prelude::*;
use std::fs;
use serde::{Serialize, Deserialize};
extern crate serde;
extern crate serde_json;

use biomes::{beach::BeachBiome,desert::DesertBiome,forest::ForestBiome,plains::PlainsBiome,river::RiverBiome,snow_forest::SnowForestBiome,snow_plains::SnowPlainsBiome,};
use objects::{cactus::Cactus,snow_tree::SnowTree,tree::Tree,};
use tiles::{grass::GrassTile,sand::SandTile,snowgrass::SnowGrassTile,water::WaterTile,};
use player::{Player, PlayerTextures};
use worldgen::generate_chunk;
use menus::start::StartMenu;
use menus::howtoplay::HowToPlayMenu;
use menus::about::AboutMenu;
use menus::worlds::WorldsMenu;
use menus::createworld::CreateWorldMenu;
use menus::game::GameMenu;
use gaymwtf_core::{Menu, MenuAction};

async fn register_tiles(registry: &mut TileRegistry) -> anyhow::Result<()> {
    // {{START_GEN_TILES}}
    // {{END_GEN_TILES}}
    registry.register(GrassTile::new(Vec2::ZERO));
    registry.register(SandTile::new(Vec2::ZERO));
    registry.register(SnowGrassTile::new(Vec2::ZERO));
    registry.register(WaterTile::new(Vec2::ZERO));
    Ok(())
}

async fn register_objects(registry: &mut ObjectRegistry) -> anyhow::Result<()> {
    // {{START_GEN_OBJECTS}}
    // {{END_GEN_OBJECTS}}
    registry.register(Tree::new(Vec2::ZERO));
    registry.register(SnowTree::new(Vec2::ZERO));
    registry.register(Cactus::new(Vec2::ZERO));
    registry.register(Player::new(Vec2::ZERO, PlayerTextures::new()?));
    Ok(())
}

async fn register_biomes(registry: &mut BiomeRegistry) -> anyhow::Result<()> {
    // {{START_GEN_BIOMES}}
    // {{END_GEN_BIOMES}}
    registry.register(RiverBiome);
    registry.register(BeachBiome);
    registry.register(DesertBiome);
    registry.register(SnowPlainsBiome);
    registry.register(SnowForestBiome);
    registry.register(PlainsBiome);
    registry.register(ForestBiome);
    Ok(())
}

fn init_registries() -> (TileRegistry, ObjectRegistry, BiomeRegistry) {
    let mut tile_registry = TileRegistry::new();
    let mut object_registry = ObjectRegistry::new();
    let mut biome_registry = BiomeRegistry::new();
    futures::executor::block_on(register_tiles(&mut tile_registry)).unwrap();
    futures::executor::block_on(register_objects(&mut object_registry)).unwrap();
    futures::executor::block_on(register_biomes(&mut biome_registry)).unwrap();
    (tile_registry, object_registry, biome_registry)
}

fn update_camera(camera: &mut Camera2D) {
    let base_zoom = 0.0066668;
    let aspect_ratio = screen_width() / screen_height();
    camera.zoom = if aspect_ratio > 1.0 {
        vec2(base_zoom / aspect_ratio, base_zoom)
    } else {
        vec2(base_zoom, base_zoom * aspect_ratio)
    };
}

#[derive(Serialize, Deserialize, Debug, Clone, Copy)]
pub struct WorldGenInfo {
    pub seed: u32,
}

#[macroquad::main.main("gaymwtf")]
async fn main.main() -> anyhow::Result<()> {
    let mut current_menu: Box<dyn Menu> = Box::new(StartMenu::new());
    let mut batch = DrawBatch::new();
    loop {
        let dt = get_frame_time();
        let action = current_menu.update(dt);
        current_menu.draw(&mut batch);
        next_frame().await;
        match action {
            MenuAction::ChangeState(state) => {
                match state.as_str() {
                    "start" | "menu" => {
                        current_menu = Box::new(StartMenu::new());
                    }
                    "howtoplay" => {
                        current_menu = Box::new(HowToPlayMenu::new());
                    }
                    "about" => {
                        current_menu = Box::new(AboutMenu::new());
                    }
                    "worlds" => {
                        current_menu = Box::new(WorldsMenu::new());
                    }
                    "createworld" => {
                        current_menu = Box::new(CreateWorldMenu::new());
                    }
                    s if s.starts_with("createworld:") => {
                        let parts: Vec<&str> = s.split(':').collect();
                        if parts.len() == 3 {
                            let name = parts[1];
                            let seed: u32 = parts[2].parse().unwrap_or(rand::gen_range(0, u32::MAX));
                            let (tile_registry, object_registry, biome_registry) = init_registries();
                            let mut world = World::new(name, tile_registry, object_registry, biome_registry);
                            let mut initial_chunk = generate_chunk((0, 0), seed, &world.tile_registry, &world.object_registry, &world.biome_registry).await?;
                            let player_pos = vec2(TILE_SIZE * 5.0, TILE_SIZE * 5.0);
                            if let Some(mut player) = world.object_registry.create_object_by_id("player") {
                                player.set_pos(player_pos);
                                initial_chunk.objects.push(player);
                            }
                            world.add_chunk(initial_chunk);
                            world.save_world(&format!("saves/{}", name)).ok();
                            let worldgen_info = WorldGenInfo { seed };
                            let worldgen_path = format!("saves/{}/gamestate.json", name);
                            let _ = fs::write(&worldgen_path, serde_json::to_string_pretty(&worldgen_info).unwrap());
                        }
                        current_menu = Box::new(WorldsMenu::new());
                    }
                    s if s.starts_with("play:") || s.starts_with("game:") => {
                        let name = s.trim_start_matches("play:").trim_start_matches("game:");
                        current_menu = Box::new(GameMenu::new(name).await?);
                    }
                    _ => {}
                }
            }
            MenuAction::Quit => return Ok(()),
            _ => {}
        }
    }
}""")
    biomeMod.writeText("""pub mod plains;
pub mod forest;
pub mod river;
pub mod beach;
pub mod snow_plains;
pub mod snow_forest;
pub mod desert;
""")
    objectMod.writeText("""pub mod tree;
pub mod snow_tree;
pub mod cactus;
""")
    tileMod.writeText("""pub mod grass;
pub mod sand;
pub mod snowgrass;
pub mod water;
""")
}

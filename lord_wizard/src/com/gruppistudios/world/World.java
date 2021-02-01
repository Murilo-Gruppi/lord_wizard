package com.gruppistudios.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.gruppistudios.entities.Boss;
import com.gruppistudios.entities.Enemy;
import com.gruppistudios.entities.Entity;
import com.gruppistudios.entities.LifePack;
import com.gruppistudios.entities.Mana;
import com.gruppistudios.entities.Player;
import com.gruppistudios.entities.Staff;
import com.gruppistudios.graphics.Spritesheet;
import com.gruppistudios.main.Game;

public class World {
	
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;
	
	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path)); 
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			tiles = new Tile[map.getWidth() * map.getHeight()];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels , 0, map.getWidth());
			for (int xx = 0; xx < map.getWidth(); xx++) {
				for (int yy = 0; yy < map.getHeight(); yy++) {
					int curPixel = pixels[xx + (yy * map.getWidth())];
					
					tiles[xx + (yy*WIDTH)] = new FloorTile(xx*16, yy*16, Tile.TILE_FLOOR);
					if (curPixel == 0xFF000000) {
						// Floor/Chão
						tiles[xx + (yy*WIDTH)] = new FloorTile(xx*16, yy*16, Tile.TILE_FLOOR);
					} else if (curPixel == 0xFFFFFFFF) {
						// Wall/Parede
						tiles[xx + (yy*WIDTH)] = new WallTile(xx*16, yy*16, Tile.TILE_WALL);
					} else if (curPixel == 0xFF57007F) {
						// Player/Jogador
						Game.player.setX(xx*16);
						Game.player.setY(yy*16);
					} else if(curPixel == 0xFFFF0000) {
						// Enemy/Inimigo
						Enemy en = new Enemy(xx*16, yy*16, 16, 16, Entity.ENEMY);
						Game.entities.add(en);
						Game.enemies.add(en);
					} else if (curPixel == 0xFF7F3300) {
						// Staff/Cajado
						Game.entities.add(new Staff(xx*16, yy*16, 16, 16, Entity.STAFF));
					} else if (curPixel == 0xFFFF7F7F) {
						// LifePack/KitMedico
						Game.entities.add(new LifePack(xx*16, yy*16, 16, 16, Entity.LIFEPACK));
					} else if (curPixel == 0xFF0026FF) {
						// Mana
						Game.entities.add(new Mana(xx*16, yy*16, 16, 16, Entity.MANA));
					} else if (curPixel == 0xFF267F00) {
						// Boss
						Game.entities.add(new Boss(xx*16, yy*16, 32, 32, Entity.BOSS));
					}
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static boolean isFree(int xnext, int ynext, int zplayer) {
		boolean isFree = false;
		
		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;
		int x2 = (xnext+TILE_SIZE-1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;
		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext+TILE_SIZE-1) / TILE_SIZE;		
		int x4 = (xnext+TILE_SIZE-1) / TILE_SIZE;
		int y4 = (ynext+TILE_SIZE-1) / TILE_SIZE;
		
		if (!(tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile ||
				tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile ||
				tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile ||
				tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile)) {
			isFree = true;
		}
		
		if (zplayer > 0) {
			isFree = true;
		}
		
		return isFree;
	}
	
	public static void restartGame(String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		// Instanciando jogador e adicionando à entities.
		Game.player = new Player(0,0, 16, 16, Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		// Instanciando mundo.
		Game.world = new World("/" + level);
		
	}
	
	public void render(Graphics g) {
		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;
		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.WIDTH >> 4);
		
		for (int xx = xstart; xx <= xfinal; xx++) {
			for (int yy = ystart; yy <= yfinal; yy++) {
				if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy * WIDTH)];
				tile.render(g);
			}
		}
	}
		
}

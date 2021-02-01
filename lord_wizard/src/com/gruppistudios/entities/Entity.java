package com.gruppistudios.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.gruppistudios.main.Game;
import com.gruppistudios.world.Camera;

public class Entity {

	public static BufferedImage LIFEPACK = Game.spritesheet.getSprite(96, 0, 16, 16);
	public static BufferedImage STAFF = Game.spritesheet.getSprite(112, 0, 16, 16);
	public static BufferedImage MANA = Game.spritesheet.getSprite(96, 16, 16, 16);
	public static BufferedImage ENEMY = Game.spritesheet.getSprite(112, 16, 16, 16);
	public static BufferedImage ENEMY_FEEDBACK = Game.spritesheet.getSprite(144, 0, 16, 16);
	public static BufferedImage STAFF_RIGHT = Game.spritesheet.getSprite(112, 0, 16, 16);
	public static BufferedImage STAFF_LEFT = Game.spritesheet.getSprite(128, 0, 16, 16);
	public static BufferedImage SPELL = Game.spritesheet.getSprite(0, 32, 16, 16);
	public static BufferedImage STAFF_DAMAGE_RIGHT = Game.spritesheet.getSprite(16, 32, 16, 16);
	public static BufferedImage STAFF_DAMAGE_LEFT = Game.spritesheet.getSprite(32, 32, 16, 16);
	public static BufferedImage BOSS = Game.spritesheet.getSprite(48, 32, 32, 32);
	
	protected double x, y;
	protected int width, height, z;
	
	private BufferedImage sprite;
	private int maskx, masky, mwidth, mheight;
	
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		this.maskx = 0;
		this.masky = 0;
		this.mwidth = width;
		this.mheight = height;
	}
	
	public void setMask(int maskx, int masky, int mwidth, int mheight) {
		this.maskx = maskx;
		this.masky = masky;
		this.mwidth = mwidth;
		this.mheight = mheight;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}
	
	public int getX() {
		return (int)this.x;
	}
	
	public int getY() {
		return (int)this.y;
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void tick() {}
	
	public double calculateDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	public static boolean isColliding(Entity e1, Entity e2) {
		boolean colliding = false;
		
		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx, e1.getY() + e1.masky, e1.mwidth, e1.mheight);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx, e2.getY() + e2.masky, e2.mwidth, e2.mheight);
		
		if (e1Mask.intersects(e2Mask) && e1.z == e2.z) {
			colliding = true;
		}
		
		return colliding;
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}

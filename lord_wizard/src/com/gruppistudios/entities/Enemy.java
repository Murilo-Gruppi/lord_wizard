package com.gruppistudios.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.gruppistudios.main.Game;
import com.gruppistudios.world.Camera;
import com.gruppistudios.world.World;

public class Enemy extends Entity {

	private double speed = 0.5;
	private boolean moved = false;
	private int frames = 0, maxFrames = 8, index = 0, maxIndex = 3;
	public int maskx = 10, masky = 8, maskw = 10, maskh = 13;
	private BufferedImage[] enemySprite;	
	
	private int life = 10;
	private boolean isDamaged = false;
	private int damageFrames = 15, damageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		enemySprite = new BufferedImage[3];
		// Obtendo sprite do inimigo
		for (int i = 0; i < enemySprite.length; i++) {
			enemySprite[i] = Game.spritesheet.getSprite(112 + (i*16) , 16, 16, 16);
		}
	}
	
	public void tick() {
		if (this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 100) {
			if (isCollidingWithPlayer() == false) {
				if ((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY(), 0)
						&& !isColliding((int)(x+speed), this.getY())) {
					moved = true;
					x += speed;
				} else if ((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY(), 0)
						&& !isColliding((int)(x-speed), this.getY())) {
					moved = true;
					x -= speed;
				}
				
				if ((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed), 0)
						&& !isColliding(this.getX(), (int)(y+speed))) {
					moved = true;
					y += speed;
				} else if ((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed), 0)
						&& !isColliding(this.getX(), (int)(y-speed))) {
					moved = true;
					y -= speed;
				} 
			} else {
				if (Game.rand.nextInt(100) < 10) {
					Game.player.life-= Game.rand.nextInt(3);
					Game.player.isDamaged = true;
				}
			}
		}
		
		if (moved) {
			frames++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				if (index == maxIndex) {
					index = 0;
				}
			}
		}
		
		if (isDamaged) {
			this.damageCurrent++;
			if (this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}
		
		collidingSpell();
		
		if (life <= 0) {
			destroySelf();
			return;
		}
		
	}
	
	public void render(Graphics g) {
		if (!isDamaged) {
			g.drawImage(enemySprite[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		} else {
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void collidingSpell() {
		for (int i = 0; i < Game.spell.size(); i++) {
			Entity e = Game.spell.get(i);
			if (e instanceof CastSpell) {
				if (Entity.isColliding(e, this)) {
					isDamaged = true;
					life--;
					Game.spell.remove(i);
					return;
				}
			}
		}
	}
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX(), this.getY(), World.TILE_SIZE, World.TILE_SIZE);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		
		return enemyCurrent.intersects(player);
	}
	
	
	public boolean isColliding(int xnext, int ynext) {
		boolean colliding = false;
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw, maskh);
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if (e == this)
				continue;
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, maskw, maskh);
			if (enemyCurrent.intersects(targetEnemy)) {
				colliding = true;
			}
		}
		
		return colliding;
	}

}

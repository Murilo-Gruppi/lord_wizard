package com.gruppistudios.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.gruppistudios.main.Game;
import com.gruppistudios.world.Camera;
import com.gruppistudios.world.World;

public class Boss extends Entity {
	
	private double speed = 1;
	private boolean moved = false;
	protected int bossHeight = 32, bossWidth = 32;
	private BufferedImage[] bossSprite;
	public int maskx = 32, masky = 32, maskh = 32, maskw = 32;
	private int frames = 0, maxFrames = 12, index = 0, maxIndex = 3;
	
	private int life = 50;
	private boolean isDamaged = false;
	private int damageFrames = 15, damageCurrent = 0;

	public Boss(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		bossSprite = new BufferedImage[3];
		// obtendo sprites do chefão 
		for (int i = 0; i < bossSprite.length; i++) {
			bossSprite[i] = Game.spritesheet.getSprite(48 + (i * 32), 32, bossHeight, bossWidth);
		}
		
	}
	
	public void tick() {
		if (this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 100) {
			if (!bossCollidingWithPlayer()) {
				if ((int)x < Game.player.getX() && World.isFree((int)(x + speed), this.getY(), 0)) {
					moved = true;
					x += speed;
				} else if ((int)x > Game.player.getX() && World.isFree((int)(x - speed), this.getY(), 0)) {
					moved = true;
					x -= speed;
				}
				
				if ((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y + speed), 0)) {
					moved = true;
					y += speed;
				} else if ((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y - speed), 0)) {
					moved = true;
					y -= speed;
				} 
			} else {
				if (Game.rand.nextInt(100) < 45) {
					Game.player.life -= Game.rand.nextInt(7);
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
			g.drawImage(bossSprite[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
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
	
	public void destroySelf() {
		Game.entities.remove(this);
	}
	
	public boolean bossCollidingWithPlayer() {
		Rectangle boss = new Rectangle(this.getX(), this.getY(), World.TILE_SIZE, World.TILE_SIZE);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		
		return boss.intersects(player);
	}
}

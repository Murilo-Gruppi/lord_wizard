package com.gruppistudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.gruppistudios.main.Game;
import com.gruppistudios.world.Camera;
import com.gruppistudios.world.World;

public class Player extends Entity {

	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	public double speed = 1.3;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private BufferedImage playerDamage;
	private int damageFrames = 0;
	
	public double life = 100, maxLife = 100;
	public double mana = 50, maxMana = 50;
	public boolean isDamaged = false;
	private boolean hasStaff = false;
	public boolean shoot = false;
	public boolean jump = false, jumpUp = false, jumpDown = false;
	public boolean isJumping = false;
	public int jumpFrames = 50, jumpCur = 0;
	public int jumpSpeed = 2;
	public int z = 0;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(0, 16, width, height);
	
		
		// Adicionando animação para direita
		for (int i = 0; i < rightPlayer.length; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16) , 0, 16, 16);
		}
		// Adicionando animação para a esquerda
		for (int i = 0; i < leftPlayer.length; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
		}
	}
	
	public void tick() {
		
		if (jump) {
			if (isJumping == false) {
				jump = false;
				isJumping = true;
				jumpUp = true;
			}
		}
		
		if (isJumping) {
			if (jumpUp) {
				jumpCur += jumpSpeed;
			} else if (jumpDown) {
				jumpCur -= jumpSpeed;
				if (jumpCur <= 0) {
					isJumping = false;
					jumpDown = false;
					jumpUp = false;
				}
			}
			z = jumpCur;
			if (jumpCur >= jumpFrames) {
				jumpUp = false;
				jumpDown = true;
			}
		}
		
		moved = false;
		if (right && World.isFree((int)(x+speed), this.getY(), this.z)) {
			moved = true;
			dir = right_dir;
			x += speed;
		} else if (left && World.isFree((int)(x-speed), this.getY(), this.z)) {
			moved = true;
			dir = left_dir;
			x -= speed;
		}
		if (up && World.isFree(this.getX(), (int)(y-speed), this.z)) {
			moved = true;
			y -= speed;
		} else if (down && World.isFree(this.getX(), (int)(y+speed), this.z)) {
			moved = true;
			y += speed;
		}
		
		if (moved) {
			frames++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				if (index > maxIndex) {
					index = 0;
				}
			}
		}
	
		this.checkCollisionLifePack();
		this.checkCollisionMana();
		this.checkCollisionStaff();
		
		if (isDamaged) {
			this.damageFrames++;
			if (this.damageFrames == 15) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if (shoot) {
			shoot = false;
			if (hasStaff && mana > 0) { 
				mana--;
				
				int dx = 0;
				if (dir == right_dir) {
					dx = 1;
				} else {
					dx = -1;
				}
				
				CastSpell spell = new CastSpell(this.getX(), this.getY(), 3, 3, null, dx, 0);
				Game.spell.add(spell);
			}
		}
		
		if (life <= 0) {
			// Game Over!
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.WIDTH/2), 0, World.HEIGHT*16 - Game.HEIGHT);
	}
	
	
	public void render(Graphics g) {
		if (!isDamaged) {
			if (dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if (hasStaff) {
					// Renderiza cajado para direita
					g.drawImage(STAFF_RIGHT, this.getX()+3  - Camera.x, this.getY() + 2 - Camera.y - z, null);
				}
			} else if (dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if (hasStaff) {
					// Renderiza cajado para esquerda
					g.drawImage(STAFF_LEFT, this.getX()-3 - Camera.x, this.getY() + 2  - Camera.y - z, null);
				} 
			}
		} else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
			if (hasStaff) {
				if (dir == right_dir) {
					g.drawImage(STAFF_DAMAGE_RIGHT, this.getX() + 3 - Camera.x, this.getY() + 2 - Camera.y - z, null);
				} else {
					g.drawImage(STAFF_DAMAGE_LEFT, this.getX() - 3 - Camera.x, this.getY() + 2 - Camera.y - z, null);
				}
			}
		}
		
		if (isJumping) {
			g.setColor(Color.black);
			g.fillOval(this.getX() - Camera.x + 4, this.getY() - Camera.y + 8, 8, 8)
			;
		}
	}
	
	public void checkCollisionLifePack() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity current = Game.entities.get(i);
			if (current instanceof LifePack) {
				if (Entity.isColliding(this, current)) {
					life += 10;
					if (life > maxLife) {
						life = maxLife;
					}
					Game.entities.remove(current);
				}
			}
		}
	}
	
	public void checkCollisionMana() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity current = Game.entities.get(i);
			if (current instanceof Mana) {
				if (Entity.isColliding(this, current)) {
					mana += 10;
					if (mana > maxMana)
						mana = maxMana;
					Game.entities.remove(current);
				}
			}
		}
	}
	
	public void checkCollisionStaff() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity current = Game.entities.get(i);
			if (current instanceof Staff) {
				if (Entity.isColliding(this, current)) {
					hasStaff = true;
				Game.entities.remove(current);
				}
			}
		}
	}
}
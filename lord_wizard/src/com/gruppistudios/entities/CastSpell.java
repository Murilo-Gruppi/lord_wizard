package com.gruppistudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.gruppistudios.main.Game;
import com.gruppistudios.world.Camera;

public class CastSpell extends Entity {
	
	private int dx, dy;
	private double speed = 4;
	
	private int life = 35, curLife = 0;
	
	public CastSpell(int x, int y, int width, int height, BufferedImage sprite, int dx, int dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}

	public void tick() {
		x += dx * speed;
		y += dy * speed;
		curLife++;
		if (curLife == life) {
			Game.spell.remove(this);
			return;
		}
	}	
	
	public void render(Graphics g) {
		g.drawImage(SPELL, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
	
}

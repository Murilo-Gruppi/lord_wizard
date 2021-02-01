package com.gruppistudios.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.gruppistudios.main.Game;

public class UI {
	public void render(Graphics g) {
		// Renderizando barra de vida
		g.setColor(Color.red);
		g.fillRect(8, 4, 70, 8);
		g.setColor(Color.green);
		g.fillRect(8, 4,(int)((Game.player.life/Game.player.maxLife)*70) ,8);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 8));
		g.drawString((int)Game.player.life+"/"+ (int)Game.player.maxLife, 8, 11);
		/***/
		//Renderizando barra de mana
		g.setColor(Color.black);
		g.fillRect(160, 4, 70, 8);
		g.setColor(Color.blue);
		g.fillRect(160, 4, (int)((Game.player.mana/Game.player.maxMana)*70), 8);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 8));
		g.drawString((int)Game.player.mana + "/" + (int)Game.player.maxMana, 160, 11);
	}
}

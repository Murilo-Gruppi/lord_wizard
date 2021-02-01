package com.gruppistudios.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.gruppistudios.world.World;

public class Menu {
	
	public String[] options = {"Novo jogo", "Carregar jogo", "Sair"};
	public int currentOption = 	0;
	public int maxOption = options.length - 1;
	public boolean up, down, enter;
	public static boolean pause = false;
	public static boolean saveExists = false;
	public static boolean saveGame = false;
	
	public void tick() {
		File file = new File("save.txt");
		if (file.exists()) {
			saveExists = true;
		} else {
			saveExists = false;
		}
		
		if (up) {
			up = false;
			currentOption --;
			if (currentOption < 0) {
				currentOption = maxOption;
			}
		} else if (down) {
			down = false;
			currentOption++;
			if (currentOption > maxOption) {
				currentOption = 0;
			}
		}
		
		if (enter) {
			enter = false;
			if (options[currentOption] == "Novo jogo") {
				Game.gameState = "NORMAL";
				pause = false;
				file = new File("save.txt");
				file.delete();
			} else if (options[currentOption] == "Carregar jogo") {
				file = new File("save.txt");
				if (file.exists()) {
					String saver = loadGame(10);
					applySave(saver);
				}
			} else if (options[currentOption] == "Sair") {
				System.exit(1);
			}
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT*Game.SCALE);
		g.setColor(new Color(87, 0, 127));
		g.setFont(new Font("arial", Font.BOLD, 36));
		g.drawString("Lord Wizard", Game.WIDTH*Game.SCALE / 2 - 100, 40);
		
		//Opções de menu
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 24));
		if (!pause) {
			g.drawString("Novo jogo", Game.WIDTH*Game.SCALE / 2 - 55, 150);
		} else {
			g.drawString("Continuar", Game.WIDTH*Game.SCALE / 2 - 55, 150);
		}
		
		g.drawString("Carregar jogo", Game.WIDTH*Game.SCALE / 2 - 75, 210);
		g.drawString("Sair", Game.WIDTH*Game.SCALE / 2 - 20, 270);
		
		if (options[currentOption] == "Novo jogo") {
			g.drawString(">", Game.WIDTH*Game.SCALE / 2 - 75, 150);
		} else if (options[currentOption] == "Carregar jogo") {
			g.drawString(">", Game.WIDTH*Game.SCALE / 2 - 95, 210);
		} else if (options[currentOption] == "Sair" ) {
			g.drawString(">", Game.WIDTH*Game.SCALE / 2 - 38, 270);
		}
	}
	
	
	
	public static void saveGame(String[] val1, int[] val2, int encode) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("save.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < val1.length; i++) {
			String current = val1[i];
			current += ":";
			char[] value = Integer.toString(val2[i]).toCharArray();
			for (int j = 0; j < value.length; j++) {
					value[j] += encode;
					current+= value[j];
			}
			try {
				writer.write(current);
				if (i < val1.length - 1)
					writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void applySave(String str) {
		String[] spl = str.split("/");
		for (int i = 0; i < spl.length; i++) {
			String[] spl2 = spl[i].split(":");
			switch (spl2[0]) {
				case "level":
					World.restartGame("level" + spl2[1] + ".png");
					Game.gameState = "NORMAL";
					pause = false;
					break;
				case "life":
					Game.player.life = Integer.parseInt(spl2[1]);
					break;
			}
		}
	}
	
	public static String loadGame(int encode) {
		String line = "";
		File file = new File("save.txt");
		if (file.exists()) {
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				try {
					while((singleLine = reader.readLine()) != null) {
						String[] transition = singleLine.split(":");
						char[] val = transition[1].toCharArray();
						transition[1] = "";
						for (int i = 0; i < val.length; i++) {
							val[i] -= encode;
							transition[1] += val[i];
						}
						line += transition[0];
						line += ":";
						line += transition[1];
						line += "/";
					}
				} catch (IOException e) {
					
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return line;
	}
	
}

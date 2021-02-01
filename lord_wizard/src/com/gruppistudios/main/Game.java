package com.gruppistudios.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.gruppistudios.entities.CastSpell;
import com.gruppistudios.entities.Enemy;
import com.gruppistudios.entities.Entity;
import com.gruppistudios.entities.Player;
import com.gruppistudios.graphics.Spritesheet;
import com.gruppistudios.graphics.UI;
import com.gruppistudios.world.World;

public class Game extends Canvas implements Runnable, KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	public static Random rand;
	private BufferedImage image;
	public static Player player;
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<CastSpell> spell;
	public static Spritesheet spritesheet;
	public static World world;
	public UI ui;
	private int CUR_LEVEL = 1, MAX_LEVEL = 2;
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0, maxFramesGameOver = 25;
	private boolean restartGame = false;
	public Menu menu;
	public boolean saveGame = false;
	
	
	public Game() {
		addKeyListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		// Inicializando objetos.
		ui = new UI();
		rand = new Random();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		spell = new ArrayList<CastSpell>();
		spritesheet = new Spritesheet("/spritesheet.png");
		// Instanciando jogador e adicionando à entities.
		player = new Player(0,0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		// Instanciando mundo.
		world = new World("/level1.png");
		// Instanciando menu.
		menu = new Menu();
	}
	
	public void initFrame() {
		frame = new JFrame("Lord Wizard");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		if (gameState == "NORMAL") {
			if (this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level", "life", "x", "y"};
				int[] opt2 = {this.CUR_LEVEL, (int) player.life, player.getX(), player.getY()};
				Menu.saveGame(opt1, opt2, 10);
			}
			this.restartGame = false;
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for (int i = 0; i < spell.size(); i++) {
				CastSpell e = spell.get(i);
				e.tick();
			}
			
			if (enemies.size() == 0) {
				// Avançar para o próximo level
				CUR_LEVEL++;
				if (CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld  = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if (gameState == "GAME_OVER") {
			this.framesGameOver ++;
			if (this.framesGameOver == this.maxFramesGameOver) {
				this.framesGameOver = 0;
				if (this.showMessageGameOver) {
					this.showMessageGameOver = false;
				} else {
					this.showMessageGameOver = true;
				}
			}
			
			if (restartGame) {
				this.restartGame = false;
				gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld  = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if (gameState == "MENU") {
			// Start Menu
			menu.tick();
		}
		

	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		/*Renderização do jogo*/
		world.render(g);
		/*Renderização das entities*/
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		/*Renderização do feitiço*/
		for (int i = 0; i < spell.size(); i++) {
			CastSpell e = spell.get(i);
			e.render(g);
		}
		/*Renderização das barras de vida e mana*/
		ui.render(g);
		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		// Tela de game over
		if (gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0, 0, 150));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g2.setFont(new Font("arial", Font.BOLD, 40));
			g2.setColor(Color.white);
			g2.drawString("Game Over", WIDTH*SCALE / 2 - 120, HEIGHT*SCALE / 2);
			g2.setFont(new Font("arial", Font.BOLD, 32));
			if (showMessageGameOver) {
				g2.drawString("Pressione Enter para reiniciar", WIDTH*SCALE / 2 - 230, HEIGHT*SCALE / 2 + 40);
			}
		} else if (gameState == "MENU") {
			menu.render(g);
		}
		bs.show();
	}

	@Override
	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		
		// Game looping
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if (System.currentTimeMillis() - timer >= 1000) {
				frames = 0;
				timer += 1000;
			}
		}
		
		stop();
	}


	@Override
	public void keyPressed(KeyEvent e) {
		
		// player jump
		if (e.getKeyCode() == KeyEvent.VK_X) {
			player.jump = true;
		}
		
		// player moviment
		if (e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_UP ||
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
			
			if (gameState == "MENU") {
				menu.up = true;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			
			if (gameState == "MENU") {
				menu.down = true;
			}
		}
		/***/
		
		// Menu
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if (gameState == "MENU") {
				menu.enter = true;
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			Menu.pause = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			if (gameState == "NORMAL") 
				this.saveGame = true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		if (e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || 
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN || 
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		} else if (e.getKeyCode() == KeyEvent.VK_UP || 
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.shoot = true;
		}
		

	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}
}

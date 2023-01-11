package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import audio.AudioPlayer;
import entities.Bullet;
import entities.Diamond;
import entities.Player;
import entities.SentinelBullet;
import entities.Square;
import gameStates.GameState;
import gameStates.KillCount;
import gameStates.SpawnState;
import inputs.KeyboardInputs;
import inputs.MouseInputs;
import upgrades.Sentinel;
import upgrades.Shield;
import upgrades.Upgrades;



public class GameScreen extends JPanel{
	
	private Game game;
	
	Font myFont, myFont2;
	
	Player player = new Player(WIDTH/2, HEIGHT/2, 50, 50);
	AudioPlayer audioPlayer;
	
	public static final int WIDTH = 1700, HEIGHT = 800;
	private MouseInputs mouseInputs;
	
	private BufferedImage backImg, playerImg, hpBar, gameOver, menuImg;
	private Color[][] backImgRGB = new Color[WIDTH][HEIGHT];
	
	private BufferedImage[] idleAnimation;
	private int animationTick;

	private int animationIndex;

	private int animationSpeed = 25;
	
	public static double previous_spawn_diamond = -1, delai_spawn_diamond =  1000000000.0;
	public static double previous_spawn_square = -1, delai_spawn_square =    7000000000.0;
	
	
	// timer
	public static long timer = 0, previous_timer = -1;
	public static double timerInMillis;
	public static long last_shot_time = -1, shoot_delay = 500000000; // (nano secondes)
	public static long last_shot_time_sentinelle = -1, shoot_delay_sentinelle = 50000000;
	
	// game states
	public static SpawnState ss = SpawnState.PHASE1;
	public static GameState gs = GameState.MENU;
	
	private boolean fondTamise = false;

	
	// ________________________________________________________________________________________________________________
	
	public GameScreen(Game game) {
		
		mouseInputs = new MouseInputs(this);
		setWindowSize();
		myFont = new Font ("TimesRoman", 1, 34);
		this.game = game;
		importImgs();
		importImgPlayer();
		loadAnimations();
		storeBckImgRGB();
		addKeyListener(new KeyboardInputs(this));
		addMouseListener(mouseInputs);
		addMouseMotionListener(mouseInputs);
		KillCount.initKillsPaliers();
		Sentinel.initSentinels();

		
	}
	
	public Game getGame() {
		return game;
	}
	
	
	// Import imgs, load etc ________________________________________________________
	
	
	private void loadAnimations() {
		idleAnimation = new BufferedImage[3];
		
		for(int i = 0; i < idleAnimation.length; i++) {
			
			idleAnimation[i] = playerImg.getSubimage(i*32, 0, 32, 32);
			
		}
	}
	
	private void importImgPlayer() {
		InputStream is = getClass().getResourceAsStream("/ball2.png");
		// on crée un inputstream depuis l'img
		
		try {
			playerImg = ImageIO.read(is);
			// on lit l'inputstream sur la var img
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void importImgs() {
		InputStream is = getClass().getResourceAsStream("/bckg2.png");
		InputStream is2 = getClass().getResourceAsStream("/hp_bar.png");
		InputStream is3 = getClass().getResourceAsStream("/GameOverF.png");
		InputStream is4 = getClass().getResourceAsStream("/alphaMenu.png");
		// on crée un inputstream depuis l'img
		
		try {
			backImg = ImageIO.read(is);
			hpBar = ImageIO.read(is2);
			gameOver = ImageIO.read(is3);
			menuImg = ImageIO.read(is4);
			// on lit l'inputstream sur la var img
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				is2.close();
				is3.close();
				is4.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setWindowSize() {
		Dimension size = new Dimension(WIDTH , HEIGHT);
		setPreferredSize(size);
	}
	
	// Updates (fonctions a mettre dans le updateGame() _____________________________________________________________________________________________________________________
	// ______________________________________________________________________________________________________________________________________________________________________
	
	public void updateGame() {
		
		if(gameOver()) {
			return;
		}
		
		
		else if(gs == GameState.PLAY) {
			player.updatePlayer();
			spawnDiamond();
			spawnSquare();
			Bullet.deplacement();
			SentinelBullet.deplacement();
			
			shooting();
			allCollisions();
			
			updateDiamonds();
			updateSquares();
			updateShields();
			Sentinel.updateSentinels();
			Upgrades.tryCastUpgrade();
			updateAnimationTick();
			updateTimer();
			
			updateGamePhase();
			updateSpawnRate();
			
		}
	}
	
	
	
	private void updateTimer() {
		
		
		long now = System.nanoTime();
		timerInMillis = now;
		
		if(previous_timer == -1) {
			previous_timer = System.nanoTime();
		}
		
		if(now - previous_timer >= 1000000000.0) {
			previous_timer = now;
			timer++;
		}
	}
	
	private void updateAnimationTick() {
		animationTick++;
		if(animationTick >= animationSpeed) {
			animationTick = 0;
			animationIndex++;
			if(animationIndex >= idleAnimation.length) {
				animationIndex = 0;
			}
		}
	}
	
	private void updateDiamonds() {
		for(Diamond d: Diamond.tabDiamond) {
			if(d != null) {
				d.updateAnimationTick();
				d.deplacementDiamond(player.getX(), player.getY());
				d.updateHitbox();
			}
		}
	}
	
	private void updateSquares() {
		for(Square d: Square.tabSquare) {
			if(d != null) {
				d.updateAnimationTick();
				d.deplacementSquare(player.getX(), player.getY());
				d.updateHitbox();
			}
		}
	}
	
	private void updateGamePhase() {
		// Update la phase de jeu selon le timer (va impliquer le chgt du spawnRate notamment dans updateSpawnRate() )
		if(timer >= 10 && timer < 20) 	   ss = SpawnState.PHASE2;
		else if(timer >= 20 && timer < 30) {
			ss = SpawnState.PHASE3;
			Diamond.hp = 125;
		}
		else if(timer >= 30 && timer < 50) {
			ss = SpawnState.PHASE4;
			Diamond.hp = 150;
		}
		else if(timer >= 50 && timer < 60) {
			ss = SpawnState.PHASE5;
			Diamond.hp = 150;
		}
		else if(timer >= 60 && timer < 80) {
			ss = SpawnState.PHASE6;
			Diamond.hp = 175;
		}
		else if(timer >= 80 && timer < 100) {
			ss = SpawnState.PHASE7;
			Diamond.hp = 200;
		}
		else if(timer >= 100 && timer < 120) {
			ss = SpawnState.PHASE8;
			Diamond.hp = 225;
		}
		else if(timer >= 120 && timer < 140) {
			ss = SpawnState.PHASE9;
			Diamond.hp = 250;
		}
		else if(timer >= 140 && timer < 160) {
			ss = SpawnState.PHASE10;
			Diamond.hp = 275;
		}
		else if(timer >= 160 && timer < 180) {
			ss = SpawnState.PHASE11;
			Diamond.hp = 300;
		}
		else if(timer >= 180 && timer < 260) {
			ss = SpawnState.PHASE12;
			Diamond.hp = 325;
		}
		else if(timer >= 260) {
			ss = SpawnState.PHASE13;
		}
		else ss = SpawnState.PHASE1;
	}
	
	private void updateSpawnRate() {
		
		switch(ss) {
			
		case PHASE1:
			delai_spawn_diamond = 1000000000.0;
			break;
			
		case PHASE2:
			delai_spawn_diamond =  800000000.0;
			break;
			
		case PHASE3:
			delai_spawn_diamond =  700000000.0;
			break;
			
		case PHASE4:
			delai_spawn_diamond =  500000000.0;
			break;
			
		case PHASE5:
			delai_spawn_diamond =  400000000.0;
			break;
			
		case PHASE6:
			delai_spawn_diamond =  300000000.0;
			break;
			
		case PHASE7:
			delai_spawn_diamond =  200000000.0;
			break;
			
		case PHASE8:
			delai_spawn_diamond =  100000000.0;
			break;
			
		case PHASE9:
			delai_spawn_diamond =  80000000.0;
			break;
			
		case PHASE10:
			delai_spawn_diamond =  70000000.0;
			break;
			
		case PHASE11:
			delai_spawn_diamond =  60000000.0;
			break;
			
		case PHASE12:
			delai_spawn_diamond =  10000000.0;
			break;
			
		case PHASE13:
			delai_spawn_diamond =  5000000.0;
			break;
			
		default:
			delai_spawn_diamond =  1000000000.0;
			break;
			
		}
		
	}
	
	private void updateShields() {
		for(Shield s: Shield.listeShield) {
			if(s != null) {
				s.updateAngle();
			}
		}
	}
	

	// Fonctions d'affichage a mettre dans paintComponent() ____________________________
	
	
	public void paintComponent(Graphics g) {
		/* Cette méthode est nécessaire a JPanel pour pouvoir dessiner, et elle passe par un objet
		 Graphics */
		
		super.paintComponent(g);		
		
		if(gs == GameState.MENU) {
			displayMenu(g);
			return;
		}
		
		if(gs == GameState.GAMEOVER) {
			displayGameOver(g);
			return;
		}
		
		
		if(gs == GameState.PLAY) {
			try {
				detamiserFond();
			} catch(IOException e) {}
		}
		
		g.drawImage(backImg, 0, 0, null);
		
		affiche_HP(g);
		displayPlayer(g);	
		displayAllDiamonds(g);
		displayAllSquares(g);
		displayShields(g);
		Bullet.displayBullets(g);
		SentinelBullet.displayBullets(g);
		Sentinel.displaySentinels(g);

		displayTimer(g);
		KillCount.displayKillCount(g);
		
		if(gs == GameState.UPGRADE_TIME) {
			Upgrades.displayRoll(g);
			highlightRoll(g);
			try {
				tamiserFond();
			} catch(IOException e) {}
				
		}		
	}
	
	private void storeBckImgRGB() {
		// On stocke les rgb de l'image originale
		for(int i = 0; i < backImg.getWidth(); i++) {
			for(int j=0; j < backImg.getHeight(); j++) {
				
				backImgRGB[i][j] = new Color(backImg.getRGB(i, j), true);
				
			}
		}
	}
	
	public void tamiserFond() throws IOException {
		// On tamise le fond lors d'une upgrade time
		int colorOffset = 80;
		
		
		if(fondTamise) return;
		for(int i = 0; i < backImg.getWidth(); i++) {
			for(int j=0; j<backImg.getHeight(); j++) {
				int oldColor = backImg.getRGB(i, j);
				Color c = new Color(oldColor, true);
				int red = c.getRed();
	            int green = c.getGreen();
	            int blue = c.getBlue();
	            
	            int nRed = red - colorOffset;
	            if(nRed < 0) nRed = 0;
	            int nGreen = green - colorOffset;
	            if(nGreen < 0) nGreen = 0;
	            int nBlue = blue - colorOffset;
	            if(nBlue < 0) nBlue = 0;
	            
	            
	            Color newC = new Color(nRed, nGreen, nBlue);
				backImg.setRGB(i, j, newC.getRGB());
			}
		}
		fondTamise = true;
	}
	
	public void detamiserFond() throws IOException {
		
		if(!fondTamise) return;
		for(int i = 0; i < backImg.getWidth(); i++) {
			for(int j=0; j<backImg.getHeight(); j++) {
				backImg.setRGB(i, j, backImgRGB[i][j].getRGB());
			}
		}
		fondTamise = false;
	}
	
	public void displayTimer(Graphics g) {
		int xoffset = 50;
		g.setColor(Color.RED);
		g.drawString("Timer: " + Double.toString(timer), WIDTH/2 - xoffset, 30);
	}
	
	public void displayPlayer(Graphics g) {
		g.drawImage(idleAnimation[animationIndex], (int)player.getX() , (int)player.getY(), 50, 50 ,null);
		//player.drawHitbox(g);
	}
	
	public void displayDiamond(Graphics g, Diamond d) {
		if(d.getHP() <= Diamond.hp * 0.51)
			g.drawImage(d.idleAnimationBroken[d.animationIndex], (int)d.getX() , (int)d.getY(), (int)d.getW(), (int)d.getH() ,null);
		else
			g.drawImage(d.idleAnimation[d.animationIndex], (int)d.getX() , (int)d.getY(), (int)d.getW(), (int)d.getH() ,null);
		//d.drawHitbox(g);
	}
	
	public void displayAllDiamonds(Graphics g) {
		for(Diamond dia: Diamond.tabDiamond) {
			if(dia != null) {
				if(!dia.displayed) {
					dia.importImgDiamond();
					dia.loadAnimations();
					dia.displayed = true;
				}
				displayDiamond(g, dia);			
			}
		}
	}
	
	public void displaySquare(Graphics g, Square d) {
		g.drawImage(d.idleAnimation[d.animationIndex], (int)d.getX() , (int)d.getY(), (int)d.getW(), (int)d.getH() ,null);
		//d.drawHitbox(g);
	}
	
	public void displayAllSquares(Graphics g) {
		for(Square dia: Square.tabSquare) {
			if(dia != null) {
				displaySquare(g, dia);			
			}
		}
	}
	
	public void displayShields(Graphics g) {
		for(Shield s: Shield.listeShield) {
			if(s != null) {
				s.displayShield(g, player.getX(), player.getY());
			}
		}
	}
	
	public void affiche_HP(Graphics g) {
		
		BufferedImage currHP;
		
		switch(player.getHP()) {
		
			case 5:
				currHP = hpBar.getSubimage(0, 0, 128, 128);
				break;
				
			case 4:
				currHP = hpBar.getSubimage(0, 128, 128, 128);
				break;
				
			case 3:
				currHP = hpBar.getSubimage(0, 2*128, 128, 128);
				break;
			
			case 2:
				currHP = hpBar.getSubimage(0, 3*128, 128, 128);
				break;
				
			case 1:
				currHP = hpBar.getSubimage(0, 4*128, 128, 128);
				break;
				
			default:
				currHP = hpBar.getSubimage(0, 0, 128, 128);
				break;
		
		}
		
		g.drawImage(currHP, 20 , (int) (HEIGHT*0.9), 240, 128 ,null);
		
	}
	
	private void highlightRoll(Graphics g) {
		// Entoure le roll de blanc lorsque la souris se trouve dessus
		float xOffset = 180;
		
		PointerInfo mouseLoc = MouseInfo.getPointerInfo();
		Point mouseLoca = mouseLoc.getLocation();
		SwingUtilities.convertPointFromScreen(mouseLoca, this);
		// Cette methode permet que la souris soit localisée par rapport a la fenetre de Jpanel et pas l'écran de l'user
		
		double mouseX = mouseLoca.getX();
		double mouseY = mouseLoca.getY();
		
		for(int i = 1; i <= 3; i++) {
			if(mouseX > i*(WIDTH/4)-xOffset && mouseX < (i*(WIDTH/4)-xOffset) + WIDTH/5) {
				if(mouseY > 100 && mouseY < (HEIGHT*0.7f)+100) {
					g.setColor(Color.WHITE);
					g.drawRect((int) (i*(WIDTH/4)-xOffset), 100, WIDTH/5, (int) (HEIGHT*0.7));
				}
			}
		}
		
		
	}
	
	// Fonction de spawn de mobs ______________________________________________________________
		
	private void auxiliaireSpawnDia(float x, float y, int width, int height) {
		Diamond tmp2 = new Diamond(x,y,width,height, Diamond.hp);
		tmp2.numero = Diamond.iterateurTab;
		Diamond.tabDiamond[Diamond.iterateurTab] = tmp2;
		Diamond.iterateurTab++;
	}
	
	public void spawnDiamond() {


		double now = System.nanoTime();
		
		if(previous_spawn_diamond == -1) {
			previous_spawn_diamond = now;
		}
		
		else if (now - previous_spawn_diamond >= delai_spawn_diamond) {
			previous_spawn_diamond = now;
			
			int width = 40, height = 40;
			
			
			// Zone de spawn
			float x = ThreadLocalRandom.current().nextInt(0, WIDTH);
			
			Random r = new Random();
			int tmp = r.nextInt(2);
			
			// Je définis ma zone de spawn sur un "cadre"
			if(tmp == 1) {
				// Partie du bas (choisie par l'aléatoire)
				if(x < 0.1*WIDTH || x > 0.9*WIDTH) {
					// Gauche ou droite
					
					if(tabDiamondPlein()) Diamond.iterateurTab = 0;
					if(x < 0.1*WIDTH) x -= 50;
					if(x > 0.9*WIDTH) x += 50;
					
					float y = ThreadLocalRandom.current().nextInt(0, HEIGHT+1);
					auxiliaireSpawnDia(x,y,width,height);
					
				}
				else {
					// Milieu
					
					if(tabDiamondPlein()) Diamond.iterateurTab = 0;
					
					float y = ThreadLocalRandom.current().nextInt((int) HEIGHT, HEIGHT + 51);
					auxiliaireSpawnDia(x,y,width,height);
				}
				
			}
			else {
				// Pareil mais partie haute
				if(x < 0.1*WIDTH || x > 0.9*WIDTH) {
					
					if(tabDiamondPlein()) Diamond.iterateurTab = 0;
					if(x < 0.1*WIDTH) x -= 50;
					if(x > 0.9*WIDTH) x += 50;
					
					float y = ThreadLocalRandom.current().nextInt(0, HEIGHT+1);
					auxiliaireSpawnDia(x,y,width,height);
				}
				else {
					
					if(tabDiamondPlein()) Diamond.iterateurTab = 0;
					float y = ThreadLocalRandom.current().nextInt(-51, 0);
					auxiliaireSpawnDia(x,y,width,height);
				}
			}
		}
	}
	
	private boolean tabDiamondPlein() {
		return Diamond.iterateurTab >= (Diamond.tabDiamond.length -2);
	}
	
	public void spawnSquare() {

		if(ss == SpawnState.PHASE1 || ss == SpawnState.PHASE2 || ss == SpawnState.PHASE3) return;

		double now = System.nanoTime();
		
		if(previous_spawn_square == -1) {
			previous_spawn_square = now;
		}
		
		else if (now - previous_spawn_square >= delai_spawn_square) {
			previous_spawn_square = now;
			int width = 30, height = 30;
			
			
			// Zone de spawn
			float x = ThreadLocalRandom.current().nextInt(0, WIDTH);
			
			Random r = new Random();
			int tmp = r.nextInt(2);
			
			// Je définis ma zone de spawn sur un "cadre"
			if(tmp == 1) {
				// Partie du bas (choisie par l'aléatoire)
				if(x < 0.1*WIDTH || x > 0.9*WIDTH) {
					// Gauche ou droite
					
					if(tabSquarePlein()) Square.iterateurTab = 0;
					if(x < 0.1*WIDTH) x -= 50;
					if(x > 0.9*WIDTH) x += 50;
					
					float y = ThreadLocalRandom.current().nextInt(0, HEIGHT+1);
					auxiliaireSpawnSquare(x,y,width,height);
					
				}
				else {
					// Milieu
					
					if(tabDiamondPlein()) Square.iterateurTab = 0;
					
					float y = ThreadLocalRandom.current().nextInt((int) HEIGHT, HEIGHT + 51);
					auxiliaireSpawnSquare(x,y,width,height);
				}
				
			}
			else {
				// Pareil mais partie haute
				if(x < 0.1*WIDTH || x > 0.9*WIDTH) {
					
					if(tabDiamondPlein()) Square.iterateurTab = 0;
					if(x < 0.1*WIDTH) x -= 50;
					if(x > 0.9*WIDTH) x += 50;
					
					float y = ThreadLocalRandom.current().nextInt(0, HEIGHT+1);
					auxiliaireSpawnSquare(x,y,width,height);
				}
				else {
					
					if(tabSquarePlein()) Square.iterateurTab = 0;
					float y = ThreadLocalRandom.current().nextInt(-51, 0);
					auxiliaireSpawnSquare(x,y,width,height);
				}
			}
		}
	}
	
	private boolean tabSquarePlein() {
		return Square.iterateurTab >= (Square.tabSquare.length -2);
	}

	private void auxiliaireSpawnSquare(float x, float y, int width, int height) {
		Square tmp2 = new Square(x,y,width,height, 1500);
		tmp2.numero = Square.iterateurTab;
		Square.tabSquare[Square.iterateurTab] = tmp2;
		Square.iterateurTab++;
	}
	
	
	// Collisions _____________________________________________________________________________
	
	
	public void allCollisions() {
		collisionBullets();
		collisionSentinelBullets();
		collisionDiamonds();
		collisionSquares();
		collisionShieldsDiamond();
		collisionShieldsSquare();
	}
	
	
	public void collisionDiamonds() {

		for(Diamond e: Diamond.tabDiamond) {
			if(e != null) {
				
				Rectangle2D.Float hb = e.getHitbox();
				Rectangle2D.Float hbp = player.getHitbox();
				
				if(hb.x + hb.width >= hbp.x && hb.y + hb.height >= hbp.y) {
					if(hb.x <= hbp.x + hbp.width && hb.y <= hbp.y + hbp.height) {
												
						player.reduceHP(1);
						Diamond.tabDiamond[e.numero] = null;	
					}
				}
			}
		}
	}
	
	public void collisionSquares() {
		for(Square s: Square.tabSquare) {
			if(s != null) {
				
				Rectangle2D.Float hb = s.getHitbox();
				Rectangle2D.Float hbp = player.getHitbox();
				
				if(hb.x + hb.width >= hbp.x && hb.y + hb.height >= hbp.y) {
					if(hb.x <= hbp.x + hbp.width && hb.y <= hbp.y + hbp.height) {
												
						player.reduceHP(2);
						Square.tabSquare[s.numero] = null;	
					}
				}
				
			}
		}
	}
	
	public void collisionShieldsDiamond() {
		for(Shield s: Shield.listeShield) {
			if(s!=null) {
				
				double x = player.getX() + s.calculateXposition();
				double d = x + Shield.width;
				double y = player.getY() + s.calculateYposition();
				double b = y + Shield.height;
				
				for(Diamond dia: Diamond.tabDiamond) {
					if(dia != null) {
						
						Rectangle2D.Float hbp = dia.getHitbox();
						
						if(d >= hbp.x && b >= hbp.y) {
							if(x <= hbp.x + hbp.width && y <= hbp.y + hbp.height) {
								
								s.dealDamage(dia);
								

											
								if(dia.isDead()) {
									Diamond.tabDiamond[dia.numero] = null;
									KillCount.increaseKillCount();
									break;
								}								
							}
						}
					}
				}
			}
		}
	}
	
	public void collisionShieldsSquare() {
		for(Shield s: Shield.listeShield) {
			if(s!=null) {
				
				double x = player.getX() + s.calculateXposition();
				double d = x + Shield.width;
				double y = player.getY() + s.calculateYposition();
				double b = y + Shield.height;
				
				for(Square squa: Square.tabSquare) {
					if(squa != null) {
						
						Rectangle2D.Float hbp = squa.getHitbox();
						
						if(d >= hbp.x && b >= hbp.y) {
							if(x <= hbp.x + hbp.width && y <= hbp.y + hbp.height) {
								
								s.dealDamage(squa);
											
								if(squa.isDead()) {
									Square.tabSquare[squa.numero] = null;
									KillCount.increaseKillCount();
									break;
								}								
							}
						}
					}
				}
			}
		}
	}
	
	public void collisionBullets() {
		
		// Un peu bourrin, pour chaque bullet on regarde si collision il y a avec CHAQUE diamond, sachant que je peux avoir jusqu'a 1000 bullet et 1000 diamond, Kounalis se retourne dans sa tombe
		
		for(Bullet b: Bullet.bulletArray) {
			
			if(b != null) {
				
				Rectangle2D.Float hb = b.getHitbox();
				
				
				if(hb.x <= 0 - hb.width || hb.x >= WIDTH + hb.width || hb.y <= 0 - hb.height || hb.y >= HEIGHT + hb.height ) {
					// out of map
					Bullet.bulletArray[b.numero] = null;
				}
				
				
				for(Diamond d: Diamond.tabDiamond) {
					
					if(d != null) {
					
						Rectangle2D.Float hbp = d.getHitbox();
						
						if(hb.x + hb.width >= hbp.x && hb.y + hb.height >= hbp.y) {
							if(hb.x <= hbp.x + hbp.width && hb.y <= hbp.y + hbp.height) {
								// Collision Bullet diamond

								b.dealDamage(d);
								
	
								if(d.isDead()) {
									Diamond.tabDiamond[d.numero] = null;
									Bullet.bulletArray[b.numero] = null;
									
									KillCount.increaseKillCount();
									
									break;
								}
								else {
									Bullet.bulletArray[b.numero] = null;
									break;
								}

								
								
							}
						}
					}
				}
				
				for(Square d: Square.tabSquare) {
					
					if(d != null) {
					
						Rectangle2D.Float hbp = d.getHitbox();
						
						if(hb.x + hb.width >= hbp.x && hb.y + hb.height >= hbp.y) {
							if(hb.x <= hbp.x + hbp.width && hb.y <= hbp.y + hbp.height) {
								// Collision Bullet diamond

								b.dealDamage(d);
								
	
								if(d.isDead()) {
									Square.tabSquare[d.numero] = null;
									Bullet.bulletArray[b.numero] = null;
									
									if(player.getHP() <= 4)
										player.receiveDamage(-1);
									// On heal le player d'un hp a la mort d'un square
									// Il faudrait jouer un ptit son ici
									KillCount.increaseKillCount();
									
									break;
								}
								else {
									Bullet.bulletArray[b.numero] = null;
									break;
								}

								
								
							}
						}
					}
				}
				
				for(Entry<Sentinel, Boolean> s: Sentinel.listeSentinels.entrySet()) {
					if(s.getValue()) {
						
						Rectangle hbp = s.getKey().getHitbox();
						if(hb.x + hb.width >= hbp.x && hb.y + hb.height >= hbp.y) {
							if(hb.x <= hbp.x + hbp.width && hb.y <= hbp.y + hbp.height) {
								
								Bullet.bulletArray[b.numero] = null;
								
								long now = System.nanoTime();
								if(last_shot_time_sentinelle == -1) {
									last_shot_time_sentinelle = now;
								} else if((now - last_shot_time_sentinelle) <= shoot_delay_sentinelle) {
									return;
								}
								
								s.getKey().shoot(s.getKey());
								
							}
						}
					}
				}
			}
		}
	}
	
	public void collisionSentinelBullets() {
		
		// Un peu bourrin, pour chaque bullet on regarde si collision il y a avec CHAQUE diamond, sachant que je peux avoir jusqu'a 1000 bullet et 100 diamond, Kounalis se retourne dans sa tombe
		
		for(SentinelBullet b: SentinelBullet.S_bulletArray) {
			
			if(b != null) {
				
				Rectangle2D.Float hb = b.getHitbox();
				
				
				if(hb.x <= 0 - hb.width || hb.x >= WIDTH + hb.width || hb.y <= 0 - hb.height || hb.y >= HEIGHT + hb.height ) {
					// out of map
					SentinelBullet.S_bulletArray[b.numero] = null;
				}
				
				
				for(Diamond d: Diamond.tabDiamond) {
					
					if(d != null) {
					
						Rectangle2D.Float hbp = d.getHitbox();
						
						if(hb.x + hb.width >= hbp.x && hb.y + hb.height >= hbp.y) {
							if(hb.x <= hbp.x + hbp.width && hb.y <= hbp.y + hbp.height) {
								// Collision Bullet diamond

								b.dealDamage(d);
								
	
								if(d.isDead()) {
									Diamond.tabDiamond[d.numero] = null;
									SentinelBullet.S_bulletArray[b.numero] = null;
									
									KillCount.increaseKillCount();
									
									break;
								}
								else {
									SentinelBullet.S_bulletArray[b.numero] = null;
									break;
								}

								
								
							}
						}
					}
				}
				
				for(Square d: Square.tabSquare) {
					
					if(d != null) {
					
						Rectangle2D.Float hbp = d.getHitbox();
						
						if(hb.x + hb.width >= hbp.x && hb.y + hb.height >= hbp.y) {
							if(hb.x <= hbp.x + hbp.width && hb.y <= hbp.y + hbp.height) {

								b.dealDamage(d);
								
	
								if(d.isDead()) {
									Square.tabSquare[d.numero] = null;
									SentinelBullet.S_bulletArray[b.numero] = null;
									
									if(player.getHP() <= 4)
										player.receiveDamage(-1);

									KillCount.increaseKillCount();
									
									break;
								}
								else {
									SentinelBullet.S_bulletArray[b.numero] = null;
									break;
								}

								
								
							}
						}
					}
				}
				
				for(Entry<Sentinel, Boolean> s: Sentinel.listeSentinels.entrySet()) {
					if(s.getValue()) {
						
						if(!(s.getKey() == b.caster)) {
						
							Rectangle hbp = s.getKey().getHitbox();
							if(hb.x + hb.width >= hbp.x && hb.y + hb.height >= hbp.y) {
								if(hb.x <= hbp.x + hbp.width && hb.y <= hbp.y + hbp.height) {
									
									long now = System.nanoTime();
									if(last_shot_time_sentinelle == -1) {
										last_shot_time_sentinelle = now;
									} else if((now - last_shot_time_sentinelle) <= shoot_delay_sentinelle) {
										return;
									}
									
									SentinelBullet.S_bulletArray[b.numero] = null;
									s.getKey().shoot(s.getKey());
									
								}
							}
						}
					}
				}
			}
		}

	}
	
	// _________________________________________________________________________________________
	// En rapport avec l'upgrade time
	
	
	public void chooseRoll() {
		
		if(maxedOutLevels()) gs = GameState.PLAY;
		
		if(gs != GameState.UPGRADE_TIME) return;
		
		Random r = new Random();
		
		float xOffset = 180;
		
		PointerInfo mouseLoc = MouseInfo.getPointerInfo();
		Point mouseLoca = mouseLoc.getLocation();
		SwingUtilities.convertPointFromScreen(mouseLoca, this);
		// Cette methode permet que la souris soit localisée par rapport a la fenetre de Jpanel et pas l'écran de l'user
		
		double mouseX = mouseLoca.getX();
		double mouseY = mouseLoca.getY();
		
		for(int i = 1; i <= 3; i++) {
			if(mouseX > i*(WIDTH/4)-xOffset && mouseX < (i*(WIDTH/4)-xOffset) + WIDTH/5) {
				if(mouseY > 100 && mouseY < (HEIGHT*0.7f)+100) {
					//
					int currentUpgrade = Upgrades.currentRollList[i-1];
					
					switch(currentUpgrade) {
					case 1:
						Upgrades.upgradeSPEEDY();
						break;
					case 2:
						Upgrades.upgradeTIR_MULTIPLE();
						break;
					case 3:
						Upgrades.upgradeDAMAGE_INCREASE();
						break;
					case 4:
						Upgrades.upgradeShield();
						Shield.addShield();
						break;
					case 5:
						Upgrades.upgradeSentinel();
						
						for (Entry<Sentinel, Boolean> s: Sentinel.listeSentinels.entrySet()) {
							if(!s.getValue()) {
								Sentinel.listeSentinels.put(s.getKey(), true);
								break;
							}
						}
						break;
					}
					
				}
				
				newUpgradeRoll();
				gs = GameState.PLAY;
				
			}
		}
	}
	
	private boolean maxedOutLevels() {
		int tmp = 0;
		
		for(Entry<String, Integer> e: Upgrades.UPGRADE_LIST.entrySet()) {
			tmp += e.getValue();
			
		}
		System.out.println(tmp);
		// On vérifie rapidement qu'on est pas level max partout
		return (tmp >= Upgrades.nombreUpgrades * Upgrades.LEVEL_MAX_UPGRADE);
	}
	

	private void newUpgradeRoll() {
		
		int[] upgradesMaxed = new int[Upgrades.nombreUpgrades];
		
		Random r = new Random();
		
		int r1, r2, r3;
		int lv1, lv2, lv3, lv4, lv5;
		
		r1 = r.nextInt(Upgrades.nombreUpgrades)+1;
		r2 = r.nextInt(Upgrades.nombreUpgrades)+1;
		r3 = r.nextInt(Upgrades.nombreUpgrades)+1;
				
		
		lv1 = Upgrades.getUpgradeLevel("Speedy");
		lv2 = Upgrades.getUpgradeLevel("Multishot");
		lv3 = Upgrades.getUpgradeLevel("Damage++");
		lv4 = Upgrades.getUpgradeLevel("Shield");
		lv5 = Upgrades.getUpgradeLevel("Sentinel");
		
		if(lv1 >= 5) upgradesMaxed[0] = 1;
		if(lv2 >= 5) upgradesMaxed[1] = 2;
		if(lv3 >= 5) upgradesMaxed[2] = 3;
		if(lv4 >= 5) upgradesMaxed[3] = 4;
		if(lv5 >= 5) upgradesMaxed[4] = 5;
		
		System.out.println(Upgrades.allUpgradesMaxed);
		
		if(isTabFull(upgradesMaxed)) {
			Upgrades.allUpgradesMaxed = true;
			return;
		}
			// Actuellement si toutes les upgrades sont au max et qu'il reste des roll, le jeu bloque
		while(inTab(r1, upgradesMaxed) || inTab(r2, upgradesMaxed) || inTab(r3, upgradesMaxed)){
			r1 = r.nextInt(Upgrades.nombreUpgrades)+1;
			r2 = r.nextInt(Upgrades.nombreUpgrades)+1;
			r3 = r.nextInt(Upgrades.nombreUpgrades)+1;
		}
		
		
		Upgrades.currentRollList[0] = r1;
		Upgrades.currentRollList[1] = r2;
		Upgrades.currentRollList[2] = r3;
		System.out.println("" +r1 +r2+ r3);
	}
	
	private boolean inTab(int x, int[] tab) {
		for(int i: tab) {
			if(i==x) {
				System.out.println("" + x + " est déja maxé");
				return true;
			}
			
		}
		return false;
	}
	
	private boolean isTabFull(int[] x) {
	    for(int i = 0; i<x.length; i++) {
	    	if(x[i] != i+1) {
	    		return false;
	    	}
	    }
	    System.out.println("Le tab est full");
	    return true;  
	}
	

	
	
	
	
	// _________________________________________________________________________________________
	// FONCTIONS DE SHOOTING
	
	
	
	public void shooting() {
		

		if(Player.shooting) {
			
			
			long now = System.nanoTime();
			if(last_shot_time == -1) {
				last_shot_time = now;
			} else if((now - last_shot_time) <= shoot_delay) {
				return;
			}
			
			// game.getAudioPlayer().playEffect(AudioPlayer.SHOOTSOUND);
			
			PointerInfo mouseLoc = MouseInfo.getPointerInfo();
			Point mouseLoca = mouseLoc.getLocation();
			SwingUtilities.convertPointFromScreen(mouseLoca, this);
			// Cette methode permet que la souris soit localisée par rapport a la fenetre de Jpanel et pas l'écran de l'user
			
			double mouseX = mouseLoca.getX();
			double mouseY = mouseLoca.getY();
					
			int i = Bullet.bulletIterateur;
			
			
			if(i >= 990) {
				i = 0;
				Bullet.bulletIterateur = 0;

			}
			
			int n = player.getNbBalles();
			
			switch(n) {
				case 1:
					spawnBullet(player.getX() + Player.width/2, player.getY() + Player.height/2, 5, 5, (int)mouseX, (int)mouseY, i);
					break;
				case 2:
					doubleShot(player.getX() + Player.width/2, player.getY() + Player.height/2, 5, 5, (int)mouseX, (int)mouseY, i);
					break;
				case 3:
					tripleShot(player.getX() + Player.width/2, player.getY() + Player.height/2, 5, 5, (int)mouseX, (int)mouseY, i);
					break;
				case 4:
					quadraShot(player.getX() + Player.width/2, player.getY() + Player.height/2, 5, 5, (int)mouseX, (int)mouseY, i);
					break;
				case 5:
					pentaShot(player.getX() + Player.width/2, player.getY() + Player.height/2, 5, 5, (int)mouseX, (int)mouseY, i);
					break;
				case 6:
					septaShot(player.getX() + Player.width/2, player.getY() + Player.height/2, 5, 5, (int)mouseX, (int)mouseY, i);
					break;
			}
			

		

		}
	}
	

	private void spawnBullet(float x, float y, int w, int h, int xDir, int yDir, int i) {
		Bullet bullet = new Bullet(player.getX() + Player.width/2, player.getY() + Player.height/2, 5, 5, xDir, yDir, i);

		Bullet.bulletArray[i] = bullet;
		Bullet.bulletIterateur++;
		last_shot_time = System.nanoTime();
	}
	
	public static int detecteCadran(int x, int y, int xp, int yp) {
		int xOffset = 130;
		int yOffset = 80;
		// Cette fonction renvoie un entier correspondant au cadran ou la souris se trouve ->
		// Permet de spécifier quel type de multishot utiliser
		// 1 = HG; 2 = HD; 3 = BG; 4 = BD; 5 = MH; 6 = MG; 7 = MB; 8 = MD
		
		
		if(y < yp-yOffset) {
			// Partie haute au dessus de la bande
			
			if(x < xp - xOffset) return 1;
			if(x > xp + xOffset) return 2;
			return 5;
		}
		
		if(y > yp+yOffset) {
			// Partie basse sous la bande
			if(x < xp - xOffset) return 3;
			if(x > xp + xOffset) return 4;
			return 7;
		}
		
		// Bande du milieu (soit MG soit MD)
		
		if(x < xp) return 6;
		return 8;
		
		}
	
	private void doubleShot(float x, float y, int w, int h, int xDir, int yDir, int i) {
		// Je ne vois pas comment faire autrement, pour chaque cadran on précise un comportement différent
		int cadran = detecteCadran(xDir, yDir, (int)x, (int)y);
		
		switch(cadran) {
		
		case 1:
			spawnBullet(x, y, w, h, xDir+40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+1);
			break;
			
		case 2:
			spawnBullet(x, y, w, h, xDir-40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+1);
			break;
			
		case 3:
			spawnBullet(x, y, w, h, xDir+40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+1);
			break;
			
		case 4:
			spawnBullet(x, y, w, h, xDir-40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+1);
			break;
		
		case 5:
		case 7:
			spawnBullet(x, y, w, h, xDir+15, yDir, i);
			spawnBullet(x, y, w, h, xDir-15, yDir, i+1);
			break;
			
		case 6:
		case 8:
			spawnBullet(x, y, w, h, xDir, yDir+15, i);
			spawnBullet(x, y, w, h, xDir, yDir-15, i+1);
			break;

		}
	}
	
	private void tripleShot(float x, float y, int w, int h, int xDir, int yDir, int i) {
		// Je ne vois pas comment faire autrement, pour chaque cadran on précise un comportement différent
		int cadran = detecteCadran(xDir, yDir, (int)x, (int)y);
		
		switch(cadran) {
		
		case 1:
			spawnBullet(x, y, w, h, xDir, yDir, i);
			spawnBullet(x, y, w, h, xDir+40, yDir, i+1);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+2);
			break;
			
		case 2:
			spawnBullet(x, y, w, h, xDir, yDir, i);
			spawnBullet(x, y, w, h, xDir-40, yDir, i+1);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+2);
			break;
			
		case 3:
			spawnBullet(x, y, w, h, xDir, yDir, i);
			spawnBullet(x, y, w, h, xDir+40, yDir, i+1);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+2);
			break;
			
		case 4:
			spawnBullet(x, y, w, h, xDir, yDir, i);
			spawnBullet(x, y, w, h, xDir-40, yDir, i+1);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+2);
			break;
		
		case 5:
		case 7:
			spawnBullet(x, y, w, h, xDir, yDir, i);
			spawnBullet(x, y, w, h, xDir+15, yDir, i+1);
			spawnBullet(x, y, w, h, xDir-15, yDir, i+2);
			break;
			
		case 6:
		case 8:
			spawnBullet(x, y, w, h, xDir, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir+15, i+1);
			spawnBullet(x, y, w, h, xDir, yDir-15, i+2);
			break;

		}
	}
	
	private void quadraShot(float x, float y, int w, int h, int xDir, int yDir, int i) {
		// Je ne vois pas comment faire autrement, pour chaque cadran on précise un comportement différent
		int cadran = detecteCadran(xDir, yDir, (int)x, (int)y);
		
		switch(cadran) {
		
		case 1:
			spawnBullet(x, y, w, h, xDir+40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+1);
			spawnBullet(x, y, w, h, xDir+10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir+10, i+3);
			break;
			
		case 2:
			spawnBullet(x, y, w, h, xDir-40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+1);
			spawnBullet(x, y, w, h, xDir-10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir+10, i+3);
			break;
			
		case 3:
			spawnBullet(x, y, w, h, xDir+40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+1);
			spawnBullet(x, y, w, h, xDir+10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir-10, i+3);
			break;
			
		case 4:
			spawnBullet(x, y, w, h, xDir-40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+1);
			spawnBullet(x, y, w, h, xDir-10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir-10, i+3);
			break;
		
		case 5:
		case 7:
			spawnBullet(x, y, w, h, xDir+15, yDir, i);
			spawnBullet(x, y, w, h, xDir-15, yDir, i+1);
			spawnBullet(x, y, w, h, xDir+30, yDir, i+2);
			spawnBullet(x, y, w, h, xDir-30, yDir, i+3);
			break;
			
		case 6:
		case 8:
			spawnBullet(x, y, w, h, xDir, yDir+15, i);
			spawnBullet(x, y, w, h, xDir, yDir-15, i+1);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+2);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+3);
			break;

		}
	}
	
	private void pentaShot(float x, float y, int w, int h, int xDir, int yDir, int i) {
		// Je ne vois pas comment faire autrement, pour chaque cadran on précise un comportement différent
		int cadran = detecteCadran(xDir, yDir, (int)x, (int)y);
		
		switch(cadran) {
		
		case 1:
			spawnBullet(x, y, w, h, xDir, yDir, i+4);
			spawnBullet(x, y, w, h, xDir+40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+1);
			spawnBullet(x, y, w, h, xDir+10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir+10, i+3);
			break;
			
		case 2:
			spawnBullet(x, y, w, h, xDir, yDir, i+4);
			spawnBullet(x, y, w, h, xDir-40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+1);
			spawnBullet(x, y, w, h, xDir-10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir+10, i+3);
			break;
			
		case 3:
			spawnBullet(x, y, w, h, xDir, yDir, i+4);
			spawnBullet(x, y, w, h, xDir+40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+1);
			spawnBullet(x, y, w, h, xDir+10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir-10, i+3);
			break;
			
		case 4:
			spawnBullet(x, y, w, h, xDir, yDir, i+4);
			spawnBullet(x, y, w, h, xDir-40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+1);
			spawnBullet(x, y, w, h, xDir-10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir-10, i+3);
			break;
		
		case 5:
		case 7:
			spawnBullet(x, y, w, h, xDir, yDir, i+4);
			spawnBullet(x, y, w, h, xDir+15, yDir, i);
			spawnBullet(x, y, w, h, xDir-15, yDir, i+1);
			spawnBullet(x, y, w, h, xDir+30, yDir, i+2);
			spawnBullet(x, y, w, h, xDir-30, yDir, i+3);
			break;
			
		case 6:
		case 8:
			spawnBullet(x, y, w, h, xDir, yDir, i+4);
			spawnBullet(x, y, w, h, xDir, yDir+15, i);
			spawnBullet(x, y, w, h, xDir, yDir-15, i+1);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+2);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+3);
			break;

		}
	}
	
	private void septaShot(float x, float y, int w, int h, int xDir, int yDir, int i) {
		// Je ne vois pas comment faire autrement, pour chaque cadran on précise un comportement différent
		
		int cadran = detecteCadran(xDir, yDir, (int)x, (int)y);
		
		switch(cadran) {
		
		case 1:
			spawnBullet(x, y, w, h, xDir, yDir, i+6);
			spawnBullet(x, y, w, h, xDir+40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+1);
			spawnBullet(x, y, w, h, xDir+10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir+10, i+3);
			spawnBullet(x, y, w, h, xDir+25, yDir, i+4);
			spawnBullet(x, y, w, h, xDir, yDir+20, i+5);
			break;
			
		case 2:
			spawnBullet(x, y, w, h, xDir, yDir, i+6);
			spawnBullet(x, y, w, h, xDir-40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+1);
			spawnBullet(x, y, w, h, xDir-10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir+10, i+3);
			spawnBullet(x, y, w, h, xDir-25, yDir, i+4);
			spawnBullet(x, y, w, h, xDir, yDir+20, i+5);
			break;
			
		case 3:
			spawnBullet(x, y, w, h, xDir, yDir, i+6);
			spawnBullet(x, y, w, h, xDir+40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+1);
			spawnBullet(x, y, w, h, xDir+10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir-10, i+3);
			spawnBullet(x, y, w, h, xDir+25, yDir, i+4);
			spawnBullet(x, y, w, h, xDir, yDir-20, i+5);
			break;
			
		case 4:
			spawnBullet(x, y, w, h, xDir, yDir, i+6);
			spawnBullet(x, y, w, h, xDir-40, yDir, i);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+1);
			spawnBullet(x, y, w, h, xDir-10, yDir, i+2);
			spawnBullet(x, y, w, h, xDir, yDir-10, i+3);
			spawnBullet(x, y, w, h, xDir-25, yDir, i+4);
			spawnBullet(x, y, w, h, xDir, yDir-20, i+5);
			break;
		
		case 5:
		case 7:
			spawnBullet(x, y, w, h, xDir, yDir, i+6);
			spawnBullet(x, y, w, h, xDir+15, yDir, i);
			spawnBullet(x, y, w, h, xDir-15, yDir, i+1);
			spawnBullet(x, y, w, h, xDir+30, yDir, i+2);
			spawnBullet(x, y, w, h, xDir-30, yDir, i+3);
			spawnBullet(x, y, w, h, xDir+25, yDir, i+4);
			spawnBullet(x, y, w, h, xDir-25, yDir, i+5);
			break;
			
		case 6:
		case 8:
			spawnBullet(x, y, w, h, xDir, yDir, i+6);
			spawnBullet(x, y, w, h, xDir, yDir+15, i);
			spawnBullet(x, y, w, h, xDir, yDir-15, i+1);
			spawnBullet(x, y, w, h, xDir, yDir+30, i+2);
			spawnBullet(x, y, w, h, xDir, yDir-30, i+3);
			spawnBullet(x, y, w, h, xDir, yDir+25, i+4);
			spawnBullet(x, y, w, h, xDir, yDir-20, i+5);
			break;

		}
	}
	
	  /* __________________________________________________________________________________________
	     __________________________________________________________________________________________
	     
	     GameOver things
	 */

	private boolean gameOver() {
		if(player.getHP() <= 0) {
			if(gs == GameState.GAMEOVER) return false; // Juste pour être sur que le gameOver ne s'execute qu'une fois, et pas que la fonction soit executé en boucle, jouant le son et réaffectant la variable a l'infini
			gs = GameState.GAMEOVER;
			playDeathSound();
			return true;
		}
		return false;
	}
	
	private void resetGame() {
		// J'ai pensé a recréer un objet Game mais flemme, je reset a la main tout de la run précédente
		
		game.getAudioPlayer().playSong(AudioPlayer.MUSIC);
		
		timer = 0;
		KillCount.initKillsPaliers();
		KillCount.setKillCount(0);
		previous_timer = -1;
		previous_spawn_diamond = -1;
		previous_spawn_square = -1;
		ss = SpawnState.PHASE1;
		
		Diamond.iterateurTab = 0;
		Square.iterateurTab = 0;
		Shield.iterateurShield = 0;
		Bullet.bulletIterateur = 0;
		SentinelBullet.S_bulletIterateur = 0;
		
		player = new Player(WIDTH/2, HEIGHT/2, 50, 50);
		Upgrades.initUpgrades();
		
		Sentinel.listeSentinels.clear();
		Sentinel.initSentinels();
		
		player.updateUpgrades();
		
		
		resetDiamonds();
		resetSquares();
		resetShields();
		resetBullets();
		
		gs = GameState.PLAY;
	}
	
	private void resetDiamonds() {
		for(Diamond d: Diamond.tabDiamond) {
			if(d != null) Diamond.tabDiamond[d.numero] = null;
		}
	}
	
	private void resetSquares() {
		for(Square d: Square.tabSquare) {
			if(d != null) Square.tabSquare[d.numero] = null;
		}
	}
	
	private void resetShields() {
		Shield.listeShield[0] = null;
		Shield.listeShield[1] = null;
		Shield.listeShield[2] = null;
	}
	
	private void resetBullets() {
		for(Bullet b: Bullet.bulletArray) {
			if(b != null) {
				Bullet.bulletArray[b.numero] = null;
			}
		}
		
		for(SentinelBullet b: SentinelBullet.S_bulletArray) {
			if(b != null) {
				SentinelBullet.S_bulletArray[b.numero] = null;
			}
		}
	}
	
	private void displayGameOver(Graphics g) {
		g.drawImage(gameOver, 0, 0, 1700, 800, null);
		displayScore(g);
	}
	
	private void displayScore(Graphics g) {
		g.setFont(myFont);
		
		g.setColor(Color.YELLOW);
		Upgrades.drawCenteredString(g, "Timer atteint: " + timer + " secondes", new Rectangle(0, gameOver.getHeight()/3, gameOver.getWidth(), gameOver.getHeight()), g.getFont());
		
		g.setColor(Color.MAGENTA);
		Upgrades.drawCenteredString(g, "Nombre de kills: " + KillCount.getKillCount(), new Rectangle(0, gameOver.getHeight()/3 + 70, gameOver.getWidth(), gameOver.getHeight()), g.getFont());
		
	}
	
	public void playDeathSound() {
		audioPlayer = game.getAudioPlayer();
		audioPlayer.stopSong();
		audioPlayer.playEffect(AudioPlayer.DEATH_SOUND);
		// audioPlayer.playSong(AudioPlayer.DEATH_THEME); // Mettre un délai ou aucune musique tout court
	}
	
	public void gameOverChoice() {
		if(gs != GameState.GAMEOVER) return;
				
		PointerInfo mouseLoc = MouseInfo.getPointerInfo();
		Point mouseLoca = mouseLoc.getLocation();
		SwingUtilities.convertPointFromScreen(mouseLoca, this);
		
		double mouseX = mouseLoca.getX();
		double mouseY = mouseLoca.getY();
		
		Rectangle boutonRetry = new Rectangle(440, 505, 728-440, 580-505);
		Rectangle boutonLeave = new Rectangle(993, 507, 1294-993, 580-507);
		
		if(mouseX >= boutonRetry.x && mouseX <= boutonRetry.x + boutonRetry.width && mouseY >= boutonRetry.y && mouseY <= boutonRetry.y + boutonRetry.height) {
			resetGame();
		}
		
		if(mouseX >= boutonLeave.x && mouseX <= boutonLeave.x + boutonLeave.width && mouseY >= boutonLeave.y && mouseY <= boutonLeave.y + boutonLeave.height) {
			System.exit(0);;
		}
		
	}
	
	// _______________________________________________________________________
	// Menu de début
	
	private void displayMenu(Graphics g) {
		g.drawImage(menuImg, 0, 0, 1700, 800, null);
	}
	
	public void menuChoice() {
		if(gs != GameState.MENU) return;
		
		PointerInfo mouseLoc = MouseInfo.getPointerInfo();
		Point mouseLoca = mouseLoc.getLocation();
		SwingUtilities.convertPointFromScreen(mouseLoca, this);
		
		double mouseX = mouseLoca.getX();
		double mouseY = mouseLoca.getY();
		
		Rectangle boutonPlay = new Rectangle(94, 239, 435-94, 334-239);
		
		if(mouseX >= boutonPlay.x && mouseX <= boutonPlay.x + boutonPlay.width && mouseY >= boutonPlay.y && mouseY <= boutonPlay.y + boutonPlay.height) {
			game.getAudioPlayer().playSong(AudioPlayer.MUSIC);
			gs = GameState.PLAY;
		}
	}
	
}



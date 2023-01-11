package upgrades;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import entities.Bullet;
import entities.Player;
import entities.SentinelBullet;

import java.util.Random;

import game.GameScreen;

public class Sentinel{

	private int x, y;
	private final int width = 80, height = 80;
	private String name;
	private Rectangle hitbox;
	private Random r = new Random();
	
	public BufferedImage[] idleAnimation;
	private BufferedImage sentinelImg;
	public int animationTick;
	public int animationIndex;
	public int animationSpeed = 25;
	
	
	public static Map<Sentinel, Boolean> listeSentinels = new HashMap<Sentinel, Boolean>(); // Boolean pour savoir si elle a été unlock par l'upgrade ou pas
	
	
	
	public double timerSentinel; 
	
	public Sentinel(String name) {
		this.name = name;
		x = r.nextInt(GameScreen.WIDTH-width);
		y = r.nextInt(GameScreen.HEIGHT-height);
		hitbox = new Rectangle(x, y, width, height);
		
		importImgSentinel();
		loadAnimations();
		timerSentinel = System.nanoTime();
		
	}
	
	public static void initSentinels() {
		listeSentinels.put(new Sentinel("Sentinel_1"), false);
		listeSentinels.put(new Sentinel("Sentinel_2"), false);
		listeSentinels.put(new Sentinel("Sentinel_3"), false);
		listeSentinels.put(new Sentinel("Sentinel_4"), false);
		listeSentinels.put(new Sentinel("Sentinel_5"), false);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getW() {
		return width;
	}
	
	public int getH() {
		return height;
	}
	
	public Rectangle getHitbox() {
		return hitbox;
	}
	
	public void updateHitbox() {
		hitbox.x = x+20;
		hitbox.y = y+20;
		hitbox.width = width-width/2;
		hitbox.height = height-height/2;
	}
	
	public void updateSentinel() {
		switchLocation();
		updateHitbox();
		updateAnimationTick();
	}
	
	public void switchLocation() {
		// Toutes les n secondes, cette fonction aura comme rôle de bouger la sentinelle
		if(GameScreen.timerInMillis - timerSentinel <= 6000000000.0) return;
		
		timerSentinel = System.nanoTime();
		
		x = r.nextInt(GameScreen.WIDTH-width);
		y = r.nextInt(GameScreen.HEIGHT-height);
		
	}
	
	public static void updateSentinels() {
		// A utiliser dans GameScreen
		for (Entry<Sentinel, Boolean> s: Sentinel.listeSentinels.entrySet()) {
	
			if(s.getValue()) {
				Sentinel tmp = s.getKey();
				tmp.updateSentinel();
				Sentinel.listeSentinels.put(tmp, s.getValue());
			}
		}
	}
	
	public static void displaySentinels(Graphics g) {
		// A utiliser dans GameScreen
		for (Entry<Sentinel, Boolean> s: Sentinel.listeSentinels.entrySet()) {

			Sentinel tmp = s.getKey();
			if(s.getValue()) {
				g.drawImage(tmp.idleAnimation[tmp.animationIndex], tmp.getX(), tmp.getY(), tmp.getW(), tmp.getH() , null);
			}
		}	
	}
	
	public void shoot(Sentinel sentinelle) {
		
		Rectangle hbp = sentinelle.getHitbox();
		// shoot
		int i = SentinelBullet.S_bulletIterateur;
		if(i >= 2800) {
			i = 0;
			SentinelBullet.S_bulletIterateur = 0;
		}
		
		spawnBullet(hbp.x + hbp.width/2, hbp.y, hbp.x + hbp.width/2, 0, i);
		spawnBullet(hbp.x + hbp.width, hbp.y, hbp.x + hbp.width + 1, hbp.y-1, i+1);
		spawnBullet(hbp.x, hbp.y, hbp.x-1, hbp.y-1, i+2);
		
		spawnBullet(hbp.x, hbp.y + hbp.height/2, hbp.x-1, hbp.y + hbp.height/2, i+3);
		spawnBullet(hbp.x + hbp.width, hbp.y + hbp.height/2, hbp.x+hbp.width+1, hbp.y + hbp.height/2, i+4);
		
		spawnBullet(hbp.x, hbp.y + hbp.height, hbp.x-1, hbp.y + hbp.height +1, i+5);
		spawnBullet(hbp.x + hbp.width/2, hbp.y + hbp.height, hbp.x + hbp.width/2, hbp.y + hbp.height + 1, i+6);
		spawnBullet(hbp.x + hbp.width, hbp.y + hbp.height, hbp.x + hbp.width + 1, hbp.y + hbp.height + 1, i+7);
		
		GameScreen.last_shot_time_sentinelle = System.nanoTime();
	}
	
	
	
	private void spawnBullet(float x, float y, int xD, int yD, int i) {
		SentinelBullet bullet = new SentinelBullet(x, y, 5, 5, xD, yD, i, this);

		SentinelBullet.S_bulletArray[i] = bullet;
		SentinelBullet.S_bulletIterateur++;
	}
	
	
	public void updateAnimationTick() {
		animationTick++;
		if(animationTick >= animationSpeed) {
			animationTick = 0;
			animationIndex++;
			if(animationIndex >= idleAnimation.length) {
				animationIndex = 0;
			}
		}
	}
	
	public void loadAnimations() {
		idleAnimation = new BufferedImage[4];
		
		for(int i = 0; i < idleAnimation.length; i++) {
			
			idleAnimation[i] = sentinelImg.getSubimage(0, i*128, 128, 128);
			
		}
	}
	
	
	public void importImgSentinel() {
		InputStream is = getClass().getResourceAsStream("/sentinel.png");
		// on crée un inputstream depuis l'img
		
		try {
			sentinelImg = ImageIO.read(is);
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
	
	
	
	
	
	
	
}

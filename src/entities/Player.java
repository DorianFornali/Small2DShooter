package entities;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map.Entry;

import game.GameScreen;
import upgrades.Sentinel;
import upgrades.Shield;
import upgrades.Upgrades;

public class Player extends Entity{
	
	
	
	public static boolean left = false, right = false, up = false, down = false;
	private float speed = 3.5f;
	
	public static int width, height;
	public static boolean shooting = false;
	
	private int nbBalles = 1; // Le nombre de balles tirées a chaque hit, va dépendre du niveau d'upgrade du MultiShot
	
	public Player(float x, float y, int w, int h) {
		super(x, y, w, h, 5);
		Player.width = w;
		Player.height = h;
		initHitbox(x, y, w, h);
		Upgrades.initUpgrades();
		
	}

	
	public void updatePlayer() {
		deplacement_joueur();
		updateHitbox();
		updateUpgrades();
	}
	
	public void reduceHP(int i) {
		hp -= i;
	}
	
	public void setHP(int x) {
		hp = x;
	}
	
	public void setCoordPlayer(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void deplacement_joueur() {
		
					
		if(right && x <= GameScreen.WIDTH-width) {
			x += speed;
		}
		if(left && x >= 0) {
			x -= speed;
		}
		if(down && y <= GameScreen.HEIGHT-height) {
			y += speed;
		}
		if(up && y >= 0) {
			y -= speed;
		}
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public int getHP() {
		return hp;
	}
	
	public int getNbBalles() {
		return nbBalles;
	}
	
	// Je réécris ces fonctions de Entity dans Player pour une meilleur hitbox, c'est fait a l'arrache et ne marche plus du tout si je change la taille de l'écran
	
	protected void initHitbox(float x, float y, float w, float h) {
		hitbox = new Rectangle2D.Float(x, y, width-18, height-23);
	}
	
	protected void updateHitbox() {
		hitbox.x = (int) x+8;
		hitbox.y = (int) y+10;
	}
	
	
	public void updateUpgrades() {
		switch(Upgrades.getUpgradeLevel(Upgrades.upgrade1)) {
		// On update le délai de shoot et vitesse de la balle
			case 0:
				GameScreen.shoot_delay = 500000000;
				break;
			
			case 1:
				GameScreen.shoot_delay = 400000000;
				Bullet.bulletSpeed = 5.5f;
				break;
			case 2:
				GameScreen.shoot_delay = 300000000;
				Bullet.bulletSpeed = 6.0f;
				break;
			case 3:
				GameScreen.shoot_delay = 200000000;
				Bullet.bulletSpeed = 6.5f;
				break;
			case 4:
				GameScreen.shoot_delay = 100000000;
				Bullet.bulletSpeed = 7.0;
				break;
			case 5:
				GameScreen.shoot_delay = 50000000;
				Bullet.bulletSpeed = 7.5f;
				break;
			
		}
		
		switch(Upgrades.getUpgradeLevel(Upgrades.upgrade2)) {
			case 0:
			nbBalles = 1;
			break;
			case 1:
				nbBalles = 2;
				break;
			case 2:
				nbBalles = 3;
				break;
			case 3:
				nbBalles = 4;
				break;
			case 4:
				nbBalles = 5;
				break;
			case 5:
				nbBalles = 6;
				break;
		}
		
		switch(Upgrades.getUpgradeLevel(Upgrades.upgrade3)) {
		// On up les dégats
			case 0:
				Bullet.damage = 50;
				Shield.damage = 50;
				SentinelBullet.damage = 100;
				break;
			case 1:
				Bullet.damage = 75;
				Shield.damage = 75;
				SentinelBullet.damage = 125;
				break;
			case 2:
				Bullet.damage = 100;
				Shield.damage = 100;
				SentinelBullet.damage = 150;
				break;
			case 3:
				Bullet.damage = 125;
				Shield.damage = 125;
				SentinelBullet.damage = 175;
				break;
			case 4:
				Bullet.damage = 150;
				Shield.damage = 150;
				SentinelBullet.damage = 200;
				break;
			case 5:
				Bullet.damage = 200;
				Shield.damage = 200;
				SentinelBullet.damage = 250;
				break;
		}
		
		switch(Upgrades.getUpgradeLevel(Upgrades.upgrade4)) {
			// On up la speed de la rotation des shield, le nbr de shield est incrémenté directement lors du choix du Roll dans GameScreen
			case 0:
			case 1:
				Shield.rotationSpeed = 0.02;
				break;
			case 2:
				Shield.rotationSpeed = 0.04;
				break;
			case 3:
				Shield.rotationSpeed = 0.06;
				break;
			case 4:
				Shield.rotationSpeed = 0.08;
				break;
			case 5:
				Shield.rotationSpeed = 0.1;
				break;

		}
		
		
	}
	
}

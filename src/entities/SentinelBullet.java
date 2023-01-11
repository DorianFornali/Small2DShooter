package entities;

import java.awt.Color;
import java.awt.Graphics;

import upgrades.Sentinel;

public class SentinelBullet extends Entity{


	public double xVelocity;
	public double yVelocity;
	
	public static double S_bulletSpeed = 5.0;
	
	public static int damage = 50;
	
	public static SentinelBullet[] S_bulletArray = new SentinelBullet[3000];
	public static int S_bulletIterateur = 0;

	public int numero;
	
	public Sentinel caster;
	
	public SentinelBullet(float x, float y, int w, int h, int xD, int yD, int numero, Sentinel caster) {
		super(x, y, w, h, -1);
		this.numero = numero;
		this.caster = caster;
		initHitbox(x,y,w,h);
		
		calculDirection(x, y, xD, yD);
	}
	
	private void calculDirection(float x, float y, int xDir, int yDir) {
		double vectorX = xDir - x;
		double vectorY = yDir - y;
		
		double length = Math.sqrt((vectorX * vectorX) + (vectorY * vectorY));
		
		double unitVectorX = vectorX / length;
		double unitVectorY = vectorY / length;
		
		this.xVelocity = unitVectorX * S_bulletSpeed;
		this.yVelocity = unitVectorY * S_bulletSpeed;
		
	}
	
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getW() {
		return width;
	}
	
	public float getH() {
		return height;
	}
	
	public static void deplacement() {
		for(SentinelBullet b: S_bulletArray) {
			if(b != null) {
				b.x += b.xVelocity;
				b.y += b.yVelocity;
			}
		}
	}
	
	
	public void updateHitbox() {
		hitbox.x = (int) x;
		hitbox.y = (int) y;
	}
	
	public static void displayBullets(Graphics g) {
		
		for(SentinelBullet b: S_bulletArray) {
			if(b != null) {
				b.updateHitbox();
				// b.drawHitbox(g);
				
				g.setColor(Color.GREEN);
				g.fillRect((int)b.x, (int)b.y, 5, 5);
				
				g.setColor(Color.YELLOW);
				g.drawRect((int)b.x, (int)b.y, 5, 5);
			}
		}
		
	}
	
	public void dealDamage(Entity e) {
		e.receiveDamage(damage);
	}
	
	
}



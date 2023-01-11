package entities;

import java.awt.Color;
import java.awt.Graphics;

public class Bullet extends Entity {
	
	public double xVelocity;
	public double yVelocity;
	
	public static double bulletSpeed = 5.0;
	
	public static int damage = 50;
	
	public static Bullet[] bulletArray = new Bullet[1000];
	public static int bulletIterateur = 0;

	public int numero;

	public Bullet(float x, float y, int w, int h, int xDir, int yDir, int numero) {
		super(x, y, w, h,-1);
		this.numero = numero;
		initHitbox(x,y,w,h);
		
		calculDirection(x, y, xDir, yDir);
	}
	
	private void calculDirection(float x, float y, int xDir, int yDir) {
		double vectorX = xDir - x;
		double vectorY = yDir - y;
		
		double length = Math.sqrt((vectorX * vectorX) + (vectorY * vectorY));
		
		double unitVectorX = vectorX / length;
		double unitVectorY = vectorY / length;
		
		this.xVelocity = unitVectorX * bulletSpeed;
		this.yVelocity = unitVectorY * bulletSpeed;
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
		for(Bullet b: bulletArray) {
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
		
		for(Bullet b: bulletArray) {
			if(b != null) {
				b.updateHitbox();
				// b.drawHitbox(g);
				
				g.setColor(Color.RED);
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

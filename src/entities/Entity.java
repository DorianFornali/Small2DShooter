package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import game.GameScreen;

public abstract class Entity {
	protected float x,y ;
	protected int width, height;
	protected Rectangle2D.Float hitbox;
	protected int hp;
	public Entity(float x, float y, int w, int h, int hp) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.hp = hp;
	}
	
	public void drawHitbox(Graphics g) {
		// Debug function
		g.setColor(Color.YELLOW);
		g.drawRect((int)hitbox.x, (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
	}
	
	protected void initHitbox(float x, float y, float w, float h) {
		hitbox = new Rectangle2D.Float(x, y, width, height);
	}
	
	protected void updateHitbox() {
		hitbox.x = (int) x;
		hitbox.y = (int) y;
	}
	
	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}
	
	public void receiveDamage(int x) {
		this.hp -= x;
	}
	
	public int getHP() {
		return hp;
	}
	
	public boolean isDead() {
		return hp<=0;
	}
	
	
}

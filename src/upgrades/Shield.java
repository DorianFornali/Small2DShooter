package upgrades;

import java.awt.Color;
import java.awt.Graphics;

import entities.Entity;
import entities.Player;

public class Shield {
	
	public static int[] listeAngles = {0, 90, 180};
	public static Shield[] listeShield = new Shield[3];
	public static int iterateurShield = 0;
	public static double rotationSpeed = 0.02;
	public static int damage = 50;
	
	public static int rayon = 150;
	
	public static int width = 10, height = 10;
	
	private double angle;
	
	public Shield(int angle) {
		this.angle = angle;
	}
	
	public void updateAngle() {
		angle = (angle + rotationSpeed)%360;
	}
	
	public void setAngle(int angle) {
		this.angle = angle;
	}
	
	public double getAngle() {
		return angle;
	}
	

	
	public double calculateXposition() {
		double tmp = angle-90 * Math.PI/180;
		
		return Shield.rayon * Math.cos(tmp);
	}
	
	public double calculateYposition() {
		double tmp = angle-90 * Math.PI/180;
		
		return Shield.rayon * Math.sin(tmp);
	}
	
	
	public void displayShield(Graphics g, float xP, float yP) {
		
		g.setColor(Color.GREEN);;
		g.fillRect((int)calculateXposition() + (int)xP -5 + Player.width/2, (int)calculateYposition() - 5 +(int)yP + Player.height/2, width, height);
		
		
	}
	
	
	
	public static void addShield() {
		if(iterateurShield >= 3) return;
		
		Shield.listeShield[Shield.iterateurShield] = new Shield(listeAngles[iterateurShield]);
		iterateurShield++;
		

		
		for(int i = 0; i < Shield.listeShield.length; i++) {
			// A chaque fois qu'on ajoute un shield, on réinitialise les positions de chacun des shield, car ajouter le prochain en fonction du dernier laissait place a qql soucis
			if(listeShield[i] != null) {
				listeShield[i].setAngle(listeAngles[i]);
			}
		}
		
		
	}
	
	public void dealDamage(Entity e) {
		e.receiveDamage(damage);
	}

}

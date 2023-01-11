package gameStates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import game.GameScreen;

public abstract class KillCount {

	private static int xOffset = 50, yOffset = 45;
	private static int nbKills = 0;
	
	public static Map<Integer, Boolean> paliersKill = new HashMap<Integer, Boolean>();
	
	
	public static void initKillsPaliers() {
		// Ma map paliersKill contient tous les paliers, 10 20 30 etc ainsi qu'un boolean spécifiant si le roll Correspondant a été effectué
		// ..  Est mis a true lors du castRoll de Upgrades
		
		// 24 upgrades, impossible de tout avoir maxé

		paliersKill.put(5, false);
		paliersKill.put(10, false);
		
		paliersKill.put(20, false);
		paliersKill.put(30, false);
		paliersKill.put(40, false);
		paliersKill.put(50, false);
		paliersKill.put(70, false);
		
		paliersKill.put(90, false);
		paliersKill.put(110, false);
		paliersKill.put(150, false);
		paliersKill.put(190, false);
		paliersKill.put(240, false);
		
		paliersKill.put(280, false);
		paliersKill.put(320, false);
		paliersKill.put(380, false);
		paliersKill.put(420, false);
		paliersKill.put(460, false);
		
		paliersKill.put(520, false);
		paliersKill.put(600, false);
		paliersKill.put(700, false);
		paliersKill.put(800, false);
		paliersKill.put(900, false);
		
		paliersKill.put(1200, false);
		paliersKill.put(1500, false);
		

	}
	
	public static void setKillCount(int x) {
		nbKills = x;
	}
	
	
	public static int getKillCount() {
		return nbKills;
	}
	
	public static void increaseKillCount() {
		KillCount.nbKills++;
	}
	
	public static void displayKillCount(Graphics g) {
		
		g.setColor(Color.CYAN);
		
		int x = GameScreen.WIDTH/2 - xOffset;
		int y = 0 + yOffset;
		
		g.drawString("KILL: " + KillCount.getKillCount(), x, y);
	}
	
	
	
}

package upgrades;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import game.GameScreen;
import gameStates.GameState;
import gameStates.KillCount;

public abstract class Upgrades {

	private static Random r = new Random();
	
	// Les upgrades joueur
	public static String upgrade1 = "Speedy";
	public static String upgrade2 = "Multishot";
	public static String upgrade3 = "Damage++";
	public static String upgrade4 = "Shield";
	public static String upgrade5 = "Sentinel";
	
	
	public static String descriptionUpgrade1 = "Vous tirez plus vite, et vos balles se font plus rapides";
	public static String descriptionUpgrade2 = "C'est le bruit de mes ba-ba-ba-balles";
	public static String descriptionUpgrade3_1 = "Booste vos dégats, ceux du shield";
	public static String descriptionUpgrade3_2 = "ainsi que ceux des sentinelles";
	public static String descriptionUpgrade4 = "Des petits carrés verts qui tournent autour de vous, wow.";
	public static String descriptionUpgrade5_1 = "Une sentinelle apparait aléatoirement";
	public static String descriptionUpgrade5_2 = "essayez de tirer dessus pour voir ...";
	
	public static final int nombreUpgrades = 5;
	public static final int LEVEL_MAX_UPGRADE = 5;
	
	public static int[] currentRollList = {r.nextInt(nombreUpgrades)+1, r.nextInt(nombreUpgrades)+1, r.nextInt(nombreUpgrades)+1};
	
	public static Map<String, Integer> UPGRADE_LIST = new HashMap<String, Integer>();
	
	public static boolean allUpgradesMaxed = false;
	
	public static void initUpgrades() {
		UPGRADE_LIST.put(upgrade1, 0);
		UPGRADE_LIST.put(upgrade2, 0);
		UPGRADE_LIST.put(upgrade3, 0);
		UPGRADE_LIST.put(upgrade4, 0);
		UPGRADE_LIST.put(upgrade5, 0);
	}
	
	
	public static void upgradeSPEEDY() {
		int currentLevel = UPGRADE_LIST.get(upgrade1);
		if(currentLevel < 5) {
			UPGRADE_LIST.put(upgrade1, currentLevel + 1);
		}
	}
	
	public static void upgradeTIR_MULTIPLE() {
		int currentLevel = UPGRADE_LIST.get(upgrade2);
		if(currentLevel < 5) {
			UPGRADE_LIST.put(upgrade2, currentLevel + 1);
		}
	}
	
	public static void upgradeDAMAGE_INCREASE() {
		int currentLevel = UPGRADE_LIST.get(upgrade3);
		if(currentLevel < 5) {
			UPGRADE_LIST.put(upgrade3, currentLevel + 1);
		}
	}
	
	public static void upgradeShield() {
		int currentLevel = UPGRADE_LIST.get(upgrade4);
		if(currentLevel < 5) {
			UPGRADE_LIST.put(upgrade4, currentLevel + 1);
		}
	}
	
	public static void upgradeSentinel() {
		int currentLevel = UPGRADE_LIST.get(upgrade5);
		if(currentLevel < 5) {
			UPGRADE_LIST.put(upgrade5, currentLevel + 1);
		}
	}
	
	public static int getUpgradeLevel(String name) {
		return UPGRADE_LIST.get(name);
	}
	
	public static void displayRoll(Graphics g) {
		float xOffset = 180;
		
		// On peut avoir 3 fois la meme upgrade, potentiellement modifier plus tard

		for(int i = 1; i <= currentRollList.length; i++) {
			switch(currentRollList[i-1]) {
			
			case 1:
				displaySingleRoll(g, i*(GameScreen.WIDTH/4)-xOffset, 100f, upgrade1);
				break;
				
			case 2:		
				displaySingleRoll(g, i*(GameScreen.WIDTH/4)-xOffset, 100f, upgrade2);
				break;
			
			case 3:
				displaySingleRoll(g, i*(GameScreen.WIDTH/4)-xOffset, 100f, upgrade3);
				break;
				
			case 4:
				displaySingleRoll(g, i*(GameScreen.WIDTH/4)-xOffset, 100f, upgrade4);
				break;
				
			case 5:
				displaySingleRoll(g, i*(GameScreen.WIDTH/4)-xOffset, 100f, upgrade5);
				break;
			}
		}
		
		// Ne pas oublier de reset le currentRollList une fois l'upgradeTime terminé
		
	}
	
	private static void displaySingleRoll(Graphics g, float x, float y, String s) {
		// On pourra upload des images selon quelle upgrade et les afficher
		g.setColor(Color.CYAN);
		
		Rectangle2D.Float currentUpgrade = new Rectangle2D.Float(x, y, GameScreen.WIDTH/5, GameScreen.HEIGHT*0.7f);
		g.fillRect((int)currentUpgrade.x, (int)currentUpgrade.y, (int)currentUpgrade.width, (int)currentUpgrade.height);
		g.setColor(Color.BLACK);
		g.drawRect((int)currentUpgrade.x, (int)currentUpgrade.y, (int)currentUpgrade.width, (int)currentUpgrade.height);

		
		drawCenteredString(g, s, new Rectangle((int)x, (int)y, GameScreen.WIDTH/5, (int)(GameScreen.HEIGHT*0.3f)), g.getFont());
		
		int upLevel = getUpgradeLevel(s);
		if(upLevel == 5)
			drawCenteredString(g, "Niveau actuel: MAX", new Rectangle((int)x, (int)y, GameScreen.WIDTH/5, (int)(GameScreen.HEIGHT*0.4f)), g.getFont());
		else drawCenteredString(g, "Niveau actuel: " + upLevel, new Rectangle((int)x, (int)y, GameScreen.WIDTH/5, (int)(GameScreen.HEIGHT*0.4f)), g.getFont());
		
		
		switch(s) {
		case "Speedy":
			drawCenteredString(g, descriptionUpgrade1, new Rectangle((int)x, (int)y, GameScreen.WIDTH/5, (int)(GameScreen.HEIGHT*0.6f)), g.getFont());
			break;
		case "Multishot":
			drawCenteredString(g, descriptionUpgrade2, new Rectangle((int)x, (int)y, GameScreen.WIDTH/5, (int)(GameScreen.HEIGHT*0.6f)), g.getFont());
			break;
		case "Damage++":
			drawCenteredString(g, descriptionUpgrade3_1, new Rectangle((int)x, (int)y, GameScreen.WIDTH/5, (int)(GameScreen.HEIGHT*0.6f)), g.getFont());
			drawCenteredString(g, descriptionUpgrade3_2, new Rectangle((int)x, (int)y, GameScreen.WIDTH/5, (int)(GameScreen.HEIGHT*0.6f)+30), g.getFont());
			break;
		case "Shield":
			drawCenteredString(g, descriptionUpgrade4, new Rectangle((int)x, (int)y, GameScreen.WIDTH/5, (int)(GameScreen.HEIGHT*0.6f)), g.getFont());
			break;
		case "Sentinel":
			drawCenteredString(g, descriptionUpgrade5_1, new Rectangle((int)x, (int)y, GameScreen.WIDTH/5, (int)(GameScreen.HEIGHT*0.6f)), g.getFont());
			drawCenteredString(g, descriptionUpgrade5_2, new Rectangle((int)x, (int)y, GameScreen.WIDTH/5, (int)(GameScreen.HEIGHT*0.6f)+30), g.getFont());
			break;
			
			
		}
		
	}
	
	public static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    g.drawString(text, x, y);
	}
	
	
	public static void tryCastUpgrade() {
		// Fonction appellé pour tenter de débuter débuter un UpgradeTime si un palier a été atteint
		
		for(Entry<Integer, Boolean> element : KillCount.paliersKill.entrySet()) {
			
			if(element.getValue() == false && KillCount.getKillCount() >= element.getKey()) {
				// Si on a atteint un palier et qu'il est pas traité, on le met a true et on lance une upgrade
				KillCount.paliersKill.put(element.getKey(), true);
				GameScreen.gs = GameState.UPGRADE_TIME;
				return;
			}		
		}	
	}
	

	
	
}

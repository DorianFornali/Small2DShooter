package inputs;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import entities.Bullet;
import entities.Player;
import game.GameScreen;
import upgrades.Sentinel;
import upgrades.Upgrades;


public class KeyboardInputs implements KeyListener {
	private GameScreen gameScreen;
	
	
	public KeyboardInputs(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		switch(e.getKeyCode()) {
		// e.getKeyCode renvoie le code du bouton correspondant
		
		case KeyEvent.VK_Z: // Bouton Z
			Player.up = true;
			break;
			
		case KeyEvent.VK_S:
			Player.down = true;
			break;
			
		case KeyEvent.VK_Q:
			Player.left = true;
			break;
			
		case KeyEvent.VK_D:
			Player.right = true;
			break;
			
		case KeyEvent.VK_SPACE:
			Player.shooting = true;
			break;
			
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
						
		case KeyEvent.VK_Y:
			for(boolean b: Sentinel.listeSentinels.values()) {
				System.out.println(b);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		switch(e.getKeyCode()) {
		// e.getKeyCode renvoie le code du bouton correspondant
		
		case KeyEvent.VK_Z: // Bouton Z
			Player.up = false;
			break;
			
		case KeyEvent.VK_S:
			Player.down = false;
			break;
			
		case KeyEvent.VK_Q:
			Player.left = false;
			break;
			
		case KeyEvent.VK_D:
			Player.right = false;
			break;
			
		case KeyEvent.VK_SPACE:
			Player.shooting = false;
			break;
			
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		}
		
	}
	

}

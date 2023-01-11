package inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;

import game.GameScreen;


public class MouseInputs implements MouseListener, MouseMotionListener {
	private GameScreen gameScreen;
	
	public MouseInputs(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//gameScreen.setXY(e.getX(), e.getY());
		// getX() et getY() renvoie la position XY de la souris
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		gameScreen.chooseRoll();
		gameScreen.gameOverChoice();
		gameScreen.menuChoice();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}

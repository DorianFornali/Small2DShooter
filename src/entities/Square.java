package entities;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Square extends Entity {
	public BufferedImage[] idleAnimation;
	private BufferedImage squareImg;
	public int animationTick;
	public int animationIndex;
	public int animationSpeed = 25;
	
	private float velocity = 0.3f;
	
	public int numero;
	
	public static Square[] tabSquare = new Square[100];
	public static int iterateurTab = 0;

	public Square(float x, float y, int w, int h, int hp) {
		super(x, y, w, h, hp);
		initHitbox(x,y,w,h);
		importImgSquare();
		loadAnimations();
	}
	
	public void deplacementSquare(float xPlayer, float yPlayer) {
		x += getXmoving(xPlayer, yPlayer);
		y += getYmoving(xPlayer, yPlayer);
	}
	
	public void updateHitbox() {
		hitbox.x = (int) x;
		hitbox.y = (int) y;
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
			
			idleAnimation[i] = squareImg.getSubimage(0, i*128, 128, 128);
			
		}
	}
	
	
	public void importImgSquare() {
		InputStream is = getClass().getResourceAsStream("/square.png");
		// on crée un inputstream depuis l'img
		
		try {
			squareImg = ImageIO.read(is);
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
	
	
	public float getXmoving(float xPlayer, float yPlayer) {

		xPlayer += Player.width/2 - width/2;
		yPlayer += Player.height/2 - height/2;
		
		
		double vectorX = xPlayer - x;
		double vectorY = yPlayer - y;
		
		double length = Math.sqrt((vectorX * vectorX) + (vectorY * vectorY));
		
		double unitVectorX = vectorX / length;
		
		return (float) (unitVectorX * velocity);

	}
	
	public float getYmoving(float xPlayer, float yPlayer) {
		xPlayer += Player.width/2 - width/2;
		yPlayer += Player.height/2 - height/2;
		
		
		double vectorX = xPlayer - x;
		double vectorY = yPlayer - y;
		
		double length = Math.sqrt((vectorX * vectorX) + (vectorY * vectorY));
		
		double unitVectorY = vectorY / length;
		
		return (float) (unitVectorY * velocity);
		
		
	}
	
	
}

package entities;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import game.GameScreen;

public class Diamond extends Entity{
	public BufferedImage[] idleAnimation, idleAnimationBroken;
	private BufferedImage diaImg, diaBrokenImg;
	public int animationTick;
	public int animationIndex;
	public int animationSpeed = 25;
	
	private float velocity = 1.2f;
	
	public static int hp = 100; // Variable globale des hp des diamond, va augmenter en fct du spawnState
	
	public int numero;
	
	public boolean displayed = false;

	public static Diamond[] tabDiamond = new Diamond[1000];
	public static int iterateurTab = 0;
	
	public Diamond(float x, float y, int w, int h, int hp) {
		super(x,y,w,h,hp);
		initHitbox(x,y,w,h);
	}
	
	
	public void deplacementDiamond(float xPlayer, float yPlayer) {
		x += getXmoving(xPlayer, yPlayer);
		y += getYmoving(xPlayer, yPlayer);
	}
	
	public void updateHitbox() {
		hitbox.x = (int) x+5;
		hitbox.y = (int) y;
		hitbox.width = width - 13;
		
		// J'arrange ma hitbox a la main comme un bon bourrin
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
		idleAnimation = new BufferedImage[3];
		idleAnimationBroken = new BufferedImage[3];
		
		for(int i = 0; i < idleAnimation.length; i++) {
			idleAnimation[i] = diaImg.getSubimage(0, i*128, 128, 128);
			idleAnimationBroken[i] = diaBrokenImg.getSubimage(0, i*128, 128, 128);
		}
		
	}
	
	
	public void importImgDiamond() {
		InputStream is = getClass().getResourceAsStream("/enemy1.png");
		InputStream is2 = getClass().getResourceAsStream("/enemy1broken.png");
		// on crée un inputstream depuis l'img
		
		try {
			diaImg = ImageIO.read(is);
			diaBrokenImg = ImageIO.read(is2);
			// on lit l'inputstream sur la var img
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				is2.close();
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

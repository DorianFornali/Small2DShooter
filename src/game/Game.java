package game;

import audio.AudioPlayer;

public class Game implements Runnable {
	
	private Window gameWindow;
	private GameScreen gameScreen;
	private Thread gameLoop;
	private final int FPS_SET = 144, UPS_SET = 200;
	
	private AudioPlayer audioplayer;
	
	public Game() {
		
		gameScreen = new GameScreen(this);
		gameWindow = new Window(gameScreen);
		audioplayer = new AudioPlayer();
		// On crée ecran puis la fenetre a laquelle on donne l'écran
		
		gameScreen.requestFocus(); // Sert a ce que les boutons soient écoutés sur Game
		
		startGameLoop();
		
		
	}
	
	private void startGameLoop() {
		// Initialisation de la gameLoop
		gameLoop = new Thread(this);
		gameLoop.start();
	}

	@Override
	public void run() {
		// Game loop, executé sur un thread différent
		
		int frames = 0; long lastCheck = 0;
		double timePerFrame = 1000000000.0 / FPS_SET; // Temps par frame (en nano secondes)
		double timePerUpdate = 1000000000.0 / UPS_SET; // Délai entre les update

		
		long previousTime = System.nanoTime();
		int updates = 0;
		double deltaU = 0;
		double deltaF = 0;
		
		while(true) {
			long currentTime = System.nanoTime();
			
			
			deltaU += (currentTime - previousTime) / timePerUpdate;
			deltaF += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;
			
			if(deltaU >= 1) {
				updates++;
				deltaU--;
			}
			
			
			if(deltaF >= 1) {
				
				
				update();
				gameScreen.repaint();
				
				deltaF--;
				frames++;
			}
			
			
			if(System.currentTimeMillis() - lastCheck >= 1000) {
				lastCheck = System.currentTimeMillis();		
				System.out.println("FPS : " + frames + " UPS : " + updates);
				frames = 0;
				updates = 0;
			}
			
			
		}
		
	}

	private void update() {
		gameScreen.updateGame();
	}
	
	public AudioPlayer getAudioPlayer() {
		return audioplayer;
	}
		
}

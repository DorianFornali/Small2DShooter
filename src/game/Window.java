package game;
import javax.swing.JFrame;
// On utilie JFrame pour la fenetre Graphique (le cadre)


public class Window {
	private JFrame jframe;
	
	
	public Window(GameScreen gameScreen) {
		
		jframe = new JFrame();
		
			
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// On fais en sorte que fermer la fenêtre met fin au programme
		
		jframe.add(gameScreen);	// On ajoute au cadre le tableau (en gros)	
		jframe.setTitle("Prototype Video game");
		jframe.setResizable(false);
		jframe.pack();
		jframe.setLocationRelativeTo(null);
		jframe.setVisible(true);
		
		
	}
}

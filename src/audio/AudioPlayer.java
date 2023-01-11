package audio;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
	
	// Ces final auront comme valeur leur place dans leurs tableaux respectifs (songs[] et effects[]) 
	public static final int MUSIC = 0;
	public static final int MENU_THEME = 1;
	public static final int DEATH_THEME = 2;
	
	public static final int SHOOTSOUND = 0;
	public static final int DEATH_SOUND = 1;
	
	
	
	private Clip[] songs, effects;
	private int currentSongId;
	private float volume = 0.7f;
	private boolean songMute, effectMute;
	private Random rand = new Random();
	
	public AudioPlayer() {
		loadSongs();
		loadEffects();
		playSong(MENU_THEME);
	}
	
	private void loadSongs() {
		// On charge toutes les musiques (contenues dans songNames)
		String[] songNames = {"musicTest", "menuTheme", "deathTheme"};
		songs =  new Clip[songNames.length];
		for(int i = 0; i< songNames.length; i++) {
			songs[i] = getClip(songNames[i]);
		}
	}
	
	private void loadEffects() {
		// On charge les effets sonores
		String[] effectNames = {"shoot", "deathSound"};
		effects =  new Clip[effectNames.length];
		for(int i = 0; i< effects.length; i++) {
			effects[i] = getClip(effectNames[i]);
		}
		updateEffectsVolume();
	}
	
	private Clip getClip(String name) {
		
		// Cette fonction nous renvoie une "boite" qui va contenir le fichier .wav et qui sera jouable etc etc
		
		URL url = getClass().getResource("/audio/" + name + ".wav");
		AudioInputStream audio;
		
		
		try {
			
			audio = AudioSystem.getAudioInputStream(url);
			Clip c = AudioSystem.getClip();
			c.open(audio);
			
			return c;
			
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {

			e.printStackTrace();
		}
		
		return null;	
	}
	
	public void setVolume(float v) {
		this.volume = v;
		updateSongVolume();
		updateEffectsVolume();
	}
	
	public void stopSong() {
		if(songs[currentSongId].isActive()) {
			songs[currentSongId].stop();
			// On coupe la musique actuelle
		}
	}
	
	public void upgradeTime() {
		// Fonction qui va jouer un son lors d'une upgrade
	}
	
	public void playEffect(int effect) {
		effects[effect].setMicrosecondPosition(0); // On place le "curseur" du clip a 0
		effects[effect].start();
	}
	
	
	public void playSong(int song) {
		stopSong();
		
		currentSongId = song;
		updateSongVolume();
		// On la remplace par la nouvelle dont on update le volume
		
		songs[currentSongId].setMicrosecondPosition(0);
		songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY);
		// Puis on la start (et on la joue en continu accessoirement)
	}
	
	public void muteAllSounds() {
		// Pour tout mute
		this.songMute = !songMute;
		this.effectMute = !effectMute;
		for(Clip c: songs) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(songMute);
		}
		
		for(Clip c: effects) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(songMute);
		}
		
		if(!effectMute) {
			playEffect(SHOOTSOUND);
		}
		
		
	}
	
	private void updateSongVolume() {
		// changer le volume de la chanson actuellement jouée
		FloatControl gainControl = (FloatControl) songs[currentSongId].getControl(FloatControl.Type.MASTER_GAIN);
		float range = gainControl.getMaximum() - gainControl.getMinimum();
		float gain = (range * volume) + gainControl.getMinimum();
		gainControl.setValue(gain);
	}
	
	private void updateEffectsVolume() {
		// changer le volume pour tous les effets sonores 
		for(Clip c: effects) {
			FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			float range = gainControl.getMaximum() - gainControl.getMinimum();
			float gain = (range * volume) + gainControl.getMinimum();
			gainControl.setValue(gain);
		}
	}
	
	
	
	
	
	
	
	
	
	
}

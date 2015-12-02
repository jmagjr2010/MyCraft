/***************************************************************
* file: Sound.java 
* author: Jorge Magana, Jonathan Wong, Michael Ng 
* class: CS 445 – Computer Graphics
* 
* assignment: Quarter Project - Final Checkpoint
* date last modified: 12/1/2015
* 
* purpose: This class is used to play any sounds used in the application.
****************************************************************/
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

	/**
         * Plays audio file of type "WAV".
         */
	public void playSound() {
	    try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("calm2.wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
}

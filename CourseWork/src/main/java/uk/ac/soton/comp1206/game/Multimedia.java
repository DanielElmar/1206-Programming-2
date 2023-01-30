package uk.ac.soton.comp1206.game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to easily play audio an music via a simple file name, simple methods are provided to manges how this is done
 */
public class Multimedia {


    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    private static MediaPlayer musicPlayer;
    private static MediaPlayer audioPlayer;

    /**
     * plays Music on a loop
     * @param fileToPlay String name of the music to play
     */
    public void playMusic(String fileToPlay){
        logger.info("Playing Music: " + fileToPlay);
        try {

            String toPlayFile = Multimedia.class.getResource("/music/" + fileToPlay).toExternalForm();

            Media toPlayMedia = new Media(toPlayFile);
            musicPlayer = new MediaPlayer(toPlayMedia);

            // loop the Music
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            musicPlayer.play();

            } catch (Exception e) { logger.info("play Music Error"); }

    }

    /**
     * plays Audio once
     * @param fileToPlay String name of the audio to play
     */
    public void playAudio(String fileToPlay){
        logger.info("Playing Audio: " + fileToPlay);

        try {
            String toPlayFile = Multimedia.class.getResource("/sounds/" + fileToPlay).toExternalForm();

            Media toPlayMedia = new Media(toPlayFile);
            audioPlayer = new MediaPlayer(toPlayMedia);

            audioPlayer.play();

        } catch (Exception e) { logger.info("Play Audio Error"); }

    }

    /**
     * stops music form playing
     */
    public void stopMusic(){
        musicPlayer.stop();
    }

    /**
     * stops audio form playing
     */
    public void stopAudio(){
        audioPlayer.stop();
    }

    /**
     * provides a method to execute a runnable when the audioplayer finishes playing
     */
    public void audioPlayerSetOnEndOfMedia( Runnable runnable ){
        audioPlayer.setOnEndOfMedia(runnable);
    }
}

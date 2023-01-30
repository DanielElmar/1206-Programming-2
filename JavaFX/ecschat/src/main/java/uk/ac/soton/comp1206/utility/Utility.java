package uk.ac.soton.comp1206.utility;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A utility class for quick and handy static functions
 *
 * We will be adding to this later, but you can add things that are handy here too!
 */
public class Utility {

    private static final Logger logger = LogManager.getLogger(Utility.class);
    private static BooleanProperty audioEnabled = new SimpleBooleanProperty(true);

    public static void setAudioEnabled(boolean enabled){
        logger.info("Setting Audio to " + enabled);
        audioEnabled.set(enabled);
    }

    public static boolean getAudioEnabled(){
        logger.info("Audio Boolean Got");
        return audioEnabled.get();
    }

    public static BooleanProperty audioEnabledProperty(){return audioEnabled;}

    public static void playAudio(String file){
        // Add Media

        if (audioEnabled.get()) {
            try {

                logger.info("/" + file);
                String toPlayFile = Utility.class.getResource("/" + file).toExternalForm();
                logger.info("Playing Audio " + file);

                Media toPlayMedia = new Media(toPlayFile);
                MediaPlayer toPlay = new MediaPlayer(toPlayMedia);

                toPlay.play();
            } catch (Exception e) {
                audioEnabled.set(false);
                logger.info("ERROR playing Audio, disabling");
            }
        }else{
            logger.info("Avoided Playing Audio");
        }
    }

}

package uk.ac.soton.comp1206;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.ChatWindow;
import uk.ac.soton.comp1206.ui.DrawWindow;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.LoginWindow;
import uk.ac.soton.comp1206.utility.Utility;

/**
 * Our Chat application main class. This will be responsible for co-ordinating the application and handling the GUI.
 */
public class App extends Application {

    private static final Logger logger = LogManager.getLogger(App.class);
    private Communicator communicator;
    private Stage stage;
    //private Stage stage2;
    //private Stage stage3;
    private static StringProperty username = new SimpleStringProperty("Guest");
    private final BooleanProperty gaming = new SimpleBooleanProperty(false);



    /**
     * Launch the JavaFX application
     * @param args
     */
    public static void main(String[] args) {
        logger.info("Starting client");
        launch();
    }

    /**
     * Start the Java FX process - prepare and display the first window
     * @param stage
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        communicator = new Communicator("ws://discord.ecs.soton.ac.uk:9500");//9501

        stage.setTitle("ECS Instant Messenger (EIM)");
        stage.setOnCloseRequest(ev -> {
            shutdown();
        });

        //TODO: We can come back to the login window and getting the username after we've made the chat work


        //For now, lets just hardcode our username (you can change it here to something better!
        //setUsername("Legend27 ");

        //Open the chat window - later, we'll do this only after the login has been done
        //openChat();



        openLogin();


    }

    /**
     * Display the login window
     */
    public void openLogin() {
        logger.info("Opening login window");
        var window = new LoginWindow(this);
        stage.setScene(window.getScene());

        stage.show();
        stage.centerOnScreen();
        window.loginFadeIn();
    }

    /**
     * Display the chat window
     */
    public void openChat() {
        logger.info("Opening chat window");
        var window = new ChatWindow(this,communicator);

        stage.setScene(window.getScene());

        stage.show();
        //stage.centerOnScreen();

        // play Aduio
        Utility.playAudio("connected.mp3");
    }

    public void openDrawWindow(){
        var stage2 = new Stage();
        stage2.setTitle("ECS WhiteBoard");
        var window = new DrawWindow(this,communicator);
        stage2.setScene(window.getScene());
        stage2.show();
        //stage2.centerOnScreen();
    }

    public void openGameWindow(){
        var stage3 = new Stage();
        stage3.setTitle("ECS GAME");
        var window = new GameWindow(this,communicator);
        stage3.setScene(window.getScene());
        stage3.show();
        gaming.set(true);
        stage3.setOnCloseRequest((e) ->  gaming.set(false) );


    }

    /**
     * Shutdown the application
     */
    public void shutdown() {
        logger.info("Shutting down");
        System.exit(0);
    }

    /**
     * Set the username from the login window
     * @param username
     */
    public void setUsername(String username) {
        logger.info("Username set to: " + username);
        this.username.setValue(username);
    }

    /**
     * Get the currently logged in user name
     * @return
     */
    public String getUsername() {
        return this.username.get();
    }

    public static StringProperty usernameProperty(){return username;}
}
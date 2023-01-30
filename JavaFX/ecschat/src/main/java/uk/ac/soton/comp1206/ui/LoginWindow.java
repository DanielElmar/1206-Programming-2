package uk.ac.soton.comp1206.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Displays the Login Window, to collect the username and then start the chat
 */
public class LoginWindow implements Initializable {

    private static final Logger logger = LogManager.getLogger(LoginWindow.class);
    private final App app;


    @FXML
    private ImageView loginImg;
    @FXML
    private Text loginText;
    @FXML
    private Label loginLabel;
    @FXML
    private TextField username;
    @FXML
    private Button loginButton;




    Scene scene = null;
    Parent root = null;


    /**
     * Create a new Login Window, linked to the main app. This should get the username of the user.
     * @param app the main app
     */
    public LoginWindow(App app) {
        this.app = app;

        //Load the Login Window GUI
        try {
            //Instead of building this GUI programmatically, we are going to use FXML
            var loader = new FXMLLoader(getClass().getResource("/login2.fxml"));

            //TODO: Fill in login.fxml

            //Link the GUI in the FXML to this class
            loader.setController(this);
            root = loader.load();

        } catch (Exception e) {
            //Handle any exceptions with loading the FXML
            logger.error("Unable to read file: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        //We are the login window
        scene = new Scene(root);
    }

    /**
     * Get the scene contained inside the Login Window
     * @return login window scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Handle what happens when the user presses the login button
     * @param event button clicked
     */
    @FXML protected void handleLogin(ActionEvent event) {
        //TODO: Link this to the login button
        //String user = "Guest";
        logger.info("Attempting Login");
        if(username.getText().isBlank()) return;
        app.setUsername(username.getText());
        app.openChat();
    }

    /**
     * Handle what happens when the user presses enter on the username field
     * @param event key pressed
     */
    @FXML protected void handleEnter(KeyEvent event) {
        //TODO: Link this to pressing enter on the username text field
        if(event.getCode() != KeyCode.ENTER) return;
        handleLogin(null);
    }

    /**
     * Initialise the Login Window
     * @param url
     * @param bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        //TODO: Any setting up of the window when it is initialised
    }

    public void loginFadeIn(){
        FadeTransition fader = new FadeTransition(new Duration(2000),loginImg);
        FadeTransition fader2 = new FadeTransition(new Duration(2000),loginText);
        FadeTransition fader3 = new FadeTransition(new Duration(2000),loginLabel);
        FadeTransition fader4 = new FadeTransition(new Duration(2000),username);
        FadeTransition fader5 = new FadeTransition(new Duration(2000),loginButton);

        fader.setToValue(1);
        fader2.setToValue(1);
        fader3.setToValue(1);
        fader4.setToValue(1);
        fader5.setToValue(1);

        fader.play();
        fader2.play();
        fader3.play();
        fader4.play();
        fader5.play();
    }
}

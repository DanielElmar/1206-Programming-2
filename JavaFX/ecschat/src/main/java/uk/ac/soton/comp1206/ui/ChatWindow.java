package uk.ac.soton.comp1206.ui;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.customComponents.Message;
import uk.ac.soton.comp1206.customComponents.SideBar;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.utility.Utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Chat window which will display chat messages and a way to send new messages
 */
public class ChatWindow {

    private static final Logger logger = LogManager.getLogger(ChatWindow.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    private final App app;
    private final Scene scene;
    private final Communicator communicator;
    @FXML
    private BorderPane pane;
    @FXML
    private VBox Vbox;
    @FXML
    private ScrollPane VboxScroller;
    @FXML
    private TextField messageTextField;
    @FXML
    private Button sendButton;
    /*@FXML
    private CheckBox audioEnabled;
    @FXML
    private TextField username;*/

    Parent root = null;

    /**
     * Create a new Chat Window, linked to the main App and the Communicator
     * @param app the main app
     * @param communicator the communicator
     */
    public ChatWindow(App app, Communicator communicator) {
        this.app = app;
        this.communicator = communicator;

        //Link the communicator to this window
        communicator.setWindow(this);


        //Load the Login Window GUI
        try {
            //Instead of building this GUI programmatically, we are going to use FXML
            var loader = new FXMLLoader(getClass().getResource("/chat2.fxml"));

            //Link the GUI in the FXML to this class
            loader.setController(this);
            root = loader.load();
        } catch (Exception e) {
            //Handle any exceptions with loading the FXML
            logger.error("Unable to read file: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // NEW SIDE BAR
        SideBar sideBar = new SideBar(app,130,communicator);
        // ADD TO SIDE
        pane.setRight(sideBar);

        //We are the chat window
        scene = new Scene(root);


        // add Binds
        sideBar.getAudioEnabledBooleanProperty().bindBidirectional(Utility.audioEnabledProperty());
        sideBar.getUsernameTextProperty().bindBidirectional(App.usernameProperty());


        // add Listeners
        communicator.addListener((message) -> {
            Platform.runLater(() -> this.receiveMessage(message));
        });

        Vbox.heightProperty().addListener((observable -> {
            if (VboxScroller.getVvalue() > 0.9) {
                VboxScroller.vvalueProperty().setValue(1);
            }
        }));

        messageTextField.setOnKeyPressed( keyEvent -> {
            if ( keyEvent.getCode().equals(KeyCode.ENTER)){
                sendCurrentMessage( messageTextField.getText() );
            }
        });

        sendButton.setOnAction( actionEvent ->  {
            sendCurrentMessage( messageTextField.getText() );
        });

        /*
        //Setup scene with a border pane
        var pane = new BorderPane();
        this.scene = new Scene(pane,640,480);


        //TODO: Set up the GUI to be more useful than this...

        this.Vbox = new VBox();
        VboxScroller = new ScrollPane(Vbox);
        VboxScroller.setFitToWidth(true);
        Vbox.heightProperty().addListener((observable -> {
            if (VboxScroller.getVvalue() > 0.9) {
                VboxScroller.vvalueProperty().setValue(1);
            }
        }));

        var sendMessageBox = new HBox();

        pane.setCenter(VboxScroller);
        pane.setBottom(sendMessageBox);



        messageTextField = new TextField();
        HBox.setHgrow(messageTextField, Priority.ALWAYS);

        messageTextField.setOnKeyPressed( keyEvent -> {
            if ( keyEvent.getCode().equals(KeyCode.ENTER)){
                sendCurrentMessage( messageTextField.getText() );
            }
        });

        var sendButton = new Button("SEND");
        sendButton.setOnAction( actionEvent ->  {
            sendCurrentMessage( messageTextField.getText() );
        });

        HBox.setHgrow(sendButton, Priority.NEVER);
        sendButton.setMinWidth(50);


        sendMessageBox.getChildren().add(messageTextField);
        sendMessageBox.getChildren().add(sendButton);



        //Set the stylesheet for this window
        String css = this.getClass().getResource("/chat.css").toExternalForm();
        scene.getStylesheets().add(css);*/

    }

    /**
     * Handle an incoming message from the Communicator
     * @param message The message that has been received, in the form User:Message
     */
    public void receiveMessage(String message) {
        //TODO: Handle incoming messages
        if (message.contains(":")) {
            Vbox.getChildren().add(new Message(message, app));
            Utility.playAudio("incoming.mp3");
        }
    }

    /**
     * Send an outgoing message from the Chatwindow
     * @param text The text of the message to send to the Communicator
     */
    private void sendCurrentMessage(String text) {
        communicator.send(app.getUsername() + ":" + text);
        messageTextField.clear();

    }

    /**
     * Get the scene contained inside the Chat Window
     * @return
     */
    public Scene getScene() {
        return scene;
    }
}

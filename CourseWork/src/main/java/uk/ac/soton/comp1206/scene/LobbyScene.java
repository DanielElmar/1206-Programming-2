package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * The Lobby scene gives the play access to all open games aswell as the option to create their own game,
 * channels also have messaging functionality
 *
 * all interaction in the Lobby Scene is closely tied to messages to and from the server via a Communicator
 *
 * This Scene is responsible for triggering the start of a Multiplayer Game
 */
public class LobbyScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    private final Communicator communicator = new Communicator("ws://discord.ecs.soton.ac.uk:9700");
    private final VBox channelsVBox = new VBox();
    private final VBox channelVBox = new VBox();
    private final TextFlow channelMembers = new TextFlow();
    private final ScrollPane messageScroller = new ScrollPane();
    private final VBox messagesScrollableVBox = new VBox();
    private final HBox buttonHBox = new HBox();
    private static final String currentChannel = null;
    private static final ArrayList<String> channelNames = new ArrayList<>();
    private static Timer repeatChannelRequest;
    private static final Multimedia audioPlayer = new Multimedia();
    private static String myName;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * Initialise the scene, scene listeners need to go here
     */
    @Override
    public void initialise() {

        scene.setOnKeyPressed( (e) -> {
            if ( e.getCode() == KeyCode.ESCAPE ){
                communicator.send("QUIT");
                close();
                gameWindow.startMenu();
            }
        } );
    }
    /**
     * Build the scene layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        communicator.addListener( (message) -> { Platform.runLater( () -> {receiveMessage(message);}); });

        // schedule repeated task to get Channel updates from sever
        TimerTask requestChannels = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater( () -> requestChannels());
            }
        };

        repeatChannelRequest = new Timer();
        repeatChannelRequest.schedule(requestChannels, 0, 300);

        // set up main scene root and layout
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var background = new StackPane();
        background.setMaxWidth(gameWindow.getWidth());
        background.setMaxHeight(gameWindow.getHeight());
        background.getStyleClass().add("menu-background");
        root.getChildren().add(background);


        var mainPane = new BorderPane();
        background.getChildren().add(mainPane);


        // Title / TopVBox
        var topVbox = new VBox();
        topVbox.setAlignment(Pos.CENTER);

        var title = new Text("Multiplayer");
        title.getStyleClass().add("title");

        topVbox.getChildren().add(title);
        mainPane.setTop(topVbox);

        // Channels Box
        var channelsWrapperVBox = new VBox();
        mainPane.setLeft(channelsWrapperVBox);
        channelsWrapperVBox.setPadding(new Insets(10,10,10,10));
        channelsWrapperVBox.setSpacing( 20 );  //
        channelsWrapperVBox.setPrefWidth(300);

        // Channels heading
        var currentGamesText = new Text("Current Games");
        currentGamesText.getStyleClass().add("heading");
        channelsWrapperVBox.getChildren().add(currentGamesText);

        // Host new Game Button
        var hostNewGameButton = new Text("Host New Game");
        hostNewGameButton.getStyleClass().add("channelItem");
        channelsWrapperVBox.getChildren().add(hostNewGameButton);

        // Host New Game Text Field
        var hostNewGameTextField = new TextField();
        channelsWrapperVBox.getChildren().add(hostNewGameTextField);
        hostNewGameTextField.setOpacity(0);

        hostNewGameButton.setOnMouseClicked( (e) -> {
            hostNewGameTextField.clear();
            hostNewGameTextField.setOpacity(1);

            scene.setOnKeyPressed( (ee) -> {
                if (ee.getCode() == KeyCode.ENTER && hostNewGameTextField.getText().length() > 1 && !channelNames.contains(hostNewGameTextField.getText()) ){
                    HostNewGame( hostNewGameTextField.getText() );
                    hostNewGameTextField.setOpacity(0);
                    hostNewGameTextField.clear();
                }else if ( ee.getCode() == KeyCode.ESCAPE ){
                    communicator.send("QUIT");
                    close();
                    gameWindow.startMenu();
                }

            });
        });

        channelsWrapperVBox.getChildren().add(channelsVBox);

        // Right Side / channel VBox
        mainPane.setRight(channelVBox);
        channelVBox.setPadding( new Insets( 10, 10, 10, 10) );
        channelVBox.setSpacing( 20 );
    }

    /**
     * Used to cancel repeating requested based on a Timer
     */
    public void close(){
        repeatChannelRequest.cancel();
    }

    /**
     * sends message to the server to start a new channel
     * @param name Name of the Channel to host
     */
    private void HostNewGame( String name ){
        communicator.send( "CREATE " + name );
    }

    /**
     * Requests a list of open channel/games fromt eh Server
     */
    private void requestChannels(){ communicator.send("LIST"); }

    /**
     * Adds open channels/games to the UI
     * @param message A string that encode all open channels/games
     */
    private void updateChannels(String message ){

        var channels = message.split("\n");

        // clear previous channels
        channelsVBox.getChildren().clear();
        channelNames.clear();

        for ( String channel: channels) {

            // create a new channel node
            var channelText = new Text(channel);
            channelText.getStyleClass().add("channelItem");

            channelText.setOnMouseClicked( (e) -> { communicator.send("JOIN " + channel); } );

            // add it to the layout and channelNames Array
            channelsVBox.getChildren().add( channelText );
            channelNames.add( channel );

        }
    }

    /**
     * Takes a message from a Communicator and parses the incoming message and executed the corresponding code
     * @param message String from communicator to be parsed
     */
    private void receiveMessage(String message){

        var parts = message.split( " ", 2);

        // Identify what command to run based off message beginning
        switch (parts[0]) {

            case "CHANNELS" -> {
                updateChannels( parts[1] );
            }

            case "JOIN" -> {
                // clear previous joined channel from UI
                channelVBox.getChildren().clear();

                // Channel Name
                var channelName = new Text( parts[1] );
                channelName.getStyleClass().add("heading");
                channelVBox.getChildren().add(channelName);

                // shaded VBox
                var messageWrapperVBox = new VBox();
                channelVBox.getChildren().add(messageWrapperVBox);
                messageWrapperVBox.getStyleClass().add("messagesWrapper");
                messageWrapperVBox.setSpacing(5);
                messageWrapperVBox.setPrefWidth( 479 );
                messageWrapperVBox.setPrefHeight(400);
                messageWrapperVBox.setOpacity( 10 );
                messageWrapperVBox.setPadding( new Insets( 10, 10, 10, 10));

                // add Members
                messageWrapperVBox.getChildren().add(channelMembers);
                channelMembers.getStyleClass().add("playerBox");

                // add chat instructions
                var chatInstructions = new Text("Welcome to the Lobby\nType /nick NewName to change your name");
                chatInstructions.getStyleClass().add("instructions");
                messageWrapperVBox.getChildren().add(chatInstructions);

                // add Messages Scroller
                messagesScrollableVBox.heightProperty().addListener(observable -> messageScroller.setVvalue(1D));
                messageWrapperVBox.getChildren().add(messageScroller);
                messageScroller.getStyleClass().add("scroller");
                messageScroller.setPrefViewportHeight(300);
                messageScroller.setContent( messagesScrollableVBox );

                // clear chat history from the previous Channel
                messagesScrollableVBox.getChildren().clear();


                // Message Text Field
                var messageTextField = new TextField();
                messageWrapperVBox.getChildren().add(messageTextField);

                scene.setOnKeyPressed( (e) -> {
                    if (e.getCode() == KeyCode.ENTER) {
                        var textFieldMessage = messageTextField.getText();
                        if (textFieldMessage.startsWith("/nick ")){

                            communicator.send("NICK " + textFieldMessage.substring( textFieldMessage.indexOf(" ") + 1));

                        }else {
                            communicator.send("MSG " + textFieldMessage);
                        }
                        messageTextField.clear();
                    }else if ( e.getCode() == KeyCode.ESCAPE ){
                        communicator.send("QUIT");
                        close();
                        gameWindow.startMenu();
                    }
                } );

                // Button HBox
                buttonHBox.getChildren().clear();
                buttonHBox.setSpacing(300);
                messageWrapperVBox.getChildren().add(buttonHBox);

                // Leave 'Button'
                var leaveChannel = new Text("Leave");
                leaveChannel.getStyleClass().add("heading");
                buttonHBox.getChildren().add(leaveChannel);

                leaveChannel.setOnMouseClicked( (e) -> { communicator.send( "PART" ); } );


            }

            case "HOST" -> {
                // add Start game Button
                var startGameText = new Text("Start");
                startGameText.getStyleClass().add("heading");
                buttonHBox.getChildren().add(startGameText);

                startGameText.setOnMouseClicked( (e) -> { communicator.send( "START" ); } );
            }

            case "ERROR" -> {
                Alert error = new Alert(Alert.AlertType.ERROR, parts[1], new ButtonType("ok") );
                error.show();
            }

            case "NICK" -> { myName = parts[1];}

            case "START" -> {
                // go to Start game subroutine
                close();
                gameWindow.startMultiplayerChallenge( communicator, myName );
            }

            case "PARTED" -> {
                channelVBox.getChildren().clear();
            }

            case "USERS" -> {
                //clear previous members of the channel
                channelMembers.getChildren().clear();

                // add members in message
                for ( String user : parts[1].split("\n")) {

                    var userText = new Text(user + "   ");
                    channelMembers.getChildren().add(userText);
                }
            }

            case "MSG" -> {
                // play audio
                audioPlayer.playAudio("message.wav");

                //create message nodes
                var chatMessageFlow = new TextFlow();
                var chatMessage = new Text();
                chatMessage.getStyleClass().add("message");

                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                Date date = new Date();

                try {
                    chatMessage.setText(formatter.format(date) + " " + parts[1].split(":")[0] + ": " + parts[1].split(":")[1]);
                }catch (ArrayIndexOutOfBoundsException e){ chatMessage.setText(formatter.format(date) + " " + parts[1].split(":")[0] + ": "); }

                // add the chat message to the chat
                chatMessageFlow.getChildren().add(chatMessage);
                messagesScrollableVBox.getChildren().add(chatMessageFlow);
            }
        }
    }
}

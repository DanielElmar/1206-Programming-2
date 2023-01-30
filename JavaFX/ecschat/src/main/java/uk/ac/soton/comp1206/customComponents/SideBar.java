package uk.ac.soton.comp1206.customComponents;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.network.Communicator;
import javafx.util.Duration;


public class SideBar extends VBox {

    private final App app;

    private boolean visible = true;

    private CheckBox audioEnabled;
    private TextField username;
    private UserList userList;
    private int width;
    private Label usernameLabel;
    private Label usersLabel;
    private ImageView imageView;
    private Button drawWindow;
    private Button gameWindow;

    public SideBar(App app, int width, Communicator communicator ){

        this.app = app;

        getStyleClass().add("sideBar");
        setPrefWidth(width);
        setAlignment(Pos.TOP_CENTER);
        setMinWidth(50);

        this.width = width;


        imageView = new ImageView(new Image("ECSChat.png"));//Image image = new Image("ECSChat.png");//imageView.setImage(image);

        imageView.setPreserveRatio(true);

        imageView.setFitWidth(width);


        imageView.setOnMouseClicked((e)->{
            toggleSideBar();
        });

        //USER LIST
        usersLabel = new Label("Users");
        userList = new UserList(communicator);

        usernameLabel = new Label("Username");

        username = new TextField();

        audioEnabled = new CheckBox("Notifications");

        drawWindow = new Button("New WhiteBoard");

        gameWindow = new Button("New Game");

        drawWindow.setOnMouseClicked((e)->{
            app.openDrawWindow();
        });

        gameWindow.setOnMouseClicked((e)->{
            app.openGameWindow();
        });

        // Set margins
        setMargin(imageView, new Insets(0,0,20,0));
        setMargin(username, new Insets(0,5,20,5));
        setMargin(audioEnabled, new Insets(0,0,20,5));
        setMargin(drawWindow, new Insets(0,0,20,5));

        // Add Children
        getChildren().add(imageView);
        getChildren().add(usersLabel);
        getChildren().add(userList);
        getChildren().add(usernameLabel);
        getChildren().add(username);
        getChildren().add(audioEnabled);
        getChildren().add(drawWindow);
        getChildren().add(gameWindow);
    }

    public StringProperty getUsernameTextProperty(){
        return username.textProperty();
    }

    public BooleanProperty getAudioEnabledBooleanProperty(){
        return audioEnabled.selectedProperty();
    }

    public void toggleSideBar(){
        if (visible){
            visible = false;
            userList.setVisible(false);
            username.setVisible(false);
            audioEnabled.setVisible(false);
            usernameLabel.setVisible(false);
            usersLabel.setVisible(false);
            drawWindow.setVisible(false);

            Duration duration = Duration.millis(200);
            var timeLine = new Timeline(
                    new KeyFrame( duration, new KeyValue(this.prefWidthProperty(), 50)),
                    new KeyFrame( duration, new KeyValue(imageView.scaleXProperty(), 0.4)),
                    new KeyFrame( duration, new KeyValue(imageView.scaleYProperty(), 0.4))
            );

            timeLine.play();


        }else{
            visible = true;
            Duration duration = Duration.millis(20);
            var timeLine = new Timeline(
                    new KeyFrame( duration, new KeyValue(this.prefWidthProperty(), width)),
                    new KeyFrame( duration, new KeyValue(imageView.scaleXProperty(), 1)),
                    new KeyFrame( duration, new KeyValue(imageView.scaleYProperty(), 1))
            );

            timeLine.play();


            timeLine.setOnFinished((e)->{
                userList.setVisible(true);
                username.setVisible(true);
                audioEnabled.setVisible(true);
                usernameLabel.setVisible(true);
                usersLabel.setVisible(true);
                drawWindow.setVisible(true);
            });
        }
    }

}

package uk.ac.soton.comp1206.customComponents;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.ChatWindow;

import java.util.ArrayList;
import java.util.HashSet;

public class UserList extends ScrollPane {

    private Communicator communicator;
    private HashSet<String> users =  new HashSet<String>();
    private VBox vbox;
    private static final Logger logger = LogManager.getLogger(UserList.class);

    UserList(Communicator communicator ) {
        this.communicator = communicator;

        setMaxHeight(200);
        setFitToWidth(true);

        vbox = new VBox();
        //getChildren().add(vbox);
        setContent(vbox);

        vbox.setSpacing(20);
        vbox.setPadding(new Insets(10,10,10,10));
        vbox.setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("userList");

        // add Listeners
        communicator.addListener((message) -> {
            if (message.contains(":")) {
                Platform.runLater(() -> this.addUser(message));
            }
        });
    }
    public void addUser(String message){
        String[] username = message.split(":");

        if (!users.contains(username[0])){
            logger.info("Adding USer To Side bar");
            Text user = new Text(username[0]);
            vbox.getChildren().add(user);
            users.add(username[0]);
        }
    }


    /*public void setVisible(Boolean value){
        setVisible(value);
    }*/
    public void build(){}



}

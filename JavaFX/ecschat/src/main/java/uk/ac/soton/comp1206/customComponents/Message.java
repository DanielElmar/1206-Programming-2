package uk.ac.soton.comp1206.customComponents;

import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.ui.ChatWindow;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Message extends TextFlow {
    private final App app;
    private static final Logger logger = LogManager.getLogger(Message.class);

    public Message(String message, App app){
        this.app = app;

        getStyleClass().add("messageLine");

        ///*
        String[] parts = message.split(":",2);
        Text user = new Text(parts[0]);
        user.setUnderline(true);
        Text separator = new Text( " : ");
        var messageNodes = new ArrayList<Node>();


        String[] messageParts = parts[1].split(" ");
        for(String part : messageParts){
            if( part.contains("@")   &&  (part.substring(1,part.length()).equals(app.getUsername()))){

                var mentionText = new Text("@" + app.getUsername());
                mentionText.getStyleClass().add(".mention"); //;?????????????/
                mentionText.setUnderline(true);
                messageNodes.add(mentionText);
            }
            else if(part.length() > 7 && part.substring(0,8).equals("https://")){
                logger.info("LINK DECTECTED : " + part);
                var link = new Hyperlink(part);
                messageNodes.add(link);
            }else{
                messageNodes.add(new Text(part ));
            }
            messageNodes.add(new Text(" "));
        }
        /*
        String[] parts = message.split(":",2);
        Text user = new Text(parts[0]);
        Text separator = new Text( " : ");
        var messageTexts = new ArrayList<Text>();
        user.setUnderline(true);

        // Search parts[1] fro emojies or urls
        if (parts[1].contains("@")){
            parts[1].trim();
            String mention = null;
            try {
                mention = parts[1].substring(parts[1].indexOf("@")+1, parts[1].indexOf(" ", parts[1].indexOf("@")));
            }catch (Exception e){
                mention = parts[1].substring(parts[1].indexOf("@")+1, parts[1].length());
            }

            if (mention.equals(app.getUsername())){
                logger.info("Mention Matches");
                messageTexts.add(new Text(parts[1].substring(0,parts[1].indexOf("@"))));
                var mentionText = new Text("@" + app.getUsername());
                mentionText.setUnderline(true);
                messageTexts.add(mentionText);
                try {
                    messageTexts.add(new Text(parts[1].substring(parts[1].indexOf(" ", parts[1].indexOf("@")), parts[1].length())));
                }catch (Exception e){}
            }else{
                messageTexts.add(new Text(parts[1]));
            }
        }else {
            messageTexts.add(new Text(parts[1]));
        }
        //*/

        getChildren().add(new Text(new SimpleDateFormat("HH:mm ").format(new Date())));
        getChildren().add(user);
        getChildren().add(separator);

        logger.info(messageNodes.size());


        for (Node messageNode : messageNodes) {
            //logger.info(messageText.getText());
            getChildren().add(messageNode);
        }


        // exstract userName
        // Rich elements (URLS, Emojies, Mentions)
        // add each segment to itsself as children

        /// custom emoji component????? no
    }
}

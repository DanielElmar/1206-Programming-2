package uk.ac.soton.comp1206.network;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;

public class PaintMessage {

    private static final Logger logger = LogManager.getLogger(PaintMessage.class);

    private Color colour = Color.BLACK;
    private final LinkedHashSet<Point2D> points = new LinkedHashSet<>();

    /** Create a new Paint Message from a received message
     *
     * @param message The incoming message received (after the DRAW header)
     */
    public PaintMessage(String message) {
        logger.info("Processing paint message: {}}",message);
        message.trim();
        String hexCode = message.substring(1,9);
        logger.info("HEX CODE DECODED AS " + hexCode);
        logger.info("HEX CODE FIRST DECODED AS " + hexCode.charAt(0));
        logger.info("HEX CODE LAST DECODED AS " + hexCode.charAt(7));
        setColour(new Color(
                Integer.valueOf( hexCode.substring( 0, 2 ), 16 ),
                Integer.valueOf( hexCode.substring( 2, 4 ), 16 ),
                Integer.valueOf( hexCode.substring( 4, 6 ), 16 ),
                Integer.valueOf( hexCode.substring( 6, 8 ), 16 )/255));


        String[] pointsStringArray = message.substring(10,message.length()).split(" ");
        for ( String point : pointsStringArray) {
            String[] splitPoint = point.split(",");
            addPoint(Double.parseDouble(splitPoint[0]),Double.parseDouble(splitPoint[1]));
        }
    }

    /**
     * Create a new empty Paint Message (ready for filling)
     */
    public PaintMessage() {

    }

    /**
     * Add a drawing point, as a pair of doubles, to this Paint Message
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void addPoint(double x, double y) {
        points.add(new Point2D(x, y));

    }

    /**
     * Encode the set of drawing points into a protocol-compliant string (DRAW colour x,y x,y x,y..)
     * @return encoded string
     */
    public String encode() {
        logger.info("Encoding {} points",points.size());
        StringBuilder message = new StringBuilder("DRAW ");
        message.append(colourToHex(colour) + " ");
        for ( Point2D point : points) {
            message.append(point.getX() + "," + point.getY() + " ");
        }
        return message.toString();
    }

    /**
     * Use the provided Graphics Context from a canvas to draw all the points held in the message
     * @param gc Graphics Context from a canvas
     */
    public void paint(GraphicsContext gc) {
        logger.info("Painting {} points",points.size());
        gc.setStroke(colour);
        gc.beginPath();

        for ( Point2D point : points) {
            gc.lineTo(point.getX(), point.getY());
        }

        gc.stroke();
        gc.closePath();

    }

    /**
     * Convert a colour object into a hex representation ready for encoding
     * @param colour colour to encode
     * @return hex colour code
     */
    private String colourToHex(Color colour) {
        int r = ((int) Math.round(colour.getRed()     * 255)) << 24;
        int g = ((int) Math.round(colour.getGreen()   * 255)) << 16;
        int b = ((int) Math.round(colour.getBlue()    * 255)) << 8;
        int a = ((int) Math.round(colour.getOpacity() * 255));
        return String.format("#%08X", (r + g + b + a));
    }

    /**
     * Set the colour represented by this Paint Message
     * @param selected chosen colour
     */
    public void setColour(Color selected) {
        this.colour = selected;
    }

}


import java.awt.*;

public class Theme {

    private String name;
    private Color colour;
    public static final Color DIGIT_COLOUR =  new Color(0,0,0,90);

    public Theme(String name, Color colour){
        this.name = name;
        this.colour = colour;
    }

    public Color getColour() {
        return colour;
    }
    public Color getBlankDayColour(){
        return new Color(colour.getRed() <= 15? 0:colour.getRed()-15, colour.getGreen() <= 15? 0:colour.getGreen()-15, colour.getBlue() <= 15? 0:colour.getBlue()-15);
    }
    public Color getDayBorderColour(){
        return getBlankDayColour();
    }
    public Color getDayHoverColour(){
        return colour.brighter();
    }
    public Color getDayPressedColour(){
        return colour.darker();
    }
    public String getName() {
        return name;
    }

    public String toString(){
        return name;
    }
}

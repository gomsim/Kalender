import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ArrowButton extends JPanel {

    private int start = 0;
    private int length = 30;
    private int tipLength = 15;
    private int height = 30;

    private int noOfReps = 3;

    private boolean leftArrow;
    private boolean hoveredOver;
    public enum State {HOVERED, PRESSED, NORMAL}
    public State state = State.NORMAL;

    private Color colour = new Color(51, 102, 255);
    private Color hoverColour = colour.brighter();
    private Color pressedColour = colour.darker();
    private Color test2 = new Color(0, 0, 0, 90);

    private int[] y = {0, 0, height /2, height, height, height /2};

    public ArrowButton(boolean leftArrow, Theme theme){
        if (leftArrow) {
            start = getWidth();
            length = -length;
            tipLength = -tipLength;
        }
        this.leftArrow = leftArrow;
        setBackground(theme.getDayBorderColour());
    }

    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        if (leftArrow)
            start = getWidth();
        length = (getWidth()-Math.abs(tipLength))/noOfReps;
        if (leftArrow)
            length = -length;
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        Color paintColour;
        switch (state){
            case NORMAL:
                paintColour = colour;
                break;
            case HOVERED:
                paintColour = hoverColour;
                break;
            case PRESSED:
                paintColour = pressedColour;
                break;
            default:
                paintColour = colour;
                break;
        }
        drawArrows(start, 0, paintColour, graphics);
    }

    public void setState(State state){
        this.state = state;
        repaint();
    }
    public State getState(){
        return state;
    }
    public boolean hoveredOver(MouseEvent event){
        return (event.getX() > 0 && event.getX() < getWidth()) && (event.getY() > 0 && event.getY() < getHeight());
    }
    private void drawArrows(int startX, int round, Color colour, Graphics graphics){
        graphics.setColor(colour);
        graphics.fillPolygon(new int[] {startX, startX + length, startX + length + tipLength, startX + length, startX, startX + tipLength}, y, y.length);
        round ++;
        colour = colour.brighter();
        if (round >= noOfReps)
            return;
        else
            drawArrows(startX + length, round, colour, graphics);
    }
}

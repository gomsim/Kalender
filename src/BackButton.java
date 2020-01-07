import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class BackButton extends JPanel {

    private Theme theme;
    private Day day;

    private static final int POS_X = 20;
    private static final int POS_Y = 9;
    private static final int TIP_LENGTH = 20;
    private static final int LENGTH = 100;
    private static final int HEIGHT = 35;

    public enum State {HOVERED, PRESSED, NORMAL}
    public State state = State.NORMAL;

    private Color colour;
    private Color hoverColour;
    private Color pressedColour;

    private int[] x = {0 +POS_X, TIP_LENGTH+POS_X, TIP_LENGTH + LENGTH+POS_X, TIP_LENGTH + LENGTH+POS_X, TIP_LENGTH+POS_X};
    private int[] y = {HEIGHT/2+ POS_Y, 0+ POS_Y, 0+ POS_Y, HEIGHT+ POS_Y, HEIGHT+ POS_Y};

    public BackButton(Theme theme, Day day){
        this.day = day;
        this.theme = theme;
        setBackground(theme.getColour());
        colour = theme.getDayBorderColour();
        hoverColour = theme.getDayHoverColour();
        pressedColour = theme.getDayPressedColour();
    }

    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
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
        graphics.setColor(paintColour);
        graphics.fillPolygon(x, y, x.length);
        graphics.setColor(paintColour.darker().darker());
        Font font = new Font("Arial", Font.BOLD, 12);
        setFont(font);
        FontMetrics fontMetrics = getFontMetrics(font);
        int length = fontMetrics.stringWidth(day.getMonth().toString());
        graphics.drawString(day.getMonth().toString(), POS_X + (TIP_LENGTH + LENGTH)/2 -length/2, POS_Y + (HEIGHT/2)+5);
    }

    public void setState(State state){
        this.state = state;
        repaint();
    }
    public State getState(){
        return state;
    }
    public boolean hoveredOver(MouseEvent event){
        return (event.getX() > POS_X && event.getX() < POS_X+TIP_LENGTH+LENGTH) && (event.getY() > POS_Y && event.getY() < getHeight());
    }
}

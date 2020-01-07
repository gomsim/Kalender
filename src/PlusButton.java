import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class PlusButton extends JComponent {

    private int one = 0, two = 6, three = 9, four = 15;
    private int hoverOffset = 1;
    private int posX = 0, posY = 0;
    private int ySlider = -5;

    private boolean allignRight;
    private boolean big;

    private Color colour = new Color(0,0,0,80);
    private Color hoverColour;
    private Color pressedColour;


    private boolean hoveredOver;

    public PlusButton(Rectangle parentBounds){
        setBounds((int)(parentBounds.getWidth()-17), 3, 15, 15);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        graphics.setColor(colour);
        if (big){
            if (allignRight)
                allignRight();
            if (!hoveredOver) {
                graphics.fillRect(posX+16, posY+11+ySlider, 8, 26);
                graphics.fillRect(posX+7, posY+20+ySlider, 9, 8);
                graphics.fillRect(posX+24, posY+20+ySlider, 9, 8);

                graphics.fillArc(posX+16, posY+4+ySlider, 8, 14, -180, -180);
                graphics.fillArc(posX, posY+20+ySlider, 14, 8, -90, -180);
                graphics.fillArc(posX+26, posY+20+ySlider, 14, 8, -270, -180);
                graphics.fillArc(posX+16, posY+30+ySlider, 8, 14, 0, -180);
            } else {
                if (hoverColour != null)
                    graphics.setColor(hoverColour);
                graphics.setColor(new Color(230, 230, 230));
                graphics.fillOval(posX-2, posY+ySlider+2, 44, 44);
                graphics.setColor(colour);
                graphics.fillRect(posX+16, posY+11+ySlider, 8, 26);
                graphics.fillRect(posX+7, posY+20+ySlider, 9, 8);
                graphics.fillRect(posX+24, posY+20+ySlider, 9, 8);

                graphics.fillArc(posX+16, posY+4+ySlider, 8, 14, -180, -180);
                graphics.fillArc(posX, posY+20+ySlider, 14, 8, -90, -180);
                graphics.fillArc(posX+26, posY+20+ySlider, 14, 8, -270, -180);
                graphics.fillArc(posX+16, posY+30+ySlider, 8, 14, 0, -180);
            }
        }else {
            if (allignRight)
                allignRight();
            if (!hoveredOver) {
                graphics.fillRect(6, 4, 3, 7);
                graphics.fillRect(4, 6, 2, 3);
                graphics.fillRect(9, 6, 2, 3);

                graphics.fillArc(6, 2, 3, 4, -180, -180);
                graphics.fillArc(2, 6, 4, 3, -90, -180);
                graphics.fillArc(9, 6, 4, 3, -270, -180);
                graphics.fillArc(6, 9, 3, 4, 0, -180);
            } else {
                if (hoverColour != null)
                    graphics.setColor(hoverColour);
                graphics.setColor(new Color(230, 230, 230));
                graphics.fillOval(0, 0, 15, 15);
                graphics.setColor(colour);
                graphics.fillRect(6, 4, 3, 7);
                graphics.fillRect(4, 6, 2, 3);
                graphics.fillRect(9, 6, 2, 3);

                graphics.fillArc(6, 2, 3, 4, -180, -180);
                graphics.fillArc(2, 6, 4, 3, -90, -180);
                graphics.fillArc(9, 6, 4, 3, -270, -180);
                graphics.fillArc(6, 9, 3, 4, 0, -180);
            }
        }
    }
    public void setColor(Color colour){
        this.colour = colour;
    }
    public void setScale(double multiplier){
        one *= multiplier;
        two *= multiplier;
        three *= multiplier;
        four *= multiplier;
        hoverOffset *= multiplier;
        setSize((int)(getWidth()*multiplier), (int)(getHeight()*multiplier));
        big = true;
        repaint();
    }
    public void setHoverOffset(int offset){
        hoverOffset = offset;
    }
    public void setHoverColour(Color colour){
        hoverColour = colour;
    }
    public void setPressedColour(Color colour){
        pressedColour = colour;
    }
    public int getHorSize(){
        return four;
    }
    public void setHoverOver(boolean hoveredOver){
        this.hoveredOver = hoveredOver;
        repaint();
    }
    public void setPos(int x, int y){
        posX = x;
        posY = y;
        repaint();
    }
    public void allignRight(){
        posX = getWidth()-four-60;
        posY = 5;
        allignRight = true;
    }
    public boolean hoveredOver(MouseEvent event){
        return (event.getX() > posX && event.getX() < posX+four) && (event.getY() > posY && event.getY() < getHeight());
    }
    public String toString(){
        return "plusButton";
    }
}

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class ActivityGraphic extends JComponent {

    Activity activity;
    Day day;
    public static final int HEIGHT = 20;
    private boolean dimmed;

    Font font = new Font("Arial", Font.PLAIN, 12);

    public ActivityGraphic(Activity activity, int y, Day day){
        this.activity = activity;
        setBounds(0,y,day.getWidth(), HEIGHT);
        setFont(font);
        this.day = day;
    }
    public ActivityGraphic(Activity activity, Day day){
        this(activity, 0, day);
    }
    public ActivityGraphic(Activity activity, int y, Day day, boolean dimmed){
        this(activity,y,day);
        this.dimmed = dimmed;
    }

    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        setBounds(0,getY(),day.getWidth(), HEIGHT);
        graphics.setColor(activity.getCategory().getColour());
        graphics.fillRect(6,0, getWidth()-12, getHeight());
        if (!activity.isOneDay()){
            if (activity.isStart(day)){
                drawLeftRound(graphics);
                drawRightSquare(graphics);
            }else if (activity.isEnd(day)){
                drawLeftSquare(graphics);
                drawRightRound(graphics);
            }else{
                drawLeftSquare(graphics);
                drawRightSquare(graphics);
            }
        }else{
            drawLeftRound(graphics);
            drawRightRound(graphics);
        }
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF));
        graphics.setColor(activity.getCategory().getBorderColour());
        graphics2D.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        if (activity.getStartDay().equals(day) || day.dayOfWeek() == 1) {
            String header = activity.getHeader();
            FontMetrics fontMetrics = graphics.getFontMetrics(font);
            graphics.setColor(activity.getCategory().getBorderColour());
            try {
                if (fontMetrics.stringWidth(header) > getWidth() - 10) {
                    while (fontMetrics.stringWidth(header) > getWidth() - 10) {
                        header = header.substring(0, header.length() - 1);
                    }
                    header = header.substring(0, header.length() - 2);
                    if (header.endsWith(" ")) {
                        header = header.substring(0, header.length() - 1);
                    }
                    header += "...";
                }
            } catch (StringIndexOutOfBoundsException e) {
                header = "...";
            }
            graphics.drawString(header, 5, 15);
        }
    }

    private void drawLeftRound(Graphics graphics){
        graphics.fillArc(1,0,10,getHeight()/2-2,-180,-90);
        graphics.fillRect(1,getHeight()/3-2,5,getHeight()/2+2);
        graphics.fillArc(1,(getHeight()/3)*2,10,getHeight()/2-2,-90,-90);
    }
    private void drawRightRound(Graphics graphics){
        graphics.fillArc(getWidth()-11,0,10,getHeight()/2-2,90,-90);
        graphics.fillRect(getWidth()-6,getHeight()/3-2,5,getHeight()/2+2);
        graphics.fillArc(getWidth()-11,(getHeight()/3)*2,10,getHeight()/2-2,0,-90);
    }
    private void drawLeftSquare(Graphics graphics){
        graphics.fillRect(0,0,6,getHeight());
    }
    private void drawRightSquare(Graphics graphics){
        graphics.fillRect(getWidth()-6,0,11,getHeight());
    }

    public boolean equals(Object other){
        if (!(other instanceof ActivityGraphic))
            return false;
        ActivityGraphic otherActivityGraphic = (ActivityGraphic)other;
        return activity.equals(otherActivityGraphic.activity) && day.equals(otherActivityGraphic.day);
    }
    public int hashCode(){
        return activity.hashCode() + day.hashCode();
    }
    public String toString(){
        return activity.getHeader()/* + ":" + day*/;
    }
}

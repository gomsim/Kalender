import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;

public class DayMenuActivityGraphic extends JPanel {

    Activity activity;
    Day day;
    DayMenu dayMenu;
    private int dynamicHeight = 69;
    private static final int LINE_HEIGHT = 16;
    public static final int LARGE_SIDE_SPACE = 20;
    public static final int SMALL_SIDE_SPACE = 10;
    private enum OverButton {EDIT, REMOVE, NONE}
    private OverButton hoversOver = OverButton.NONE;

    public DayMenuActivityGraphic(Activity activity, int y, Day day, DayMenu dayMenu,Theme theme){
        this.day = day;
        this.activity = activity;
        this.dayMenu = dayMenu;
        setLayout(null);
        setBounds(LARGE_SIDE_SPACE, y, MainWindow.instance.getWidth()-16-(LARGE_SIDE_SPACE *2), dynamicHeight);
        if (!activity.isSubscribed()) {
            addMouseListener(new EditRemoveListener());
            addMouseMotionListener(new EditRemoveListener());
        }
        setBackground(theme.getColour());
    }

    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        Font descriptionFont = new Font("Arial", Font.PLAIN, 12);
        setBounds(LARGE_SIDE_SPACE, getY(), MainWindow.instance.getWidth()-16-(LARGE_SIDE_SPACE *2), dynamicHeight);
        graphics.setColor(activity.getCategory().getColour());
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        graphics.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
        graphics.setColor(activity.getCategory().getBorderColour());

        graphics.setFont(new Font("Arial", Font.BOLD, 15));
        graphics.setColor(activity.getCategory().getBorderColour());
        graphics.drawString(activity.getHeader(), 7, 20);
        graphics.setFont(new Font("Arial", Font.ITALIC+Font.BOLD, 11));
        graphics.drawString(formatStartStop(), 7, getHeight()-10);
        if (activity instanceof DescribedActivity) {
            graphics.setFont(descriptionFont);
            printDescription(((DescribedActivity)activity).getDescription(), 37, graphics, graphics.getFontMetrics(descriptionFont));
        }
        if (!activity.isSubscribed()) {
            graphics.setColor(hoversOver.equals(OverButton.EDIT) ? activity.getCategory().getBorderColour().darker() : activity.getCategory().getBorderColour());
            graphics.setFont(new Font("Arial", Font.BOLD, 13));
            graphics.drawString("Ändra", getWidth() - 70, 19);
            graphics.setColor(hoversOver.equals(OverButton.REMOVE) ? activity.getCategory().getBorderColour().darker() : activity.getCategory().getBorderColour());
            graphics.setFont(new Font("Arial", Font.BOLD, 20));
            graphics.drawString("x", getWidth() - 20, 19);
        }
    }

    public String getToolTipText(MouseEvent event){
        if (activity instanceof DescribedActivity) {
            return withinDescriptionText(event) ? ((DescribedActivity) activity).getDescription() : null;
        }else{
            return null;
        }
    }
    private void printDescription(String string, int y, Graphics graphics, FontMetrics fontMetrics){
        boolean longWord = false;
        boolean hasNewLine = false;
        if (fontMetrics.stringWidth(string) > getWidth()-10){
            String fragment = string;
            while (fontMetrics.stringWidth(fragment) > getWidth()-10){
                fragment = fragment.substring(0,fragment.length()-1);
            }
            if (fragment.contains(" ")) {
                while (!fragment.endsWith(" ")) {
                    fragment = fragment.substring(0, fragment.length() - 1);
                }
            }else{
                fragment = fragment.substring(0,fragment.length()-1);
                longWord = true;
            }
            //Checks for \n and breaks line there
            if (fragment.contains("\\n")){
                fragment = fragment.substring(0,fragment.indexOf("\\n"));
                hasNewLine = true;
            }
            graphics.drawString(longWord? fragment+"-":fragment, 7, y);
            string = string.substring(fragment.length()+ (hasNewLine? 2:0));
            printDescription(string, y + 16, graphics, fontMetrics);
        }else{
            //Checks for \n and breaks line there
            if (string.contains("\\n")){
                String fragment = string;
                fragment = fragment.substring(0,fragment.indexOf("\\n"));
                graphics.drawString(fragment, 7, y);
                string = string.substring(fragment.length()+2);
                printDescription(string, y + 16, graphics, fontMetrics);
            }else {
                graphics.drawString(string, 7, y);
                if (y > getHeight() - 31) {
                    dynamicHeight = y + 31;
                    setBounds(getX(), getY(), getWidth(), dynamicHeight);
                }
            }
        }
    }
    public boolean equals(Object other){
        if (!(other instanceof DayMenuActivityGraphic)) {
            return false;
        }
        DayMenuActivityGraphic otherGraphic = (DayMenuActivityGraphic)other;
        return activity.equals(otherGraphic.activity);
    }

    private String formatStartStop(){
        if (activity.isOneDay()) {
            if (activity.isWholeDay()) {
                return "Heldagsaktivitet";
            }else{
                return activity.getStartHour() + ":" + (activity.getStartMinute()==0? "00":activity.getStartMinute()) + "  -  " + activity.getStopHour() + ":" + (activity.getStopMinute()==0? "00":activity.getStopMinute());
            }
        }else {
            if (activity.startWith(day)) {
                return "Idag " + (activity.getStartHour() == 0 && activity.getStartMinute() == 0 ? "" : activity.getStartHour() +
                        ":" + (activity.getStartMinute()==0? "00":activity.getStartMinute())) +
                        "  -  " + (activity.getStopDay().isRightAfter(day)? "Nästa dag":activity.getStopDay().toString("long")) + " " + (activity.getStopHour() == 0 && activity.getStopMinute() == 0 ? "" : activity.getStopHour() + ":" + (activity.getStopMinute()==0? "00":activity.getStopMinute()));
            }else if (activity.endsWith(day)){
                return (activity.getStartDay().isRightBefore(day)? "Föregående dag":activity.getStartDay().toString("long")) + " " + (activity.getStartHour() == 0 && activity.getStartMinute() == 0 ? "" : activity.getStartHour() +
                        ":" + (activity.getStartMinute()==0? "00":activity.getStartMinute())) + "  -  " + "Idag " + (activity.getStopHour() == 0 && activity.getStopMinute() == 0 ? "" : activity.getStopHour() + ":" + (activity.getStopMinute()==0? "00":activity.getStopMinute()));
            }else{
                return activity.getStartDay().toString("long") + "  -  " + activity.getStopDay().toString("long");
            }
        }
    }

    private boolean withinEditText(MouseEvent event){
        return event.getX() > getWidth()-70 && event.getX() < getWidth()-30 && event.getY() < 21 && event.getY() > 6;
    }
    private boolean withinRemoveText(MouseEvent event){
        return event.getX() > getWidth()-20 && event.getX() < getWidth()-5 && event.getY() < 21 && event.getY() > 6;
    }
    private boolean withinDescriptionText(MouseEvent event){
        return event.getX() > 7 && event.getX() < getWidth()-15 && event.getY() < 69 && event.getY() > 37;
    }
    private class EditRemoveListener extends MouseAdapter{
        public void mouseClicked(MouseEvent event){
            if (withinEditText(event)) {
                Activity edited = day.showNewActivityDialog(activity);
                dayMenu.integrateEdited(activity, edited);
            }
            if (withinRemoveText(event)){
                int answer = JOptionPane.showConfirmDialog(null, "Vill du ta bort " + activity.getHeader() + "?", "Ta bort aktivitet", JOptionPane.OK_CANCEL_OPTION);
                if (answer == JOptionPane.OK_OPTION) {
                    Activity removed = activity.remove();
                    dayMenu.integrateRemoved(removed);
                }
            }
        }
        public void mouseMoved(MouseEvent event) {
            if (withinEditText(event)) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                hoversOver = OverButton.EDIT;
            }else if(withinRemoveText(event)){
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                hoversOver = OverButton.REMOVE;
            }else if(withinDescriptionText(event)) {
                //SHOW TOOLTIP WITH FULL DESCRIPTION TEXT
            }else{
                setCursor(Cursor.getDefaultCursor());
                hoversOver = OverButton.NONE;
            }
            repaint();
        }
    }
    public String toString(){
        return activity.getHeader();
    }
}

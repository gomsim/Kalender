import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

public class Day extends JPanel implements Comparable<Day>{


    private Calendar date;
    private Month month;
    private Collection<Activity> activities = new ArrayList<>();
    private Collection<ActivityGraphic> activityGraphics = new ArrayList<>();

    private Theme theme;

    private Color colour;
    private PlusButton plusButton;
    private boolean hoveredOver;
    private boolean dimmed;

    private static final int SPACE = 21;
    private int lastPossibleStackHeight;

    private static final Category BLANK_CATEGORY = new Category("Blank", Theme.DIGIT_COLOUR);

    private int lastX, lastY;

    public Day(Calendar date, Month month, Theme theme){
        setLayout(null);
        this.date = date;
        this.month = month;
        this.theme = theme;
        colour = theme.getColour();
        setBorder(BorderFactory.createLineBorder(theme.getDayBorderColour()));
        setFont(new Font("Times New Roman", Font.PLAIN, 15));
        addMouseListener(new DayMouseListener());
        addMouseMotionListener(new DayMouseMotionListener());
        setBounds(0,0,1,117); //So first days can be generated during MainWindow construction (where MainWindow has not yet gotten its size to cascade to Day-objects)
    }

    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        graphics.setColor(Theme.DIGIT_COLOUR);
        String dateText = date.get(Calendar.DAY_OF_MONTH) + "";
        graphics.drawString(dateText,7,15);
        setBackground(colour);

        if (lastPossibleStackHeight != possibleStackHeight()) // BUGGAR UT!!!
            MainWindow.instance.showActivities();

        if (!activities.isEmpty() && activityStackHeight() > getHeight()) {
            graphics.setColor(Theme.DIGIT_COLOUR);
            graphics.fillOval((getWidth()/2)-12, getHeight()-10, 5, 5);
            graphics.fillOval((getWidth()/2)-2, getHeight()-10, 5, 5);
            graphics.fillOval((getWidth()/2)+8, getHeight()-10, 5, 5);
        }
        if (dimmed) {
            graphics.setColor(new Color(150,150,150,65));
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    public void setTheme(Theme theme){
        this.theme = theme;
    }
    private int activityStackHeight(){
        int activityStackHeight = SPACE;
        for (int i = 0; i < activities.size(); i++){
            activityStackHeight += ActivityGraphic.HEIGHT;
        }
        return activityStackHeight;
    }
    public void setDimmed(boolean dimmed){
        this.dimmed = dimmed;
    }
    private int possibleStackHeight(){
        int possibleHeight = 0;
        int activityStackHeight = SPACE;
        for (int i = 0; i < activities.size(); i++){
            activityStackHeight += ActivityGraphic.HEIGHT;
            if (activityStackHeight <= getHeight())
                possibleHeight += 1;
        }
        lastPossibleStackHeight = possibleHeight;
        return possibleHeight;
    }
    private int activityGraphicStackHeight(int numberOfGraphics){
        int activityGraphicStackHeight = SPACE;
        for (int i = 0; i < numberOfGraphics; i++){
            activityGraphicStackHeight += ActivityGraphic.HEIGHT;
        }
        return activityGraphicStackHeight;
    }
    public Collection<Activity> getActivities(){
        return activities;
    }

    public void showActivities(){
        clearActivityGraphics();
        int lastY = SPACE;
        ArrayList<Activity> tempStart = new ArrayList<>();
        ArrayList<Activity> tempOngoing = new ArrayList<>();
        ArrayList<Activity> toAdd = new ArrayList<>();
        for (Activity activity: activities){
            if (activity.getStartDay().equals(this)){
                tempStart.add(activity);
            }else{
                tempOngoing.add(activity);
            }
        }
        Collections.sort(tempStart, new ActivitySizeComparator());
        toAdd.addAll(tempStart);
        Collections.sort(tempOngoing, new ActivityPriorityComparator());
        for (Activity activity: tempOngoing){
            int priority = activity.getPriority();
            if (this.dayOfWeek() == 1)
                activity.setPriority(0);
            if (priority > toAdd.size()){
                for (int p = toAdd.size(); p < priority; p++){
                    toAdd.add(null);
                }
                toAdd.add(activity);
            }else{

                toAdd.add(priority, activity);
            }
        }
        for (int i = 0; i < toAdd.size(); i++){
            if (toAdd.get(i) != null)
                toAdd.get(i).setPriority(i);
        }
        for (int i = 0; i < toAdd.size(); i++){
            if (toAdd.get(i) != null && activityGraphicStackHeight(i+1) <= getHeight()) {
                ActivityGraphic graphic = new ActivityGraphic(toAdd.get(i), lastY, this, dimmed);
                add(graphic);
                activityGraphics.add(graphic);
            }
            lastY += SPACE;
        }
    }
    private void clearActivityGraphics(){
        for (ActivityGraphic activityGraphic: activityGraphics){
            remove(activityGraphic);
        }
        activityGraphics.clear();
    }

    public boolean isEmpty(){
        return activities.isEmpty();
    }
    public void setAsToday(){
        setBorder(BorderFactory.createLineBorder(theme.getDayPressedColour(), 2));
    }
    public Calendar getDate(){
        return date;
    }
    public Month getMonth(){
        return month;
    }
    public int compareTo(Day other){
        return date.compareTo(other.getDate());
    }
    public int dayOfWeek(){
        if (date.get(Calendar.DAY_OF_WEEK) == 1){
            return 7;
        }else{
            return date.get(Calendar.DAY_OF_WEEK)-1;
        }
    }
    public String dayNameOfWeekShort() throws IllegalArgumentException{
        switch(dayOfWeek()){
            case 1:
                return "Mån";
            case 2:
                return "Tis";
            case 3:
                return "Ons";
            case 4:
                return "Tor";
            case 5:
                return "Fre";
            case 6:
                return "Lör";
            case 7:
                return "Sön";
            default:
                throw new IllegalArgumentException();
        }
    }
    public String dayNameOfWeekLong() throws IllegalArgumentException{
        switch(dayOfWeek()){
            case 1:
                return "Måndag";
            case 2:
                return "Tisdag";
            case 3:
                return "Onsdag";
            case 4:
                return "Torsdag";
            case 5:
                return "Fredag";
            case 6:
                return "Lördag";
            case 7:
                return "Söndag";
            default:
                throw new IllegalArgumentException();
        }
    }
    public Activity showNewActivityDialog(Activity activity){
        NewActivityForm form = new NewActivityForm(this, activity);
        Activity newActivity = null;
        boolean inCorrect;
        do {
            inCorrect = false;
            try {
                int answer = JOptionPane.showConfirmDialog(MainWindow.instance, form, "Ny aktivitet", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (answer != JOptionPane.OK_OPTION)
                    return null;
                if (activity != null)
                    activity.remove();
                newActivity = createActivity(form);
            }catch(IllegalArgumentException e){
                if (e.getMessage().equals("nameEmpty")){
                    JOptionPane.showMessageDialog(MainWindow.instance, "En aktivitet måste ha ett namn", "Tomt fält", JOptionPane.ERROR_MESSAGE);
                }else if (e.getMessage().equals("impossibleTime")){
                    JOptionPane.showMessageDialog(MainWindow.instance, "En aktivitet kan inte sluta innan den börjar", "Omöjlig tidsrymd", JOptionPane.ERROR_MESSAGE);
                }
                inCorrect = true;
            }
        }while (inCorrect);
        MainWindow.instance.getDisplay().repaint();
        return newActivity;
    }
    public boolean before(Day other){
        return date.before(other.date);
    }
    public boolean after(Day other){
        return date.after(other.date);
    }
    private static Activity createActivity(NewActivityForm form){
        String header = form.getHeader();
        Category category = form.getCategory();
        String description = form.getDescription();
        int startHour = form.getTime(NewActivityForm.START, NewActivityForm.HOUR);
        int stopHour = form.getTime(NewActivityForm.STOP, NewActivityForm.HOUR);
        int startMinute = form.getTime(NewActivityForm.START, NewActivityForm.MINUTE);
        int stopMinute = form.getTime(NewActivityForm.STOP, NewActivityForm.MINUTE);

        Day startDay = form.getDay(NewActivityForm.START);
        Day stopDay = form.getDay(NewActivityForm.STOP);

        if (stopDay.getDate().before(startDay.getDate()) ||
                startDay.equals(stopDay) && ((stopHour != 0 && startHour > stopHour) || (startHour == stopHour && startMinute > stopMinute)))
            throw new IllegalArgumentException("impossibleTime");
        Day day = startDay;
        Activity newActivity;
        if (description.isEmpty()){
            newActivity = new NamedActivity(header, category, startHour, stopHour, startMinute, stopMinute, day, false);//Start och stop hour måste fixas
        }else{
            newActivity = new DescribedActivity(header, category, description, startHour, stopHour, startMinute, stopMinute, day, false); //Start och stop hour måste fixas
        }
        category.add(newActivity);
        day.addActivity(newActivity);
        while (!day.equals(stopDay)) {
            day = nextDay(day);
            newActivity.addDay(day);
            day.addActivity(newActivity);
        }
        MainWindow.instance.addActivity(newActivity);
        return newActivity;
    }
    public static Activity createActivity(String header, Category category, int startHour, int stopHour, int startMinute, int stopMinute, Day startDay, Day stopDay, String description, boolean subscribed){
        Day day = startDay;
        Activity newActivity;
        if (description == null){
            newActivity = new NamedActivity(header, category, startHour, stopHour, startMinute, stopMinute, day, subscribed);//Start och stop hour måste fixas
        }else{
            newActivity = new DescribedActivity(header, category, description, startHour, stopHour, startMinute, stopMinute, day, subscribed); //Start och stop hour måste fixas
        }
        category.add(newActivity);
        day.addActivity(newActivity);
        while (!day.equals(stopDay)) {
            day = nextDay(day, stopDay);
            newActivity.addDay(day);
            day.addActivity(newActivity);
        }
        return newActivity;
    }
    public void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public boolean equals(Object other){
        if (!(other instanceof Day))
            return false;
        Day otherDay = (Day)other;
        return date.equals(otherDay.date);
    }
    private void addActivity(Activity activity){
        activities.add(activity);
    }

    public static Day previousDay(Day day){
        Month month = day.month;
        Calendar date = day.getDate();
        try {
            return month.getDays()[date.get(Calendar.DAY_OF_MONTH) - 2];
        }catch(ArrayIndexOutOfBoundsException dayE){
            try{
                Month previousMonth = month.getYear().getMonth(month.getAsInt()-1);
                int lastDay = previousMonth.amountDays()-1;
                return previousMonth.getDays()[lastDay];
            }catch(ArrayIndexOutOfBoundsException monthE){
                return MainWindow.instance.getYear(month.getYear().getAsInt()-1).getMonth(11).getDays()[30];
            }
        }
    }
    public static Day nextDay(Day day){
        Month month = day.month;
        Calendar date = day.getDate();
        try {
            return month.getDays()[date.get(Calendar.DAY_OF_MONTH)];
        }catch(ArrayIndexOutOfBoundsException dayE){
            try{
                Month nextMonth = month.getYear().getMonth(month.getAsInt() + 1);
                return nextMonth.getDays()[0];
            }catch(ArrayIndexOutOfBoundsException monthE){
                return MainWindow.instance.getYear(month.getYear().getAsInt() + 1).getMonth(0).getDays()[0];
            }
        }
    }
    private static Day nextDay(Day day, Day stopDay){
        Month month = day.month;
        Calendar date = day.getDate();
        try {
            return month.getDays()[date.get(Calendar.DAY_OF_MONTH)];
        }catch(ArrayIndexOutOfBoundsException dayE){
            try{
                Month nextMonth = month.getYear().getMonth(month.getAsInt() + 1);
                return nextMonth.getDays()[0];
            }catch(ArrayIndexOutOfBoundsException monthE){
                return stopDay.getMonth().getDays()[0];
            }
        }
    }
    public boolean isRightAfter(Day day){
        Calendar other = Calendar.getInstance();
        other.setTime(day.date.getTime());
        other.add(Calendar.DATE, 1);
        return date.equals(other);

    }
    public boolean isRightBefore(Day day){
        Calendar other = Calendar.getInstance();
        other.setTime(day.date.getTime());
        Calendar thisDate = Calendar.getInstance();
        thisDate.setTime(date.getTime());
        thisDate.add(Calendar.DATE, 1);
        return thisDate.equals(other);
    }
    public boolean isHoveredOver(){
        return hoveredOver;
    }
    public void deSelect(){
        hoveredOver = false;
        colour = theme.getColour();
        if (plusButton != null)
            remove(plusButton);
        repaint();
    }
    private boolean hoveredOverDay(MouseEvent event){
        return (event.getX() > 0 && event.getX() < getWidth()) && (event.getY() > 0 && event.getY() < getHeight());
    }
    private boolean hoveredOverPlus(MouseEvent event){
        return (event.getX() > plusButton.getX() && event.getX() < plusButton.getX() + plusButton.getWidth())
                && (event.getY() > plusButton.getY() && event.getY() < plusButton.getY() + plusButton.getHeight());
    }
    public class DayMouseListener extends MouseAdapter{
        public void mouseEntered(MouseEvent event){
            if (!dimmed) {
                hoveredOver = true;
                colour = theme.getDayHoverColour();
                plusButton = new PlusButton(getBounds());
                add(plusButton);
            }
        }
        public void mouseExited(MouseEvent event){
            if (!dimmed) {
                deSelect();
            }
        }
        public void mousePressed(MouseEvent event){
            if (!dimmed) {
                colour = theme.getDayPressedColour();
                repaint();
            }
        }
        public void mouseReleased(MouseEvent event){
            if (!dimmed) {
                if (hoveredOverDay(event)) {
                    colour = theme.getDayHoverColour();
                    if (!hoveredOverPlus(event))
                        plusButton.setHoverOver(false);
                } else {
                    colour = theme.getColour();
                }
            }
        }
        public void mouseClicked(MouseEvent event){
            if (!dimmed) {
                if (event.getButton() == MouseEvent.BUTTON1) {
                    if (hoveredOverPlus(event)) {
                        showNewActivityDialog(null);
                    } else if (!isEmpty()) {
                        deSelect();
                        MainWindow.instance.displayDayMenu(Day.this);
                    }
                }
            }
        }
    }
    public int getLastX(){
        return lastX;
    }
    public int getLastY(){
        return lastY;
    }
    private class DayMouseMotionListener implements MouseMotionListener{
        public void mouseMoved(MouseEvent event){
            if (!dimmed) {
                if (hoveredOverPlus(event)) {
                    plusButton.setHoverOver(true);
                } else {
                    plusButton.setHoverOver(false);
                }
                repaint();
                lastX = event.getXOnScreen();
                lastY = event.getYOnScreen();
            }
        }
        public void mouseDragged(MouseEvent event){

        }
    }
    private class ActivitySizeComparator implements Comparator<Activity>{
        public int compare(Activity first, Activity second){
            if (second.getLength() != first.getLength())
                return second.getLength() - first.getLength();
            return first.compareTo(second
            );
        }
    }
    private class ActivityPriorityComparator implements Comparator<Activity>{
        public int compare(Activity first, Activity second){
            return first.getPriority() - second.getPriority();
        }
    }

    public int hashCode(){
        return date.hashCode();
    }
    public String toString(){
        return dayNameOfWeekShort() + " " + date.get(Calendar.DAY_OF_MONTH) + " " + month + " " + date.get(Calendar.YEAR);
    }
    public String toString(String format){
        if (format.equals("long"))
            return dayNameOfWeekLong() + " " + date.get(Calendar.DAY_OF_MONTH) + " " + month + " " + date.get(Calendar.YEAR);
        else if (format.equals("digit")){
            return date.get(Calendar.DAY_OF_MONTH) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.YEAR);
        }
        return toString();
    }
}
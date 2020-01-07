import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public abstract class Activity implements Comparable<Activity>{
    private String header;

    private Category category;
    private int startHour, stopHour, startMinute, stopMinute;
    private ArrayList<Day> days = new ArrayList<>();
    private int priority;
    private boolean subscribed;

    public Activity(String header, Category category, int startHour, int stopHour, int startMinute, int stopMinute, Day day, boolean subscribed)throws IllegalArgumentException{
        if (header == null || header.isEmpty())
            throw new IllegalArgumentException("nameEmpty");
        days.add(day);
        this.header = header;
        this.category = category;
        this.startHour = startHour;
        this.stopHour = stopHour;
        this.startMinute = startMinute;
        this.stopMinute = stopMinute;
        this.subscribed = subscribed;
    }
    public Activity remove(){
        for (Day day: days){
            day.removeActivity(this);
            day.showActivities();
        }
        MainWindow.instance.removeActivity(this);
        return this;
    }
    public void setCategory(Category category){
        this.category.remove(this);
        category.add(this);
        this.category = category;
    }
    public Day getDay(int index){
        return days.get(index);
    }
    public Day getStartDay(){
        return days.get(0);
    }
    public Day getStopDay(){
        return days.get(days.size()-1);
    }
    public void addDay(Day day){
        days.add(day);
    }


    public boolean isSubscribed(){
        return subscribed;
    }
    public int getPriority(){
        return priority;
    }
    public void setPriority(int priority){
        this.priority = priority;
    }
    public int getLength(){
        return days.size();
    }
    public boolean isStart(Day day){
        return days.indexOf(day) == 0;
    }
    public boolean isEnd(Day day){
        return days.indexOf(day) == days.size()-1;
    }
    public boolean isOneDay(){
        return days.size() == 1;
    }
    public String getHeader(){
        return header;
    }
    public Category getCategory(){
        return category;
    }
    public int hashCode(){
        return startHour + startMinute*100 + stopHour*10000 + stopMinute*1000000 + header.hashCode() + category.hashCode();
    }
    public boolean equals(Object other){
        if (!(other instanceof Activity))
            return false;
        Activity otherActivity = (Activity)other;
        return other == this || (header.equals(otherActivity.header) && category.equals(otherActivity.category) && startHour == otherActivity.startHour && stopHour == otherActivity.stopHour && startMinute == otherActivity.startMinute && stopMinute == otherActivity.stopMinute && getStartDay().equals(otherActivity.getStartDay()) && getStopDay().equals(otherActivity.getStopDay()));
    }
    public int compareTo(Activity other){
        if (!getStartDay().getDate().equals(other.getStartDay().getDate()))
            if(getStartDay().getDate().before(other.getStartDay().getDate()))
                return -1;
            else
                return 1;
        if (startHour != other.startHour)
            return startHour - other.startHour;
        if (startMinute != other.startMinute)
            return startMinute - other.startMinute;
        if (stopHour != other.stopHour)
            return stopHour - other.stopHour;
        return stopMinute - other.stopMinute;
    }
    public boolean startWith(Day day){
        return days.get(0).equals(day);
    }
    public boolean endsWith(Day day){
        return days.get(days.size()-1).equals(day);
    }
    public int getStartHour(){
        return startHour;
    }
    public int getStopHour(){
        return stopHour;
    }
    public int getStartMinute(){
        return startMinute;
    }
    public int getStopMinute(){
        return stopMinute;
    }
    public boolean isWholeDay(){
        return startHour == 0 && stopHour == 0 && startMinute == 0 && stopMinute == 0;
    }

    public abstract String toString();
}

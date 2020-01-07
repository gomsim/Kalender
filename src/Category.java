import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class Category {

    private Color colour;
    private String name;
    private ArrayList<Activity> activities = new ArrayList<>();

    public Category(String name, Color colour) throws IllegalArgumentException{
        this.name = name;
        if (name.length() > 15)
            throw new IllegalArgumentException();
        this.colour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 90);
    }

    public Color getColour(){
        return colour;
    }
    public Color getBorderColour(){
        int red = colour.getRed();
        int green = colour.getGreen();
        int blue = colour.getBlue();

        return new Color(red>99? red-100:0, green>99? green-100:0, blue>99? blue-100:0);
    }
    public Color getHardColour(){
        int red = colour.getRed();
        int green = colour.getGreen();
        int blue = colour.getBlue();

        return new Color(red, green, blue, 150);
    }
    public void add(Activity activity){
        activities.add(activity);
    }
    public Activity remove(Activity activity){
        activities.remove(activity);
        return activity;
    }
    public boolean contains(Activity activity){
        return activities.contains(activity);
    }
    public boolean isEmpty(){
        return activities.isEmpty();
    }
    public ArrayList<Activity> getActivities(){
        return activities;
    }
    public void setColour(Color colour) {
        this.colour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 90);
    }
    public void setName(String newName){
        if (isSubscribed()){
            MainWindow.instance.getUser().getSubCategories().remove(this.name);
            MainWindow.instance.getUser().getSubCategories().put(newName, this);
        }else{
            MainWindow.instance.getUser().getCategories().remove(this.name);
            MainWindow.instance.getUser().getCategories().put(newName, this);
        }
        this.name = newName;
    }
    public boolean isSubscribed(){
        return MainWindow.instance.getUser().getSubCategories().containsValue(this);
    }

    public String getName(){
        return name;
    }

    public int hashCode(){
        return colour.hashCode();
    }
    public boolean equals(Object other){
        if (!(other instanceof Category))
            return false;
        Category otherCategory = (Category)other;
        return otherCategory == this || colour.equals(otherCategory.getColour());
    }
    public String toString(){
        return name;
    }
    public String toString(boolean detailed){
        if (detailed) {
            System.out.println("Preinting les colores: " + colour.getRed() + "##BREAK@" + colour.getGreen() + "##BREAK@" + colour.getBlue());
            return name + "##BREAK@" + colour.getRed() + "##BREAK@" + colour.getGreen() + "##BREAK@" + colour.getBlue();
        }else
            return toString();
    }
}

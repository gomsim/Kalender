import javax.swing.*;
import java.awt.*;
import java.util.*;

public class User {

    private String name;
    private HashMap<String, Category> categories = new HashMap<>();
    private HashMap<String, Category> subCategories = new HashMap<>();
    private String filePath;
    private HashMap<String, String> subscriptions = new HashMap<>();

    public User(String name, String filePath){
        this.name = name;
        this.filePath = filePath;
    }
    public User(String name){
        this(name, null);
    }
    public HashMap<String, Category> getCategories(){
        return categories;
    }
    public Category getCategory(String name){
        return categories.get(name);
    }
    public void addCategory(Category category){
        System.out.println("adding category");
        categories.put(category.getName(), category);
    }
    public void removeCategory(Category category){
        System.out.println(category);
        categories.remove(category.getName());
        System.out.println(categories);
    }

    public HashMap<String, Category> getSubCategories(){
        return subCategories;
    }
    public Category getSubCategory(String name){
        return subCategories.get(name);
    }
    public void addSubCategory(Category category){
        subCategories.put(category.getName(), category);
    }
    public void removeSubCategory(Category category){
        subCategories.remove(category.getName());
    }

    public void add(String subName, String url){
        subscriptions.put(subName, url);
    }
    public void removeSubscription(String subName){
        subscriptions.remove(subName);
    }
    public HashMap<String, String> getSubs(){
        return subscriptions;
    }

    public String getName(){
        return name;
    }
    public void setFilePath(String path){
        this.filePath = path;
    }
    public String getFilePath(){
        return filePath;
    }

    public String toString(){
        return name;
    }
}

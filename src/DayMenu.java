import com.sun.tools.javac.Main;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class DayMenu extends JPanel {

    Day day;
    JLabel headerLabel;
    JPanel activitiesPanel;
    BackButton backButton;
    PlusButton plusButton;
    Theme currentTheme;

    private ArrayList<Activity> activities = new ArrayList<>();

    private ArrayList<DayMenuActivityGraphic> activityGraphics = new ArrayList<>();

    public DayMenu(Day day, Theme theme){
        this.day = day;
        currentTheme = theme;
        addAncestorListener(new AddedToGlassPaneListener());

        addMouseListener(new MouseEventInterceptor());
        setLayout(new BorderLayout());

        addKeyListener(new CloseAddKeyListener());
        setFocusable(true);
        setRequestFocusEnabled(true);
        requestFocusInWindow();

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
        northPanel.setBackground(theme.getColour());
        add(northPanel, BorderLayout.NORTH);
        JPanel backPanel = new JPanel();
        backPanel.setBackground(theme.getColour());

        backButton = new BackButton(theme, day);
        backButton.addMouseListener(new ButtonListener());
        backButton.addMouseMotionListener(new ButtonListener());
        northPanel.add(backButton); //PLACEHOLDER
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(theme.getColour());
        headerLabel = new JLabel(day.toString("long").toUpperCase());
        headerLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        headerLabel.setForeground(new Color(0,0,0,90));
        headerPanel.add(headerLabel); //Placeholder
        northPanel.add(headerPanel);
        plusButton = new PlusButton(getBounds());
        plusButton.addMouseListener(new ButtonListener());
        plusButton.addMouseMotionListener(new ButtonListener());
        northPanel.add(plusButton);
        plusButton.setScale(2.7);
        plusButton.allignRight();
        plusButton.setCursor(Cursor.getDefaultCursor());
        plusButton.setHoverColour(theme.getDayHoverColour());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        activitiesPanel = new JPanel();
        scrollPane.getViewport().add(activitiesPanel);
        scrollPane.setBorder(null);
        activitiesPanel.setBackground(theme.getColour());
        activitiesPanel.setLayout(null);
        add(centerPanel, BorderLayout.CENTER);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel blankPanel = new JPanel();
        blankPanel.setBackground(theme.getColour());
        centerPanel.add(blankPanel, BorderLayout.NORTH);

        showActivities();
    }
    public void showActivities(){
        System.out.println("showing activities");
        activities = new ArrayList<>(day.getActivities());
        Collections.sort(activities);
        activitiesPanel.removeAll();
        activityGraphics.clear();
        int lastY = 20;
        for (Activity activity: activities){
            DayMenuActivityGraphic graphic = new DayMenuActivityGraphic(activity, lastY, day, this, currentTheme);
            System.out.println(graphic.getHeight());
            lastY += graphic.getHeight() + 5;
            activityGraphics.add(graphic);
            activitiesPanel.add(graphic);
            activitiesPanel.setPreferredSize(new Dimension(0,lastY));
        }
        repaint();
    }
    public void repositionActivityGraphics(){
        System.out.println("Repositioning activitygraphics");
        System.out.println(activityGraphics);
        int lastY = 20;
        for (DayMenuActivityGraphic graphic: activityGraphics){
            graphic.setBounds(graphic.getX(), lastY, graphic.getWidth(), graphic.getHeight());
            System.out.println(graphic.getHeight());
            lastY += graphic.getHeight()/*DayMenuActivityGraphic.dynamicHeight*/ + 5;
            activitiesPanel.setPreferredSize(new Dimension(0,lastY));
        }
        repaint();
    }
    public void integrateAdded(Activity newActivity){
        System.out.println(newActivity);
        if (newActivity == null)
            return;
        addActivity(newActivity);
        repositionActivityGraphics();
    }
    public void integrateEdited(Activity old, Activity edited){
        System.out.println(edited);
        if (edited == null)
            return;
        removeActivity(old);
        addActivity(edited);
        repositionActivityGraphics();
    }
    public void integrateRemoved(Activity removed){
        removeActivity(removed);
        repositionActivityGraphics();
    }
    private void addActivity(Activity newActivity){
        DayMenuActivityGraphic graphic = new DayMenuActivityGraphic(newActivity, 0, day, this,currentTheme);
        activityGraphics.add(graphic);
        activitiesPanel.add(graphic);
    }
    private void removeActivity(Activity removed){
        DayMenuActivityGraphic graphic = new DayMenuActivityGraphic(removed, 0, day, this,currentTheme);
        activityGraphics.remove(graphic);
        for (Component component: activitiesPanel.getComponents())
            if (component.equals(graphic))
                activitiesPanel.remove(component);
        activities.remove(removed);
    }

    private void close(){
        MainWindow.instance.displayMonth(day.getMonth());
        MainWindow.instance.requestFocus();
        setVisible(false);
    }

    private class ButtonListener extends MouseAdapter{
        public void mouseClicked(MouseEvent event){
            JComponent button = (JComponent)event.getSource();
            if (button == backButton) {
                close();
            }else if (button == plusButton){
                Activity newActivity = day.showNewActivityDialog(null);
                integrateAdded(newActivity);
            }
        }
        public void mouseExited(MouseEvent event){
            JComponent button = (JComponent)event.getSource();
            if (button == backButton) {
                backButton.setState(BackButton.State.NORMAL);
            }else if (button == plusButton){
                plusButton.setHoverOver(false);
            }
        }
        public void mousePressed(MouseEvent event){
            JComponent button = (JComponent)event.getSource();
            if (button == backButton) {
                backButton.setState(BackButton.State.PRESSED);
            }
        }
        public void mouseReleased(MouseEvent event){
            JComponent button = (JComponent)event.getSource();
            if (button == backButton && backButton.hoveredOver(event)){
                backButton.setState(BackButton.State.HOVERED);
            }
        }
        public void mouseMoved(MouseEvent event){
            JComponent button = (JComponent)event.getSource();
            if (button == backButton) {
                if (backButton.hoveredOver(event)){
                    backButton.setState(BackButton.State.HOVERED);
                }else{
                    backButton.setState(BackButton.State.NORMAL);
                }
            }else if (button == plusButton && plusButton.hoveredOver(event)){
                plusButton.setHoverOver(true);
            }else if (button == plusButton && !plusButton.hoveredOver(event)){
                plusButton.setHoverOver(false);
            }
        }
    }
    private class CloseAddKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent event){
            if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                close();
            }else if(event.getKeyCode() == KeyEvent.VK_ESCAPE){
                MainWindow.instance.saveAndExit();
            }else if (event.getKeyCode() == KeyEvent.VK_ENTER){
                Activity newActivity = day.showNewActivityDialog(null);
                integrateAdded(newActivity);
            }else if(event.getKeyCode() == KeyEvent.VK_LEFT){
                close();
                MainWindow.instance.displayDayMenu(Day.previousDay(day));
            }else if(event.getKeyCode() == KeyEvent.VK_RIGHT){
                close();
                MainWindow.instance.displayDayMenu(Day.nextDay(day));
            }
        }
    }

    private class MouseEventInterceptor extends MouseAdapter {
        public void mouseClicked(MouseEvent event){
            event.consume();
        }
        public void mousePressed(MouseEvent event){
            event.consume();
        }
        public void mouseReleased(MouseEvent event){
            event.consume();
        }
        public void mouseEntered(MouseEvent event){
            event.consume();
        }
        public void mouseExited(MouseEvent event){
            event.consume();
        }
    }
    private class AddedToGlassPaneListener implements AncestorListener {
        public void ancestorMoved(AncestorEvent event){}
        public void ancestorRemoved(AncestorEvent event){}
        public void ancestorAdded(AncestorEvent event){
            Window window = SwingUtilities.getWindowAncestor(DayMenu.this);
            if (window instanceof MainWindow){
                repositionActivityGraphics();
            }
        }
    }
}

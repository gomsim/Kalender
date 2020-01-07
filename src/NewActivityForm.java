import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class NewActivityForm extends JPanel {

    private Day thisDay;
    private Dialog dialog;

    public static final int START = 0, STOP = 1, HOUR = 2, MINUTE = 3;

    private Set<Day> days = new TreeSet<>();
    private Set<Category> categories = new HashSet<>();
    private DefaultComboBoxModel<Day> startBoxModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel<Day> stopBoxModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel<Category> categoryBoxModel = new DefaultComboBoxModel();
    private static final Integer[] HOURS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 0};
    private static final Integer[] MINUTES = {0, 15, 30, 45};

    private JTextField headerField = new JTextField(15);
    private JTextArea descriptionArea = new JTextArea(3, 81);
    private JComboBox<Day> startDayComboBox = new JComboBox<>(startBoxModel), stopDayComboBox = new JComboBox<>(stopBoxModel);
    private JComboBox<Integer> startHourComboBox = new JComboBox<>(HOURS), stopHourComboBox = new JComboBox<>(HOURS);
    private JComboBox<Integer> startMinuteComboBox = new JComboBox<>(MINUTES), stopMinuteComboBox = new JComboBox<>(MINUTES);
    private JComboBox<Category> categoryJComboBox = new JComboBox<>(categoryBoxModel);
    private JCheckBox detailedViewCheckBox = new JCheckBox("Detaljerad vy");

    private JPanel descriptionPanel;
    private JPanel startHourPanel;
    private JPanel startMinutePanel;
    private JPanel stopHourPanel;
    private JPanel stopMinutePanel;

    public NewActivityForm(Day day, Activity preexistingActivity){
        thisDay = day;
        detailedViewCheckBox.addActionListener(new checkBoxListener());

        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        add(northPanel, BorderLayout.NORTH);
        JPanel headerPanel = new JPanel();
        headerPanel.setBorder(BorderFactory.createTitledBorder("Rubrik"));
        northPanel.add(headerPanel);
        headerPanel.add(headerField);
        JPanel categoryPanel = new JPanel();
        categoryPanel.setBorder(BorderFactory.createTitledBorder("Category"));
        categoryJComboBox.addActionListener(new NewCategoryListener());
        categoryPanel.add(categoryJComboBox);
        northPanel.add(categoryPanel);
        northPanel.add(detailedViewCheckBox);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);
        descriptionPanel = new JPanel();
        descriptionPanel.setBorder(BorderFactory.createTitledBorder("Beskrivning"));
        //descriptionArea.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        descriptionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.getVerticalScrollBar().setUnitIncrement(3);
        descriptionPanel.add(scrollPane);
        descriptionPanel.setVisible(false);
        centerPanel.add(descriptionPanel, BorderLayout.NORTH);

        JPanel timePanel = new JPanel();
        centerPanel.add(timePanel, BorderLayout.SOUTH);

        /////////////////////////////
        populateComboBoxes(day.getMonth().getYear());
        /////////////////////////////


        JPanel startPanel = new JPanel();
        startPanel.setBorder(BorderFactory.createTitledBorder("Från"));
        timePanel.add(startPanel);
        JPanel startDayPanel = new JPanel();
        startDayPanel.setBorder(BorderFactory.createTitledBorder("Dag"));
        startPanel.add(startDayPanel);
        startDayComboBox.setModel(startBoxModel);
        startDayPanel.add(startDayComboBox);
        //startDayComboBox.setSelectedItem(day);
        startHourPanel = new JPanel();
        startHourPanel.setBorder(BorderFactory.createTitledBorder("Timme"));
        startHourPanel.setVisible(false);
        startPanel.add(startHourPanel);
        startHourPanel.add(startHourComboBox);
        startMinutePanel = new JPanel();
        startMinutePanel.setBorder(BorderFactory.createTitledBorder("Kvart"));
        startMinutePanel.setVisible(false);
        startPanel.add(startMinutePanel);
        startMinutePanel.add(startMinuteComboBox);

        JPanel stopPanel = new JPanel();
        stopPanel.setBorder(BorderFactory.createTitledBorder("Till"));
        timePanel.add(stopPanel);
        JPanel stopDayPanel = new JPanel();
        stopDayPanel.setBorder(BorderFactory.createTitledBorder("Dag"));
        stopPanel.add(stopDayPanel);
        stopDayComboBox.setModel(stopBoxModel);
        stopDayPanel.add(stopDayComboBox);
        stopDayComboBox.setSelectedItem(day);
        stopHourPanel = new JPanel();
        stopHourPanel.setBorder(BorderFactory.createTitledBorder("Timme"));
        stopHourPanel.setVisible(false);
        stopPanel.add(stopHourPanel);
        stopHourPanel.add(stopHourComboBox);
        stopMinutePanel = new JPanel();
        stopMinutePanel.setBorder(BorderFactory.createTitledBorder("Kvart"));
        stopMinutePanel.setVisible(false);
        stopPanel.add(stopMinutePanel);
        stopMinutePanel.add(stopMinuteComboBox);

        if (preexistingActivity != null){
            headerField.setText(preexistingActivity.getHeader());
            categoryJComboBox.setSelectedItem(preexistingActivity.getCategory());
            if (preexistingActivity instanceof DescribedActivity)
                descriptionArea.setText(((DescribedActivity)preexistingActivity).getDescription().replaceAll("\\\\n",System.lineSeparator()));
            startDayComboBox.setSelectedItem(preexistingActivity.getStartDay());
            startHourComboBox.setSelectedItem(preexistingActivity.getStartHour());
            startMinuteComboBox.setSelectedItem(preexistingActivity.getStartMinute());
            stopDayComboBox.setSelectedItem(preexistingActivity.getStopDay());
            stopHourComboBox.setSelectedItem(preexistingActivity.getStopHour());
            stopMinuteComboBox.setSelectedItem(preexistingActivity.getStopMinute());
        }

        addAncestorListener(new ResizeListener());
    }

    private void showDetailedView(boolean show){
        descriptionPanel.setVisible(show);
        startHourPanel.setVisible(show);
        startMinutePanel.setVisible(show);
        stopHourPanel.setVisible(show);
        stopMinutePanel.setVisible(show);
        if (show)
            headerField.setColumns(42);
        else
            headerField.setColumns(15);
    }

    private void populateComboBoxes(Year year){
        for (int y = -1; y < 2; y++){
            Year searchYear = MainWindow.instance.getYear(year.getAsInt()+y);
            for (int m = 0; m < 12; m++) {
                days.addAll(Arrays.asList(searchYear.getMonth(m).getDays()));
            }
        }
        startBoxModel.removeAllElements();
        stopBoxModel.removeAllElements();
        for (Day d: days){
            startBoxModel.addElement(d);
            stopBoxModel.addElement(d);
        }
        categories.clear();
        categories.addAll(MainWindow.instance.getUser().getCategories().values());
        categoryBoxModel.removeAllElements();
        for (Category c: categories){
            categoryBoxModel.addElement(c);
        }
        categoryBoxModel.addElement(new Category("Ny", Color.WHITE));
        startDayComboBox.setSelectedItem(thisDay);
        stopDayComboBox.setSelectedItem(thisDay);
    }

    public String getHeader(){
        return headerField.getText();
    }
    public String getDescription(){
        return descriptionArea.getText();
    }
    public Category getCategory(){
        return (Category)categoryJComboBox.getSelectedItem();
    }
    public Day getDay(int startStop) throws IllegalArgumentException{
        if (startStop == START){
            return (Day) startDayComboBox.getSelectedItem();
        }else if (startStop == STOP){
            return (Day) stopDayComboBox.getSelectedItem();
        }else{
            throw new IllegalArgumentException();
        }
    }
    public int getTime(int startStop, int hourMinute) throws IllegalArgumentException{
        if (startStop == START){
            if (hourMinute == HOUR){
                return (int) startHourComboBox.getSelectedItem();
            }else if(hourMinute == MINUTE){
                return (int) startMinuteComboBox.getSelectedItem();
            }else{
                throw new IllegalArgumentException();
            }
        }else if (startStop == STOP){
            if (hourMinute == HOUR){
                return (int) stopHourComboBox.getSelectedItem();
            }else if(hourMinute == MINUTE){
                return (int) stopMinuteComboBox.getSelectedItem();
            }else{
                throw new IllegalArgumentException();
            }
        }else{
            throw new IllegalArgumentException();
        }
    }

    private class checkBoxListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            dialog.setVisible(false);
            if (detailedViewCheckBox.isSelected()){
                showDetailedView(true);
                dialog.setSize(720, 348);
            }else{
                showDetailedView(false);
                dialog.setSize(456, 267);
            }
            dialog.setLocationRelativeTo(null);
            validate();
            repaint();
            dialog.setVisible(true);
        }
    }
    private class ResizeListener implements AncestorListener{
        public void ancestorMoved(AncestorEvent event){

        }
        public void ancestorRemoved(AncestorEvent event){

        }
        public void ancestorAdded(AncestorEvent event){
            Window window = SwingUtilities.getWindowAncestor(NewActivityForm.this);
            if (window instanceof Dialog){
                dialog = (Dialog)window;
                headerField.requestFocusInWindow();
            }
        }
    }
    private class NewCategoryListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if (categoryJComboBox.getSelectedItem().toString().equals("Ny")){
                Category newCategory = CategoryChooser.showNewCategoryPane(NewActivityForm.this);
                if (newCategory != null) {
                    populateComboBoxes(thisDay.getMonth().getYear());
                    categoryJComboBox.setSelectedItem(newCategory);
                }else{
                    categoryJComboBox.setSelectedIndex(0);
                }
            }
        }
    }
}

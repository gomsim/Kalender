import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class MainWindow extends JFrame {


    private User user;
    private HashMap<String, String> recentUsers = new HashMap<>();
    private RecentListModel recentListModel;

    private Map<Integer, Year> years = new TreeMap<>();
    private Month displayedMonth;
    private Collection<Activity> activities = new ArrayList<>();
    private Collection<Activity> subActivities = new ArrayList<>();

    private JPanel display = new JPanel();
    private JLabel monthLabel = new JLabel();
    private GridLayout grid = new GridLayout(5, 7);
    private JFileChooser fileChooser = new JFileChooser(".");
    private ArrowButton leftButton;
    private ArrowButton rightButton;

    private JMenuItem saveAsItem = new JMenuItem("Spara som...");
    private JMenuItem openItem = new JMenuItem("Öppna");
    private JMenuItem newItem = new JMenuItem("Ny profil");
    private JMenuItem categoriesItem = new JMenuItem("Kategorier");

    private HashMap<String, Theme> themes = new HashMap<>();
    private Theme theme;
    private boolean updateUnsuccessful;

    public static MainWindow instance;

    public static void main(String[] args) {
        setLookAndFeel();
        instance = new MainWindow();
    }

    public MainWindow(){
        setSize(900, 703);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new CloseWindowListener());
        ImageIcon icon = new ImageIcon("Icon.png");
        setIconImage(icon.getImage());
        addKeyListener(new NavigationKeyListener());

        generateThemes();
        theme = loadTheme();

        JMenuBar menuBar = new JMenuBar();
        menuBar.setVisible(false);
        setJMenuBar(menuBar);
        JMenu archiveMenu = new JMenu("Arkiv");
        JMenu settingsMenu = new JMenu("Inställningar");
        menuBar.add(archiveMenu);
        archiveMenu.add(newItem);
        newItem.addActionListener(new NewProfileListener());
        archiveMenu.add(openItem);
        openItem.addActionListener(new OpenProfileListener());
        archiveMenu.add(saveAsItem);
        saveAsItem.addActionListener(new SaveAsListener());
        readRecentUsers(archiveMenu);


        menuBar.add(settingsMenu);
        JMenu themeMenu = new JMenu("Tema");
        settingsMenu.add(categoriesItem);
        categoriesItem.addActionListener(new CategoriesListener());
        JMenu subscriptionsMenu = new JMenu("Prenumerationer");
        settingsMenu.add(subscriptionsMenu);
        JMenuItem subscriptionsItem = new JMenuItem("Hantera");
        subscriptionsItem.addActionListener(new SubscriptionsListener());
        subscriptionsMenu.add(subscriptionsItem);
        JMenuItem updateSubscriptionItem = new JMenuItem("Upptadera");
        updateSubscriptionItem.addActionListener(new UpdateSubscriptionsListener());
        subscriptionsMenu.add(updateSubscriptionItem);
        settingsMenu.add(themeMenu);
        for (Theme theme: themes.values()){
            JMenuItem item = new JMenuItem(theme.toString());
            item.addActionListener(new ThemeMenuListener());
            themeMenu.add(item);
            if (theme.equals(this.theme))
                item.setEnabled(false);
        }

        FileFilter saveFilter = new FileNameExtensionFilter("Sparfiler", "save");
        fileChooser.setFileFilter(saveFilter);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);

        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(theme.getDayBorderColour());
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
        controlPanel.add(navigationPanel, BorderLayout.CENTER);

        JPanel monthPanel = new JPanel();
        monthPanel.setBackground(theme.getDayBorderColour());
        leftButton = new ArrowButton(true, theme);
        rightButton = new ArrowButton(false, theme);
        navigationPanel.add(leftButton);
        navigationPanel.add(monthPanel);
        navigationPanel.add(rightButton);
        leftButton.addMouseListener(new NavigationButtonListener());
        rightButton.addMouseListener(new NavigationButtonListener());

        monthLabel.setFont(new Font("Times New Roman", Font.BOLD, 17));
        monthLabel.setForeground(Theme.DIGIT_COLOUR);
        monthLabel.addMouseListener(new monthLabelListener());
        monthLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        monthPanel.add(monthLabel);

        JPanel weekdaysPanel = new JPanel();
        weekdaysPanel.setBackground(theme.getDayBorderColour());
        controlPanel.add(weekdaysPanel, BorderLayout.SOUTH);
        weekdaysPanel.setLayout(new GridLayout(1, 7));
        String[] dayStrings = {"Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag"};
        Font fontWeekdays = new Font("Times New Roman", Font.BOLD, 14);
        for (int i = 0; i < dayStrings.length; i++){
            JPanel panel = new JPanel();
            panel.setBackground(theme.getDayBorderColour());
            JLabel weekdayLabel = new JLabel(dayStrings[i]);
            weekdayLabel.setForeground(Theme.DIGIT_COLOUR);
            weekdayLabel.setFont(fontWeekdays);
            panel.add(weekdayLabel);
            weekdaysPanel.add(panel);
        }

        display.setBackground(theme.getDayBorderColour());
        display.setLayout(grid);
        add(display, BorderLayout.CENTER);

        setUpSession();

        setVisible(true);
    }
    private static void setLookAndFeel(){
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
            System.out.println("UnsupportedLookAndFeelException");
        }
        catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        }
        catch (InstantiationException e) {
            System.out.println("InstantiationException");
        }
        catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException");
        }
    }
    private void generateThemes(){
        themes.put("Standard(bright)", new Theme("Standard(bright)", new Color(250, 250, 250)));
        themes.put("Standard", new Theme("Standard", new Color(245, 245, 245)));
        themes.put("Paper", new Theme("Paper", new Color(255, 255, 204)));
        themes.put("Latte", new Theme("Latte", new Color(240, 234, 214)));
        themes.put("Coffee", new Theme("Coffee", new Color(216, 201, 164)));
        themes.put("Sky", new Theme("Sky", new Color(204, 255, 255)));
        themes.put("Night", new Theme("Night", new Color(153, 153, 153)));
    }
    private Theme loadTheme(){
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("Configuration.txt"));
            reader.readLine();
            line = reader.readLine();
        }catch (FileNotFoundException e){

        }catch (IOException e){

        }
        if (themes.get(line) != null){
            return themes.get(line);
        }else{
            return themes.get("Standard");
        }
    }
    public Year getYear(int yearAsInt) {
        Year year = years.get(yearAsInt);
        if (year == null)
            return createYear(yearAsInt);
        return year;
    }
    public void setUpSession(){
        String path = getLastUserPath();
        if (path == null){
            createNewUser(showNewUserPane());
        }else{
            startNewUser(path);
        }
        displayMonth(getTodaysMonth());
    }
    private void readRecentUsers(JMenu menu){
        try {
            JMenu recentMenu = new JMenu("Senaste");
            menu.add(recentMenu);
            recentListModel = new RecentListModel(recentMenu);
            BufferedReader bufferedReader = new BufferedReader(new FileReader("Configuration.txt"));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                if (line.equals("Recent:")){
                    ArrayList<String> toAdd = new ArrayList<>();
                    while((line = bufferedReader.readLine()) != null){
                        String[] tokens = line.split("##BREAK@");
                        toAdd.add(tokens[0]);
                        recentUsers.put(tokens[0], tokens[1]);
                    }
                    recentListModel.addAll(toAdd);
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("FileNotFound");
        }catch(IOException e){
            System.out.println("IOException");
        }
    }
    private void tryURL(String urlString){
        try {
            URL url = new URL(urlString);
            URLConnection connection= url.openConnection();
            connection.connect();
            System.out.println("getLastModified: " + connection.getLastModified());
            System.out.println("getHeaderField('Last-Modified'): " + connection.getHeaderField("Last-Modified"));
        }catch (MalformedURLException e){
        System.out.println("Malformed URL");
        }catch (IOException e){
        System.out.println("IO-exception");
        }
    }
    public void downloadOnlineCalendar(String urlName, String urlString) throws IOException{
        URL url = new URL(urlString);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("BEGIN:VEVENT")) {
                String header = null;
                String description = null;
                String categoryName = null;
                int startYear = 0;
                int startMonth = 0;
                int startDayOfMonth = 0;
                int stopYear = 0;
                int stopMonth = 0;
                int stopDayOfMonth = 0;
                int startHour = 0;
                int stopHour = 0;
                int startMinute = 0;
                int stopMinute = 0;
                while (!(line = reader.readLine()).equals("END:VEVENT")) {
                    if (line.startsWith("DTSTART")) {
                        String[] tokens = line.split(":");
                        startYear = Integer.parseInt(tokens[1].substring(0, 4));
                        startMonth = Integer.parseInt(tokens[1].substring(4, 6));
                        startDayOfMonth = Integer.parseInt(tokens[1].substring(6, 8));
                        startHour = Integer.parseInt(tokens[1].substring(9, 11));
                        startMinute = Integer.parseInt(tokens[1].substring(11, 13));
                    }else if (line.startsWith("DTEND")) {
                        String[] tokens = line.split(":");
                        stopYear = Integer.parseInt(tokens[1].substring(0, 4));
                        stopMonth = Integer.parseInt(tokens[1].substring(4, 6));
                        stopDayOfMonth = Integer.parseInt(tokens[1].substring(6, 8));
                        stopHour = Integer.parseInt(tokens[1].substring(9, 11));
                        stopMinute = Integer.parseInt(tokens[1].substring(11, 13));
                    }else if (line.startsWith("SUMMARY")){
                        String[] tokens = line.split("SUMMARY:");
                        header = tokens[1];
                    }else if (line.startsWith("DESCRIPTION")){
                        String[] tokens = line.split("DESCRIPTION:");
                        if (tokens.length >= 2) {
                            description = tokens[1];
                        }
                    }else if (line.startsWith("CATEGORIES")){
                        String[] tokens = line.split(":");
                        categoryName = tokens[1];
                    }
                }
                Random random = new Random();
                Category category = user.getSubCategory(categoryName);
                if (category == null) {
                    category = new Category(categoryName, new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    user.addSubCategory(category);
                    if (urlName.startsWith("Inväntar internetuppkoppling")){
                        user.getSubs().remove(urlName);
                        user.getSubs().put(categoryName, urlString);
                    }
                }
                if (years.get(startYear) == null) {
                    createYear(startYear);
                }
                if (years.get(stopYear) == null) {
                    createYear(startYear);
                }
                Day startDay = years.get(startYear).getMonth(startMonth-1).getDay(startDayOfMonth);
                Day stopDay = years.get(stopYear).getMonth(stopMonth-1).getDay(stopDayOfMonth);
                Activity loadedActivity = Day.createActivity(header, category, startHour, stopHour, startMinute, stopMinute, startDay, stopDay, description, true);
                if (!subActivities.contains(loadedActivity)) {
                    subActivities.add(loadedActivity);
                }
            }
        }
    }
    private void downloadOnlineCalendars() throws IOException{
        for (Map.Entry<String, String> entry: user.getSubs().entrySet()){
            downloadOnlineCalendar(entry.getKey(), entry.getValue());
        }
    }
    private void startNewUser(String userPath){
        try{
            loadUser(userPath);
        }catch (IOException | ArrayIndexOutOfBoundsException e){
            askIfCreateNew(e);
        }
    }
    private void loadUser(String userPath) throws IOException, ArrayIndexOutOfBoundsException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(userPath)), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null){
            if (line.contains("Name:")) { // USING CONTAINS INSTEAD OF EQUALS CAUSE EQUALS DOESN*T WORK OF SOME REASON. MAY CHANGE LATER.
                user = new User(reader.readLine(), userPath);
            }else if (line.equals("Subs:")){
                while (!(line = reader.readLine()).equals("//STOP//")){
                    String[] tokens = line.split("##BREAK@");
                    user.add(tokens[0], tokens[1]);
                }
            }else if (line.equals("Categories:")){
                while (!(line = reader.readLine()).equals("//STOP//")){
                    String[] tokens = line.split("##BREAK@");
                    for (int i = 0; i < tokens.length; i++){
                        //System.out.println("token " + i + ":" + tokens[i]);
                    }
                    user.addCategory(new Category(tokens[0], new Color(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]))));
                }
            }else if (line.equals("SubCategories:")){
                while (!(line = reader.readLine()).equals("//STOP//")){
                    String[] tokens = line.split("##BREAK@");
                    user.addSubCategory(new Category(tokens[0], new Color(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]))));
                }
            }else if (line.equals("Activities:")){
                while (!(line = reader.readLine()).equals("//STOP//")){
                    unpackActivity(line, false);
                }
            }else if (line.startsWith("SubActivities:")){
                if (!user.getSubs().isEmpty()) {
                    try {
                        String[] tokens = line.split(":");
                        if (tokens[1].equals(getToday().toString("digit"))) {
                            throw new UpToDateException();
                        } else if (user.getSubs().values().size() != 0) {
                            downloadOnlineCalendars();
                        }
                    } catch (IOException e) {
                        updateUnsuccessful = true;
                        showNoInternetMessage();
                        unpackLocalSubActivities(reader);
                    } catch (UpToDateException e) {
                        unpackLocalSubActivities(reader);
                    }
                }
            }
        }
        recentListModel.remove(user.getName());
    }
    private boolean fileAvailable(String path){
        boolean available;
        try{
            System.out.print("Checking availabillity: ");
            new FileInputStream(new File(path));
            available = true;
        }catch (IOException e){
            available = false;
        }
        System.out.print(available);
        return available;
    }
    private void askIfCreateNew(Exception e){
        boolean ioException = e instanceof IOException;
        String[] options = {"Skapa ny", "Öppna"};
        int answer = JOptionPane.showOptionDialog(MainWindow.this, ioException? "Hittar inte föregående användares sparfil":"Filen som försöker öppnas verkar vara korrupt", ioException? "Fil saknas":"Fil korrupt", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        if (answer == 0){
            createNewUser(showNewUserPane());
        }else if (answer == 1){
            int approval = fileChooser.showOpenDialog(MainWindow.this);
            if (approval == JFileChooser.APPROVE_OPTION){
                try {
                    loadUser(fileChooser.getSelectedFile().getAbsolutePath());
                }catch (IOException | ArrayIndexOutOfBoundsException e2){

                }
            }else{
                System.exit(0); // KANSKE VILL ÅTERGÅ TILL OPTIONDIALOG
            }
        }else{
            System.exit(0);
        }
    }
    private void unpackLocalSubActivities(BufferedReader reader) throws IOException{
        String line = null;
        while ((line = reader.readLine()) != null){
            unpackActivity(line, true);
        }
    }
    private void unpackActivity(String line, boolean subscribed){
        String[] tokens = line.split("##BREAK@");
        String[] startString = tokens[6].split("/");
        String[] stopString = tokens[7].split("/");
        int[] startDate = new int[startString.length];
        int[] stopDate = new int[startString.length];
        for (int i = 0; i < startString.length; i++){
            startDate[i] = Integer.parseInt(startString[i]);
            stopDate[i] = Integer.parseInt(stopString[i]);
        }
        if (years.get(startDate[2]) == null)
            createYear(startDate[2]);
        if (years.get(stopDate[2]) == null)
            createYear(stopDate[2]);
        Day startDay = years.get(startDate[2]).getMonth(startDate[1]).getDay(startDate[0]);
        Day stopDay = years.get(stopDate[2]).getMonth(stopDate[1]).getDay(stopDate[0]);
        if (subscribed) {
            subActivities.add(Day.createActivity(tokens[0], user.getSubCategory(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), startDay, stopDay, 8 >= tokens.length ? null : tokens[8], true));
        }else{
            activities.add(Day.createActivity(tokens[0], user.getCategory(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), startDay, stopDay, 8 >= tokens.length ? null : tokens[8], false));
        }
    }

    private String getLastUserPath(){
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("Configuration.txt"));
            line = reader.readLine();
        }catch (FileNotFoundException e){

        }catch (IOException e){

        }
        return line;
    }
    public void showActivities(){
        for (Day day: previousMonth().getDays()){
            if (!day.isEmpty())
                day.showActivities();
        }
        for (Day day: displayedMonth.getDays()){
            if (!day.isEmpty())
                day.showActivities();
        }
        for (Day day: nextMonth().getDays()){
            if (!day.isEmpty())
                day.showActivities();
        }
    }
    private Day getToday(){
        Calendar calendar = Calendar.getInstance();
        return getTodaysMonth().getDay(calendar.get(Calendar.DAY_OF_MONTH));
    }
    public JPanel getDisplay(){
        return display;
    }
    public User getUser(){
        return user;
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }
    public Activity removeActivity(Activity activity){
        activities.remove(activity);
        return activity;
    }

    private User createNewUser(String name){
        do {
            if (name == null)
                //MÅSTE HANDERAS BÄTTRE FÖR NÄR MAN SKAPAR NY PROFIL FRÅN MENYN
                System.exit(0);
            if (name.isEmpty()) {
                try {
                    JOptionPane.showMessageDialog(MainWindow.this, "Välj ett namn.", "Namn saknas", JOptionPane.ERROR_MESSAGE);
                }catch (NullPointerException e){
                    System.exit(0);
                }
            }
            if (name.isEmpty())
                name = JOptionPane.showInputDialog(MainWindow.this, "Namn: ", "Ny användare", JOptionPane.PLAIN_MESSAGE);
        } while (name == null || name.isEmpty());
        user = new User(name, "Users\\" + name + ".save");
        user.addCategory(new Category("Min kategori", Color.RED));
        return user;
    }
    private String showNewUserPane(){
        return JOptionPane.showInputDialog(MainWindow.this, "Namn: ", "Ny användare", JOptionPane.PLAIN_MESSAGE);
    }
    private void updateTitle(){
        setTitle((displayedMonth.toString() + " " + displayedMonth.getYear().getAsInt()) + " " + user.toString());
    }
    public void displayDayMenu(Day day){
        DayMenu glassPanel = new DayMenu(day, theme);
        setGlassPane(glassPanel);
        glassPanel.setVisible(true);
        glassPanel.requestFocus();
    }
    private Month getTodaysMonth(){
        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH);
        try{
            return years.get(currentYear).getMonth(currentMonth);
        }catch (NullPointerException e){
            return createYear(currentYear).getMonth(currentMonth);
        }
    }

    private Year createYear(int year){
        Year newYear = new Year(year, theme);
        years.put(year, newYear);
        return newYear;
    }
    public void displayMonth(Month month){
        display.removeAll();
        displayedMonth = month;
        monthLabel.setText((month.toString() + " " + month.getYear().getAsInt()).toUpperCase());
        updateTitle();
        Day[] previousDays = previousMonth().getDays();
        Day[] days = month.getDays();
        Day[] nextDays = nextMonth().getDays();
        calibrateGrid(month);
        //Adding previous months days
        for (int i = 0; i < days[0].dayOfWeek()-1; i++){
            for (int j = 0; j < previousDays.length; j++)
                previousDays[j].setDimmed(true);
            display.add(previousDays[previousDays.length-days[0].dayOfWeek() + 1 + i]);
        }
        //Adding MONTH days
        for (int i = 0; i < days.length; i++){
            Calendar today = Calendar.getInstance();
            int y = today.get(Calendar.YEAR);
            int m = today.get(Calendar.MONTH);
            int d = today.get(Calendar.DAY_OF_MONTH);
            if (days[i].getDate().get(Calendar.YEAR) == y && days[i].getDate().get(Calendar.MONTH) == m &&
                    days[i].getDate().get(Calendar.DAY_OF_MONTH) == d){
                days[i].setAsToday();
            }
            days[i].setDimmed(false);
            display.add(days[i]);
        }
        //Adding next months days
        for (int i = 0; i < 7 - days[days.length-1].dayOfWeek(); i++){
            for (int j = 0; j < nextDays.length; j++)
                nextDays[j].setDimmed(true);
            display.add(nextDays[i]);
        }
        showActivities();
        display.validate();
        display.repaint();
    }
    private void calibrateGrid(Month month){
        if (month.amountDays() == 28){
            if (month.getDays()[0].dayOfWeek() != 1){
                grid.setRows(5);
            }else{
                grid.setRows(4);
            }
        }else if (month.amountDays() == 29){
            grid.setRows(5);
        }else if (month.amountDays() == 30){
            if (month.getDays()[0].dayOfWeek() != 7){
                grid.setRows(5);
            }else{
                grid.setRows(6);
            }
        }else if (month.amountDays() == 31){
            if (month.getDays()[0].dayOfWeek() < 6 ){
                grid.setRows(5);
            }else{
                grid.setRows(6);
            }
        }
    }
    public Theme getTheme(String theme){
        return themes.get(theme);
    }

    private Month nextMonth(){
        Year currentYear = displayedMonth.getYear();
        try {
            return currentYear.getNextMonth(displayedMonth);
        }catch (ArrayIndexOutOfBoundsException e){
            int targetYear = currentYear.getAsInt()+1;
            if (years.get(targetYear) != null){
                return years.get(targetYear).getMonth(0);
            }
            Year newYear = createYear(targetYear);
            return newYear.getMonth(0);
        }
    }
    private Month previousMonth(){
        Year currentYear = displayedMonth.getYear();
        try {
            return currentYear.getPreviousMonth(displayedMonth);
        }catch(ArrayIndexOutOfBoundsException e){
            int targetYear = currentYear.getAsInt()-1;
            if (years.get(targetYear) != null){
                return years.get(targetYear).getMonth(11);
            }
            Year newYear = createYear(targetYear);
            return newYear.getMonth(11);
        }
    }
    private void saveConfigurationData(){
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File("Configuration.txt")), "UTF-8"));
            printWriter.println(user.getFilePath());
            printWriter.println(theme);
            printWriter.println("Recent:");
            for (String user: recentListModel){
                printWriter.println(user + "##BREAK@" + recentUsers.get(user));
            }
            printWriter.close();
        }catch (FileNotFoundException e){

        }catch (IOException e){

        }
    }
    private void saveUserData(){
        try {
            new File("Users").mkdir();
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(user.getFilePath())), "UTF-8"));
            //Saving username
            printWriter.println("Name:");
            printWriter.println(user.getName());
            //Saving categories
            printWriter.println("Subs:");
            for (Map.Entry<String, String> sub: user.getSubs().entrySet())
                printWriter.println(sub.getKey() + "##BREAK@" + sub.getValue());
            printWriter.println("//STOP//");
            printWriter.println("Categories:");
            for (Category category: user.getCategories().values()) {
                printWriter.println(category.toString(true));
            }
            printWriter.println("//STOP//");
            printWriter.println("SubCategories:");
            for (Category category: user.getSubCategories().values())
                printWriter.println(category.toString(true));
            printWriter.println("//STOP//");
            //Saving activities
            printWriter.println("Activities:");
            for (Activity activity: activities)
                printWriter.println(activity);
            printWriter.println("//STOP//");
            printWriter.println("SubActivities:" + (updateUnsuccessful? ".":(getToday().toString("digit"))));
            for (Activity activity: subActivities)
                printWriter.println(activity);
            printWriter.close();
        }catch (FileNotFoundException e){

        }catch (IOException e){

        }
    }
    public void saveAndExit(){
        saveConfigurationData();
        saveUserData();
        System.exit(0);
    }
    private void saveAndRestart(){
        saveConfigurationData();
        saveUserData();
        MainWindow.this.dispose();
        main(null);
    }
    private void saveAndLoad(String path) throws IOException, ArrayIndexOutOfBoundsException{
        setVisible(false);
        recentListModel.add(user.getName());
        recentUsers.put(user.getName(), user.getFilePath());
        saveUserData();
        resetCalendar();
        loadUser(path);
        displayMonth(getTodaysMonth());
        setVisible(true);
    }
    private void saveAndCreateNew(){
        String newUser = showNewUserPane();
        if (newUser == null)
            return;
        recentListModel.add(user.getName());
        recentUsers.put(user.getName(), user.getFilePath());
        saveUserData();
        resetCalendar();
        createNewUser(newUser);
        displayMonth(getTodaysMonth());
    }
    private class NavigationButtonListener extends MouseAdapter{
        public void mouseClicked(MouseEvent event){
            ArrowButton button = (ArrowButton)event.getSource();
            if (button == leftButton){
                displayMonth(previousMonth());
            }else if (button == rightButton){
                displayMonth(nextMonth());
            }
        }
        public void mouseEntered(MouseEvent event){
            ArrowButton button = (ArrowButton)event.getSource();
            if (button == leftButton){
                leftButton.setState(ArrowButton.State.HOVERED);
            }else if (button == rightButton){
                rightButton.setState(ArrowButton.State.HOVERED);
            }
        }
        public void mouseExited(MouseEvent event){
            ArrowButton button = (ArrowButton)event.getSource();
            if (button == leftButton){
                leftButton.setState(ArrowButton.State.NORMAL);
            }else if (button == rightButton){
                rightButton.setState(ArrowButton.State.NORMAL);
            }
        }
        public void mousePressed(MouseEvent event){
            ArrowButton button = (ArrowButton)event.getSource();
            if (button == leftButton){
                leftButton.setState(ArrowButton.State.PRESSED);
            }else if (button == rightButton){
                rightButton.setState(ArrowButton.State.PRESSED);
            }
        }
        public void mouseReleased(MouseEvent event){
            ArrowButton button = (ArrowButton)event.getSource();
            if (button == leftButton){
                if (leftButton.hoveredOver(event))
                    leftButton.setState(ArrowButton.State.HOVERED);
                else
                    leftButton.setState(ArrowButton.State.NORMAL);
            }else if (button == rightButton){
                if (rightButton.hoveredOver(event))
                    rightButton.setState(ArrowButton.State.HOVERED);
                else
                    rightButton.setState(ArrowButton.State.NORMAL);
            }
        }
    }
    private class NavigationKeyListener extends KeyAdapter{
        public void keyPressed(KeyEvent event){
            if (event.getKeyCode() == KeyEvent.VK_RIGHT){
                deselectDays();
                displayMonth(nextMonth());
            }else if (event.getKeyCode() == KeyEvent.VK_LEFT){
                deselectDays();
                displayMonth(previousMonth());
            }else if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                displayMonth(getTodaysMonth());
            }else if(event.getKeyCode() == KeyEvent.VK_ESCAPE){
                saveAndExit();
            }else if (event.getKeyCode() == KeyEvent.VK_ALT){
                getJMenuBar().setVisible(!getJMenuBar().isVisible());
            }
        }
    }
    private void deselectDays(){
        for (Day day: displayedMonth.getDays()){
            if (day.isHoveredOver())
                day.deSelect();
        }
    }
    private class CloseWindowListener extends WindowAdapter{
        public void windowClosing(WindowEvent event){
            saveAndExit();
        }
    }
    private class monthLabelListener extends MouseAdapter{
        public void mouseClicked(MouseEvent event){
            displayMonth(getTodaysMonth());
        }
    }

    private void resetCalendar(){
        years.clear();
        activities.clear();
        subActivities.clear();
    }

    public void reloadOnlineCalendars(){
        try {
            for (String url: user.getSubs().values()) {
                new URL(url).openStream();
            }
            for (Activity activity: subActivities)
                activity.remove();
            subActivities.clear();
            downloadOnlineCalendars();
            showActivities();
            repaint();
        }catch(IOException e){
            System.out.println("Inget internet");
            updateUnsuccessful = true;
            showNoInternetMessage();
        }
    }
    public void setUpdateUnsuccessful(boolean unsuccessful){
        updateUnsuccessful = unsuccessful;
    }
    private void showNoInternetMessage(){
        JOptionPane.showMessageDialog(this, "Kan inte uppdatera prenumerationer. Behåller " +
                "gamla.\nKolla din internetuppkoppling.", "Misslyckad uppdatering", JOptionPane.INFORMATION_MESSAGE);
    }

    private class NewProfileListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            saveAndCreateNew();
        }
    }
    private class OpenProfileListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            try {
                int answer = fileChooser.showOpenDialog(MainWindow.this);
                if (answer == JFileChooser.APPROVE_OPTION) {
                    saveAndLoad(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }catch(IOException | ArrayIndexOutOfBoundsException e){
                askIfCreateNew(e);
            }
        }
    }
    private class SaveAsListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            int answer = fileChooser.showSaveDialog(MainWindow.this);
            if (answer == JFileChooser.APPROVE_OPTION) {
                user.setFilePath(fileChooser.getSelectedFile().getAbsolutePath());
                saveUserData();
            }
        }
    }
    private class CategoriesListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            JOptionPane.showOptionDialog(MainWindow.this, new CategoryManagementPane(user), "Hantera" +
                    " dina kategorier", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] {"Stäng"}, 0);
        }
    }
    private class ThemeMenuListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            String target = ((JMenuItem)event.getSource()).getText();
            theme = themes.get(target);
            saveAndRestart();
        }
    }
    private class RecentUsersListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            JMenuItem item = (JMenuItem)event.getSource();
            String name = item.getText();
            String path = recentUsers.get(name);
            try {
                if (fileAvailable(path)) {
                    saveAndLoad(path);
                }else{
                    int answer = JOptionPane.showConfirmDialog(MainWindow.this,
                            "Profilen existerar inte. Ta bort från listan?", "Existerar inte",
                            JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
                        recentUsers.remove(name);
                        recentListModel.remove(name);
                    }
                }
            }catch (IOException | ArrayIndexOutOfBoundsException e){
            }
        }
    }
    private class RecentListModel extends ArrayList<String>{

        JMenu menu;

        public RecentListModel(JMenu menu){
            this.menu = menu;
        }

        public boolean add(String name){
            if (contains(name))
                remove(name);
            super.add(0, name);
            rePopulate();
            return true;
        }
        public boolean addAll(Collection<? extends String> recentList){
            for (String item : recentList)
                super.add(item);
            rePopulate();
            return true;
        }
        public String remove(String name){
            super.remove(name);
            rePopulate();
            return name;
        }
        private void rePopulate(){
            menu.removeAll();
            RecentUsersListener listener = new RecentUsersListener();
            for (String name: this){
                JMenuItem item = new JMenuItem(name);
                item.addActionListener(listener);
                menu.add(item);
            }
            if (size() < 1)
                menu.setEnabled(false);
            else
                menu.setEnabled(true);
        }
    }
    private class SubscriptionsListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            SubscriptionsManagementPane pane = new SubscriptionsManagementPane(user);
            JOptionPane.showOptionDialog(MainWindow.this, pane, "Hantera dina prenumerationer",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] {"Stäng"}, 0);
        }
    }
    private class UpdateSubscriptionsListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            reloadOnlineCalendars();
        }
    }
    private class UpToDateException extends Exception{}
}

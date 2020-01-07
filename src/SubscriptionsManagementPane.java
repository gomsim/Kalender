import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class SubscriptionsManagementPane extends JPanel {

    private User user;

    private boolean changed;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> subscriptionsList = new JList<>(listModel);

    JButton newButton, removeButton;

    public SubscriptionsManagementPane(User user){
        this.user = user;

        setLayout(new BorderLayout());
        add(new JScrollPane(subscriptionsList), BorderLayout.CENTER);
        subscriptionsList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        subscriptionsList.addListSelectionListener(new SelectionListener());
        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.EAST);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        newButton = new JButton("    Ny    ");
        removeButton = new JButton("Ta bort");
        newButton.addActionListener(new ButtonListener());
        removeButton.addActionListener(new ButtonListener());
        removeButton.setEnabled(false);

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        panel1.add(newButton);
        panel2.add(removeButton);

        buttonPanel.add(panel1);
        buttonPanel.add(panel2);

        for (String sub: user.getSubs().keySet()){
            listModel.addElement(sub);
        }
    }

    public boolean hasChanged(){
        return changed;
    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            if (event.getSource() == newButton) {
                String stringUrl = JOptionPane.showInputDialog(SubscriptionsManagementPane.this, "Länk: ", "Ny prenumeration", JOptionPane.PLAIN_MESSAGE);
                if (stringUrl == null)
                    return;
                if (user.getSubs().containsValue(stringUrl)){
                    JOptionPane.showMessageDialog(SubscriptionsManagementPane.this, "Du prenumererar redan på denna kalender", "Finns redan", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                getTopLevelAncestor().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                SwingWorker<Boolean, Void> worker = new DetectOnlineCalendarWorker(stringUrl);
                worker.execute();
            }else if (event.getSource() == removeButton){
                String subName = subscriptionsList.getSelectedValue();
                user.removeSubscription(subName);
                user.removeSubCategory(user.getSubCategory(subName));
                listModel.removeElement(subName);
                MainWindow.instance.reloadOnlineCalendars();
            }
        }
    }

    private class SelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event){
            if (subscriptionsList != null){
                removeButton.setEnabled(true);
            }else{
                removeButton.setEnabled(false);
            }
        }
    }
    private class DetectOnlineCalendarWorker extends SwingWorker<Boolean, Void>{

        String stringUrl;

        private DetectOnlineCalendarWorker(String stringUrl){
            this.stringUrl = stringUrl;
        }

        public Boolean doInBackground(){
            MainWindow.instance.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                String name = "";
                URL url = new URL(stringUrl);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                for (String line = reader.readLine(); !line.equals("END:VEVENT"); line = reader.readLine()) {
                    if (line.startsWith("CATEGORIES")) {
                        String[] tokens = line.split(":");
                        name = tokens[1];
                    }
                }
                user.add(name, stringUrl);
                listModel.addElement(name);
                changed = true;
            }catch (MalformedURLException e){
                JOptionPane.showMessageDialog(SubscriptionsManagementPane.this, "Du har angett en felaktig länk", "Felaktig URL", JOptionPane.ERROR_MESSAGE);
                return Boolean.FALSE;
            }catch (IOException e){
                String message = "Inväntar internetuppkoppling " + (user.getSubs().isEmpty()? "":(user.getSubs().size()) + " ");
                user.add(message, stringUrl);
                listModel.addElement(message);
                MainWindow.instance.setUpdateUnsuccessful(true);
            }
            return Boolean.TRUE;
        }
        public void done(){
            System.out.println("Done executing!!");
            MainWindow.instance.setCursor(Cursor.getDefaultCursor());
            MainWindow.instance.reloadOnlineCalendars();
            getTopLevelAncestor().setCursor(Cursor.getDefaultCursor());
        }
    }
}

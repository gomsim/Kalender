import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CategoryManagementPane extends JPanel {

    private User user;

    private JButton newButton;
    private JButton removeButton;
    private JButton editButton;

    private DefaultListModel<Category> listModel = new DefaultListModel<>();
    private JList<Category> categoryList = new JList<>(listModel);

    public CategoryManagementPane(User user){
        this.user = user;

        setLayout(new BorderLayout());
        add(new JScrollPane(categoryList), BorderLayout.CENTER);
        categoryList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        categoryList.addListSelectionListener(new SelectionListener());
        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.EAST);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        newButton = new JButton("    Ny    ");
        removeButton = new JButton("Ta bort");
        editButton = new JButton(" Ändra ");
        newButton.addActionListener(new ButtonListener());
        removeButton.addActionListener(new ButtonListener());
        editButton.addActionListener(new ButtonListener());
        removeButton.setEnabled(false);
        editButton.setEnabled(false);

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();

        panel1.add(newButton);
        panel2.add(removeButton);
        panel3.add(editButton);

        buttonPanel.add(panel1);
        buttonPanel.add(panel2);
        buttonPanel.add(panel3);

        for (Category category: user.getCategories().values()){
            listModel.addElement(category);
        }
        for (Category category: user.getSubCategories().values()){
            listModel.addElement(category);
        }
    }
    private boolean isLast(){
        return user.getCategories().size() <= 1;
    }
    private boolean isSubscribed(Category category){
        return user.getSubCategories().containsValue(category);
    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            if (event.getSource() == newButton){
                Category newCategory = CategoryChooser.showNewCategoryPane(CategoryManagementPane.this);
                if (newCategory != null)
                    listModel.addElement(newCategory);
            }else if (event.getSource() == removeButton){
                Category toRemove = categoryList.getSelectedValue();
                if (!toRemove.isEmpty()){
                    String[] options = {"Befintlig kategori", "Ny kategori", "Ta bort aktiviteter", "Avbryt"};
                    int choice = JOptionPane.showOptionDialog(CategoryManagementPane.this, "Du försöker ta bort en kategori som har aktiviteter bundna till sig.\nVilken kategori vill du binda aktiviteterna till?", "Aktiviteter behöver en kategori", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                    switch(choice){
                        case 0:
                            listModel.removeElement(toRemove);
                            for (Category category: user.getSubCategories().values())
                                listModel.removeElement(category);
                            JList<Category> tempList = new JList<>(listModel);
                            int answer = JOptionPane.showConfirmDialog(CategoryManagementPane.this, new JScrollPane(tempList), "Välj befintlig", JOptionPane.OK_CANCEL_OPTION);
                            for (Category category: user.getSubCategories().values())
                                listModel.addElement(category);
                            if (answer != JOptionPane.OK_OPTION){
                                listModel.add(user.getCategories().size()-1, toRemove);
                                return;
                            }
                            Category chosenCategory = tempList.getSelectedValue();
                            changeCategory(toRemove, chosenCategory);
                            break;
                        case 1:
                            Category newCategory = CategoryChooser.showNewCategoryPane(CategoryManagementPane.this);
                            changeCategory(toRemove, newCategory);
                            listModel.addElement(newCategory);
                            break;
                        case 2:
                            int deleteAnswer = JOptionPane.showConfirmDialog(CategoryManagementPane.this, "Ta bort " + toRemove.getName() + " och alla tillhörande aktiviteter?",  "Är du säker?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (deleteAnswer == JOptionPane.YES_OPTION){
                                for (int i = toRemove.getActivities().size()-1; i >= 0; i--){
                                    toRemove.getActivities().get(i).remove();
                                }
                                removeCategory(toRemove);
                            }
                            break;
                        default:
                            return;
                    }
                }
            }else if (event.getSource() == editButton){
                CategoryChooser.showEditCategoryPane(CategoryManagementPane.this, categoryList.getSelectedValue());
            }
            repaint();
            MainWindow.instance.repaint();
        }
    }
    private void changeCategory(Category from, Category to){
        for (int i = from.getActivities().size()-1; i >= 0; i--)
            from.getActivities().get(i).setCategory(to);
        removeCategory(from);
    }
    private Category removeCategory(Category category){
        listModel.removeElement(category);
        user.removeCategory(category);
        return category;
    }

    private class SelectionListener implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent event){
            if (categoryList.getSelectedValue() != null){
                removeButton.setEnabled(!(isLast() || isSubscribed(categoryList.getSelectedValue())));
                editButton.setEnabled(true);
            }else{
                removeButton.setEnabled(false);
                editButton.setEnabled(false);
            }
        }
    }
}

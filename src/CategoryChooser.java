import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import java.awt.*;

public class CategoryChooser extends JColorChooser {

    private JTextField nameField = new JTextField(15);

    public CategoryChooser(Category category){
        super(category != null? category.getColour():Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel labelPanel = new JPanel();
        JLabel label = new JLabel("Välj ett namn och en färg");
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(Color.GRAY);
        labelPanel.add(label);
        add(labelPanel);
        add(nameField);
        if (category != null)
            nameField.setText(category.getName());
        if (MainWindow.instance.getUser().getSubCategories().containsValue(category))
            nameField.setEditable(false);

        AbstractColorChooserPanel[] defaultPanels = getChooserPanels();
        for (int i = 1; i < defaultPanels.length; i++)
            removeChooserPanel(defaultPanels[i]);
        remove(getComponents()[0]);

        for (int i = 0; i < getComponents().length; i++)
            System.out.println(getComponents()[i]);
        }
    public String getName(){
        return nameField.getText();
    }
    public static Category showNewCategoryPane(JComponent parent){
        JColorChooser chooser = new CategoryChooser(null);
        String name;
        Color colour;
        int answer;
        boolean correct;
        do{
            correct = true;
            answer = JOptionPane.showConfirmDialog(null, chooser, "Ny kategori", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            name = chooser.getName();
            colour = chooser.getColor();
            if (answer == JOptionPane.OK_OPTION && (name.isEmpty() || colour.equals(Color.WHITE))){
                correct = false;
                JOptionPane.showMessageDialog(parent, "En kategori måste ha ett namn och en färg.", "Data saknas", JOptionPane.ERROR_MESSAGE);
            }else if (answer == JOptionPane.OK_OPTION && name.equals("Ny")){
                correct = false;
                JOptionPane.showMessageDialog(parent, "'Ny' är ett reserverat namn.", "Data saknas", JOptionPane.ERROR_MESSAGE);
            }
        }while (!correct);
        if (answer == JOptionPane.OK_OPTION) {
            Category newCategory = new Category(name, colour);
            MainWindow.instance.getUser().addCategory(newCategory);
            return newCategory;
        }
        return null;
    }
    public static void showEditCategoryPane(JComponent parent, Category preExisting){
        JColorChooser chooser = new CategoryChooser(preExisting);
        String name;
        Color colour;
        int answer;
        boolean correct;
        do{
            correct = true;
            answer = JOptionPane.showConfirmDialog(null, chooser, "Ändra kategori", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            name = chooser.getName();
            colour = chooser.getColor();
            if (answer != JOptionPane.OK_OPTION)
                return;
            if (name.isEmpty() || colour.equals(Color.WHITE)){
                correct = false;
                JOptionPane.showMessageDialog(parent, "En kategori måste ha ett namn och en färg.", "Data saknas", JOptionPane.ERROR_MESSAGE);
            }else if (name.equals("Ny")){
                correct = false;
                JOptionPane.showMessageDialog(parent, "'Ny' är ett reserverat namn.", "Ogiltigt namn", JOptionPane.ERROR_MESSAGE);
            }
        }while (!correct);
        preExisting.setName(name);
        preExisting.setColour(colour);
    }
}

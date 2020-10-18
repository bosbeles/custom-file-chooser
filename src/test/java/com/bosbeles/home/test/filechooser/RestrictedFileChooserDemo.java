package com.bosbeles.home.test.filechooser;

import com.bsbls.home.gui.test.GuiTester;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

public class RestrictedFileChooserDemo {
    private JPanel content;
    private JTextField defaultDirectoryField;
    private JButton button1;
    private JTextField textField2;
    private JButton button2;
    private JTextField prefixDirectoryField;
    private JCheckBox searchForOtherDrivesCheckBox;
    private JComboBox fileTypeCombo;
    private JCheckBox readOnlyCheckBox;

    public RestrictedFileChooserDemo() {
        button1.addActionListener(this::defaultDirSelection);
        button2.addActionListener(this::customFileChooserAction);
        searchForOtherDrivesCheckBox.addItemListener(this::searchForOtherDrivesAction);
        searchForOtherDrivesCheckBox.setSelected(true);
    }

    private void searchForOtherDrivesAction(ItemEvent e) {
        prefixDirectoryField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);

    }

    public JPanel getContent() {
        return content;
    }


    private void defaultDirSelection(ActionEvent e) {
        RestrictedFileChooser cfc = new RestrictedFileChooser();
        cfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = cfc.showOpenDialog(null);
        File selectedFile = cfc.getSelectedFile();
        defaultDirectoryField.setText(selectedFile == null ? "" : selectedFile.toPath().toAbsolutePath().toString());
    }

    private void customFileChooserAction(ActionEvent e) {
        RestrictedFileChooser fileChooser;
        if (searchForOtherDrivesCheckBox.isSelected()) {
            fileChooser = new RestrictedFileChooser(
                    Paths.get(defaultDirectoryField.getText()),
                    Paths.get(prefixDirectoryField.getText()));

        } else {
            fileChooser = new RestrictedFileChooser(
                    Paths.get(defaultDirectoryField.getText()));
        }


        fileChooser.setReadOnly(readOnlyCheckBox.isSelected());
        fileChooser.setAcceptAllFileFilterUsed(false);
        Object selectedItem = fileTypeCombo.getSelectedItem();
        if ("Directories".equals(selectedItem)) {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else if ("csv".equals(selectedItem)) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("Track Route - CSV", "csv"));
        } else if (selectedItem != null) {
            String[] extensions = Arrays.stream(selectedItem.toString().split("\\s*,\\s*")).filter(s -> !s.isEmpty()).toArray(String[]::new);
            if (extensions.length > 0) {
                fileChooser.setFileFilter(new FileNameExtensionFilter("Custom", extensions));
            } else {
                fileChooser.setAcceptAllFileFilterUsed(true);
            }
        } else {
            fileChooser.setAcceptAllFileFilterUsed(true);
        }

        int ret = fileChooser.showOpenDialog(null);
        File selectedFile = fileChooser.getSelectedFile();
        textField2.setText(selectedFile == null ? "" : selectedFile.toPath().toAbsolutePath().toString());
    }

    public static void main(String[] args) {
        GuiTester.test(f -> new RestrictedFileChooserDemo().getContent(),
                "FlatDark",
                f -> {
                    f.setSize(800, 600);
                    f.setLocationRelativeTo(null);
                });
    }
}

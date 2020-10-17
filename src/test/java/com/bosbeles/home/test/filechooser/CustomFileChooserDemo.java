package com.bosbeles.home.test.filechooser;

import com.bsbls.home.gui.test.GuiTester;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;

public class CustomFileChooserDemo {
    private JPanel content;
    private JTextField textField1;
    private JButton button1;
    private JTextField textField2;
    private JButton button2;

    public CustomFileChooserDemo() {
        button1.addActionListener(this::createDefault);
        button2.addActionListener(this::createForDefaultAndAllDrives);
    }

    public JPanel getContent() {
        return content;
    }


    private void createDefault(ActionEvent e) {
        CustomFileChooser cfc = new CustomFileChooser();
        cfc.setAcceptAllFileFilterUsed(false);
        cfc.setFileFilter(new FileNameExtensionFilter("Track Route - CSV", "csv"));
        int ret = cfc.showOpenDialog(null);
        File selectedFile = cfc.getSelectedFile();
        textField1.setText(selectedFile == null ? "" : selectedFile.toPath().toAbsolutePath().toString());
    }

    private void createForDefaultAndAllDrives(ActionEvent e) {
        CustomFileChooser cfc = new CustomFileChooser(
                Paths.get(".", "TrackRoutes"),
                Paths.get("Milsoft", "Tdlsim"));
        cfc.setReadOnly(true);
        cfc.setAcceptAllFileFilterUsed(false);
        cfc.setFileFilter(new FileNameExtensionFilter("Track Route - CSV", "csv"));
        int ret = cfc.showOpenDialog(null);
        File selectedFile = cfc.getSelectedFile();
        textField2.setText(selectedFile == null ? "" : selectedFile.toPath().toAbsolutePath().toString());
    }

    public static void main(String[] args) {
        GuiTester.test(f -> new CustomFileChooserDemo().getContent(),
                "FlatDark",
                f -> {
                    f.setSize(800, 600);
                    f.setLocationRelativeTo(null);
                });
    }
}

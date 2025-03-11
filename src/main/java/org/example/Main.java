package org.example;

import org.example.views.MainWindow;

import javax.swing.*;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow mainWindow = null;
                try {
                    mainWindow = new MainWindow();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                mainWindow.show();
            }
        });
    }
}
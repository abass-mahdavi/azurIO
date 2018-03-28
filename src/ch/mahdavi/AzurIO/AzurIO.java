package ch.mahdavi.AzurIO;

/*
    File: AzurIO.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    AzurIO simply lunches the GUI interface provided by AzurIO_GUI

 */


import javax.swing.*;

public class AzurIO {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                AzurIO_GUI azurIOGui = new AzurIO_GUI();
                azurIOGui.azurIO_GUI_Go();
            }
        });

    }
}

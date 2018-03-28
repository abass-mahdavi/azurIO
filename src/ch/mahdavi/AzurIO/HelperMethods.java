package ch.mahdavi.AzurIO;

/*
    File: HelperMethods.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    HelperMethods is a mixed basket of static methods used in AzurIO classes

 */

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

public class HelperMethods {

    private HelperMethods(){}

    // private method
    // returns a String that tells the current time
    // example:  2007-12-03T10:15:30
    private static String currentTime(){
        return LocalDateTime.now().toString();
    }

    //public method
    // returns a reformatted string of the current time
    // by replacing all seperators of previous method
    // with underscores
    // ex 2007_12_03_10_15_30
    public static String currentTimeFormattedToDirectoryName(){
        String currentTimeFormattedToDirectoryName = currentTime();
        currentTimeFormattedToDirectoryName = currentTimeFormattedToDirectoryName.
                replaceAll("\\-","_").
                replaceAll("\\.","_").
                replaceAll("\\:","_").
                replaceAll("T","_");

        return currentTimeFormattedToDirectoryName ;
    }

    public static String today(){
        return LocalDate.now().toString().substring(2); //ex 18-03-10
    }

    //modifies the string representing a file name in a way that
    // the local time is inserted before the extension
    // we will say that it adds a time stamp to filename
    //for example "myFile.txt" could return
    // "myFile_2007_12_03_10_15_30.txt"
    public static String addTodayDateBeforeExtension(String fileName){
        String[] fileNameParts = fileName.split("\\.");
        String suffix = "";
        if (fileNameParts.length >= 2){
            suffix += "." + fileNameParts[fileNameParts.length - 1];
        }
        return fileNameParts[0] + "!_" + today() + suffix;
    }

    //will do the opposite of previous method
    //"myFile_2007_12_03_10_15_30.txt" would return
    //"myFile.txt"
    public static String removeTodayDateBeforeExtension(String fileName){
        String[] fileNameParts = fileName.split("\\.");
        String suffix = "";
        if (fileNameParts.length >= 2){
            suffix += "." + fileNameParts[fileNameParts.length - 1];
        }
        String[] fileNameWithoutDateWithoutExtention = fileNameParts[0].split("\\!");
        return fileNameWithoutDateWithoutExtention[0] + suffix;
    }

    //this method will either insert the timestamp similarly to addTodayDateBeforeExtension
    // if there is not already any time stamp, and would update the time stamp to
    // current time and date if the filename had previously been time stamped
    public static String stampWithTodayDateBeforExtention (String fileName){
        return addTodayDateBeforeExtension(removeTodayDateBeforeExtension(fileName));
    }

    // AzurIO_GUI helper methods
    public static File selectFileOrDirectory(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return jfc.getSelectedFile();
        } else {
            return null;
        }
    }

    public static File selectDirectory(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return jfc.getSelectedFile();
        } else {
            return null;
        }
    }

    public static Path reportWorkingDirectoryLevelOne(Path reportsSavingDirectory){
        Path workingDirectory = ( reportsSavingDirectory == null ?
                        Paths.get(System.getProperty("user.home")).resolve("AzurIO_reports"):
                        reportsSavingDirectory).resolve(currentTimeFormattedToDirectoryName() + "_report");

        //create directories if not already existing
        if (!Files.exists(workingDirectory)) {
            try {
                Files.createDirectories(workingDirectory);
            } catch (IOException e) {
                //fail to create directory
                e.printStackTrace();
            }
        }
        return workingDirectory;
    }

    public static Path reportWorkingDirectoryLevelTwo(Path reportsSavingDirectory, String typeOfReport, String extension){
        Path workingDirectory = reportsSavingDirectory.resolve(typeOfReport + extension);
        //create directories if not already existing
        if (!Files.exists(workingDirectory)) {
            try {
                Files.createDirectories(workingDirectory);
            } catch (IOException e) {
                //fail to create directory
                e.printStackTrace();
            }
        }
        return workingDirectory;
    }

    private static String formatIfNumber (String input){
        return input.trim().replaceAll(" +", " ").chars().allMatch( Character::isDigit )?
                NumberFormat.getNumberInstance(Locale.FRANCE).format(Long.parseLong(input)) : input;
    }

    public static String formattedMessage(String message, int numberOfCharactersPerLine){
        ArrayList<String> formattedMessageArray = new ArrayList<>();
        // replace all those multiple spaces with a single space before splitting
        String[] splitMessage = message.trim().replaceAll(" +", " ").split(" ");
        StringBuffer newLine = new StringBuffer();
        int index = 0;
        while (index < splitMessage.length){
            newLine.append(formatIfNumber(splitMessage[index]) + " ");
            if (newLine.length() >= numberOfCharactersPerLine || index == splitMessage.length -1) {
                formattedMessageArray.add(newLine.toString());
                newLine.setLength(0); // clears newline
            }
            index++;
        }
        StringBuffer formattedMessage = new StringBuffer();
        for (String line : formattedMessageArray){
            formattedMessage.append(line + "\n");
        }
        return formattedMessage.toString() + "\n";
    }
}

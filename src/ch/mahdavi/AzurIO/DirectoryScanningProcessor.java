package ch.mahdavi.AzurIO;

/*
    File: DirectoryScanningProcessor.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoryScanningProcessor uses a DirectoryScanningManager object and a process
    method to traverse a given directory, and count the
    number of real files (not directories) included in the directory and their
    total size in bytes. Since the scanning process is a lot quicker than the audit
    process, a directory scan is made before each directory audit in order to
    get an idea of the work to be done and its progress.

 */


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Locale;

public class DirectoryScanningProcessor {

    private DirectoryScanningManager directoryScanningManager;

    DirectoryScanningProcessor(){

        directoryScanningManager = new DirectoryScanningManager();
    }


    public void process(Path directoryToScan ){
        editStartOfDirectoryScanningMessage(directoryToScan);


        FileVisitorWithExecutor fileVisitorWithExecutor = new FileVisitorWithExecutor(directoryScanningManager);
        directoryScanningManager.initiate();

        try {
            Files.walkFileTree(directoryToScan, fileVisitorWithExecutor);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            directoryScanningManager.terminate();
            editDirectoryScanningReport();
        }
    }




    public DirectoryScanningManager getDirectoryScanningManager() {

        return directoryScanningManager;
    }



    // helper methods

    private void editStartOfDirectoryScanningMessage(Path directoryToScan){
        MessageNotifier.notifyMessage( "starting to scan:  " + directoryToScan.toString());
    }

    private void editDirectoryScanningReport(){
        MessageNotifier.notifyMessage(
                "Directory scanning duration in milliseconds:   " +
                    (
                    directoryScanningManager.getDirectoryScanningAgent().getEndTimeInMilliSeconds() -
                    directoryScanningManager.getDirectoryScanningAgent().getStartTimeInMilliSeconds()
                    ) +
                " Scanned directory contains:   " +
                    directoryScanningManager.getDirectoryScanningAgent().getNumberOfVisitedFiles() + " files " +
                " Scanned directory total size in bytes:   " +
                    directoryScanningManager.getDirectoryScanningAgent().getCombinedSizeOfVisitedFilesInBytes());
    }

}

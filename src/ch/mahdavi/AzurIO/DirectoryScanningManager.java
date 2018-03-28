package ch.mahdavi.AzurIO;

/*
    File: DirectoryScanningManager.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoryScanningManager is a class that implements the Executable interface, as a result
    a DirectoryScanningManager may be incapsulated to a FileVisitorWithExecutor, and in this case
    the execute method would consist in counting each visited file and getting its size in Bytes
    in order to allow the directory scanning process. to do so, DirectoryScanningManager uses a
    DirectoryScanningAgent object

 */


import java.nio.file.Path;


public class DirectoryScanningManager implements Executable{

    private DirectoryScanningAgent directoryScanningAgent;

    DirectoryScanningManager(){
        directoryScanningAgent = new DirectoryScanningAgent();
    }

    void initiate(){
        directoryScanningAgent.setStartTimeInMilliSeconds(System.currentTimeMillis());
    }


    @Override
    public void execute(Path sourcePath) {
        directoryScanningAgent.updateNumberOfVisitedFiles();
        directoryScanningAgent.updateCombinedSizeOfVisitedFilesInBytes(sourcePath.toFile().length());
    }

    void terminate(){
        directoryScanningAgent.setEndTimeInMilliSeconds(System.currentTimeMillis());
    }

    public DirectoryScanningAgent getDirectoryScanningAgent() {
        return directoryScanningAgent;
    }

}



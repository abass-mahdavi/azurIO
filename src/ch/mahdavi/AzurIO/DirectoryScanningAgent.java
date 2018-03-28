package ch.mahdavi.AzurIO;

/*
    File: DirectoryScanningAgent.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoryScanningAgent is a an object that allows the DirectoryScanningManager to
    hold the accounting of the number of visited files and their combined size in bytes
    it also helps to record the start and end time of the directory visit,
    hence its duration

 */


public class DirectoryScanningAgent {

    //numbers of visited files and bytes
    private int numberOfVisitedFiles;
    private long combinedSizeOfVisitedFilesInBytes;

    // start and end times of the directory visit
    long startTimeInMilliSeconds;    //= System.currentTimeMillis();
    long endTimeInMilliSeconds;


    //numbers of visited files and bytes getters and setters and updaters
    public int getNumberOfVisitedFiles() {
        return numberOfVisitedFiles;
    }
    public long getCombinedSizeOfVisitedFilesInBytes() {
        return combinedSizeOfVisitedFilesInBytes;
    }
    public void updateNumberOfVisitedFiles() {
        numberOfVisitedFiles++;
    }
    public void updateCombinedSizeOfVisitedFilesInBytes(long sizeOfVisitedFilesInBytes) {
        combinedSizeOfVisitedFilesInBytes += sizeOfVisitedFilesInBytes;
    }



    //start and end times of the directory visit getters and setters and updaters
    public long getStartTimeInMilliSeconds() {
        return startTimeInMilliSeconds;
    }
    public void setStartTimeInMilliSeconds(long startTimeInMilliSeconds) {
        this.startTimeInMilliSeconds = startTimeInMilliSeconds;
    }
    public long getEndTimeInMilliSeconds() {
        return endTimeInMilliSeconds;
    }
    public void setEndTimeInMilliSeconds(long endTimeInMilliSeconds) {
        this.endTimeInMilliSeconds = endTimeInMilliSeconds;
    }
}

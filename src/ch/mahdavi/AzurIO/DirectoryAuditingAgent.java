package ch.mahdavi.AzurIO;

/*
    File: DirectoryAuditingAgent.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoryAuditingAgent takes care of all the hashMaps, PrintWriters and counters that are used during the
    auditing process

 */


import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;


// will encapsulate the PrintWriter and the directory Map<String,String>
// the purpose of this class is to provide the DirectoryAuditingManger with the required tool to
// audit a directory, in order to edit the reports concerning unique files, redundant files based on the
// shaOne hash of the file. it will also help to identify the "large files". It is up to the user to set the
// size from wich a file will be considered as large.
public class DirectoryAuditingAgent {

    // constants
    final static long LARGE_FILE = 25000000L; // files larger than 10_000 kiloBytes will be considered "large"
    final static long VERY_LARGE_FILE = LARGE_FILE * 3;
    final static String SHA_ONE_VS_FILE_PATH_REPORT = "shaOneVsFilePathReport.txt";
    final static String LARGE_FILES_REPORT = "largeFilesReport.txt";
    final static String REDUNDANT_FILES_REPORT = "redundantFilesReport.txt";
    final static String PROCESSING_ERRORS_REPORT = "processingErrorsReport.txt";
    final static String DIRECTORY_AUDIT_SUMMARY_REPORT = "directoryAuditSummaryReport.txt";


    // number of visited files and bytes counters
    private long numberOfVisitedFiles;
    private long numberOfRedundantFiles;
    private long numberOfLargeFiles;
    private long combinedSizeOfVisitedFilesInBytes;
    private long combinedSizeOfRedundantFilesInBytes;
    private long combinedSizeOfLargeFiles;
    private long startAuditingTimeInMilliSeconds;    //= System.currentTimeMillis();
    private long endAuditingTimeInMilliSeconds;


    //hashMaps
    private Map<String,DirectoryAuditingAgentHelper> shaOneHashAndFileProperties;
    private Map<String,Long>  largeFilesMapShaOneAndSizeInBytes;


    // PrintWriters
    private PrintWriter shaOneHashVsFilePathReportPrintWriter;
    private PrintWriter largeFilesReportPrintWriter;
    private PrintWriter redundantFilesReportPrintWriter;
    private PrintWriter processingErrorsReportPrintWriter;
    private PrintWriter directoryAuditSummaryReportPrintWriter;


    // constructor
    DirectoryAuditingAgent(){
        shaOneHashAndFileProperties = new HashMap<String, DirectoryAuditingAgentHelper>();
        largeFilesMapShaOneAndSizeInBytes = new HashMap<String, Long>();
    }

    //**********************************
    // audit initiation method
    // creates and start all the PrintWriters
    //**********************************
    void initiate(Path directoryToAuditPath, Path reportsSavingDirectory) throws IOException{
        startAuditingTimeInMilliSeconds = System.currentTimeMillis();
        shaOneHashVsFilePathReportPrintWriter =
                new PrintWriter(reportsSavingDirectory.resolve(SHA_ONE_VS_FILE_PATH_REPORT).toString());
        largeFilesReportPrintWriter =
                new PrintWriter(reportsSavingDirectory.resolve(LARGE_FILES_REPORT).toString());
        redundantFilesReportPrintWriter =
                new PrintWriter(reportsSavingDirectory.resolve(REDUNDANT_FILES_REPORT).toString());
        processingErrorsReportPrintWriter =
                new PrintWriter(reportsSavingDirectory.resolve(PROCESSING_ERRORS_REPORT).toString());
        directoryAuditSummaryReportPrintWriter =
                new PrintWriter(reportsSavingDirectory.resolve(DIRECTORY_AUDIT_SUMMARY_REPORT).toString());
        directoryAuditSummaryReportPrintWriter.println("Path of audited directory : " +
                directoryToAuditPath.toString() + "\nStarting date and time of audit : " +
                HelperMethods.currentTimeFormattedToDirectoryName());
    }



    //**********************************
    // audit termination method
    // edits the large file and summary reports and closes all the PrintWriters
    //**********************************
    void terminate(){
        endAuditingTimeInMilliSeconds = System.currentTimeMillis();
        largeFilesReportEdition();
        directoryAuditSummaryReportEdition();
        shaOneHashVsFilePathReportPrintWriter.close();
        largeFilesReportPrintWriter.close();
        redundantFilesReportPrintWriter.close();
        processingErrorsReportPrintWriter.close();
        directoryAuditSummaryReportPrintWriter.close();
    }

    private void largeFilesReportEdition(){
        //sorting hashMap
        Map<String,Long> sortedLargeFilesMapShaOneAndSizeInBytes = largeFilesMapShaOneAndSizeInBytes.entrySet().
                stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for (String shaOne : sortedLargeFilesMapShaOneAndSizeInBytes.keySet()){
            long sourcePathSize = sortedLargeFilesMapShaOneAndSizeInBytes.get(shaOne);
            largeFilesReportPrintWriter.println(shaOne + shaOneHashAndFileProperties.get(shaOne).toString());
            updateNumberOfLargeFiles();
            updateCombinedSizeOfLargeFilesInBytes(sourcePathSize);
        }
    }

    private void directoryAuditSummaryReportEdition(){
        directoryAuditSummaryReportPrintWriter.println(
                        "audited directory statistics : " +
                        "audited directory contained : " +
                        numberOfVisitedFiles + " files " +
                        "  total size of audited directory : " + combinedSizeOfVisitedFilesInBytes +
                        " bytes    audited directory contained : " + numberOfRedundantFiles +
                        " redundant files  total size of redundant files in audited directory : " +
                                combinedSizeOfRedundantFilesInBytes +
                        " bytes   audited directory contained : " + numberOfLargeFiles +
                        " large files  total size of large files in audited directory : " + combinedSizeOfLargeFiles + " bytes");

        String message = "Directory Audit finished the auditing process took:   " +
                (endAuditingTimeInMilliSeconds - startAuditingTimeInMilliSeconds)/1000 +  " seconds ";

        MessageNotifier.notifyMessage(message);
    }




    //**********************************
    // getters setters and updaters
    //**********************************

    // hahMaps getters and setters
    public Map<String, DirectoryAuditingAgentHelper> getShaOneHashAndFileProperties() {
        return shaOneHashAndFileProperties;
    }
    public Map<String, Long> getLargeFilesMapShaOneAndSizeInBytes() {
        return largeFilesMapShaOneAndSizeInBytes;
    }



    // PrintWriters getters and setters
    public PrintWriter getShaOneHashVsFilePathReportPrintWriter() {
        return shaOneHashVsFilePathReportPrintWriter;
    }
    public void setShaOneHashVsFilePathReportPrintWriter(PrintWriter shaOneHashVsFilePathPrintWriter) {
        this.shaOneHashVsFilePathReportPrintWriter = shaOneHashVsFilePathPrintWriter;    }

    public PrintWriter getLargeFilesReportPrintWriter() {
        return largeFilesReportPrintWriter;
    }
    public void setLargeFilesReportPrintWriter(PrintWriter largeFilesReportPritWriter) {
        this.largeFilesReportPrintWriter = largeFilesReportPritWriter;
    }
    public PrintWriter getRedundantFilesReportPrintWriter() {
        return redundantFilesReportPrintWriter;
    }
    public void setRedundantFilesReportPrintWriter(PrintWriter redundantFilesReportPrintWriter) {
        this.redundantFilesReportPrintWriter = redundantFilesReportPrintWriter;
    }
    public PrintWriter getProcessingErrorsReportPrintWriter() {
        return processingErrorsReportPrintWriter;
    }
    public void setProcessingErrorsReportPrintWriter(PrintWriter processingErrorsReportPrintWriter) {
        this.processingErrorsReportPrintWriter = processingErrorsReportPrintWriter;
    }
    public PrintWriter getDirectoryAuditSummaryReportPrintWriter() {
        return directoryAuditSummaryReportPrintWriter;
    }
    public void setDirectoryAuditSummaryReportPrintWriter(PrintWriter directoryAuditSummaryReportPrintWriter) {
        this.directoryAuditSummaryReportPrintWriter = directoryAuditSummaryReportPrintWriter;
    }



    //numbers of visited files and bytes getters and setters and updaters
    public long getNumberOfVisitedFiles() {
        return numberOfVisitedFiles;
    }
    public long getCombinedSizeOfVisitedFilesInBytes() {
        return combinedSizeOfVisitedFilesInBytes;
    }
    public long getNumberOfRedundantFiles() {
        return numberOfRedundantFiles;
    }
    public long getNumberOfLargeFiles() {
        return numberOfLargeFiles;
    }
    public long getCombinedSizeOfRedundantFilesInBytes() {
        return combinedSizeOfRedundantFilesInBytes;
    }
    public long getCombinedSizeOfLargeFiles() {
        return combinedSizeOfLargeFiles;
    }
    public void updateNumberOfVisitedFiles() {
        numberOfVisitedFiles++;
    }
    public void updateNumberOfRedundantFiles() {
        numberOfRedundantFiles++;
    }
    public void updateNumberOfLargeFiles() {
        numberOfLargeFiles++;
    }
    public void updateCombinedSizeOfVisitedFilesInBytes(long sizeOfVisitedFileInKiloBytes) {
        combinedSizeOfVisitedFilesInBytes += sizeOfVisitedFileInKiloBytes;
    }
    public void updateCombinedSizeOfRedundantFilesInBytes(long sizeOfVisitedFileInKiloBytes) {
        combinedSizeOfRedundantFilesInBytes += sizeOfVisitedFileInKiloBytes;
    }
    public void updateCombinedSizeOfLargeFilesInBytes(long sizeOfVisitedFileInKiloBytes) {
        combinedSizeOfLargeFiles += sizeOfVisitedFileInKiloBytes;
    }



    //start and nd time getters and setters
    public long getStartAutingTimeInMilliSeconds() {
        return startAuditingTimeInMilliSeconds;
    }
    public void setStartAutingTimeInMilliSeconds(long startAutingTimeInMilliSeconds) {
        this.startAuditingTimeInMilliSeconds = startAutingTimeInMilliSeconds;
    }
    public long getEndAutingTimeInMilliSeconds() {
        return endAuditingTimeInMilliSeconds;
    }
    public void setEndAutingTimeInMilliSeconds(long endAutingTimeInMilliSeconds) {
        this.endAuditingTimeInMilliSeconds = endAutingTimeInMilliSeconds;
    }
}

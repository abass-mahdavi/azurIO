package ch.mahdavi.AzurIO;

/*
    File: DirectoryAuditingManager.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoryAuditingManager is an object that lays between the DirectoryAuditingAgent and the
    DirectoryAuditingProcessor. The DirectoryAuditingManager extends the Executable interface, and as consequence, its
    execute method can be called by the FileVisitorWithExecutor Object, that is called by DirectoryAuditingProcessor
    during its process method

 */


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class DirectoryAuditingManager implements Executable {

    private DirectoryScanningProcessor directoryScanningProcessor;
    private DirectoryAuditingAgent directoryAuditingAgent;

    DirectoryAuditingManager(){
        directoryScanningProcessor = new DirectoryScanningProcessor();
        directoryAuditingAgent = new DirectoryAuditingAgent();
    }

    //**********************************
    // audit initiation method
    // creates and start all the PrintWriters using initiate() method of directoryAuditingAgent
    //**********************************
    public void initiate(Path directoryToAuditPath, Path reportsSavingDirectory) throws IOException{
        MessageNotifier.notifyMessage(  "starting to audit:   " + directoryToAuditPath.toString());
        getDirectoryScanningProcessor().process(directoryToAuditPath);
        directoryAuditingAgent.initiate(directoryToAuditPath, reportsSavingDirectory);
    }

    //**********************************
    // audit termination method
    // edits the large file and summary reports and closes all the
    // PrintWriters using terminate() method of directoryAuditingAgent
    //**********************************
    void terminate(){
        directoryAuditingAgent.terminate();
    }


    // these are the steps that will be executed during the directory visit
    // check FileVisitorWithExecutor, class used by DirectoryAuditingProcessor
    @Override
    public void execute(Path sourcePath) {
        long sizeOfVisitedFileInBytes = countAndEditFilesAndSizeInBytes(sourcePath);
        String fileChecksumHash = FileOrStringChecksumHash.SHA1.checkSumString(sourcePath.toFile());

        DirectoryAuditingAgentHelper sourcePathDirectoryAuditingAgentHelper = new DirectoryAuditingAgentHelper();
        sourcePathDirectoryAuditingAgentHelper.setFilePath(sourcePath.toString());
        sourcePathDirectoryAuditingAgentHelper.setSizeOfFileInBytes(sizeOfVisitedFileInBytes);

        try {
            BasicFileAttributes sourcePathAttributes = Files.readAttributes(sourcePath, BasicFileAttributes.class);
            sourcePathDirectoryAuditingAgentHelper.setDateOfCreation(sourcePathAttributes.creationTime().toString());
            sourcePathDirectoryAuditingAgentHelper.
                    setDateOfLastModification(sourcePathAttributes.lastModifiedTime().toString());
            sourcePathDirectoryAuditingAgentHelper.setDateOfLasteAccess(sourcePathAttributes.lastAccessTime().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(directoryAuditingAgent.getShaOneHashAndFileProperties().containsKey(fileChecksumHash)){
            directoryAuditingAgent.getRedundantFilesReportPrintWriter().println(
                    fileChecksumHash + "|" + sourcePathDirectoryAuditingAgentHelper.toString());
            directoryAuditingAgent.updateNumberOfRedundantFiles();
            directoryAuditingAgent.updateCombinedSizeOfRedundantFilesInBytes(sizeOfVisitedFileInBytes);
        } else {
            directoryAuditingAgent.getShaOneHashAndFileProperties().put(
                    fileChecksumHash, sourcePathDirectoryAuditingAgentHelper);
            directoryAuditingAgent.getShaOneHashVsFilePathReportPrintWriter().println(
                    fileChecksumHash + "|" + sourcePathDirectoryAuditingAgentHelper.toString());
        }

        if(sizeOfVisitedFileInBytes > DirectoryAuditingAgent.LARGE_FILE){
            directoryAuditingAgent.getLargeFilesMapShaOneAndSizeInBytes().put(fileChecksumHash, sizeOfVisitedFileInBytes);
        }
    }


    // helper method
    // check the size of the file and informs the GUI about estimated remaining work to do to complete the audit
    private long countAndEditFilesAndSizeInBytes(Path sourcePath){

        long sizeOfVisitedFileInBytes = sourcePath.toFile().length();

        if (sizeOfVisitedFileInBytes > DirectoryAuditingAgent.VERY_LARGE_FILE){
            MessageNotifier.notifyMessage( "visiting a large file : " + sizeOfVisitedFileInBytes +
                    " bytes   estimated visit duration: " + estimatedDurationInSeconds(sizeOfVisitedFileInBytes) +
                    " seconds  estimated audit duration left " + estimatedRemainingProcessingDuration() + " seconds");
        }
        directoryAuditingAgent.updateNumberOfVisitedFiles();
        directoryAuditingAgent.updateCombinedSizeOfVisitedFilesInBytes(sizeOfVisitedFileInBytes);

        if (directoryAuditingAgent.getNumberOfVisitedFiles() % 250 == 0){
            MessageNotifier.notifyMessage(directoryAuditingAgent.getNumberOfVisitedFiles() +
                    " files already visited for a total size of " +
                     directoryAuditingAgent.getCombinedSizeOfVisitedFilesInBytes() +
                    " bytes time elapsed since start of audit:   " +
                    processingTimeSoFarInSeconds() +
                    " seconds  estimated audit duration left " + estimatedRemainingProcessingDuration() + " seconds");
        }
        return sizeOfVisitedFileInBytes;
    }

    // helper method
    //measurements of durations and speeds of directory auditing steps
    private long estimatedProcessingSpeedInBytesPerMilliSecond(){
        if(directoryAuditingAgent.getNumberOfVisitedFiles() > 50 ||
                directoryAuditingAgent.getCombinedSizeOfVisitedFilesInBytes() > DirectoryAuditingAgent.VERY_LARGE_FILE) {
            long estimatedProcessingSpeedInBytesPerMilliSecond = 1; // arbitrary non null value
            long now = System.currentTimeMillis();
            long start = directoryAuditingAgent.getStartAutingTimeInMilliSeconds();

            if (now > start) {
                estimatedProcessingSpeedInBytesPerMilliSecond =
                        directoryAuditingAgent.getCombinedSizeOfVisitedFilesInBytes() / (now - start);
            }
            return estimatedProcessingSpeedInBytesPerMilliSecond;
        } else{
            return 500000L; // based on experience to put in a config file
        }
    }
    private long estimatedProcessingSpeedInMicroFilePerMilliSecond(){  //1 file = 10^6 micro files
        if(directoryAuditingAgent.getNumberOfVisitedFiles() > 50) {
            long estimatedProcessingSpeedInMicroFilePerMilliSecond = 1; // arbitrary non null value
            long now = System.currentTimeMillis();
            long start = directoryAuditingAgent.getStartAutingTimeInMilliSeconds();

            if (now > start) {
                estimatedProcessingSpeedInMicroFilePerMilliSecond =
                        (1000000L * directoryAuditingAgent.getNumberOfVisitedFiles()) / (now - start);
            }
            return estimatedProcessingSpeedInMicroFilePerMilliSecond;
        } else {
            return 1500L; // based on experience to put in a config file
        }
    }
    private long estimatedDurationInSeconds (long numberOfBytesToProcess, long numberOfFilesToProcess){
        return ((4*numberOfBytesToProcess/estimatedProcessingSpeedInBytesPerMilliSecond()) / 1000 +
                1000L*numberOfFilesToProcess / estimatedProcessingSpeedInMicroFilePerMilliSecond())/5 + 2 ; //2 is arbitrary
    }
    private long estimatedDurationInSeconds (long numberOfBytesToProcess){
        return estimatedDurationInSeconds (numberOfBytesToProcess, 1);
    }
    private long processingTimeSoFarInSeconds(){
        return  (System.currentTimeMillis() - directoryAuditingAgent.getStartAutingTimeInMilliSeconds()  + 500 )/1000;
    }
    private long estimatedRemainingProcessingDuration(){
        return estimatedDurationInSeconds(
                directoryScanningProcessor.getDirectoryScanningManager().
                                        getDirectoryScanningAgent().getCombinedSizeOfVisitedFilesInBytes() -
                        directoryAuditingAgent.getCombinedSizeOfVisitedFilesInBytes() ,
                directoryScanningProcessor.getDirectoryScanningManager().
                        getDirectoryScanningAgent().getNumberOfVisitedFiles() -
                        directoryAuditingAgent.getNumberOfVisitedFiles());
    }

    // getters
    public DirectoryAuditingAgent getDirectoryAuditingAgent() {
        return directoryAuditingAgent;
    }
    public DirectoryScanningProcessor getDirectoryScanningProcessor() {
        return directoryScanningProcessor;
    }

}


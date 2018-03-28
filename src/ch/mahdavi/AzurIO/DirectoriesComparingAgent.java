package ch.mahdavi.AzurIO;

/*
    File: DirectoriesComparingAgent.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoriesComparingAgent takes care of all the hashMaps, PrintWriters and counters that are used during the
    comparison process

 */


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DirectoriesComparingAgent {

    final static String FILES_CONTAINED_ONLY_IN_DIRECTORY_A = "filesContainedOnlyInDirectoryA.txt";
    final static String FILES_CONTAINED_ONLY_IN_DIRECTORY_B = "filesContainedOnlyInDirectoryB.txt";
    final static String FILES_CONTAINED_IN_BOTH_DIRECTORIES = "filesContainedInBothDirectories.txt";

    private Map<String,DirectoriesComparingAgentHelper> filesContainedOnlyInDirectoryA;
    private Map<String,DirectoriesComparingAgentHelper> filesContainedOnlyInDirectoryB;
    private Map<String,DirectoriesComparingAgentHelper> filesContainedInA_AndPresentInB;
    private Map<String,DirectoriesComparingAgentHelper> filesContainedInB_AndPresentInA;

    private PrintWriter filesContainedOnlyInDirectoryA_ReportPrintWriter;
    private PrintWriter filesContainedOnlyInDirectoryB_ReportPrintWriter;
    private PrintWriter filesContainedInBothDirectoriesReportPrintWriter;

    private long numberOfFilesContainedOnlyInDirectoryA;
    private long numberOfBytesContainedOnlyInDirectoryA;
    private long numberOfFilesContainedOnlyInDirectoryB;
    private long numberOfBytesContainedOnlyInDirectoryB;
    private long numberOfFilesContainedInBothDirectories;
    private long numberOfBytesContainedInBothDirectories;

    DirectoriesComparingAgent(){}

    DirectoriesComparingAgent(DirectoryAuditingProcessor directoryAuditingProcessorA,
                              DirectoryAuditingProcessor directoryAuditingProcessorB){

        Map<String, DirectoryAuditingAgentHelper> directoryA =
                directoryAuditingProcessorA.getDirectoryAuditingManager().
                        getDirectoryAuditingAgent().getShaOneHashAndFileProperties();
        Map<String, DirectoryAuditingAgentHelper> directoryB =
                directoryAuditingProcessorB.getDirectoryAuditingManager().
                        getDirectoryAuditingAgent().getShaOneHashAndFileProperties();

        Map<String, DirectoryAuditingAgentHelper> tempoUnionAandB =
                new HashMap<String, DirectoryAuditingAgentHelper>();
        Map<String, DirectoryAuditingAgentHelper> tempoIntersectAandB =
                new HashMap<String, DirectoryAuditingAgentHelper>();
        Map<String,DirectoriesComparingAgentHelper> tempoFilesContainedOnlyInDirectoryA =
                new HashMap<String, DirectoriesComparingAgentHelper>();
        Map<String,DirectoriesComparingAgentHelper> tempoFilesContainedOnlyInDirectoryB =
                new HashMap<String, DirectoriesComparingAgentHelper>();
        Map<String,DirectoriesComparingAgentHelper> tempoFilesContainedInA_AndPresentInB =
                new HashMap<>();
        Map<String,DirectoriesComparingAgentHelper> tempoFilesContainedInB_AndPresentInA =
                new HashMap<>();

        for (String shaOne : directoryA.keySet()){
            long fileSizeInBytes = directoryA.get(shaOne).getSizeOfFileInBytes();
            if (directoryB.containsKey(shaOne)){
                transfer(shaOne, directoryA, tempoFilesContainedInA_AndPresentInB);
                numberOfFilesContainedInBothDirectories++;
                numberOfBytesContainedInBothDirectories += fileSizeInBytes;
            }else {
                transfer(shaOne, directoryA, tempoFilesContainedOnlyInDirectoryA);
                numberOfFilesContainedOnlyInDirectoryA++;
                numberOfBytesContainedOnlyInDirectoryA += fileSizeInBytes;
            }
        }
        for (String shaOne : directoryB.keySet()){
            long fileSizeInBytes = directoryB.get(shaOne).getSizeOfFileInBytes();
            if (directoryA.containsKey(shaOne)){
                transfer(shaOne, directoryB, tempoFilesContainedInB_AndPresentInA);
            }else {
                transfer(shaOne, directoryB, tempoFilesContainedOnlyInDirectoryB);
                numberOfFilesContainedOnlyInDirectoryB++;
                numberOfBytesContainedOnlyInDirectoryB += fileSizeInBytes;
            }
        }

        //sort the maps by key (now path name)
        filesContainedOnlyInDirectoryA = new TreeMap<>(tempoFilesContainedOnlyInDirectoryA);
        filesContainedOnlyInDirectoryB = new TreeMap<>(tempoFilesContainedOnlyInDirectoryB);
        filesContainedInA_AndPresentInB = new TreeMap<>(tempoFilesContainedInA_AndPresentInB);
        filesContainedInB_AndPresentInA = new TreeMap<>(tempoFilesContainedInB_AndPresentInA);
    }

    //helper method
    private void transfer(String shaOne,
                          Map<String, DirectoryAuditingAgentHelper> source,
                          Map<String,DirectoriesComparingAgentHelper> destination){
        String path = source.get(shaOne).getFilePath();
        DirectoriesComparingAgentHelper directoriesComparingAgentHelper = new DirectoriesComparingAgentHelper();
        directoriesComparingAgentHelper.setShaOneHashOfFile(shaOne);
        directoriesComparingAgentHelper.setSizeOfFileInBytes(source.get(shaOne).getSizeOfFileInBytes());
        directoriesComparingAgentHelper.setDateOfCreation(source.get(shaOne).getDateOfCreation());
        directoriesComparingAgentHelper.setDateOfLasteAccess(source.get(shaOne).getDateOfLasteAccess());
        directoriesComparingAgentHelper.setDateOfLastModification(source.get(shaOne).getDateOfLastModification());
        destination.put(path,directoriesComparingAgentHelper);
    }



    // to move to DirectoryComparingManager

    void createAndEditPrintWriters(Path directorySourceOrA,
                                           Path directoryDestinationOrB,
                                           Path directorySelectedForReportSaving) throws FileNotFoundException {

        filesContainedOnlyInDirectoryA_ReportPrintWriter = new PrintWriter(directorySelectedForReportSaving.
                resolve(FILES_CONTAINED_ONLY_IN_DIRECTORY_A).toString());
        filesContainedOnlyInDirectoryA_ReportPrintWriter.println("Comparison of directory: " +
                directorySourceOrA.toString() + " with: " + directoryDestinationOrB.toString());
        filesContainedOnlyInDirectoryA_ReportPrintWriter.println("The following files are contained only in " +
                directorySourceOrA.toString() + " designated as directory_A or source directory");
        for(String path : filesContainedOnlyInDirectoryA.keySet()){
            filesContainedOnlyInDirectoryA_ReportPrintWriter.println(path + "|" +
                    filesContainedOnlyInDirectoryA.get(path.toString()));
        }
        filesContainedOnlyInDirectoryA_ReportPrintWriter.close();

        filesContainedOnlyInDirectoryB_ReportPrintWriter = new PrintWriter(directorySelectedForReportSaving.
                resolve(FILES_CONTAINED_ONLY_IN_DIRECTORY_B).toString());
        filesContainedOnlyInDirectoryB_ReportPrintWriter.println("Comparison of directory: " +
                directorySourceOrA.toString() + " with: " + directoryDestinationOrB.toString());
        filesContainedOnlyInDirectoryB_ReportPrintWriter.println("The following files are contained only in " +
                directoryDestinationOrB.toString() + " designated as directory_B or destination directory");
        for(String path : filesContainedOnlyInDirectoryB.keySet()){
            filesContainedOnlyInDirectoryB_ReportPrintWriter.println(path + "|" +
                    filesContainedOnlyInDirectoryB.get(path.toString()));
        }
        filesContainedOnlyInDirectoryB_ReportPrintWriter.close();

        filesContainedInBothDirectoriesReportPrintWriter = new PrintWriter(directorySelectedForReportSaving.
                resolve(FILES_CONTAINED_IN_BOTH_DIRECTORIES).toString());
        filesContainedInBothDirectoriesReportPrintWriter.println("Comparison of directory: " +
                directorySourceOrA.toString() + " with: " + directoryDestinationOrB.toString());
        filesContainedInBothDirectoriesReportPrintWriter.println("The following files are contained in " +
                directorySourceOrA.toString() + " designated as directory_A or source directory and "+
                directoryDestinationOrB.toString() + " designated as directory_B or destination directory");
        for(String path : filesContainedInA_AndPresentInB.keySet()){
            filesContainedInBothDirectoriesReportPrintWriter.println(path + "|" +
                    filesContainedInA_AndPresentInB.get(path.toString()) + "|directory_A or source directory" );
        }
        for(String path : filesContainedInB_AndPresentInA.keySet()){
            filesContainedInBothDirectoriesReportPrintWriter.println(path + "|" +
                    filesContainedInB_AndPresentInA.get(path.toString()) + "|directory_B or destination directory" );
        }
        filesContainedInBothDirectoriesReportPrintWriter.close();
    }









    // setters
    public void setFilesContainedOnlyInDirectoryA_ReportPrintWriter(PrintWriter filesContainedOnlyInDirectoryA_ReportPrintWriter) {
        this.filesContainedOnlyInDirectoryA_ReportPrintWriter = filesContainedOnlyInDirectoryA_ReportPrintWriter;
    }

    public void setFilesContainedOnlyInDirectoryB_ReportPrintWriter(PrintWriter filesContainedOnlyInDirectoryB_ReportPrintWriter) {
        this.filesContainedOnlyInDirectoryB_ReportPrintWriter = filesContainedOnlyInDirectoryB_ReportPrintWriter;
    }

    public void setFilesContainedInBothDirectoriesReportPrintWriter(PrintWriter filesContainedInBothDirectoriesReportPrintWriter) {
        this.filesContainedInBothDirectoriesReportPrintWriter = filesContainedInBothDirectoriesReportPrintWriter;
    }

    //getters
    public Map<String, DirectoriesComparingAgentHelper> getFilesContainedOnlyInDirectoryA() {
        return filesContainedOnlyInDirectoryA;
    }

    public Map<String, DirectoriesComparingAgentHelper> getFilesContainedOnlyInDirectoryB() {
        return filesContainedOnlyInDirectoryB;
    }

    public Map<String, DirectoriesComparingAgentHelper> getFilesContainedInA_AndPresentInB() {
        return filesContainedInA_AndPresentInB;
    }

    public Map<String, DirectoriesComparingAgentHelper> getFilesContainedInB_AndPresentInA() {
        return filesContainedInB_AndPresentInA;
    }

    public PrintWriter getFilesContainedOnlyInDirectoryA_ReportPrintWriter() {
        return filesContainedOnlyInDirectoryA_ReportPrintWriter;
    }

    public PrintWriter getFilesContainedOnlyInDirectoryB_ReportPrintWriter() {
        return filesContainedOnlyInDirectoryB_ReportPrintWriter;
    }

    public PrintWriter getFilesContainedInBothDirectoriesReportPrintWriter() {
        return filesContainedInBothDirectoriesReportPrintWriter;
    }

    public long getNumberOfFilesContainedOnlyInDirectoryA() {
        return numberOfFilesContainedOnlyInDirectoryA;
    }

    public long getNumberOfBytesContainedOnlyInDirectoryA() {
        return numberOfBytesContainedOnlyInDirectoryA;
    }

    public long getNumberOfFilesContainedOnlyInDirectoryB() {
        return numberOfFilesContainedOnlyInDirectoryB;
    }

    public long getNumberOfBytesContainedOnlyInDirectoryB() {
        return numberOfBytesContainedOnlyInDirectoryB;
    }

    public long getNumberOfFilesContainedInBothDirectories() {
        return numberOfFilesContainedInBothDirectories;
    }

    public long getNumberOfBytesContainedInBothDirectories() {
        return numberOfBytesContainedInBothDirectories;
    }


}



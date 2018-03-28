package ch.mahdavi.AzurIO;

/*
    File: DirectoriesComparingManager.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoriesComparingManager allows to compare 2 different directories, in order to check the files that are unique
    to each directory and those that are common (exact copies) to both directories.
    DirectoriesComparingManager relies on 2 instances of DirectoryAuditingProcessor, one for each directory to
    compare, and also an instance of DirectoriesComparingAgent which contains the objects used to achieve the comparison

 */


import java.io.FileNotFoundException;
import java.nio.file.Path;

public class DirectoriesComparingManager {

    private DirectoryAuditingProcessor directoryAuditingProcessorSourceOrA;
    private DirectoryAuditingProcessor directoryAuditingProcessorDestinationOrB ;

    private DirectoriesComparingAgent directoriesComparingAgent;

    private Path directorySourceOrA;
    private Path directoryDestinationOrB;
    private Path reportSavingDirectory;

    private String comparingStartingTime;

    private boolean baseReportSavingDirectoryAlreadyCreated;

    DirectoriesComparingManager(){}

    DirectoriesComparingManager(Path directorySourceOrA,
                                Path directoryDestinationOrB,
                                Path reportSavingDirectory)  {
        this.directorySourceOrA = directorySourceOrA;
        this.directoryDestinationOrB = directoryDestinationOrB;
        this.reportSavingDirectory = reportSavingDirectory;

        comparingStartingTime = HelperMethods.currentTimeFormattedToDirectoryName();
    }

    void compareDirectoriesAndEditReports() {
        Path reportSavingBaseDirectory = currentReportSavingBaseDirectory(reportSavingDirectory);
        Path sourceReportSavingDirectory = HelperMethods.reportWorkingDirectoryLevelTwo(
                reportSavingBaseDirectory,"SourceOrA", "_auditReport");
        Path destinationReportSavingDirectory = HelperMethods.reportWorkingDirectoryLevelTwo(
                reportSavingBaseDirectory,"DestinationOrB", "_auditReport");
        Path comparisonReportSavingDirectory = HelperMethods.reportWorkingDirectoryLevelTwo(
                reportSavingBaseDirectory,"comparison", "SourceAndDestination");

        directoryAuditingProcessorSourceOrA = new DirectoryAuditingProcessor(sourceReportSavingDirectory);
        directoryAuditingProcessorDestinationOrB = new DirectoryAuditingProcessor(destinationReportSavingDirectory);

        directoryAuditingProcessorSourceOrA.setBaseReportSavingDirectoryAlreadyCreated(true);
        directoryAuditingProcessorDestinationOrB.setBaseReportSavingDirectoryAlreadyCreated(true);

        MessageNotifier.notifyMessage("Starting to audit directory: " + directorySourceOrA.toString() +
                " designated as source directory or directory_A ");
        directoryAuditingProcessorSourceOrA.process(directorySourceOrA);
        MessageNotifier.notifyMessage("Starting to audit directory: " + directoryDestinationOrB.toString() +
                " designated as destination directory or directory_B ");
        directoryAuditingProcessorDestinationOrB.process(directoryDestinationOrB);

        MessageNotifier.notifyMessage("Starting directory comparison Comparing directories" +
                directorySourceOrA.toString() + " designated as source directory or directory_A with" +
                directoryDestinationOrB.toString() + " designated as destination directory or directory_B");
        directoriesComparingAgent = new DirectoriesComparingAgent(directoryAuditingProcessorSourceOrA,
                directoryAuditingProcessorDestinationOrB);
        try {
            directoriesComparingAgent.createAndEditPrintWriters(directorySourceOrA, directoryDestinationOrB,
                    comparisonReportSavingDirectory);
        } catch (FileNotFoundException e) {
            MessageNotifier.notifyMessage("FileNotFoundException occurred");
            e.printStackTrace();
        }
        MessageNotifier.notifyMessage("directories comparison finished, reports edited");
    }



    // code duplication to correct later
    private Path currentReportSavingBaseDirectory(Path reportsSavingDirectory){
        Path workingDirectory;
        if(baseReportSavingDirectoryAlreadyCreated) {
            workingDirectory = reportsSavingDirectory;
        } else {
            workingDirectory = HelperMethods.reportWorkingDirectoryLevelOne(reportsSavingDirectory);
        }
        return workingDirectory;
    }

    public DirectoriesComparingAgent getDirectoriesComparingAgent() {
        return directoriesComparingAgent;
    }

    public boolean getBaseReportSavingDirectoryAlreadyCreated() {
        return baseReportSavingDirectoryAlreadyCreated;
    }

    public void setBaseReportSavingDirectoryAlreadyCreated(boolean baseReportSavingDirectoryAlreadyCreated) {
        this.baseReportSavingDirectoryAlreadyCreated = baseReportSavingDirectoryAlreadyCreated;
    }
}

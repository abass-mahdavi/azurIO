package ch.mahdavi.AzurIO;

/*
    File: DirectoryArchivingManager.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoryArchivingManager purpose is to copy in a destination directory all the files that are contained in a
    source directory, and that do NOT already have an exact copy in the destination directory.
    DirectoryArchivingManager relies on DirectoryComparingManager to find out the files that need to be copied before
    executing the copying process. It also verifies if each file was correctly copied (integrity check with sha-1 hash)
    It finally publishes a copying report.

 */


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DirectoryArchivingManager {

    final static long LARGE_FILE = 25000000L; // files larger than 10_000 kiloBytes will be considered "large"
    final static long VERY_LARGE_FILE = LARGE_FILE * 3;
    private static String archivingStartingTime = HelperMethods.currentTimeFormattedToDirectoryName();

    private DirectoriesComparingManager directoriesComparingManager;

    private Path sourceDirectory;
    private Path destinationDirectory;
    private Path reportSavingDirectory;

    private PrintWriter filesCopiedFromAtoB_ReportPrintWriter;
    final static String FILES_COPIED_FROM_SOURCE_TO_DESTINATION = "FilesCopiedFromSourceToDestination.txt";

    private boolean directorySuccessfullyCopied = true;



    DirectoryArchivingManager(){}

    DirectoryArchivingManager(Path sourceDirectory,
                              Path destinationDirectory,
                              Path reportSavingDirectory){
        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = destinationDirectory;
        if(reportSavingDirectory == null) {
            this.reportSavingDirectory = HelperMethods.reportWorkingDirectoryLevelOne(reportSavingDirectory);
        } else {
            this.reportSavingDirectory =reportSavingDirectory;
        }
        directoriesComparingManager = new DirectoriesComparingManager(sourceDirectory,
                destinationDirectory, this.reportSavingDirectory);
        directoriesComparingManager.setBaseReportSavingDirectoryAlreadyCreated(true);
    }


    public void archiveSourceDirectoryInDestination (){
        try {
            initiateDirectoryCopyReportsInReportSavingDirectory ();
            selectivelyCopySourceDirectoryFilesToDestinationDirectory();
            MessageNotifier.notifyMessage("directory copy finished");
            if (directorySuccessfullyCopied){
                MessageNotifier.notifyMessage("all files copied with success ");
            } else {
                MessageNotifier.notifyMessage("!!!!!WARNING!!!!!!  some errors happened during " +
                        "the archiving process check report for more details ");
            }
            publishDirectoryCopyReportsInReportSavingDirectory ();
            MessageNotifier.notifyMessage("archiving report published ");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void selectivelyCopySourceDirectoryFilesToDestinationDirectory(){
        int numberOfProcessedFiles = 0;
        int numberOfCopiedFiles = 0;
        directoriesComparingManager.compareDirectoriesAndEditReports();

        String header = "source directory: " + sourceDirectory.toString() + " contains " +
                directoriesComparingManager.getDirectoriesComparingAgent().getNumberOfFilesContainedOnlyInDirectoryA() +
                " files that are not contained in destination directory: " + destinationDirectory.toString() +
                " totaling: " + directoriesComparingManager.getDirectoriesComparingAgent().
                getNumberOfBytesContainedOnlyInDirectoryA() + " bytes";

        filesCopiedFromAtoB_ReportPrintWriter.println(header);
        MessageNotifier.notifyMessage(header);


        for (String fileToCopyPath : directoriesComparingManager.getDirectoriesComparingAgent().
                getFilesContainedOnlyInDirectoryA().keySet()){
            Path relativePathOfFileToCopyInDestination = sourceDirectory.relativize(Paths.get(fileToCopyPath));
            Path absolutePathOfFileToCopyInDestination =  destinationDirectory.
                    resolve(relativePathOfFileToCopyInDestination);
            long sizeOfFileInBytes = directoriesComparingManager.getDirectoriesComparingAgent().
                    getFilesContainedOnlyInDirectoryA().get(fileToCopyPath).getSizeOfFileInBytes();
            if( sizeOfFileInBytes > LARGE_FILE){
                MessageNotifier.notifyMessage(" copying a large file  " + sizeOfFileInBytes +" bytes");
            }

            if (numberOfProcessedFiles % 100 == 0 && numberOfProcessedFiles > 0 ){
                MessageNotifier.notifyMessage(numberOfProcessedFiles +
                        " files have been processed of which " + numberOfCopiedFiles +" have been copied");
            }
            //create directories if not already existing
            if (!Files.exists(absolutePathOfFileToCopyInDestination)) {
                try {
                    Files.createDirectories(absolutePathOfFileToCopyInDestination);
                } catch (IOException e) {
                    //fail to create directory
                    e.printStackTrace();
                }
            }
            //solve the case if a different file with the same name already exists in the destination directory
            while (absolutePathOfFileToCopyInDestination.toFile().isFile()){
                int pathLength = absolutePathOfFileToCopyInDestination.getNameCount();
                String fileName = HelperMethods.addTodayDateBeforeExtension(absolutePathOfFileToCopyInDestination.
                        subpath(pathLength-1, pathLength).toString()) ;
                absolutePathOfFileToCopyInDestination = absolutePathOfFileToCopyInDestination.getParent().
                        resolve(fileName);
            }

            String copyStatus = " copy status = failure !!!!";

            try {
                Files.copy(Paths.get(fileToCopyPath),
                        absolutePathOfFileToCopyInDestination,
                        StandardCopyOption.REPLACE_EXISTING);

                if(directoriesComparingManager.getDirectoriesComparingAgent().getFilesContainedOnlyInDirectoryA().
                        get(fileToCopyPath).getShaOneHashOfFile().equals(FileOrStringChecksumHash.SHA1.
                                    checkSumString(absolutePathOfFileToCopyInDestination.toFile()))){
                    copyStatus = " copy status = success";
                } else {
                    directorySuccessfullyCopied = false;
                    MessageNotifier.notifyMessage("* warning: " +
                                                    copyStatus );
                }
                numberOfCopiedFiles++;
            }
            catch (IOException e){
                e.printStackTrace();
            }
            filesCopiedFromAtoB_ReportPrintWriter.println(fileToCopyPath + " copied to " +
                    absolutePathOfFileToCopyInDestination + copyStatus);

            numberOfProcessedFiles++;
        }
    }

    private void publishDirectoryCopyReportsInReportSavingDirectory () {

        filesCopiedFromAtoB_ReportPrintWriter.close();

    }

    private void initiateDirectoryCopyReportsInReportSavingDirectory () throws FileNotFoundException {
        Path comparisonReportSavingDirectory = HelperMethods.reportWorkingDirectoryLevelTwo(
                reportSavingDirectory,"copy", "SourceToDestination");

        filesCopiedFromAtoB_ReportPrintWriter = new PrintWriter(comparisonReportSavingDirectory.
                resolve(FILES_COPIED_FROM_SOURCE_TO_DESTINATION).toString());
    }
}

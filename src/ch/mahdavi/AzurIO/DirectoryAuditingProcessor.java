package ch.mahdavi.AzurIO;

/*
    File: DirectoryAuditingProcessor.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    The directory audit process is the cornerstone of AzurIO solution. by auditing a
    directory, we mean to:
    list all the real files (that are not directories) included in the directory
    for each single file, get the main attributes (especially fileName, the file path
    and file size in bytes)
    for each file calculate its checksum hash (We used the SHA-1 hash for that purpose)
    with the previous information, we would be able to know for each file weather it
    is unique or redundant (more than one copy of the same file is included in the
    regardless of the filename attribute *), we can also sort or group the files
    according their size (usually a small number of larger file require most of the
    space on the file archiving device)
    The process method of the DirectoryAuditingProcessor will ultimately build the
    hashMap that would capture the above information (all files attributes plus the
    hashChecksum of all the file of the audited directory). It will also create and
    publish several report files related to the directory audit and save them in
    place mentioned by the user.
    He audit information (ie the hashMap) could subsequently used by other classes
    such as the DirectoriesComparingManager and the DirectoryArchivingManager to
    fulfill their tasks.

    DirectoryAuditingProcessor uses a DirectoryAuditingManager object and a process
    method to traverse and audit given directory.

 */


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class DirectoryAuditingProcessor {

    private DirectoryAuditingManager directoryAuditingManager;
    private Path reportSavingDirectory;
    private boolean baseReportSavingDirectoryAlreadyCreated;

    DirectoryAuditingProcessor(){}

    DirectoryAuditingProcessor(Path reportSavingDirectory){
        this.reportSavingDirectory = reportSavingDirectory;
        directoryAuditingManager = new DirectoryAuditingManager();
    }

    public void process(Path directoryToAudit){
        try {
            directoryAuditingManager.initiate(directoryToAudit,currentReportSavingBaseDirectory(reportSavingDirectory));

            FileVisitorWithExecutor fileVisitorWithExecutor =
                                    new FileVisitorWithExecutor(directoryAuditingManager);

            Files.walkFileTree(directoryToAudit, fileVisitorWithExecutor);

        } catch (IOException e) {
            MessageNotifier.notifyMessage("IOException occurred");
            e.printStackTrace();
        } finally {
            directoryAuditingManager.terminate();
        }
    }

    public DirectoryAuditingManager getDirectoryAuditingManager() {
        return directoryAuditingManager;
    }

    public boolean getBaseReportSavingDirectoryAlreadyCreated() {
        return baseReportSavingDirectoryAlreadyCreated;
    }

    public void setBaseReportSavingDirectoryAlreadyCreated(boolean baseReportSavingDirectoryAlreadyCreated) {
        this.baseReportSavingDirectoryAlreadyCreated = baseReportSavingDirectoryAlreadyCreated;
    }


    private Path currentReportSavingBaseDirectory(Path reportsSavingDirectory){
        Path workingDirectory;
        if(baseReportSavingDirectoryAlreadyCreated) {
            //workingDirectory = reportsSavingDirectory;
            workingDirectory = HelperMethods.reportWorkingDirectoryLevelTwo(reportsSavingDirectory,
                    "audit", "Report");
        } else {
            workingDirectory = HelperMethods.reportWorkingDirectoryLevelTwo(HelperMethods.
                    reportWorkingDirectoryLevelOne(reportsSavingDirectory), "audit", "Report");
        }
        return workingDirectory;
    }

}

package ch.mahdavi.AzurIO;

/*
    File: DirectoryStampingManger.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoryStampingManger is a class that implements the Executable interface, as a result
    a DirectoryStampingManger may be incapsulated to a FileVisitorWithExecutor, and in this case
    the execute method would consist in renaming each visited file in order to insert the
    current time stamp (or update it)

 */


import java.nio.file.Path;

public class DirectoryStampingManger implements Executable {
    @Override
    public void execute(Path sourcePath) {
        if(sourcePath.toFile().isFile()) {
            String fileName = sourcePath.getFileName().toString();
            String StampedFileName = HelperMethods.stampWithTodayDateBeforExtention(fileName);
            while(sourcePath.getParent().resolve(StampedFileName).toFile().exists()){
                StampedFileName = "fileNameConflict_" + StampedFileName;
            }
            sourcePath.toFile().renameTo(sourcePath.getParent().resolve(StampedFileName).toFile());

            System.out.println(sourcePath.toString());
            System.out.println(StampedFileName);
        }
    }
}

package ch.mahdavi.AzurIO;

/*
    File: DirectoryStampingProcessor.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoryStampingProcessor uses a DirectoryStampingManger object, and
    provides with a "process" method that traverses a given directory and
    each time it encounters a a real file (that is not a directory), it modifies
    the filename in order to insert a time stamp string before the final extension
    In case the filename had previously been time stamped, it will delete the previous
    time stamp and insert the current time stamp

 */


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryStampingProcessor {
    private DirectoryStampingManger directoryStampingManger;
    DirectoryStampingProcessor(){
        directoryStampingManger = new DirectoryStampingManger();
    }

    public void process(Path directoryToStamp ){
        FileVisitorWithExecutor fileVisitorWithExecutor = new FileVisitorWithExecutor(directoryStampingManger);

        try {
            Files.walkFileTree(directoryToStamp, fileVisitorWithExecutor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

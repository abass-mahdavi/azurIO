package ch.mahdavi.AzurIO;

/*
    File: FileVisitorWithExecutor.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    FileVisitorWithExecutor is a file visitor class that extends SimpleFileVisitor<Path>
    it also encapsulate an object of a type that implements the Executable interface
    As a result, during a directory visit, each time the visited file is a real file
    (by opposition to a directory), the "execute" method of the object will be
    executed.

 */


import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileVisitorWithExecutor extends SimpleFileVisitor<Path> {

    private Executable executor;

    FileVisitorWithExecutor(Executable executor){
        super();
        this.executor = executor;
    }

    @Override
    public FileVisitResult visitFile(Path sourcePath, BasicFileAttributes attrs) {

        if (attrs.isRegularFile()){
            executor.execute(sourcePath);
        }

        return FileVisitResult.CONTINUE;

    }
}
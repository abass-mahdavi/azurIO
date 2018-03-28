package ch.mahdavi.AzurIO;

/*
    File: Executable.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    Executable is a simple java interface that includes a unique method : execute(Path sourcePath)

 */


import java.nio.file.Path;

public interface Executable {
    void execute(Path sourcePath);
}

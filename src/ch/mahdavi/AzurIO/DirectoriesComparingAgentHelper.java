package ch.mahdavi.AzurIO;

/*
    File: DirectoriesComparingAgentHelper.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoriesComparingAgentHelper is a helper class of DirectoriesComparingAgent that allows it to handle fewer variables.
    it extends the GenericAgentHelper abstract class, and, as a result  share some features with
    DirectoryAuditingAgentHelper

 */


public class DirectoriesComparingAgentHelper extends GenericAgentHelper {
    private String shaOneHashOfFile;

    @Override
    public String toString() {
        return shaOneHashOfFile + "|" + super.toString();
    }

    public String getShaOneHashOfFile() {
        return shaOneHashOfFile;
    }

    public void setShaOneHashOfFile(String shaOneHashOfFile) {
        this.shaOneHashOfFile = shaOneHashOfFile;
    }
}

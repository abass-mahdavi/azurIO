package ch.mahdavi.AzurIO;

/*
    File: GenericAgentHelper.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    GenericAgentHelper is an abstract class that encapsulates some of the attributes
    usually associated with a file.
    This abstract class is used a base class by the "agentHelper" classes of azurIO

 */


public abstract class GenericAgentHelper {

    private long   sizeOfFileInBytes;
    private String dateOfCreation;
    private String dateOfLastModification;
    private String dateOfLastAccess;

    @Override
    public String toString() {
        return  sizeOfFileInBytes + "|" +
                dateOfCreation + "|" +
                dateOfLastModification + "|" +
                dateOfLastAccess;
    }

    // getters, setters and updaters

    public long getSizeOfFileInBytes() {
        return sizeOfFileInBytes;
    }
    public void setSizeOfFileInBytes(long sizeOfFileInBytes) {
        this.sizeOfFileInBytes = sizeOfFileInBytes;
    }
    public String getDateOfCreation() {
        return dateOfCreation;
    }
    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }
    public String getDateOfLastModification() {
        return dateOfLastModification;
    }
    public void setDateOfLastModification(String dateOfLastModification) {
        this.dateOfLastModification = dateOfLastModification;
    }
    public String getDateOfLasteAccess() {
        return dateOfLastAccess;
    }
    public void setDateOfLasteAccess(String dateOfLasteConsultation) {
        this.dateOfLastAccess = dateOfLasteConsultation;
    }
}


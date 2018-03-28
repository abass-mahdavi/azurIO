package ch.mahdavi.AzurIO;

/*
    File: DirectoryAuditingAgentHelper.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    DirectoryAuditingAgentHelper is a helper class of DirectoryAuditingAgent that allows it to handle fewer variables.
    it extends the GenericAgentHelper abstract class, and, as a result  share some features with
    DirectoryComparingAgentHelper

 */



//helps the DirectoryAuditingAgent class to group the needed file properties in a single object
// before adding it to the hashMaps that helps to audit the directory
// shaOneHashAndFileProperties = new HashMap<String, DirectoryAuditingAgentHelper>();
public class DirectoryAuditingAgentHelper extends GenericAgentHelper{

    private String filePath;

    @Override
    public String toString() {
        return  filePath + "|" + super.toString();
    }


    // getters, setters and updaters
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}

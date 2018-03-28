package ch.mahdavi.AzurIO;

/*
    File: AzurIO_GUI.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    AzurIO_GUI provides a GUI interface that allows a user friendly way to take advantage of all possibilities offered
    by AzurIO, plus some extra features, such a the hash of a string or a single file, of the comparison of 2 different
    files

 */



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

public class AzurIO_GUI {

    // Gui components from deepest to shalowest, top to down and left to right
    // and their related constants and variables

    // fileProcessingGuiFrame
    //
    private JFrame azurIO_GUI_Frame;
    private final static  String frameName = "azurIO";
    private final static int FRAME_WIDTH  = 450;
    private final static int FRAME_HEIGHT = 600;
    private final static String FRAME_NAME = "frame name"; // to update

    // fileProcessingGuiPanel
    //
    private JPanel azurIO_GUI_Panel;
    private final static int PREFERRED_LABEL_HEIGHT = 25;
    private final static int PREFERRED_BUTTON_HEIGHT = 25;


    //selectedSourcePathTextField, selectedDestinationPathTextField;
    //
    private JTextField selectedSourcePathTextField, selectedDestinationPathTextField, selectedReportEditionTextField;
    private final static int TEXT_FIELDS_SIZE_CHARS = 25; // 35 chars


    // sourcePathSelectionButton and destinationPathSelectionButton
    // these buttons have dedicated listeneners
    // look further inner classes
    // private class SourcePathSelectionButtonListener
    // and
    // private class DestinationPathSelectionButtonListener
    //
    private JButton sourcePathSelectionButton, destinationPathSelectionButton, reportEditionPathSelectionButton;
    private final static String SELECT_SOURCE =             "source";
    private final static String SELECT_DESTINATION =        "destination";
    private final static String SELECT_REPORTS_LOCATION =   "reports";


    // sha1RequestButton, sha256RequestButton, sha512RequestButton and md5RequestButton
    // these buttons have dedicated listeners
    // look further inner classes
    // private class Sha1RequestButtonListener
    // and
    // private class Sha256RequestButtonListener
    // and
    // private class Sha512RequestButtonListener
    // and
    // private class Md5RequestButtonListener
    //
    private JButton sha1RequestButton, sha256RequestButton, sha512RequestButton, md5RequestButton;
    private final static String ISSUE_SHA1 =    "SHA - 1";
    private final static String ISSUE_SHA256 =  "SHA-256";
    private final static String ISSUE_SHA512 =  "SHA-512";
    private final static String ISSUE_MD5 =     "MD - 5 ";
    private final static int FILE_PROCESSING_BUTTONS_WIDTH  = FRAME_WIDTH / 7;


    // outputTextArea
    //
    //
    private JTextArea textAreaOutput;
    private JScrollPane textAreaOutputScroller;
    private final static int TEXT_OUTPUT_AREA_NUMBER_OF_LINES  = 6;
    private final static int TEXT_OUTPUT_AREA_NUMBER_OF_COLUMNS = 50;
    private final static String WELCOME_MESSAGE = "welcome message to AzurIO";


    //tempoButton
    private JButton auditButton, compareButton, copyButton, stampButton;
    private final static String AUDIT_DIRECTORY =       " audit ";
    private final static String COMPARE_DIRECTORIES =   "compare"; //maybe limit to files
    private final static String COPY_DIRECTORY =        " copy  ";
    private final static String STAMP_DIRECTORY =       " stamp ";


    private JTextPane statusUpdatePane;

    //solutionNam and author
    private JLabel solutionName, solutionSubtitle, authorName;
    private final static String SOLUTION_NAME = "AzurIO";
    private final static String SOLUTION_SUBTITLE = " - the directory handling solution";
    private final static String AUTHOR_NAME = "Author: Abass MAHDAVI";


    //auditButtonActionListener
    private class AuditButtonActionListener implements ActionListener, Observer{
        DirectoryAuditingProcessor directoryAuditingProcessor;

        public void actionPerformed(ActionEvent ev){
            purgeTextAreas();
            if(Paths.get(selectedSourcePathTextField.getText()).toFile().exists()) {
                Path reportSavingDirectory = reportSavingDirectory();
                directoryAuditingProcessor = new DirectoryAuditingProcessor(reportSavingDirectory);

                if (reportSavingDirectory != null){
                    directoryAuditingProcessor.setBaseReportSavingDirectoryAlreadyCreated(true);
                }

                MessageNotifier.registerObserver(this);
                start();
            }else {
                textAreaOutput.setText(selectedSourcePathTextField.getText() + " is not an existing directory");
            }
        }

        @Override
        public void update(Observable o, Object message) {
            updateHelper((String)message);
        }

        private void start() {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {

                    directoryAuditingProcessor.process(Paths.get(selectedSourcePathTextField.getText()));
                    return null;
                }
            };
            worker.execute();
        }
    }

    //compareButtonActionListener
    private class CompareButtonActionListener implements ActionListener, Observer{
        DirectoriesComparingManager directoriesComparingManager;

        public void actionPerformed(ActionEvent ev){
            purgeTextAreas();
            if(sourceAndDestinationAreComaprable() &&
                Paths.get(selectedSourcePathTextField.getText()).toFile().isDirectory()){
                    textAreaOutput.setText("comparison requested\n");
                Path reportSavingDirectory = reportSavingDirectory();
                    directoriesComparingManager = new DirectoriesComparingManager(
                            Paths.get(selectedSourcePathTextField.getText()),
                            Paths.get(selectedDestinationPathTextField.getText()),
                            reportSavingDirectory);
                if (reportSavingDirectory != null){
                    directoriesComparingManager.setBaseReportSavingDirectoryAlreadyCreated(true);
                }
                    MessageNotifier.registerObserver(this);
                    start();
                } else if(Paths.get(selectedSourcePathTextField.getText()).toFile().isFile() &&
                        Paths.get(selectedDestinationPathTextField.getText()).toFile().isFile() &&
                        Paths.get(selectedSourcePathTextField.getText()).toFile().exists() &&
                        Paths.get(selectedDestinationPathTextField.getText()).toFile().exists()) {
                String sourceShaOne = FileOrStringChecksumHash.SHA1.checkSumString(Paths.get(selectedSourcePathTextField.getText()).toFile());
                String destinationShaOne = FileOrStringChecksumHash.SHA1.checkSumString(Paths.get(selectedDestinationPathTextField.getText()).toFile());

                if (sourceShaOne.equals(destinationShaOne)){
                    textAreaOutput.setText("source and destination files are equal \n " );
                } else{
                    textAreaOutput.setText("source and destination files are not equal");
                }
                textAreaOutput.append("\n sourceFile  SHA-1 = " + sourceShaOne + "\n destination SHA-1 = " + destinationShaOne + "\n");
            } else {
                textAreaOutput.setText("source and destination need \n to be both existing files or directories \n");
            }
        }

        @Override
        public void update(Observable o, Object message) {
            updateHelper((String)message);
        }

        private void start() {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    directoriesComparingManager.compareDirectoriesAndEditReports();
                    return null;
                }
            };
            worker.execute();
        }
    }



    //copyButtonActionListener
    private class CopyButtonActionListener implements ActionListener , Observer {
        DirectoryArchivingManager directoryArchivingManager;

        public void actionPerformed(ActionEvent ev){
            purgeTextAreas();
            if(sourceAndDestinationAreComaprable()) {
                textAreaOutput.setText("copy requested\n");
                directoryArchivingManager = new DirectoryArchivingManager(
                        Paths.get(selectedSourcePathTextField.getText()),
                        Paths.get(selectedDestinationPathTextField.getText()),
                        reportSavingDirectory());

                MessageNotifier.registerObserver(this);

                start();
            } else {
                textAreaOutput.setText("oups something doesn't work, \n please make sure " +
                "selected destination is an existing directory and \n" +
                "selected source is an existing directory or file");
            }
        }

        @Override
        public void update(Observable o, Object message) {
            updateHelper((String)message);
        }

        private void start() {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    directoryArchivingManager.archiveSourceDirectoryInDestination();
                    return null;
                }
            };
            worker.execute();
        }
    }

    private class StampButtonActionListener implements ActionListener {
        DirectoryStampingProcessor directoryStampingProcessor;

        @Override
        public void actionPerformed(ActionEvent e) {
            purgeTextAreas();

            // selectedDestinationPathTextField, selectedReportEditionTextField;
            if(selectedSourcePathTextField.getText().equals(selectedDestinationPathTextField.getText()) &&
                    selectedSourcePathTextField.getText().equals(selectedReportEditionTextField.getText())) {
                directoryStampingProcessor = new DirectoryStampingProcessor();
                directoryStampingProcessor.process(Paths.get(selectedSourcePathTextField.getText()));
                textAreaOutput.setText(selectedSourcePathTextField.getText() + "\n has been stamped");
            }else{
                textAreaOutput.setText("For security reasons \n in order to stamp the files\n of a directory you need\n to select that directory\n in all 3 fields");
            }
        }
    }

    private void updateHelper(String message) {

        statusUpdatePane.setText(message);
        textAreaOutput.append(message);
        textAreaOutput.setCaretPosition(textAreaOutput.getDocument().getLength());

    }



    // inner classes - listeners
    //
    // SourcePathSelectionButtonListener allows  sourcePathSelectionButton to
    // listen when user clicks it and subsequently select the source path and display it in selectedSourcePathTextField
    private class SourcePathSelectionButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev){
            File selectedSourceFile = HelperMethods.selectFileOrDirectory();
            selectedSourcePathTextField.setText(selectedSourceFile.toString());
        }
    }
    //
    // DestinationPathSelectionButtonListener allows  destinationPathSelectionButton to
    // listen when user clicks it and subsequently select the source path and display it in selectedDestinationPathTextField

    private class DestinationPathSelectionButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev){
            File selectedDestination = HelperMethods.selectFileOrDirectory();
            selectedDestinationPathTextField.setText(selectedDestination.toString());
        }
    }

    private class ReportEditionPathSelectionButtoListener implements ActionListener {
        public void actionPerformed(ActionEvent ev){
            File selectedDestination = HelperMethods.selectFileOrDirectory();
            selectedReportEditionTextField.setText(selectedDestination.toString());
        }
    }


    //    Sha1RequestButtonListener
    // Sha1RequestButtonListener allows  dedicated Button to
    // listen when user clicks it and subsequently select the source path and display it in textAreaOutput
    private class Sha1RequestButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev){
            purgeTextAreas();
            File selectedSourcePathFile = selectedSourcePathFieldToRegularFile();
            if (selectedSourcePathFile != null){
                textAreaOutput.setText(("SHA1   : " +
                        FileOrStringChecksumHash.SHA1.checkSumString(selectedSourcePathFile)) + "\n");
            }
            else {
                textAreaOutput.setText("Selected file is not regular file,\n The SHA-1 of the String : \n \"" +
                        selectedSourcePathTextField.getText() + "\" is: \n " +
                        FileOrStringChecksumHash.SHA1.checkSumString(selectedSourcePathTextField.getText()) + "\n");
            }
        }
    }
    //    Sha256RequestButtonListener
    // Sha256RequestButtonListener allows  dedicated Button to
    // listen when user clicks it and subsequently select the source path and display it in textAreaOutput
    private class Sha256RequestButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev){
            purgeTextAreas();
            File selectedSourcePathFile = selectedSourcePathFieldToRegularFile();
            if (selectedSourcePathFile != null){
                textAreaOutput.setText(("SHA256   : " +
                        FileOrStringChecksumHash.SHA256.checkSumString(selectedSourcePathFile)) + "\n");
            }
            else {
                textAreaOutput.setText("Selected file is not regular file,\n The SHA-256 of the String : \n \"" +
                        selectedSourcePathTextField.getText() + "\" is \n " +
                        FileOrStringChecksumHash.SHA256.checkSumString(selectedSourcePathTextField.getText()) + "\n");
            }
        }
    }
    //    Sha512RequestButtonListener
    // Sha512RequestButtonListener allows dedicated Button to
    // listen when user clicks it and subsequently select the source path and display it in textAreaOutput
    private class Sha512RequestButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev){
            purgeTextAreas();
            File selectedSourcePathFile = selectedSourcePathFieldToRegularFile();
            if (selectedSourcePathFile != null){
                textAreaOutput.setText(("SHA512   : " +
                        FileOrStringChecksumHash.SHA512.checkSumString(selectedSourcePathFile)) + "\n");
            }
            else {
                textAreaOutput.setText("Selected file is not regular file,\n The SHA-512 of the String : \n \"" +
                        selectedSourcePathTextField.getText() + "\" is \n " +
                        FileOrStringChecksumHash.SHA512.checkSumString(selectedSourcePathTextField.getText()) + "\n");
            }
        }
    }
    //    MD5RequestButtonListener
    // MD5RequestButtonListener allows dedicated Button to
    // listen when user clicks it and subsequently select the source path and display it in textAreaOutput
    private class Md5RequestButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev){
            purgeTextAreas();
            File selectedSourcePathFile = selectedSourcePathFieldToRegularFile();
            if (selectedSourcePathFile != null){
                textAreaOutput.setText(("MD5   : " +
                        FileOrStringChecksumHash.MD5.checkSumString(selectedSourcePathFile)) + "\n");
            }
            else {
                textAreaOutput.setText("Selected file is not regular file,\n The MD5 of the String : \n \"" +
                        selectedSourcePathTextField.getText() + "\" is \n " +
                        FileOrStringChecksumHash.MD5.checkSumString(selectedSourcePathTextField.getText()) + "\n");
            }
        }
    }



    // helper methods
    //
    //
    Boolean selectedSourcePathFieldIsRegularFile() {
        return Files.isRegularFile(Paths.get(selectedSourcePathTextField.getText()));
    }

    File selectedSourcePathFieldToRegularFile(){
        Path selectedSourcePath = Paths.get(selectedSourcePathTextField.getText());
        if (Files.isRegularFile(selectedSourcePath)){
            return selectedSourcePath.toFile();
        }else {
            return null;
        }
    }

    //
    // inserts a jumpLineLabel
    private void insertJumpLineLabel(){
        if (azurIO_GUI_Panel != null){
            JLabel jumpLineLabel = new JLabel();
            jumpLineLabel.setPreferredSize(new Dimension(FRAME_WIDTH, PREFERRED_LABEL_HEIGHT / 4));
            azurIO_GUI_Panel.add(jumpLineLabel);
        }
    }
    //
    // inserts a spaceSeparationLabel
    private void insertSpaceSeparationLabel(int sepration){
        if (azurIO_GUI_Panel != null){
            JLabel spaceSeparationLabel = new JLabel();
            spaceSeparationLabel.setPreferredSize(new Dimension(sepration, PREFERRED_LABEL_HEIGHT));
            azurIO_GUI_Panel.add(spaceSeparationLabel);
        }
    }

    // prepares for the report saving directory
    private Path reportSavingDirectory() {
        Path reportSavingDirectory;
        if (Paths.get(selectedReportEditionTextField.getText()).toFile().isDirectory() &&
                Paths.get(selectedReportEditionTextField.getText()).toFile().exists()){
            reportSavingDirectory = HelperMethods.reportWorkingDirectoryLevelOne(
                    Paths.get(selectedReportEditionTextField.getText()));
        }else {
            reportSavingDirectory = null;
        }
        return  reportSavingDirectory;
    }

    boolean sourceAndDestinationAreComaprable() {
        return (Paths.get(selectedSourcePathTextField.getText()).toFile().isDirectory() ||
                Paths.get(selectedSourcePathTextField.getText()).toFile().isFile()) &&
                Paths.get(selectedSourcePathTextField.getText()).toFile().exists() &&
                Paths.get(selectedDestinationPathTextField.getText()).toFile().isDirectory() &&
                Paths.get(selectedDestinationPathTextField.getText()).toFile().exists() &&
                (!selectedSourcePathTextField.getText().equals(Paths.get(selectedDestinationPathTextField.getText())));
    }

    private void purgeTextAreas(){
        textAreaOutput.setText("");
        statusUpdatePane.setText("");
    }


    // fileProcessingGui 'go method'
    // generates the Gui
    //
    public void azurIO_GUI_Go(){

        // azurIO_GUI_Frame creation
        azurIO_GUI_Frame = new JFrame(frameName);
        azurIO_GUI_Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        azurIO_GUI_Frame.setSize(FRAME_WIDTH,FRAME_HEIGHT);

        // fileProcessingGuiPanel creation
        azurIO_GUI_Panel = new JPanel();
        azurIO_GUI_Panel.setBackground(Color.BLACK);
        azurIO_GUI_Panel.setLayout(new FlowLayout());

        // fileProcessingGuiPanel insertion into fileProcessingGuiFrame
        azurIO_GUI_Frame.getContentPane().add(BorderLayout.CENTER,azurIO_GUI_Panel);

        // insert jump line
        insertJumpLineLabel();

        // textFields creation
        selectedSourcePathTextField = new JTextField(TEXT_FIELDS_SIZE_CHARS);
        selectedDestinationPathTextField = new JTextField(TEXT_FIELDS_SIZE_CHARS);
        selectedReportEditionTextField = new JTextField(TEXT_FIELDS_SIZE_CHARS);

        // sourcePathSelectionButton, destinationPathSelectionButton creation, reportEditionPathSelectionButton
        sourcePathSelectionButton = new JButton(SELECT_SOURCE);
        sourcePathSelectionButton.setPreferredSize(new Dimension(100, 20));
        sourcePathSelectionButton.addActionListener(new SourcePathSelectionButtonListener());

        destinationPathSelectionButton = new JButton(SELECT_DESTINATION);
        destinationPathSelectionButton.setPreferredSize(new Dimension(100, 20));
        destinationPathSelectionButton.addActionListener(new DestinationPathSelectionButtonListener());

        reportEditionPathSelectionButton = new JButton(SELECT_REPORTS_LOCATION);
        reportEditionPathSelectionButton.setPreferredSize(new Dimension(100, 20));
        reportEditionPathSelectionButton.addActionListener(new ReportEditionPathSelectionButtoListener());

        //sourcePathSelectionButton, destinationPathSelectionButton and related componenents insertion intoo fileProcessingGuiPanel
        azurIO_GUI_Panel.add(selectedSourcePathTextField);
        insertSpaceSeparationLabel(20); // inserts a spaceSeparationLabel
        azurIO_GUI_Panel.add(sourcePathSelectionButton);
        insertJumpLineLabel(); // insert jump line
        azurIO_GUI_Panel.add(selectedDestinationPathTextField);
        insertSpaceSeparationLabel(20); // inserts a spaceSeparationLabel
        azurIO_GUI_Panel.add(destinationPathSelectionButton);
        insertJumpLineLabel(); // insert jump line
        azurIO_GUI_Panel.add(selectedReportEditionTextField);
        insertSpaceSeparationLabel(20); // inserts a spaceSeparationLabel
        azurIO_GUI_Panel.add(reportEditionPathSelectionButton);
        insertJumpLineLabel(); // insert jump line

        // sha1RequestButton, sha256RequestButton, sha512RequestButton,  creation
        sha1RequestButton = new JButton(ISSUE_SHA1);
        sha1RequestButton.addActionListener(new Sha1RequestButtonListener());
        sha256RequestButton = new JButton(ISSUE_SHA256);
        sha256RequestButton.addActionListener(new Sha256RequestButtonListener());
        sha512RequestButton = new JButton(ISSUE_SHA512);
        sha512RequestButton.addActionListener(new Sha512RequestButtonListener());
        md5RequestButton = new JButton(ISSUE_MD5);
        md5RequestButton.addActionListener(new Md5RequestButtonListener());

        //SHA1 256 512 and MD5 request buttons and related componenents insertion intoo fileProcessingGuiPanel
        azurIO_GUI_Panel.add(sha1RequestButton);
        azurIO_GUI_Panel.add(sha256RequestButton);
        azurIO_GUI_Panel.add(sha512RequestButton);
        azurIO_GUI_Panel.add(md5RequestButton);
        insertJumpLineLabel(); // insert jump line

        //audit button
        auditButton = new JButton(AUDIT_DIRECTORY);
        auditButton.addActionListener(new AuditButtonActionListener());
        azurIO_GUI_Panel.add(auditButton);
        insertSpaceSeparationLabel(20); // inserts a spaceSeparationLabel

        //compare button
        compareButton = new JButton(COMPARE_DIRECTORIES);
        compareButton.addActionListener(new CompareButtonActionListener());
        azurIO_GUI_Panel.add(compareButton);
        insertSpaceSeparationLabel(20); // inserts a spaceSeparationLabel


        //copy Button
        copyButton = new JButton(COPY_DIRECTORY);
        copyButton.addActionListener(new CopyButtonActionListener());
        azurIO_GUI_Panel.add(copyButton);

        insertSpaceSeparationLabel(20); // inserts a spaceSeparationLabel

        //stamp Button
        stampButton = new JButton(STAMP_DIRECTORY);
        stampButton.addActionListener(new StampButtonActionListener());
        azurIO_GUI_Panel.add(stampButton);

        insertJumpLineLabel(); // insert jump line

        // outputTextArea and textAreaOutputScroller creation
        textAreaOutput = new JTextArea(TEXT_OUTPUT_AREA_NUMBER_OF_LINES,TEXT_OUTPUT_AREA_NUMBER_OF_COLUMNS);
        textAreaOutputScroller = new JScrollPane(textAreaOutput);
        textAreaOutput.setBackground(Color.black);
        textAreaOutput.setForeground(Color.white);
        textAreaOutput.setFont(new Font("monospaced", Font.PLAIN, 14));
        textAreaOutput.setLineWrap(false);
        textAreaOutput.setText(WELCOME_MESSAGE);
        textAreaOutputScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        textAreaOutputScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        //
        // textAreaOutputScroller insertion intoo cryptoUtilGuiPanel
        azurIO_GUI_Panel.add(textAreaOutputScroller, BorderLayout.CENTER);
        //insertJumpLineLabel(); // insert jump line

        statusUpdatePane = new JTextPane();
        statusUpdatePane.setPreferredSize(new Dimension(330, 100));
        statusUpdatePane.setBackground(Color.BLACK);
        statusUpdatePane.setForeground(Color.green);
        azurIO_GUI_Panel.add(statusUpdatePane, BorderLayout.CENTER);
        insertJumpLineLabel(); // insert jump line

        /*
    private JLabel solutionName, solutionSubtitle, authorName;
    private final static String SOLUTION_NAME = "AzurIO";
    private final static String SOLUTION_SUBTITLE = "the directory handling solution";
    private final static String AUTHOR_NAME = "Author: Abass MAHDAVI";
         */

        solutionName = new JLabel(SOLUTION_NAME);
        solutionName.setForeground(Color.red);
        solutionName.setFont(new Font("Serif", Font.BOLD, 36));

        solutionSubtitle = new JLabel(SOLUTION_SUBTITLE);
        solutionSubtitle.setForeground(Color.red);
        solutionSubtitle.setFont(new Font("Serif", Font.ITALIC, 18));

        authorName = new JLabel(AUTHOR_NAME);
        authorName.setForeground(Color.green);
        authorName.setFont(new Font("Serif", Font.PLAIN, 12));

        azurIO_GUI_Panel.add(solutionName);
        azurIO_GUI_Panel.add(solutionSubtitle);
        //insertJumpLineLabel(); // insert jump line
        azurIO_GUI_Panel.add(authorName);



        azurIO_GUI_Frame.setVisible(true);

    }

}

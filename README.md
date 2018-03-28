# azurIO
«azurIO» is a directory handling application.

It requires Java Runtime Environment 1.8.0 or newer.

## What is “azurIO”

azurIO is a java application that allows its user to:
* audit (1) a directory
* compare (2) two different directories
* archive (3) a source directory in a destination directory
* time stamp (4) a directory

(1 , 2, 3, 4) more on those later

In addition, azurIO may be used to get (calculate) the hash checksum (SHA-1 or 256 or 512 or MD5) of a string or a regular existing file (that is not a directory)
compare two different existing regular files, in or der to check if they are exact copies of not, regardless of the file name

## Directory audit (audit button)

When asked to audit a given directory, azurIO will 
* visit all the files contained that directory
* calculates the SHA-1 checksum of each file and checks weather another copy of the same file had been visited before or not (regardless of the file name)

during the auditing process, azurIO will generate a number of text files (reports), that will allow the user to check the paths of unique or redundant files. It also generates a report containing all the files considered as “large” (currently more than 25 000 000 bytes).

### In order to run a directory audit, 
the user needs to specify the directory to be audited by clicking the “source” button. That will open a window that will allow the user to browse the available directories. The user may also copy past the path of the directory to visit if she knows it.

The user may also select a directory where azurIO will publish the reports, by using the “reports” button, or by copy pasting the path of the report saving directory. If the “reports” field is left blank, the reports will be edited in the user’s home directory.
Then, the user needs to click the “audit” button to launch the auditing process.

## Directories comparison (compare button)
By comparing two different directories, the user will find out the files that are unique to each directory, and those that are common to both (both directories have an exact copy of the same file)

In order to compare two directories, azurIO will first proceed to the auditing of each directory (and therefore publish the related reports) and then compare the directories and publish the comparison reports.

In order to proceed to the comparison of two directories, the user need to specify the first directory in the “source” field using the “source” button, and the second directory in the “destination” field, using the “destination” button. The user may also specify the reports saving directory.

All the paths may also be copy past instead of using the buttons to browse.
Finally, the user needs to click the “compare” button to launch the comparison process.

## Directory archiving (copy button)
When asked to archive a source directory into a destination directory, azurIO will first compare to two directories, then copy from source to destination all the files that are unique to the source. Those that already exist (regardless of the files names) will be skipped. 

azurIO will also make an integrity check (SHA-1 checksum) of each copied file in order to inform the user if any error occurred during the archiving process. azurIO will also publish a number of archiving reports.

In order to archive a source directory to a destination directory, the user needs to proceed in the same manner than she would do for comparing two directories, but at the end click the “copy” button instead of the “compare” button.

## Directory time stamping (stamping button)
Warning, the directory time stamping is a one-way operation and may not be undone. Therefore, it has to be used with caution.

When asked to “stamp” a given directory, azurIO will visit all the files of that directory and rename each of them by adding the current time stamp at the end of the file name (before the extension if any). For example a file named “example.txt” that would be stamped on march 20th 2018 would be renamed “example_!_2018_03_20.txt”. if a file had previously been stamped, the time stamp would be modified to the current day.

In order to time-stamp a given, the user needs to specify the path of that directory in all three available fields (source, destination and reports), that is to reduce the risk of an accidental time-stamping. Then the user needs to click the “stamp” button.

## Other functions
### How to get the hash checksum of a string or a regular file
The user may also type (or copy past) a string in the “source” field, and then click one of the four available hash buttons the get the corresponding hash checksum of the entered string. Similarly, if instead of a string, the user specifies the path to a regular and existing file (not a directory), azurIO can provide the user with the desired hash checksum of the selected file.

### How to compare two regular files
The user may also specify the paths of two different regular an existing file in the “source” and “destination” fields, and then click the “compare” button, and azurIO will verify if the two lected files are exact copies or different, based on their SHA-1 hash checksum.


## Disclaimer  
The solutions proposed by azurIO mobile app (the "application") is for general information purposes only. 
The “application” assumes no responsibility for errors or.
In no event shall the "application" be liable for any special, direct, indirect, consequential, or incidental damages or any damages whatsoever, whether in an action of contract, negligence or other tort, arising out of or in connection with the use of the Service or the contents of the Service. reserves the right to make additions, deletions, or modification to the contents on the Service at any time without prior notice. 
The “application” does not warrant that the solution is free of viruses or other harmful components.


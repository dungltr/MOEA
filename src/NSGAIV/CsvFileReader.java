/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NSGAIV;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author letrung
 */
public class CsvFileReader {
    //Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
        private static final String NEW_LINE_SEPARATOR = "\n";
	
	//Student attributes index
	private static final int STUDENT_ID_IDX = 0;
	private static final int STUDENT_FNAME_IDX = 1;
	private static final int STUDENT_LNAME_IDX = 2;
	private static final int STUDENT_GENDER = 3; 
	private static final int STUDENT_AGE = 4;
	
	public static void readCsvFile(String fileName) {

		BufferedReader fileReader = null;
     
        try {
        	
        	//Create a new list of student to be filled by CSV file data 
        	List students = new ArrayList();
        	
            String line = "";
            
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(fileName));
            
            //Read the CSV file header to skip it
            fileReader.readLine();
            
            //Read the file line by line starting from the second line
            while ((line = fileReader.readLine()) != null) {
                //Get all tokens available in line
                String[] tokens = line.split(COMMA_DELIMITER);
                if (tokens.length > 0) {
                	//Create a new student object and fill his  data
					Student student = new Student(Long.parseLong(tokens[STUDENT_ID_IDX]), tokens[STUDENT_FNAME_IDX], tokens[STUDENT_LNAME_IDX], tokens[STUDENT_GENDER], Integer.parseInt(tokens[STUDENT_AGE]));
					students.add(student);
				}
            }
            
            //Print the new student list
            for (Object student : students) {
				System.out.println(student.toString());
			}
        } 
        catch (Exception e) {
        	System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
            	System.out.println("Error while closing fileReader !!!");
                e.printStackTrace();
            }
        }

	}
        public static String tail(String file ) {
            RandomAccessFile fileHandler = null;
            try {
                fileHandler = new RandomAccessFile( file, "r" );
                long fileLength = fileHandler.length() - 1;
                StringBuilder sb = new StringBuilder();

                for(long filePointer = fileLength; filePointer != -1; filePointer--){
                    fileHandler.seek( filePointer );
                    int readByte = fileHandler.readByte();

                    if( readByte == 0xA ) {
                        if( filePointer == fileLength ) {
                            continue;
                        }
                        break;

                    } else if( readByte == 0xD ) {
                        if( filePointer == fileLength - 1 ) {
                            continue;
                        }
                        break;
                    }

                    sb.append( ( char ) readByte );
                }

                String lastLine = sb.reverse().toString();
                /////////////
/*                String line = lastLine;
                List students = new ArrayList();
                String[] tokens = line.split(COMMA_DELIMITER);
                if (tokens.length > 0) {
                	//Create a new student object and fill his  data
					Student student = new Student(Long.parseLong(tokens[STUDENT_ID_IDX]), tokens[STUDENT_FNAME_IDX], tokens[STUDENT_LNAME_IDX], tokens[STUDENT_GENDER], Integer.parseInt(tokens[STUDENT_AGE]));
					students.add(student);
				}
                for (Object student : students) {
				System.out.println(student.toString());
			}
*/                /////////////
                printstudent(readstudent(lastLine));
                return lastLine;
            } catch( java.io.FileNotFoundException e ) {
                e.printStackTrace();
                return null;
            } catch( java.io.IOException e ) {
                e.printStackTrace();
                return null;
            } finally {
                if (fileHandler != null )
                    try {
                        fileHandler.close();
                    } catch (IOException e) {
                        /* ignore */
                    }
            }
        }
        public static ArrayList readstudent(String lastLine){
//            String line = lastLine;
/*                List students = new ArrayList();
                String[] tokens = line.split(COMMA_DELIMITER);
                if (tokens.length > 0) {
                	//Create a new student object and fill his  data
					Student student = new Student(Long.parseLong(tokens[STUDENT_ID_IDX]), tokens[STUDENT_FNAME_IDX], tokens[STUDENT_LNAME_IDX], tokens[STUDENT_GENDER], Integer.parseInt(tokens[STUDENT_AGE]));
					students.add(student);
				}
*/
//                for (Object student : students) {
//				System.out.println(student.toString());
//			}
            List students = new ArrayList();
            String[] abc = null;
            abc = lastLine.split(NEW_LINE_SEPARATOR);
            int  i = 0;
            while (i< abc.length) {
                //Get all tokens available in line
                String[] tokens = abc[i].split(COMMA_DELIMITER);
                if (tokens.length > 0) {
                	//Create a new student object and fill his  data
					Student student = new Student(Long.parseLong(tokens[STUDENT_ID_IDX]), tokens[STUDENT_FNAME_IDX], tokens[STUDENT_LNAME_IDX], tokens[STUDENT_GENDER], Integer.parseInt(tokens[STUDENT_AGE]));
					students.add(student);
				}
                i++;
            }
            return (ArrayList) students;    
        }
        public static void printstudent(ArrayList students){
            
            for (Object student : students) {
				System.out.println(student.toString());
			}
        }
                
        public static String tail2(String file, int lines) {
        java.io.RandomAccessFile fileHandler = null;
        try {
            fileHandler = 
                new java.io.RandomAccessFile( file, "r" );
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();
            int line = 0;

            for(long filePointer = fileLength; filePointer != -1; filePointer--){
                fileHandler.seek( filePointer );
                int readByte = fileHandler.readByte();

                 if( readByte == 0xA ) {
                    if (filePointer < fileLength) {
                        line = line + 1;
                    }
                } else if( readByte == 0xD ) {
                    if (filePointer < fileLength-1) {
                        line = line + 1;
                    }
                }
                if (line >= lines) {
                    break;
                }
                sb.append( ( char ) readByte );
            }

            String lastLine = sb.reverse().toString();
            ///////////////////////
/*            List students = new ArrayList();
            String[] abc = null;
            abc = lastLine.split(NEW_LINE_SEPARATOR);
            int  i = 0;
            while (i< abc.length) {
                //Get all tokens available in line
                String[] tokens = abc[i].split(COMMA_DELIMITER);
                if (tokens.length > 0) {
                	//Create a new student object and fill his  data
					Student student = new Student(Long.parseLong(tokens[STUDENT_ID_IDX]), tokens[STUDENT_FNAME_IDX], tokens[STUDENT_LNAME_IDX], tokens[STUDENT_GENDER], Integer.parseInt(tokens[STUDENT_AGE]));
					students.add(student);
				}
                i++;
            }
*/
//                printstudent((ArrayList) students);
            printstudent(readstudent(lastLine));
            //////////////////////
            return lastLine;
        } catch( java.io.FileNotFoundException e ) {
            e.printStackTrace();
            return null;
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (fileHandler != null )
            try {
                fileHandler.close();
            } catch (IOException e) {
            }
        }

    }
    public static int count(String filename) throws IOException {
    InputStream is = new BufferedInputStream(new FileInputStream(filename));
    try {
    byte[] c = new byte[1024];
    int count = 0;
    int readChars = 0;
    boolean empty = true;
    while ((readChars = is.read(c)) != -1) {
        empty = false;
        for (int i = 0; i < readChars; ++i) {
            if (c[i] == '\n') {
                ++count;
            }
        }
    }
    return (count == 0 && !empty) ? 1 : count;
    } finally {
    is.close();
   }
}
    
}

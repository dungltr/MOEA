/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NSGAIV;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author letrung
 */
public class ReadMatrixCSV {
        //Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
        private static final String NEW_LINE_SEPARATOR = "\n";
        
    public static void readMatrixCsvFile(String fileName) throws FileNotFoundException, IOException {
	BufferedReader reader = new BufferedReader(new FileReader(fileName));
        int lines = 0;
        reader.readLine();
        while (reader.readLine() != null) lines++;
        reader.close();	
        BufferedReader fileReader = null;    
        try {        	
        	//Create a new list of student to be filled by CSV file data 
            List arrays = new ArrayList();        	
            String line = "";           
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(fileName));           
            //Read the CSV file header to skip it
            String tmp = fileReader.readLine();
            System.out.println(tmp);
            int i = 0;
            int j =0;
            int m = 0;
            double value;
            String[] tokens = line.split(COMMA_DELIMITER);
            m = tokens.length - 1;
            double[][] a = new double[lines][m];
            while ((line = fileReader.readLine()) != null) {
                //Get all tokens available in line
                tokens = line.split(COMMA_DELIMITER);
                m = tokens.length;
                double[] number = new double[tokens.length];               
                for (j = 0; tokens.length > j; j++)
                {
                   value = Double.parseDouble(tokens[j]);
                   number[j] = value;
//                   System.out.println(value);
                }
                a[i] = number;
                i++;
            }
//            testScilab.printMatrix(a);           
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
                double[][] abc = convertlastline(lastLine);
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
    public static double[][] convertlastline(String lastLine){
            String[] abc = null;
            abc = lastLine.split(NEW_LINE_SEPARATOR);
            int i = 0;
            int j = 0;
            String[] tokens = abc[0].split(COMMA_DELIMITER);
            double[][] matrix = new double[abc.length][tokens.length]; 
            while (i < abc.length) {
                tokens = abc[i].split(COMMA_DELIMITER);
                for (j = 0; tokens.length > j; j++)
                matrix[i][j] = Double.parseDouble(tokens[j]);
                i++;
            }            
//            testScilab.printMatrix(matrix);
            return matrix;    
        
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
            double[][] abc = convertlastline(lastLine);
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
    public static double[][] readMatrix(String file, int lines) {
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
            double[][] abc = convertlastline(lastLine);
            //////////////////////
            return abc;
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
    public static void printMatrix(double[][] a) {
        int m = a.length;
        int n = a[0].length;
        for (int i = 0; i < m; i++){
            for (int j = 0; j < n; j++)
            {
                System.out.printf("%.2f",a[i][j]);
                System.out.print(" ");
            }    
            System.out.println(); 
        }        
    }
}

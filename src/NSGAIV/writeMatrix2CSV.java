/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NSGAIV;

//import WriteReadData.Student;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author letrung
 */
public class writeMatrix2CSV {
        //Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";	
	//CSV file header
	
    public static void writeMatrix2CSV(double[][] a, String fileName, String FILE_HEADER){
        int m = a.length;
        int n = a[0].length;
        int i = 0;
        String tmp = "";
/*        for (i = 0; n -1 >i; i++)
            tmp = tmp + "number[" + i + "]" + COMMA_DELIMITER;
        if (n - 1 == i) tmp = tmp + "number[" + i + "]";
        String FILE_HEADER = tmp;
*/        
        double[] b = null;
        List arrays = new ArrayList();
        for (i = 0; m >i; i++) 
        arrays.add(a[i]);       
        FileWriter fileWriter = null;				
            try {
                    fileWriter = new FileWriter(fileName);
                    //Write the CSV file header
                    fileWriter.append(FILE_HEADER.toString());
                    //Add a new line separator after the header
                    fileWriter.append(NEW_LINE_SEPARATOR);
                    for (Iterator it = arrays.iterator(); it.hasNext();) {
                        double[] array = (double[]) it.next();
                        tmp = Arrays.toString(array);
                        tmp = tmp.replace("[","");
                        tmp = tmp.replace("]","");
                        tmp = tmp.replace(" ","");
                        fileWriter.append(tmp);
                        fileWriter.append(NEW_LINE_SEPARATOR);
                    }
                    System.out.println("CSV file was created successfully !!!");
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {			
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}			
		}        
    } 
    public static void addArray2Csv(String filename, double[] tmp) throws IOException {		
            Path filePath = Paths.get(filename);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                }
            String add = "";
            int i = 0;
            for (i = 0; tmp.length - 1 > i; i++)
            add = add + tmp[i] + COMMA_DELIMITER;
            if (tmp.length - 1 == i)
            add = add + tmp[i] + NEW_LINE_SEPARATOR;
            Files.write(filePath, add.getBytes(), StandardOpenOption.APPEND);
        }
    public static void addMatrix2Csv(String filename, double[][] tmp) throws IOException {		
            Path filePath = Paths.get(filename);
            if (Files.exists(filePath)) Files.delete(filePath);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                }
            double[] abc = new double[tmp[0].length];
           
            for (int i = 0; i < tmp.length; i++)
            {
                for (int j = 0; j < tmp[0].length; j++)
                    abc[j] = tmp[i][j];
                addArray2Csv(filename, abc);
            }   
            
        }
    public static void storeStringToCSV(String string, String fileName, int index) throws IOException{
        string = string.replace("}", "");
        //System.out.println(string);
        String[] temp = string.split(",");
        double[] value = new double[temp.length+1];
	value[0] = index;
        //System.out.println("value of metric[]"+temp[0]);
        for (int i = 0; i < temp.length; i++){
            temp[i] = temp[i].substring(temp[i].indexOf("=")+1);
	    //temp[i] = (String) temp[i].subSequence(temp[i].indexOf("=")+1,temp[i].indexOf(","));   
            value[i+1]=Double.valueOf(temp[i]);
            //System.out.println("value of metric"+"["+i+"]"+value[i+1]);
        }           
        addArray2Csv(fileName,value);       
    }
}


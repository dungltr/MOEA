package NSGAV;
import NSGAIV.ReadFile;
import NSGAIV.writeMatrix2CSV;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class utilsCSVtoLatex{
	public static void convertCSVtoLatex(String [] nameFiles, String [] algorithms){
		for (int i=0; i< nameFiles.length; i++){
			String Caption = "Average computation time in "+nameFiles[i]+" experiment";
			String home = ReadFile.readhome("HOME_jMetalData")+"/"+"MOEA";
			String fileName = home +"/"+ nameFiles[i].replaceAll(" ", "")+"ComputeTime.tex";
			String file = home +"/"+nameFiles[i].replaceAll(" ", "")+".csv";
			File filePath = new File (file);
			String problem = "";
			String line;
			try{
				if(filePath.exists()){
					writeMatrix2CSV.addHeader2tex(Caption, fileName, algorithms);
	    		    FileReader fr = new FileReader(filePath);
	    		    LineNumberReader lnr = new LineNumberReader(fr);
	    		    int linenumber = 0;
	    	            while (lnr.readLine() != null){
	    	        	linenumber++;
	    	            }
	    	            System.out.println("Total number of lines : " + linenumber);
	    	            lnr.close();
	    	        for (int j = 1; j<linenumber;j++){
	    	        	line = Files.readAllLines(Paths.get(file)).get(j);
	    	        	String[] numbers = line.split(",");
	    	        	double[] tmp = new double[numbers.length-1];
	    	        	problem = numbers[0];
	    	        	for (int k=0; k<tmp.length;k++){
	    	        		tmp[k] = Double.parseDouble(numbers[k+1]);
	    	        	}
	    	        	writeMatrix2CSV.addArray2tex(fileName, tmp, problem);
	    	        }
	    	        writeMatrix2CSV.addBottom2tex(fileName, algorithms);
	    		}else{
	    			 System.out.println("File does not exists! in convertCSVtoLatex");
	    		}
			}catch(IOException e){
	    		e.printStackTrace();
	    	}
		}			
	}
	public static double averageTime(String file, String problem, String algorithms) {
		String fileCSV = file + "/" + algorithms + "_" + problem + ".csv";
		double average = 0;
		File filePath = new File(fileCSV);
		String line;
		try{
			if(filePath.exists()){
    		    FileReader fr = new FileReader(filePath);
    		    LineNumberReader lnr = new LineNumberReader(fr);
    		    int linenumber = 0;
    	            while (lnr.readLine() != null){
    	        	linenumber++;
    	            }
    	        lnr.close();
    	        for (int j = 0; j<linenumber;j++){
    	        		line = Files.readAllLines(Paths.get(fileCSV)).get(j);
    	        		String[] numbers = line.split(",");
    	        		//System.out.println(Double.parseDouble(numbers[numbers.length-1])+"\n");
    	        		average = average + Double.parseDouble(numbers[numbers.length-1])/linenumber;
    	        }
    			}else{
    			 System.out.println("File does not exists! in averageTime in"+fileCSV);
    			}
		}catch(IOException e){
    			e.printStackTrace();
		}
		return average;		
	}
}
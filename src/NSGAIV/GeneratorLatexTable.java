package NSGAIV;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.moeaframework.Analyzer;

import NSGAV.utilsCSVtoLatex;
public class GeneratorLatexTable{
	public static void storeData(Analyzer analyzer, String File, String problem, String [] algorithms) throws IOException{
		String texFile = File+ ".tex";
		String dirProblem = File + "/" + problem;
		File fileProblem = new File(dirProblem);
		if (!fileProblem.exists()) fileProblem.mkdirs();
		String medianFile = File + "/"+problem + "/median.csv";
		String reportFile = File + "/"+problem + "/report.txt";
		File filePath = new File(File);
		if(!filePath.exists()) filePath.createNewFile();
		File fileAnalysis = new File(reportFile);
		analyzer.saveData(filePath,"","_"+problem+".txt");
		if (!fileAnalysis.exists()) fileAnalysis.createNewFile();
		analyzer.saveAnalysis(fileAnalysis);
		double [] Median = new double[algorithms.length];
		String line = "";
		for (int i = 0; i< Median.length; i++){
			line = Files.readAllLines(Paths.get(reportFile)).get(3+i*7);
			line = line.substring(line.indexOf(":")).replaceAll(": ", "");
			Median[i] = Double.parseDouble(line);
		}
		/*
		
		line = Files.readAllLines(Paths.get(reportFile)).get(10);
		line = line.substring(line.indexOf(":")).replaceAll(": ", "");
		Median[1] = Double.parseDouble(line);
		line = Files.readAllLines(Paths.get(reportFile)).get(17);
		line = line.substring(line.indexOf(":")).replaceAll(": ", "");
		Median[2] = Double.parseDouble(line);
		*/
		writeMatrix2CSV.addArray2Csv(medianFile, Median);
		writeMatrix2CSV.addArray2tex(texFile, Median, problem);
	}
	public static void GeneratorComputeTimeToLatex(String caption, List<String> Problems, String[] algorithms) {
		String directory = caption.replace(" ", "");
		String fileCSV = ReadFile.readhome("HOME_jMetalData")+"/"+"MOEA"+"/"+ directory;
		String File = fileCSV + "ComputeTime";//
		
		//File fileDir = new File(File);
		//if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		String Caption = "Average compute time in "+caption+" experiment";
		File fileTex = new File(texFile);
		double[][] averageTime = ComputeTime(fileCSV, Problems, algorithms); 
		if (!fileTex.exists()){
			try {
				fileTex.createNewFile();
				writeMatrix2CSV.addHeader2tex(Caption, texFile,algorithms);
				for (int i=0; i < Problems.size(); i++) {
					writeMatrix2CSV.addArray2tex(texFile, averageTime[i], Problems.get(i));
				}
				writeMatrix2CSV.addBottom2tex(texFile,algorithms);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	public static double [][] ComputeTime(String fileCSV, List<String> Problems, String[] algorithms){
		double [][] average = new double[Problems.size()][algorithms.length];
		for(int i = 0; i<average.length;i++) {
			for(int j = 0; j<average[0].length; j++) {
				average[i][j] = utilsCSVtoLatex.averageTime(fileCSV,Problems.get(i), algorithms[j]);
			}
		}
		ReadMatrixCSV.printMatrix(average);
		return average;
	}
}

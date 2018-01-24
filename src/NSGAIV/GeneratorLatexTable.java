package NSGAIV;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
//import org.moeaframework.ProblemBuilder;
import org.moeaframework.problem.DTLZ.DTLZ1;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.DTLZ.DTLZ3;
import org.moeaframework.problem.DTLZ.DTLZ4;

import NSGAV.utilsCSVtoLatex;
public class GeneratorLatexTable{
	public GeneratorLatexTable() {
		super();
	}
	public static String [] titleAlgorithms(String File, String problem, String [] algorithms){
		String reportFile = File + "/"+problem + "/report.txt";
		String [] titleAlgorithms = new String[algorithms.length];
		String line = "";
		for (int i = 0; i< algorithms.length; i++){
			try {
				line = Files.readAllLines(Paths.get(reportFile)).get(0+i*7);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			line = line.replaceAll(":", "");
			titleAlgorithms[i] = line;
			//System.out.println(titleAlgorithms[i]);
		}
		return  titleAlgorithms;
	}
	public static void storeData(Analyzer analyzer, String File, String problem, String [] algorithms, String Caption) throws IOException{
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
		
		String [] titleAlgorithms = new String[algorithms.length];
		String line = "";
		for (int i = 0; i< algorithms.length; i++){
			line = Files.readAllLines(Paths.get(reportFile)).get(0+i*7);
			line = line.replaceAll(":", "");
			titleAlgorithms[i] = line;
			//System.out.println(titleAlgorithms[i]);
		}
		File fileTex = new File(texFile);
		if (!fileTex.exists()){
			fileTex.createNewFile();
			writeMatrix2CSV.addHeader2tex(Caption, texFile, titleAlgorithms);
		}
		//writeMatrix2CSV.addHeader2tex(Caption, texFile, titleAlgorithms);
		double [] Median = new double[algorithms.length];
		
		line = "";
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
	};
	
	public static void GeneratorComputeTimeToLatex(String homeFile, String caption, List<String> Problems, String[] algorithms) throws IOException {
		String directory = caption.replace(" ", "");
		String fileCSV = homeFile +"/"+ directory;
		String File = fileCSV + "ComputeTime";//
		
		//File fileDir = new File(File);
		//if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		String texFileTable = homeFile +"/"+ directory + ".tex";
		String Caption = "Average compute time in "+caption+" experiment";
		File fileTex = new File(texFile);
		
		String temp = Files.readAllLines(Paths.get(texFileTable)).get(6);
		temp = temp.replaceAll(" ", "").replaceAll("\\\\", "").replaceAll("hline", "").replaceAll("IV", "V").substring(1);
		String [] titleAlgorithms = temp.split("&");
		double[][] averageTime = ComputeTime(fileCSV, Problems, titleAlgorithms); 
		if (!fileTex.exists()){
			try {
				fileTex.createNewFile();
				writeMatrix2CSV.addHeader2tex(Caption, texFile,titleAlgorithms);
				for (int i=0; i < Problems.size(); i++) {
					writeMatrix2CSV.addArray2tex(texFile, averageTime[i], Problems.get(i));
				}
				writeMatrix2CSV.addBottom2tex(texFile,titleAlgorithms);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	/*
	public GeneratorLatexTable GeneratorComputeTimeToLatex1(String homeFile, String caption, List<DTLZ1> Problems, String[] algorithms) {
		return (GeneratorLatexTable)super.GeneratorComputeTimeToLatex(homeFile, caption, Problems, algorithms);
	}
	*/
	public static void GeneratorComputeTimeToLatex(String homeFile, String caption, List<DTLZ1> Problems, String[] algorithms, DTLZ1 pro) throws IOException {
		
		String directory = caption.replace(" ", "");
		String fileCSV = homeFile +"/"+ directory;
		String File = fileCSV + "ComputeTime";//
		
		//File fileDir = new File(File);
		//if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		String texFileTable = homeFile +"/"+ directory + ".tex";
		String Caption = "Average compute time in "+caption+" experiment";
		File fileTex = new File(texFile);
		
		String temp = Files.readAllLines(Paths.get(texFileTable)).get(6);
		temp = temp.replaceAll(" ", "").replaceAll("\\\\", "").replaceAll("hline", "").replaceAll("IV", "V").substring(1);
		String [] titleAlgorithms = temp.split("&");
		double[][] averageTime = ComputeTime(fileCSV, convertProblemsDTLZ1ToString(Problems), titleAlgorithms); 
		if (!fileTex.exists()){
			try {
				fileTex.createNewFile();
				writeMatrix2CSV.addHeader2tex(Caption, texFile,titleAlgorithms);
				for (int i=0; i < Problems.size(); i++) {
					writeMatrix2CSV.addArray2tex(texFile, averageTime[i], convertProblemToString(Problems.get(i)));
				}
				writeMatrix2CSV.addBottom2tex(texFile,titleAlgorithms);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	public static void GeneratorComputeTimeToLatex(String homeFile, String caption, List<DTLZ2> Problems, String[] algorithms, DTLZ2 pro) throws IOException {
		
		String directory = caption.replace(" ", "");
		String fileCSV = homeFile +"/"+ directory;
		String File = fileCSV + "ComputeTime";//
		
		//File fileDir = new File(File);
		//if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		String texFileTable = homeFile +"/"+ directory + ".tex";
		String Caption = "Average compute time in "+caption+" experiment";
		File fileTex = new File(texFile);
		
		String temp = Files.readAllLines(Paths.get(texFileTable)).get(6);
		temp = temp.replaceAll(" ", "").replaceAll("\\\\", "").replaceAll("hline", "").replaceAll("IV", "V").substring(1);
		String [] titleAlgorithms = temp.split("&");
		double[][] averageTime = ComputeTime(fileCSV, convertProblemsDTLZ2ToString(Problems), titleAlgorithms); 
		if (!fileTex.exists()){
			try {
				fileTex.createNewFile();
				writeMatrix2CSV.addHeader2tex(Caption, texFile,titleAlgorithms);
				for (int i=0; i < Problems.size(); i++) {
					writeMatrix2CSV.addArray2tex(texFile, averageTime[i], convertProblemToString(Problems.get(i)));
				}
				writeMatrix2CSV.addBottom2tex(texFile,titleAlgorithms);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	public static void GeneratorComputeTimeToLatex(String homeFile, String caption, List<DTLZ3> Problems, String[] algorithms, DTLZ3 pro) throws IOException {
		
		String directory = caption.replace(" ", "");
		String fileCSV = homeFile +"/"+ directory;
		String File = fileCSV + "ComputeTime";//
		
		//File fileDir = new File(File);
		//if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		String texFileTable = homeFile +"/"+ directory + ".tex";
		String Caption = "Average compute time in "+caption+" experiment";
		File fileTex = new File(texFile);
		
		String temp = Files.readAllLines(Paths.get(texFileTable)).get(6);
		temp = temp.replaceAll(" ", "").replaceAll("\\\\", "").replaceAll("hline", "").replaceAll("IV", "V").substring(1);
		String [] titleAlgorithms = temp.split("&");
		double[][] averageTime = ComputeTime(fileCSV, convertProblemsDTLZ3ToString(Problems), titleAlgorithms); 
		if (!fileTex.exists()){
			try {
				fileTex.createNewFile();
				writeMatrix2CSV.addHeader2tex(Caption, texFile,titleAlgorithms);
				for (int i=0; i < Problems.size(); i++) {
					writeMatrix2CSV.addArray2tex(texFile, averageTime[i], convertProblemToString(Problems.get(i)));
				}
				writeMatrix2CSV.addBottom2tex(texFile,titleAlgorithms);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	public static void GeneratorComputeTimeToLatex(String homeFile, String caption, List<DTLZ4> Problems, String[] algorithms, DTLZ4 pro) throws IOException {
		
		String directory = caption.replace(" ", "");
		String fileCSV = homeFile +"/"+ directory;
		String File = fileCSV + "ComputeTime";//
		
		//File fileDir = new File(File);
		//if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		String texFileTable = homeFile +"/"+ directory + ".tex";
		String Caption = "Average compute time in "+caption+" experiment";
		File fileTex = new File(texFile);
		
		String temp = Files.readAllLines(Paths.get(texFileTable)).get(6);
		temp = temp.replaceAll(" ", "").replaceAll("\\\\", "").replaceAll("hline", "").replaceAll("IV", "V").substring(1);
		String [] titleAlgorithms = temp.split("&");
		double[][] averageTime = ComputeTime(fileCSV, convertProblemsDTLZ4ToString(Problems), titleAlgorithms); 
		if (!fileTex.exists()){
			try {
				fileTex.createNewFile();
				writeMatrix2CSV.addHeader2tex(Caption, texFile,titleAlgorithms);
				for (int i=0; i < Problems.size(); i++) {
					writeMatrix2CSV.addArray2tex(texFile, averageTime[i], convertProblemToString(Problems.get(i)));
				}
				writeMatrix2CSV.addBottom2tex(texFile,titleAlgorithms);
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
	public static double [][] ComputeTimeDTLZ1(String fileCSV, List<DTLZ1> Problems, String[] algorithms){
		double [][] average = new double[Problems.size()][algorithms.length];
		for(int i = 0; i<average.length;i++) {
			for(int j = 0; j<average[0].length; j++) {
				average[i][j] = utilsCSVtoLatex.averageTime(fileCSV,convertProblemToString(Problems.get(i)), algorithms[j]);
			}
		}
		ReadMatrixCSV.printMatrix(average);
		return average;
	}
	
	public static String convertProblemToString(DTLZ1 Problem){
		return Problem.getName().toString()+"_"+Problem.getNumberOfObjectives();
	}
	
	public static String convertProblemToString(DTLZ2 Problem){
		return Problem.getName().toString()+"_"+Problem.getNumberOfObjectives();
	}
	public static String convertProblemToString(DTLZ3 Problem){
		return Problem.getName().toString()+"_"+Problem.getNumberOfObjectives();
	}
	
	public static String convertProblemToString(DTLZ4 Problem){
		return Problem.getName().toString()+"_"+Problem.getNumberOfObjectives();
	}
	public static List<String> convertProblemsDTLZ1ToString(List<DTLZ1> Problems){
		List<String> problemsName = new ArrayList<>();
		for (int i=0; i< Problems.size(); i++)
			problemsName.add(Problems.get(i).getName().toString()+"_"+Problems.get(i).getNumberOfObjectives());
		return problemsName;
	}
	public static List<String> convertProblemsDTLZ2ToString(List<DTLZ2> Problems){
		List<String> problemsName = new ArrayList<>();
		for (int i=0; i< Problems.size(); i++)
			problemsName.add(Problems.get(i).getName().toString()+"_"+Problems.get(i).getNumberOfObjectives());
		return problemsName;
	}
	public static List<String> convertProblemsDTLZ3ToString(List<DTLZ3> Problems){
		List<String> problemsName = new ArrayList<>();
		for (int i=0; i< Problems.size(); i++)
			problemsName.add(Problems.get(i).getName().toString()+"_"+Problems.get(i).getNumberOfObjectives());
		return problemsName;
	}
	public static List<String> convertProblemsDTLZ4ToString(List<DTLZ4> Problems){
		List<String> problemsName = new ArrayList<>();
		for (int i=0; i< Problems.size(); i++)
			problemsName.add(Problems.get(i).getName().toString()+"_"+Problems.get(i).getNumberOfObjectives());
		return problemsName;
	}
}

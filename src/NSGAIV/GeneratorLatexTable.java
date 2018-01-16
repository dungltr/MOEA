package NSGAIV;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.moeaframework.Analyzer;
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
}

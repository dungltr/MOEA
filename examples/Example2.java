/* Copyright 2009-2016 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;

import NSGAIV.GeneratorLatexTable;
import NSGAIV.ReadFile;
import NSGAIV.ReadMatrixCSV;
import NSGAIV.writeMatrix2CSV;
import NSGAV.utilsCSVtoLatex;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**

/**
 * Demonstrates how only a few lines of code are necessary to setup, run
 * and statistically compare multiple algorithms.
 */
public class Example2 {

	public static void main(String[] args) throws IOException {
		List<String> Problems = new ArrayList<>();
		String homeFile =ReadFile.readhome("HOME_jMetalData")+"/"+"MOEA_CrowDistance";
		//String texFile = File + ".tex";
		//String[] problems = {"UF1","UF2","UF3","DTLZ1.8D","DTLZ2.8D","DTLZ3.8D"};
		String[] UF = {"UF1","UF2","UF3"};
		String[] ZDT = {"ZDT1","ZDT2","ZDT3"};
		String[] DTLZ_2 = {"DTLZ1_2","DTLZ2_2","DTLZ3_2"};
		String[] DTLZ_3 = {"DTLZ1_3","DTLZ2_3","DTLZ3_3"};
		String[] DTLZ_4 = {"DTLZ1_4","DTLZ2_4","DTLZ3_4"};
		String[] DTLZ_6 = {"DTLZ1_6","DTLZ2_6","DTLZ3_6"};
		String[] DTLZ_8 = {"DTLZ1_8","DTLZ2_8","DTLZ3_8"};
		//String[] DTLZ = DTLZ_8;
		Problems = addToString(Problems,UF);
		Problems = addToString(Problems,ZDT);
		//Problems = addToString(Problems,DTLZ_3);
		Problems = addToString(Problems,DTLZ_8);
		String[] algorithms = {"NSGAII","NSGAIII", "NSGAV"};//, "GDE3", "eMOEA" };//, "GDE3", "eMOEA" };
		String [] fileNames = new String [4];
		fileNames[0] = "Generational Distance";
		fileNames[1] = "Hyper volume";
		fileNames[2] = "Inverted Generational Distance";
		fileNames[3] = "Maximum Pareto Front Error";		
		
		GenerationalDistance(homeFile, Problems, algorithms);
		GeneratorLatexTable.GeneratorComputeTimeToLatex(homeFile,fileNames[0], Problems, algorithms);
		
		Hypervolume(homeFile, Problems, algorithms);
		GeneratorLatexTable.GeneratorComputeTimeToLatex(homeFile,fileNames[1], Problems, algorithms);
		
		InvertedGenerationalDistance(homeFile, Problems, algorithms);
		GeneratorLatexTable.GeneratorComputeTimeToLatex(homeFile,fileNames[2], Problems, algorithms);
		
		MaximumParetoFrontError(homeFile, Problems, algorithms);
		GeneratorLatexTable.GeneratorComputeTimeToLatex(homeFile,fileNames[3], Problems, algorithms);
		
		//Contribution(File, Problems, algorithms);
		//utilsCSVtoLatex.convertCSVtoLatex(fileNames, algorithms);
	}
	
	public static List<String> addToString (List<String> Problems, String[] problems){
		for (int i =0; i < problems.length; i++)
			Problems.add(problems[i]);
		return Problems;
		
	}
	
	public static void GenerationalDistance(String homeFile, List<String> Problems, String[] algorithms)throws IOException{
		String directory = "GenerationalDistance";
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		String Caption = "Generational Distance";
		File fileTex = new File(texFile);
		if (!fileTex.exists()){
			fileTex.createNewFile();
			writeMatrix2CSV.addHeader2tex(Caption, texFile,algorithms);
		}
		
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testGenerationalDistance(File, Problems.get(i),algorithms);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
	}
	public static void testGenerationalDistance(String File, String problem, String[] algorithms) throws IOException{		
		String directory = "GenerationalDistance";
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(10000);
		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				//.saveAnalysis(filePath)
				.includeGenerationalDistance()
				//.includeHypervolume()
				.showStatisticalSignificance();
		//run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(File,25));
		}

		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		GeneratorLatexTable.storeData(analyzer, File, problem, algorithms);
		analyzer.showStatisticalSignificance();
	}
	
	public static void Hypervolume(String homeFile, List<String> Problems, String[] algorithms)throws IOException{
		String directory = "Hypervolume";
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		String Caption = "Hyper volume";
		File fileTex = new File(texFile);
		if (!fileTex.exists()){
			fileTex.createNewFile();
			writeMatrix2CSV.addHeader2tex(Caption, texFile,algorithms);
		}
		
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testHypervolume(File, Problems.get(i),algorithms);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
	}
	public static void testHypervolume(String File, String problem, String[] algorithms){
		//String directory = "Hypervolume";
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(10000);

		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				//.includeGenerationalDistance()
				.includeHypervolume()
				.showStatisticalSignificance();

		//run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(File,25));
		}

		//print the results
		//analyzer.showAggregate();
		System.out.println("Preprare printing analyzer");
		analyzer.printAnalysis();
		try {
			GeneratorLatexTable.storeData(analyzer, File, problem,algorithms);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		analyzer.showStatisticalSignificance();
	}
	public static void InvertedGenerationalDistance(String homeFile, List<String> Problems, String[] algorithms)throws IOException{
		String directory = "InvertedGenerationalDistance";
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		String Caption = "Inverted Generational Distance";
		File fileTex = new File(texFile);
		if (!fileTex.exists()){
			fileTex.createNewFile();
			writeMatrix2CSV.addHeader2tex(Caption, texFile,algorithms);
		}
		
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testInvertedGenerationalDistance(File, Problems.get(i),algorithms);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
	}
	public static void testInvertedGenerationalDistance(String File, String problem, String[] algorithms){
		//String directory = "InvertedGenerationalDistance";
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(10000);

		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeInvertedGenerationalDistance()
				.showStatisticalSignificance();

		//run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(File,25));
		}

		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		try {
			GeneratorLatexTable.storeData(analyzer, File, problem,algorithms);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		analyzer.showStatisticalSignificance();
	}
	public static void MaximumParetoFrontError(String homeFile, List<String> Problems, String[] algorithms)throws IOException{
		String directory = "MaximumParetoFrontError";
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		String Caption = "Maximum Pareto Front Error";
		File fileTex = new File(texFile);
		if (!fileTex.exists()){
			fileTex.createNewFile();
			writeMatrix2CSV.addHeader2tex(Caption, texFile,algorithms);
		}
		
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testMaximumParetoFrontError(File, Problems.get(i),algorithms);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
	}
	public static void testMaximumParetoFrontError(String File, String problem, String[] algorithms){
		//String directory = "MaximumParetoFrontError";
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(10000);

		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeMaximumParetoFrontError()
				.showStatisticalSignificance();

		//run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(File,25));
		}

		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		try {
			GeneratorLatexTable.storeData(analyzer, File, problem, algorithms);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		analyzer.showStatisticalSignificance();
	}
	public static void Contribution(String homeFile, List<String> Problems, String[] algorithms)throws IOException{
		String directory = "Contribution";
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		String Caption = "Contribution";
		File fileTex = new File(texFile);
		if (!fileTex.exists()){
			fileTex.createNewFile();
			writeMatrix2CSV.addHeader2tex(Caption, texFile,algorithms);
		}
		
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testContribution(File, Problems.get(i),algorithms);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
	}
	public static void testContribution(String File, String problem, String[] algorithms){
		//String directory = "Contribution";
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(10000);

		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeContribution()
				.showStatisticalSignificance();

		//run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(File ,25));
		}

		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		try {
			GeneratorLatexTable.storeData(analyzer, File, problem,algorithms);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		analyzer.showStatisticalSignificance();
	}
	

	
}

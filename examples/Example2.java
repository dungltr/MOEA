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
import org.moeaframework.problem.DTLZ.DTLZ1;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.DTLZ.DTLZ3;
import org.moeaframework.problem.DTLZ.DTLZ4;

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
	public static List<DTLZ1> DTLZ1 (int Min, int Max){
		List<DTLZ1> problemsDTLZ1 = new ArrayList<>();
		for (int i = Min; i <= Max; i++){
			DTLZ1 problem = new DTLZ1(i);
			problemsDTLZ1.add(problem);
		}
		return problemsDTLZ1; 	
	}
	public static List<DTLZ2> DTLZ2 (int Min, int Max){
		List<DTLZ2> problemsDTLZ2 = new ArrayList<>();
		for (int i = Min; i <= Max; i++){
			DTLZ2 problem = new DTLZ2(i);
			problemsDTLZ2.add(problem);
		}
		return problemsDTLZ2; 	
	}
	public static List<DTLZ3> DTLZ3 (int Min, int Max){
		List<DTLZ3> problemsDTLZ3 = new ArrayList<>();
		for (int i = Min; i <= Max; i++){
			DTLZ3 problem = new DTLZ3(i);
			problemsDTLZ3.add(problem);
		}
		return problemsDTLZ3; 	
	}
	public static List<DTLZ4> DTLZ4 (int Min, int Max){
		List<DTLZ4> problemsDTLZ4 = new ArrayList<>();
		for (int i = Min; i <= Max; i++){
			DTLZ4 problem = new DTLZ4(i);
			problemsDTLZ4.add(problem);
		}
		return problemsDTLZ4; 	
	}
	
	public static void main(String[] args) throws IOException {
		List<String> Problems = new ArrayList<>();
		int Min = 5;
		int Max = 8;
		List<DTLZ1> problemsDTLZ1 = DTLZ1 (Min, Max);
		List<DTLZ2> problemsDTLZ2 = DTLZ2 (Min, Max);
		List<DTLZ3> problemsDTLZ3 = DTLZ3 (Min, Max);
		List<DTLZ4> problemsDTLZ4 = DTLZ4 (Min, Max);
		
		String Caption = "";
		String homeFile =ReadFile.readhome("HOME_jMetalData")+"/"+"MOEA_DTLZ_problems_Referencepoint_2standard_NonFast_3algoritms";
		//String texFile = File + ".tex";
		//String[] problems = {"UF1","UF2","UF3","DTLZ1.8D","DTLZ2.8D","DTLZ3.8D"};
		String [] allAlgorithms = { "NSGAV", "NSGAII", "NSGAIII"};//, "eMOEA", "eNSGAII", "GDE3", "MOEAD", "SPEA2","Random"};
				//"MOEAD", "MSOPS", "CMA-ES", "SPEA2", "PAES", "PESA2", "OMOPSO",
				//"SMPSO", "IBEA", "SMS-EMOA", "VEGA", "DBEA", "Random", "RVEA",
				//"RSO" };
		// CMA-ES,"SMS-EMOA" use fastNondominatedSorting.java
		String[] allProblems = { 
			"DTLZ1_2", "DTLZ2_2", "DTLZ3_2", "DTLZ4_2", "DTLZ7_2", 
			"ROT_DTLZ1_2", "ROT_DTLZ2_2", "ROT_DTLZ3_2", "ROT_DTLZ4_2", "ROT_DTLZ7_2", 
			"UF1", "UF2", "UF3", "UF4", "UF5", "UF6", "UF7", "UF8", "UF9", "UF10", "UF11", "UF12", "UF13",
			"CF1", "CF2", "CF3", "CF4", "CF5", "CF6", "CF7", "CF8", "CF9", "CF10",
			"LZ1", "LZ2", "LZ3", "LZ4", "LZ5", "LZ6", "LZ7", "LZ8", "LZ9",
			"WFG1_2", "WFG2_2", "WFG3_2", "WFG4_2", "WFG5_2", "WFG6_2", "WFG7_2", "WFG8_2", "WFG9_2",
			"ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT5", "ZDT6"};/*,
			"Belegundu", "Binh", "Binh2", "Binh3", "Binh4", "Fonseca", 
			"Fonseca2", "Jimenez", "Kita", "Kursawe", "Laumanns", "Lis", 
			"Murata", "Obayashi", "OKA1", "OKA2", "Osyczka", "Osyczka2", 
			"Poloni", "Quagliarella", "Rendon", "Rendon2", "Schaffer", 
			"Schaffer2", "Srinivas", "Tamaki", "Tanaka", "Viennet", 
			"Viennet2", "Viennet3", "Viennet4"};*/
		String[] UF = {"UF1","UF2","UF3"};
		String[] ZDT = {"ZDT1","ZDT2","ZDT3"};
		String[] DTLZ_2 = {"DTLZ1_2","DTLZ2_2","DTLZ3_2"};
		String[] DTLZ_3 = {"DTLZ1_3","DTLZ2_3","DTLZ3_3"};
		String[] DTLZ_4 = {"DTLZ1_4","DTLZ2_4","DTLZ3_4"};
		String[] DTLZ_6 = {"DTLZ1_6","DTLZ2_6","DTLZ3_6"};
		String[] DTLZ_8 = {"DTLZ1_8","DTLZ2_8","DTLZ3_8"};
		//String[] DTLZ = DTLZ_8;
		//Problems = addToString(Problems,allProblems);
		//Problems = addToString(Problems,UF);
		//Problems = addToString(Problems,ZDT);
		Problems = addToString(Problems,DTLZ_2);
		Problems = addToString(Problems,DTLZ_3);
		Problems = addToString(Problems,DTLZ_4);
		Problems = addToString(Problems,DTLZ_6);
		Problems = addToString(Problems,DTLZ_8);
		String[] algorithms = allAlgorithms;//{"NSGAII","NSGAIII", "NSGAV"};//, "GDE3", "eMOEA" };//, "GDE3", "eMOEA" };
		List<String> Standards = new ArrayList<>();
		Standards.add("Generational Distance");
		//Standards.add("Hyper volume");
		Standards.add("Inverted Generational Distance");
		//Standards.add("Maximum Pareto Front Error");
		
		String [] fileNames = new String [4];
		fileNames[0] = "Generational Distance";
		fileNames[1] = "Hyper volume";
		fileNames[2] = "Inverted Generational Distance";
		fileNames[3] = "Maximum Pareto Front Error";	
		
		for (int i=0; i<Standards.size(); i++){
			Caption = Standards.get(i);
			StandardDistance(homeFile, Problems, algorithms, Caption);
			//DTLZ1Distance(homeFile, problemsDTLZ1, algorithms, Caption);
			GeneratorLatexTable.GeneratorComputeTimeToLatex(homeFile, Caption, Problems, algorithms);			
		}
	
		/*
		Caption = GenerationalDistance(homeFile, Problems, algorithms);
		GeneratorLatexTable.GeneratorComputeTimeToLatex(homeFile, Caption, Problems, algorithms);
		
		Caption = Hypervolume(homeFile, Problems, algorithms);
		GeneratorLatexTable.GeneratorComputeTimeToLatex(homeFile, Caption, Problems, algorithms);
		
		Caption = InvertedGenerationalDistance(homeFile, Problems, algorithms);
		GeneratorLatexTable.GeneratorComputeTimeToLatex(homeFile, Caption, Problems, algorithms);
		Caption = MaximumParetoFrontError(homeFile, Problems, algorithms);
		GeneratorLatexTable.GeneratorComputeTimeToLatex(homeFile, Caption, Problems, algorithms);
		*/
		//Contribution(File, Problems, algorithms);
		//utilsCSVtoLatex.convertCSVtoLatex(fileNames, algorithms);
	}
	public static void DTLZ1Distance(String homeFile, List<DTLZ1> Problems, String[] algorithms, String Caption)throws IOException{
		
		String directory = Caption.replace(" ", "");
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
					
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i).getName()+"_"+Problems.get(i).getNumberOfObjectives());
			testStandardDistance(File, Problems.get(i),algorithms, Caption);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
	}
	public static void StandardDistance(String homeFile, List<String> Problems, String[] algorithms, String Caption)throws IOException{
		String directory = Caption.replace(" ", "");
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
					
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testStandardDistance(File, Problems.get(i),algorithms, Caption);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
	}
	public static void testStandardDistance(String File, DTLZ1 problem, String[] algorithms, String Caption) throws IOException{		
		String directory = Caption.replace(" ", "");
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(25000);
		Analyzer analyzer = new Analyzer()
				.withProblem(problem);
		switch(Caption) {
		   case "Generational Distance" :
			  analyzer.includeGenerationalDistance();// Statements
		      break; // optional
		   case "Hyper volume" :
			  analyzer.includeHypervolume();// Statements
		      break; // optional
		   case "Inverted Generational Distance" :
				  analyzer.includeInvertedGenerationalDistance();// Statements
			      break; // optional
		   case "Maximum Pareto Front Error" :
				  analyzer.includeMaximumParetoFrontError();// Statements
			      break; // optional
			      // You can have any number of case statements.
		   default : // Optional
			   analyzer.includeGenerationalDistance();// Statements
		}
		analyzer.showStatisticalSignificance();
		//run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(File,25));
		}
		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		String Problem = problem.getName()+"_"+problem.getNumberOfObjectives();
		GeneratorLatexTable.storeData(analyzer, File, Problem, algorithms, Caption);
		analyzer.showStatisticalSignificance();
	}
	public static void testStandardDistance(String File, String problem, String[] algorithms, String Caption) throws IOException{		
		String directory = Caption.replace(" ", "");
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(25000);
		Analyzer analyzer = new Analyzer()
				.withProblem(problem);
		switch(Caption) {
		   case "Generational Distance" :
			  analyzer.includeGenerationalDistance();// Statements
		      break; // optional
		   case "Hyper volume" :
			  analyzer.includeHypervolume();// Statements
		      break; // optional
		   case "Inverted Generational Distance" :
				  analyzer.includeInvertedGenerationalDistance();// Statements
			      break; // optional
		   case "Maximum Pareto Front Error" :
				  analyzer.includeMaximumParetoFrontError();// Statements
			      break; // optional
			      // You can have any number of case statements.
		   default : // Optional
			   analyzer.includeGenerationalDistance();// Statements
		}
		analyzer.showStatisticalSignificance();
		//run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(File,25));
		}
		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		GeneratorLatexTable.storeData(analyzer, File, problem, algorithms, Caption);
		analyzer.showStatisticalSignificance();
	}
	
	public static List<String> addToString (List<String> Problems, String[] problems){
		for (int i =0; i < problems.length; i++)
			Problems.add(problems[i]);
		return Problems;
		
	}
	
	public static String GenerationalDistance(String homeFile, List<String> Problems, String[] algorithms)throws IOException{
		String Caption = "Generational Distance";
		String directory = Caption.replace(" ", "");
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
					
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testGenerationalDistance(File, Problems.get(i),algorithms, Caption);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
		return Caption;
	}
	public static void testGenerationalDistance(String File, String problem, String[] algorithms, String Caption) throws IOException{		
		String directory = Caption.replace(" ", "");
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(25000);
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
		GeneratorLatexTable.storeData(analyzer, File, problem, algorithms, Caption);
		analyzer.showStatisticalSignificance();
	}
	
	public static String Hypervolume(String homeFile, List<String> Problems, String[] algorithms)throws IOException{
		String Caption = "Hyper volume";
		String directory = Caption.replace(" ", "");
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
			
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testHypervolume(File, Problems.get(i),algorithms, Caption);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
		return Caption;
	}
	public static void testHypervolume(String File, String problem, String[] algorithms, String Caption){
		//String directory = "Hypervolume";
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(25000);

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
			GeneratorLatexTable.storeData(analyzer, File, problem,algorithms, Caption);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		analyzer.showStatisticalSignificance();
	}
	public static String InvertedGenerationalDistance(String homeFile, List<String> Problems, String[] algorithms)throws IOException{
		String Caption = "Inverted Generational Distance";
		String directory = Caption.replace(" ", "");
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
				
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testInvertedGenerationalDistance(File, Problems.get(i),algorithms, Caption);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
		return Caption;
	}
	public static void testInvertedGenerationalDistance(String File, String problem, String[] algorithms, String Caption){
		//String directory = "InvertedGenerationalDistance";
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(25000);

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
			GeneratorLatexTable.storeData(analyzer, File, problem,algorithms, Caption);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		analyzer.showStatisticalSignificance();
	}
	public static String MaximumParetoFrontError(String homeFile, List<String> Problems, String[] algorithms)throws IOException{
		String Caption = "Maximum Pareto Front Error";
		String directory = Caption.replace(" ", "");
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testMaximumParetoFrontError(File, Problems.get(i),algorithms, Caption);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
		return Caption;
	}
	public static void testMaximumParetoFrontError(String File, String problem, String[] algorithms, String Caption){
		//String directory = "MaximumParetoFrontError";
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(25000);

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
			GeneratorLatexTable.storeData(analyzer, File, problem, algorithms, Caption);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		analyzer.showStatisticalSignificance();
	}
	public static String Contribution(String homeFile, List<String> Problems, String[] algorithms)throws IOException{
		String Caption = "Contribution";
		String directory = Caption.replace(" ", "");
		String File = homeFile + "/"+ directory;//
		File fileDir = new File(File);
		if (!fileDir.exists()) fileDir.mkdirs();
		String texFile = File + ".tex";
		
		File fileTex = new File(texFile);
		if (!fileTex.exists()){
			fileTex.createNewFile();
			writeMatrix2CSV.addHeader2tex(Caption, texFile,algorithms);
		}
		
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testContribution(File, Problems.get(i),algorithms, Caption);
		}
		writeMatrix2CSV.addBottom2tex(texFile,algorithms);
		return Caption;
	}
	public static void testContribution(String File, String problem, String[] algorithms, String Caption){
		//String directory = "Contribution";
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(25000);

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
			GeneratorLatexTable.storeData(analyzer, File, problem,algorithms, Caption);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		analyzer.showStatisticalSignificance();
	}
	

	
}

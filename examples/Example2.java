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

import NSGAIV.ReadFile;
import NSGAIV.writeMatrix2CSV;

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
		
		//String[] problems = {"UF1","UF2","UF3","DTLZ1.8D","DTLZ2.8D","DTLZ3.8D"};
		String[] UF = {"UF1","UF2","UF3"};
		String[] ZDT = {"ZDT1","ZDT2","ZDT3"};
		String[] DTLZ_2 = {"DTLZ1_2","DTLZ2_2","DTLZ3_2"};
		String[] DTLZ_3 = {"DTLZ1_3","DTLZ2_3","DTLZ3_3"};
		String[] DTLZ_8 = {"DTLZ1_8","DTLZ2_8","DTLZ3_8"};
		Problems = addToString(Problems,UF);
		Problems = addToString(Problems,ZDT);
		Problems = addToString(Problems,DTLZ_8);
		//Problems = addToString(Problems,DTLZ_8);
		String[] algorithms = {"NSGAII","NSGAIII", "NSGAV"};//, "GDE3", "eMOEA" };//, "GDE3", "eMOEA" };
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testGenerationalDistance(Problems.get(i),algorithms);
		}
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testHypervolume(Problems.get(i),algorithms);
		}
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testInvertedGenerationalDistance(Problems.get(i),algorithms);
			
		}
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testMaximumParetoFrontError(Problems.get(i),algorithms);
		}
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testContribution(Problems.get(i),algorithms);
		}
	}
	public static List<String> addToString (List<String> Problems, String[] problems){
		for (int i =0; i < problems.length; i++)
			Problems.add(problems[i]);
		return Problems;
		
	}
	public static void testAll(String problem, String[] algorithms){
		/*
		Instrumenter instrumenter = new Instrumenter()
				.withProblem(problem)
				.withFrequency(100)
				.attachElapsedTimeCollector()
				.attachGenerationalDistanceCollector();
		*/
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				//.withInstrumenter(instrumenter)
				.withMaxEvaluations(10000);

		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeGenerationalDistance()
				.includeHypervolume()
				.showStatisticalSignificance();
				

		//run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(50));
		}

		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		analyzer.showAll();
		//analyzer.showStatisticalSignificance();
		// print the runtime dynamics
		/*
		Accumulator accumulator = instrumenter.getLastAccumulator();
				System.out.format("  NFE    Time      Generational Distance%n");
				
				for (int i=0; i<accumulator.size("NFE"); i++) {
					System.out.format("%5d    %-8.4f  %-8.4f%n",
							accumulator.get("NFE", i),
							accumulator.get("Elapsed Time", i),
							accumulator.get("GenerationalDistance", i));
				}
		*/		
				
	}
	public static void testGenerationalDistance(String problem, String[] algorithms) throws IOException{
		String File =ReadFile.readhome("HOME_jMetalData")+"/"+problem+"/GenerationalDistance";
		String medianFile = File + "/median.csv";
		String texFile = File + "/median.tex";
		String reportFile = File + "report.txt";
		File filePath = new File(File);
		File fileAnalysis = new File(reportFile);
		
		//filePath.createNewFile();
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
					executor.withAlgorithm(algorithm).runSeeds(5));
		}

		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		/*
		String[] origin = analyzer.getAnalysis().toString().split("\n");
		for (int i =0; i< origin.length;i++) {
			if (origin[i].toLowerCase().contains("median")){
				System.out.println("-----------------"+origin[i]);
				origin[i] = origin[i].substring(origin[i].indexOf(":")).replace(": ", "");
				System.out.println("+++++++++++++++++++++"+origin[i]);
			}
		}
		*/
		
		analyzer.saveData(filePath,"","_"+problem+".txt");
		if (!fileAnalysis.exists()) fileAnalysis.createNewFile();
		analyzer.saveAnalysis(fileAnalysis);
		double [] Median = new double[3];
		String line = Files.readAllLines(Paths.get(reportFile)).get(3);
		line = line.substring(line.indexOf(":")).replaceAll(": ", "");
		Median[0] = Double.parseDouble(line);
		line = Files.readAllLines(Paths.get(reportFile)).get(10);
		line = line.substring(line.indexOf(":")).replaceAll(": ", "");
		Median[1] = Double.parseDouble(line);
		line = Files.readAllLines(Paths.get(reportFile)).get(17);
		line = line.substring(line.indexOf(":")).replaceAll(": ", "");
		Median[2] = Double.parseDouble(line);
		writeMatrix2CSV.addArray2Csv(medianFile, Median);
		writeMatrix2CSV.addArray2tex(texFile, Median, problem);
		//analyzer.saveData(filePath, "a", "");
		//analyzer.saveData(filePath, "a", "");
		
		analyzer.showStatisticalSignificance();
	}
	public static void testHypervolume(String problem, String[] algorithms){
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
					executor.withAlgorithm(algorithm).runSeeds(50));
		}

		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		analyzer.showStatisticalSignificance();
	}
	public static void testInvertedGenerationalDistance(String problem, String[] algorithms){
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
					executor.withAlgorithm(algorithm).runSeeds(50));
		}

		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		analyzer.showStatisticalSignificance();
	}
	public static void testMaximumParetoFrontError(String problem, String[] algorithms){
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
					executor.withAlgorithm(algorithm).runSeeds(50));
		}

		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		analyzer.showStatisticalSignificance();
	}
	public static void testContribution(String problem, String[] algorithms){
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
					executor.withAlgorithm(algorithm).runSeeds(50));
		}

		//print the results
		//analyzer.showAggregate();
		analyzer.printAnalysis();
		analyzer.showStatisticalSignificance();
	}
	

	
}

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;

/**
 * Demonstrates how only a few lines of code are necessary to setup, run
 * and statistically compare multiple algorithms.
 */
public class Example2 {

	public static void main(String[] args) throws IOException {
		List<String> Problems = new ArrayList<>();
		
		String[] problems = {"UF1","UF2","UF3","DTLZ1.8D","DTLZ2.8D","DTLZ3.8D"};
		String[] UF = {"UF1","UF2","UF3"};
		String[] ZDT = {"ZDT1","ZDT2","ZDT3"};
		String[] DTLZ_2 = {"DTLZ1_2","DTLZ2_2","DTLZ3_2"};
		String[] DTLZ_3 = {"DTLZ1_3","DTLZ2_3","DTLZ3_3"};
		String[] DTLZ_4 = {"DTLZ1_4","DTLZ2_4","DTLZ3_4"};
		String[] DTLZ_6 = {"DTLZ1_6","DTLZ2_6","DTLZ3_6"};
		String[] DTLZ_8 = {"DTLZ1_8","DTLZ2_8","DTLZ3_8"};
		//Problems = addToString(Problems,UF);
		//Problems = addToString(Problems,ZDT);
		//Problems = addToString(Problems,DTLZ_2);
		//Problems = addToString(Problems,DTLZ_3);
		Problems = addToString(Problems,DTLZ_4);
		//Problems = addToString(Problems,DTLZ_8);
		String[] algorithms = { "NSGAII","NSGAIII", "NSGAV"};//, "GDE3", "eMOEA"};//, "GDE3", "eMOEA" };
		for (int i = 0; i<Problems.size(); i++){
			System.out.println("Testing algorithms on: "+Problems.get(i));
			testGenerationalDistance(Problems.get(i),algorithms);
			testHypervolume(Problems.get(i),algorithms);
			//testUF(Problems.get(i),algorithms);
		}
	}
	public static List<String> addToString (List<String> Problems, String[] problems){
		for (int i =0; i < problems.length; i++)
			Problems.add(problems[i]);
		return Problems;
		
	}
	public static void testUF(String problem, String[] algorithms){
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
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
		analyzer.showStatisticalSignificance();
	}
	public static void testGenerationalDistance(String problem, String[] algorithms){
		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(10000);

		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeGenerationalDistance()
				//.includeHypervolume()
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
	
}

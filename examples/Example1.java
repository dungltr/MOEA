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
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * Demonstrates using an Executor to solve the UF1 test problem with NSGA-II,
 * one of the most widely-used multiobjective evolutionary algorithms.
 */
public class Example1 {
	public static class MyDTLZ2_Dung extends AbstractProblem {

		/**
		 * Constructs a new instance of the DTLZ2 function, defining it
		 * to include 11 decision variables and 2 objectives.
		 */
		public MyDTLZ2_Dung() {
			super(11, 2);
		}

		/**
		 * Constructs a new solution and defines the bounds of the decision
		 * variables.
		 */
		@Override
		public Solution newSolution() {
			Solution solution = new Solution(getNumberOfVariables(), 
					getNumberOfObjectives());

			for (int i = 0; i < getNumberOfVariables(); i++) {
				solution.setVariable(i, new RealVariable(0.0, 1.0));
			}

			return solution;
		}
		
		/**
		 * Extracts the decision variables from the solution, evaluates the
		 * Rosenbrock function, and saves the resulting objective value back to
		 * the solution. 
		 */
		@Override
		public void evaluate(Solution solution) {
			double[] x = EncodingUtils.getReal(solution);
			double[] f = new double[numberOfObjectives];

			int k = numberOfVariables - numberOfObjectives + 1;

			double g = 0.0;
			for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
				g += Math.pow(x[i] - 0.5, 2.0);
			}

			for (int i = 0; i < numberOfObjectives; i++) {
				f[i] = 1.0 + g;

				for (int j = 0; j < numberOfObjectives - i - 1; j++) {
					f[i] *= Math.cos(0.5 * Math.PI * x[j]);
				}

				if (i != 0) {
					f[i] *= Math.sin(0.5 * Math.PI * x[numberOfObjectives - i - 1]);
				}
			}

			solution.setObjectives(f);
		}
		
	}

	public static void main(String[] args) {
		test1();
		//test3();
		//test4();
		//test5();
		//configure and run this experiment
		
	}
	
	public static void test1(){
		DTLZ2 problem = new DTLZ2(20);
		NondominatedPopulation result = new Executor()
				.withProblem(problem)
				.withAlgorithm("NSGAV")
				.withMaxEvaluations(10000)
				.run();
		
		//display the results
		System.out.format("Objective1  Objective2%n");
		
		for (Solution solution : result) {
			System.out.format("%.4f      %.4f%n",
					solution.getObjective(0),
					solution.getObjective(1));
		}
	}
	public static void test3(){
        //configure and run this experiment
		NondominatedPopulation result = new Executor()
		.withProblem("UF1")
		.withAlgorithm("NSGAV")
		.withMaxEvaluations(10000)
		.distributeOnAllCores()
		.run();

		//display the results
		System.out.format("Objective1  Objective2%n");

		for (Solution solution : result) {
			System.out.format("%.4f      %.4f%n",
			solution.getObjective(0),
			solution.getObjective(1));
		}
	}
	public static void test4(){
        //configure and run this experiment
		NondominatedPopulation result = new Executor()
				//.withProblemClass(MyDTLZ2_Dung.class)
				.withProblem("UF1")
				.withAlgorithm("NSGAV")
				.withMaxEvaluations(10000)
				.distributeOnAllCores()
				.run();

		//display the results
		System.out.format("Objective1  Objective2%n");

		for (Solution solution : result) {
			System.out.format("%.4f      %.4f%n",
			solution.getObjective(0),
			solution.getObjective(1));
		}
	}
	public static void test5(){
		String problem = "UF1";

        //setup the experiment
        Executor executor = new Executor()
                .withProblem(problem)
                .withMaxEvaluations(10000);

        Analyzer analyzer = new Analyzer()
                .withProblem(problem)
                .includeHypervolume()
                .showStatisticalSignificance();

        analyzer.addAll("NSGA-II with Replacement", executor.withAlgorithm("NSGA-II").withProperty("withReplacement", true).runSeeds(50));
        analyzer.addAll("NSGAV with Replacement", executor.withAlgorithm("NSGAV").withProperty("withReplacement", true).runSeeds(50));      
        analyzer.addAll("NSGA-II without Replacement", executor.withAlgorithm("NSGA-II").withProperty("withReplacement", false).runSeeds(50));
        analyzer.addAll("NSGAV without Replacement", executor.withAlgorithm("NSGAV").withProperty("withReplacement", false).runSeeds(50));

        //print the results
        analyzer.printAnalysis();
	}

}

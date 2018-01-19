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
package NSGAV;

import static org.moeaframework.core.FastNondominatedSorting.RANK_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.io.Serializable;

import org.moeaframework.core.*;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.RankComparator;
import org.moeaframework.util.Vector;
import org.moeaframework.util.weights.NormalBoundaryIntersectionGenerator;

/**
 * Implementation of the reference-point-based nondominated sorting method
 * for NSGA-III.  NSGA-III includes an additional parameter, the number of
 * divisions, that controls the spacing of reference points.  For large
 * objective counts, an alternate two-layered approach was also proposed
 * allowing the user to specify the divisions on the outer and inner layer.
 * When using the two-layered approach, the number of outer divisions should
 * less than the number of objectives, otherwise it will generate reference
 * points overlapping with the inner layer.  If there are M objectives and
 * p divisions, then {@code binomialCoefficient(M+p-1, p)} reference points are
 * generated.
 * <p>
 * Unfortunately, since no official implementation has been released by the
 * original authors, we have made our best effort to implement the algorithm as
 * described in the journal article.  We would like to thank Tsung-Che Chiang
 * for developing the first publicly available implementation of NSGA-III in
 * C++.
 * <p>
 * References:
 * <ol>
 *   <li>Deb, K. and Jain, H.  "An Evolutionary Many-Objective Optimization
 *       Algorithm Using Reference-Point-Based Nondominated Sorting Approach,
 *       Part I: Solving Problems With Box Constraints."  IEEE Transactions on
 *       Evolutionary Computation, 18(4):577-601, 2014.
 *   <li>Deb, K. and Jain, H.  "Handling Many-Objective Problems Using an
 *       Improved NSGA-II Procedure.  WCCI 2012 IEEE World Contress on
 *       Computational Intelligence, Brisbane, Australia, June 10-15, 2012.
 *   <li><a href="http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm">C++ Implementation by Tsung-Che Chiang</a>
 * </ol>
 */
public class NSGAVReferencePointNondominatedSortingPopulation extends NondominatedSortingPopulation {

	/**
	 * The name of the attribute for storing the normalized objectives.
	 */
	static final String NORMALIZED_OBJECTIVES = "Normalized Objectives";

	/**
	 * The number of objectives.
	 */
	private final int numberOfObjectives;

	/**
	 * The number of outer divisions.
	 */
	private final int divisionsOuter;

	/**
	 * The number of inner divisions, or {@code 0} if no inner divisions should
	 * be used.
	 */
	private final int divisionsInner;

	/**
	 * The ideal point, updated each iteration.
	 */
	double[] idealPoint;
	static double[] averagePreviousPoint;
	static double[] averageCurrentPoint;

	/**
	 * The list of reference points, or weights.
	 */
	private List<double[]> weights;

	private Solution average;

	private static DominanceComparator comparator;

	//private static DominanceComparator comparator2;

	private static Population resultFilter;

	//private static DominanceComparator comparator3;
	/**
	 * Constructs an empty population that maintains the {@code rank}
	 * attribute for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 */
	public NSGAVReferencePointNondominatedSortingPopulation(int numberOfObjectives) {
		super();
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = 0;
		this.divisionsInner = 0;
		initialize();
	}
	
	/**
	 * Constructs an empty population that maintains the {@code rank}
	 * attribute for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 */
	public NSGAVReferencePointNondominatedSortingPopulation(int numberOfObjectives,
			int divisions) {
		super();
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisions;
		this.divisionsInner = 0;
		initialize();
	}

	/**
	 * Constructs a new population with the specified solutions that maintains
	 * the {@code rank} attribute for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 * @param comparator the dominance comparator
	 * @param iterable the solutions used to initialize this population
	 */
	public NSGAVReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisions,
			DominanceComparator comparator,
			Iterable<? extends Solution> iterable) {
		super(comparator, iterable);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisions;
		this.divisionsInner = 0;

		initialize();
	}

	/**
	 * Constructs an empty population that maintains the {@code rank} attribute
	 * for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 * @param comparator the dominance comparator
	 */
	public NSGAVReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisions,
			DominanceComparator comparator) {
		super(comparator);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisions;
		this.divisionsInner = 0;

		initialize();
	}

	/**
	 * Constructs a new population with the specified solutions that maintains
	 * the {@code rank} attribute for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 * @param iterable the solutions used to initialize this population
	 */
	public NSGAVReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisions,
			Iterable<? extends Solution> iterable) {
		super(iterable);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisions;
		this.divisionsInner = 0;

		initialize();
	}

	/**
	 * Constructs an empty population that maintains the {@code rank} attribute
	 * for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisionsOuter the number of outer divisions
	 * @param divisionsInner the number of inner divisions
	 */
	public NSGAVReferencePointNondominatedSortingPopulation(int numberOfObjectives,
			int divisionsOuter, int divisionsInner) {
		super();
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;

		initialize();
	}

	/**
	 * Constructs a new population with the specified solutions that maintains
	 * the {@code rank} attribute for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisionsOuter the number of outer divisions
	 * @param divisionsInner the number of inner divisions
	 * @param comparator the dominance comparator
	 * @param iterable the solutions used to initialize this population
	 */
	public NSGAVReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisionsOuter, int divisionsInner,
			DominanceComparator comparator,
			Iterable<? extends Solution> iterable) {
		super(comparator, iterable);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;

		initialize();
	}

	/**
	 * Constructs an empty population that maintains the {@code rank} attribute
	 * for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisionsOuter the number of outer divisions
	 * @param divisionsInner the number of inner divisions
	 * @param comparator the dominance comparator
	 */
	public NSGAVReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisionsOuter, int divisionsInner,
			DominanceComparator comparator) {
		super(comparator);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;

		initialize();
	}

	/**
	 * Constructs a new population with the specified solutions that maintains
	 * the {@code rank} attribute for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisionsOuter the number of outer divisions
	 * @param divisionsInner the number of inner divisions
	 * @param iterable the solutions used to initialize this population
	 */
	public NSGAVReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisionsOuter, int divisionsInner,
			Iterable<? extends Solution> iterable) {
		super(iterable);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;

		initialize();
	}

	/**
	 * Initializes the ideal point and reference points (weights).
	 */
	private void initialize() {
		idealPoint = new double[numberOfObjectives];
		//Arrays.fill(idealPoint, Double.POSITIVE_INFINITY);
		//System.out.println("Say hello from initialize() in NSGAV");
		//
		//weights = new NormalBoundaryIntersectionGenerator(numberOfObjectives,
		//		divisionsOuter, divisionsInner).generate();
		
		//for (int j=0;j<weights.size();j++)
		//System.out.println(weights.get(j));
		//NSGAIV.matrixPrint.printArray(weights.);
	}

	/**
	 * Updates the ideal point given the solutions currently in this population.
	 * Determine new coordinates 
	 */
	protected void updateIdealPoint() {
		for (Solution solution : this) {
			if (solution.getNumberOfObjectives() != numberOfObjectives) {
				throw new FrameworkException("incorrect number of objectives");
			}

			for (int i = 0; i < numberOfObjectives; i++) {
				idealPoint[i] = Math.min(idealPoint[i], solution.getObjective(i));
			}
			/*
			System.out.println("\nidealPoint:=");
			NSGAIV.matrixPrint.printArray(idealPoint);
			*/
		}
	}

	/**
	 * Offsets the solutions in this population by the ideal point.  This
	 * method does not modify the objective values, it creates a new attribute
	 * with the name {@value NORMALIZED_OBJECTIVES}.
	 * Convert any point to new points with new coordinates
	 */
	protected void translateByIdealPoint() {
		for (Solution solution : this) {
			double[] objectives = solution.getObjectives();

			for (int i = 0; i < numberOfObjectives; i++) {
				objectives[i] -= idealPoint[i];
			}

			solution.setAttribute(NORMALIZED_OBJECTIVES, objectives);
		}
	}

	/**
	 * Normalizes the solutions in this population by the given intercepts
	 * (or scaling factors).  This method does not modify the objective values,
	 * it modifies the {@value NORMALIZED_OBJECTIVES} attribute.
	 * 
	 * @param intercepts the intercepts used for scaling
	 * Translate to new coordinates with scaling
	 */
	protected void normalizeByIntercepts(double[] intercepts) {
		for (Solution solution : this) {
			double[] objectives = (double[])solution.getAttribute(NORMALIZED_OBJECTIVES);

			for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
				objectives[i] /= intercepts[i];
			}
		}
	}

	/**
	 * The Chebyshev achievement scalarizing function.
	 * 
	 * @param solution the normalized solution
	 * @param weights the reference point (weight vector)
	 * @return the value of the scalarizing function
	 * Finding maximum of scalarizing values
	 */
	protected static double achievementScalarizingFunction(Solution solution, double[] weights) {
		double max = Double.NEGATIVE_INFINITY;
		double[] objectives = (double[])solution.getAttribute(NORMALIZED_OBJECTIVES);

		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			max = Math.max(max, objectives[i]/weights[i]);
		}

		return max;
	}

	/**
	 * Returns the extreme point in the given objective.  The extreme point is
	 * the point that minimizes the achievement scalarizing function using a
	 * reference point near the given objective.
	 * 
	 * The NSGA-III paper (1) does not provide any details on the scalarizing
	 * function, but an earlier paper by the authors (2) where some precursor
	 * experiments are performed does define a possible function, replicated
	 * below.
	 * 
	 * @param objective the objective index
	 * @return the extreme point in the given objective
	 */
	protected Solution findExtremePoint(int objective) {
		double eps = 0.000001;
		double[] weights = new double[numberOfObjectives];

		for (int i = 0; i < numberOfObjectives; i++) {
			if (i == objective) {
				weights[i] = 1.0;
			} else {
				weights[i] = eps;
			}
		}

		Solution result = null;
		double resultASF = Double.POSITIVE_INFINITY;

		for (int i = 0; i < size(); i++) {
			Solution solution = get(i);
			double solutionASF = achievementScalarizingFunction(solution, weights);
			/*
			System.out.println("The solutionASF is:="+solutionASF);
			System.out.println("The resultASF is:="+resultASF);
			*/
			if (solutionASF < resultASF) {
				result = solution;
				resultASF = solutionASF;
			}
		}
		/*
		System.out.println("\nThis is the weights array");
		NSGAIV.matrixPrint.printArray(weights);
		System.out.println("\nEnd of the weights array");
		*/
		return result;
	}

	/**
	 * Returns the extreme points for all objectives.
	 * 
	 * @return an array of extreme points, each index corresponds to each
	 *         objective
	 */
	private Solution[] extremePoints() {
		Solution[] result = new Solution[numberOfObjectives];

		for (int i = 0; i < numberOfObjectives; i++) {
			result[i] = findExtremePoint(i);
		}

		return result;
	}

	/**
	 * Calculates the intercepts between the hyperplane formed by the extreme
	 * points and each axis.  The original paper (1) is unclear how to handle
	 * degenerate cases, which occurs more frequently at larger dimensions.  In
	 * this implementation, we simply use the nadir point for scaling.
	 * 
	 * @return an array of the intercept points for each objective
	 */
	protected double[] calculateIntercepts() {
		Solution[] extremePoints = extremePoints();
		boolean degenerate = false;
		double[] intercepts = new double[numberOfObjectives];

		try {
			double[] b = new double[numberOfObjectives];
			double[][] A = new double[numberOfObjectives][numberOfObjectives];
			
			for (int i = 0; i < numberOfObjectives; i++) {
				double[] objectives = (double[])extremePoints[i].getAttribute(NORMALIZED_OBJECTIVES);

				b[i] = 1.0;

				for (int j = 0; j < numberOfObjectives; j++) {
					A[i][j] = objectives[j];
				}
			}

			double[] result = lsolve(A, b);

			for (int i = 0; i < numberOfObjectives; i++) {
				intercepts[i] = 1.0 / result[i];
			}
		} catch (RuntimeException e) {
			degenerate = true;
		}

		if (!degenerate) {
			// avoid small or negative intercepts
			for (int i = 0; i < numberOfObjectives; i++) {
				if (intercepts[i] < 0.001) {
					degenerate = true;
					break;
				}
			}
		}
		
		if (degenerate) {
			Arrays.fill(intercepts, Double.NEGATIVE_INFINITY);
			
			for (Solution solution : this) {
				for (int i = 0; i < numberOfObjectives; i++) {
					intercepts[i] = Math.max(
							Math.max(intercepts[i], Settings.EPS),
							solution.getObjective(i));
				}
			}
		}

		return intercepts;
	}

	// Gaussian elimination with partial pivoting
	// Copied from http://introcs.cs.princeton.edu/java/95linear/GaussianElimination.java.html
	/**
	 * Gaussian elimination with partial pivoting.
	 * 
	 * @param A the A matrix
	 * @param b the b vector
	 * @return the solved equation using Gaussian elimination
	 */
	private double[] lsolve(double[][] A, double[] b) {
		int N  = b.length;

		for (int p = 0; p < N; p++) {
			// find pivot row and swap
			int max = p;

			for (int i = p + 1; i < N; i++) {
				if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
					max = i;
				}
			}

			double[] temp = A[p];
			A[p] = A[max];
			A[max] = temp;

			double t = b[p];
			b[p] = b[max];
			b[max] = t;

			// singular or nearly singular
			if (Math.abs(A[p][p]) <= Settings.EPS) {
				throw new RuntimeException("Matrix is singular or nearly singular");
			}

			// pivot within A and b
			for (int i = p + 1; i < N; i++) {
				double alpha = A[i][p] / A[p][p];
				b[i] -= alpha * b[p];

				for (int j = p; j < N; j++) {
					A[i][j] -= alpha * A[p][j];
				}
			}
		}

		// back substitution
		double[] x = new double[N];

		for (int i = N - 1; i >= 0; i--) {
			double sum = 0.0;

			for (int j = i + 1; j < N; j++) {
				sum += A[i][j] * x[j];
			}

			x[i] = (b[i] - sum) / A[i][i];
		}

		return x;
	}

	/**
	 * Returns the minimum perpendicular distance between a point and a line.
	 * 
	 * @param line the line originating from the origin
	 * @param point the point
	 * @return the minimum distance
	 */
	protected static double pointLineDistance(double[] line, double[] point) {
		return Vector.magnitude(Vector.subtract(Vector.multiply(
				Vector.dot(line, point) / Vector.dot(line, line),
				line), point));
	}

	/**
	 * Associates each solution to the nearest reference point, returning a
	 * list-of-lists.  The outer list maps to each reference point using their
	 * index.  The inner list is an unordered collection of the solutions
	 * associated with the reference point.
	 * 
	 * @param population the population of solutions
	 * @return the association of solutions to reference points
	 */
	protected List<List<Solution>> associateToReferencePoint(Population population) {
		List<List<Solution>> result = new ArrayList<List<Solution>>();

		for (int i = 0; i < weights.size(); i++) {
			result.add(new ArrayList<Solution>());
		}

		for (Solution solution : population) {
			double[] objectives = (double[])solution.getAttribute(NORMALIZED_OBJECTIVES);
			/*
			System.out.println("\nThis is the objectives array");
			NSGAIV.matrixPrint.printArray(objectives);
			System.out.println("\nEnd of the objectives array");
			*/
			double minDistance = Double.POSITIVE_INFINITY;
			int minIndex = -1;

			for (int i = 0; i < weights.size(); i++) {
				double distance = pointLineDistance(weights.get(i), objectives);

				if (distance < minDistance) {
					minDistance = distance;
					minIndex = i;
				}
			}

			result.get(minIndex).add(solution);
		}

		return result;
	}

	/**
	 * Returns the solution with the minimum perpendicular distance to the
	 * given reference point.
	 * 
	 * @param solutions the list of solutions being considered
	 * @param weight the reference point
	 * @return the solution nearest to the reference point
	 */
	protected Solution findSolutionWithMinimumDistance(List<Solution> solutions, double[] weight) {
		//System.out.println("Say hello from findSolutionWithMinimumDistance in NSGAV Reference point");
		double minDistance = Double.POSITIVE_INFINITY;
		Solution minSolution = null;

		for (int i = 0; i < solutions.size(); i++) {
			double[] objectives = (double[])solutions.get(i).getAttribute(NORMALIZED_OBJECTIVES);
			double distance = pointLineDistance(weight, objectives);
			
			if (distance < minDistance) {
				minDistance = distance;
				minSolution = solutions.get(i);
			}
		}

		return minSolution;
	}
	public static double[] average (List<Solution> solutions,int numberOfObjectives) {
		double [] averagePoint = new double[numberOfObjectives];
		for (int i = 0; i < numberOfObjectives; i++) {
			averagePoint[i] = 0;
			for (Solution solution : solutions) {
				NSGAIV.utilsPopulation.printSolution(solution);
				averagePoint[i] = averagePoint[i] - solution.getObjective(i)/solutions.size();
				System.out.println("\n The solution.getObjective("+i+")"+solution.getObjective(i));
				System.out.println("\n The solution.solutions.size()"+solutions.size());
				}
			}
		NSGAIV.matrixPrint.printArray(averagePoint);
		return averagePoint;
	}
/*	
	protected  Solution average(List<Solution> solutions){
		Solution average = null;
		
		double[] objectives = new double[numberOfObjectives];
		for (int j=0; j < numberOfObjectives; j++){
			objectives[j] = 0;
			for (int i = 0; i < solutions.size(); i++) {
				objectives[j] = objectives[j] + solutions.get(i).getObjective(j)/solutions.size();			
				}
//			average.setObjective(j, 1);//objectives[j]);
		}
		average.setObjectives(objectives);
		NSGAIV.utilsPopulation.printSolution(average);
		return average;
	}
*/	
	public static List<Solution> getAllSolution(Population population){
		List<Solution> result = new ArrayList<Solution>();
		for (int i=0; i< population.size()-1; i++) {
			result.add(population.get(i));
		}
		//.add(i, population.get(i));
		return result;
	}
	public static int compareSolution(Solution solution1, Solution solution2) {
		return compare(solution1, solution2);
	}
	public static int compareSolutionAproximate(Solution solution1, Solution solution2, int k, double beta) {
		return compareAproximate(solution1, solution2, k, beta);
	}
	public static double[] addDelta(double[] objectives, double[] delta){
		double [] temp = new double[objectives.length];
		for (int i = 0; i< objectives.length; i++){
			temp[i] = objectives[i] - delta[i];
		}
		return temp;
	}
	public static double[] updateObjecitves(double[] objectives, double[] epsilon){
		double [] temp = new double[objectives.length];
		for (int i = 0; i< objectives.length; i++){
			temp[i] = objectives[i] + epsilon[i];
		}
		return temp;
	}
	public static List<double []> updateDeltas (Population population, double epsilon){
		List<double []> Deltas = new ArrayList<double[]>();
		for (Solution solution : population) {
			double [] temp = new double [solution.getNumberOfObjectives()];
			for (int i = 0; i< solution.getNumberOfObjectives(); i++) {
				temp[i] = Math.abs(solution.getObjective(i)*epsilon);
			}
			Deltas.add(temp);
			//System.out.println("\n This is the solution:");
			//NSGAIV.utilsPopulation.printSolution(solution);
			//System.out.println("\n This is the delta");
			//NSGAIV.matrixPrint.printArray(temp);
		}
		return Deltas;
	}
	public static List<double []> updateReduceDeltas (List<double []> Deltas){
		for (int i = 0; i < Deltas.size(); i++) {
			double [] temp = new double [Deltas.get(i).length];
			for (int j = 0; j < Deltas.get(i).length; j++) {
				Deltas.get(i)[j] = -Math.abs(Deltas.get(i)[j])/2;
			}
			//System.out.println("\n This is the solution:");
			//NSGAIV.utilsPopulation.printSolution(solution);
			//System.out.println("\n This is the delta");
			//NSGAIV.matrixPrint.printArray(Deltas.get(i));
		}
		return Deltas;
	}
	public static List<double []> updateIncreaseDeltas (List<double []> Deltas){
		for (int i = 0; i < Deltas.size(); i++) {
			double [] temp = new double [Deltas.get(i).length];
			for (int j = 0; j < Deltas.get(i).length; j++) {
				Deltas.get(i)[j] = Math.abs(Deltas.get(i)[j]);
			}
			//System.out.println("\n This is the solution:");
			//NSGAIV.utilsPopulation.printSolution(solution);
			//System.out.println("\n This is the delta");
//			NSGAIV.matrixPrint.printArray(Deltas.get(i));
		}
		return Deltas;
	}
	/*
	public static Solution findMaxSolution (Population resultFilter){
		double[] Distance = new double[resultFilter.size()];
		int i = 0;
		for (Solution solution: resultFilter) {
			Distance[i] = 0;
			for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
				Distance[i] += solution.getObjective(j)*solution.getObjective(j);
			}
			i++;
			//System.out.println("\n This is the solution:");
			//NSGAIV.utilsPopulation.printSolution(solution);
			//System.out.println("\n This is the delta");
//			NSGAIV.matrixPrint.printArray(Deltas.get(i));
		}
		double max = 0;
		int index = 0;
		for (i=0; i<Distance.length;i++){
			max = Math.max(max, Distance[i]);
			if (max==Distance[i]) index = i;
		}
			
		return resultFilter.get(index);
	}
	*/
	protected static Solution updateObjecitvesCurrent(Solution solution, double[] epsilon){
		Solution tempSolution = solution;
		for (int i = 0; i< solution.getNumberOfObjectives(); i++){
			tempSolution.setObjective(i, solution.getObjective(i) - epsilon[i]);
		}
		return tempSolution;
	}
	protected static Solution findMaxSolution (Population resultFilter){
		double[] Distance = new double[resultFilter.size()];
		for (int i=0; i<resultFilter.size();i++) {
			Distance[i] = 0;
			for (int j = 0; j < resultFilter.get(i).getNumberOfObjectives(); j++) {
				Distance[i] = Distance[i] + resultFilter.get(i).getObjective(j)*resultFilter.get(i).getObjective(j);
			}
		}
		double max = 0;
		int index = 0;
		for (int i=0; i<Distance.length;i++){
			max = Math.max(max, Distance[i]);
			if (max==Distance[i]) {
				index = i;
			}
		}		
		return resultFilter.get(index);
	}
	protected static Solution findMinSolution (Population resultFilter){
		double[] Distance = new double[resultFilter.size()];
		for (int i=0; i<resultFilter.size();i++) {
			Distance[i] = 0;
			for (int j = 0; j < resultFilter.get(i).getNumberOfObjectives(); j++) {
				Distance[i] = Distance[i] + resultFilter.get(i).getObjective(j)*resultFilter.get(i).getObjective(j);
			}
		}
		double min = Double.POSITIVE_INFINITY;;
		int index = 0;
		for (int i=0; i<Distance.length;i++){
			min = Math.min(min, Distance[i]);
			if (min==Distance[i]) {
				index = i;
			}
		}
		return resultFilter.get(index);
	}
	protected static int findMinSolutionIndex (Population resultFilter){
		double[] Distance = new double[resultFilter.size()];
		for (int i=0; i<resultFilter.size();i++) {
			Distance[i] = 0;
			for (int j = 0; j < resultFilter.get(i).getNumberOfObjectives(); j++) {
				Distance[i] = Distance[i] + resultFilter.get(i).getObjective(j)*resultFilter.get(i).getObjective(j);
			}
		}
		double min = Double.POSITIVE_INFINITY;
		int index = 0;
		for (int i=0; i<Distance.length;i++){
			min = Math.min(min, Distance[i]);
			if (min==Distance[i]) {
				index = i;
			}
		}
		return index;
	}
	protected static double distance(Solution solution){
		double distance = 0;
		for (int i = 0; i< solution.getNumberOfObjectives();i++){
			distance = distance + solution.getObjective(i)*solution.getObjective(i);
		}
		return distance;
	}
	protected static double [] backUpsolution(Solution solution, double epsilon){
		double [] Deltas = new double[solution.getNumberOfObjectives()];
		for (int j = 0; j< solution.getNumberOfObjectives(); j++) {
			Deltas[j] = Math.abs(solution.getObjective(j)*epsilon);
		}
		return Deltas;
	}
	protected static List<double[]> initDelta(Solution currentMin, int index, Solution previousMax, List<double[]> Deltas){
		double distanceMax = distance(previousMax);

		double distanceMin = distance(currentMin);
		double distanceMinOld = distanceMin;
		//System.out.println("The system are in while with distanceMin: "+distanceMin);
		List<double []> deltas = Deltas;
		double[] backUpCurrentMin = backUpsolution(currentMin,1);
		int k=0;
		Solution tempSolution = currentMin;
		while((distanceMin>distanceMax)&&(distanceMin<=distanceMinOld)){
			k=k+1;
			for (int i = 0; i< currentMin.getNumberOfObjectives(); i++){
				tempSolution.setObjective(i, currentMin.getObjective(i) - Deltas.get(index)[i]);
				for (int j=0; j< deltas.size(); j++){
					deltas.get(j)[i] = deltas.get(j)[i]+Deltas.get(j)[i];
				}
			}
			distanceMin = distance(currentMin);
			//System.out.println("The system are in while with distanceMin: "+distanceMin+" and distanceMax: " + distanceMax);
			//utilsPopulation.printArray(Deltas.get(index));
		}
		//if (k>0) System.out.println("The sys tem reduce in the step: "+k);
		for (int i = 0; i< currentMin.getNumberOfObjectives(); i++){
			currentMin.setObjective(i, backUpCurrentMin[i]);
		}
		return deltas;
	}
	public static Population filter (Population previousFront, Population currentFront, int newSize, int numberOfObjectives) {//,Comparator<? super Solution> comparator) {
		Population resultFilter = new Population();	
		//List<Solution> solutionsCurrentFront = getAllSolution(currentFront);		
		//List<Solution> solutionsPreviousFront = getAllSolution(previousFront);
		/*
		System.out.println("\nThe average CurrentPoint");
		averageCurrentPoint = average(solutionsCurrentFront,numberOfObjectives);
		System.out.println("\nThe average PreviousPoint");
		averagePreviousPoint = average(solutionsPreviousFront,numberOfObjectives);
		
		double[] delta = new double [numberOfObjectives];
		for (int i=0; i< delta.length;i++)
			delta[i] = averageCurrentPoint[i] - averagePreviousPoint[i];
		System.out.println("\nThe Delta");
		NSGAIV.matrixPrint.printArray(delta);
		*/
		//int N = previousFront.size()+currentFront.size();
			 
		double epsilon = 0.001;
		int k=0;
		int k_Max = 1000;
		//int S=0;
		/*Population temp = new Population();
		for (Solution solution: previousFront) {
			temp.add(solution);
		}
		*/
		Population temp = new Population();
		for (Solution solution: currentFront) {
			temp.add(solution);
		}
		//System.out.println("\nThis is the tempFront");
		//NSGAIV.utilsPopulation.printPopulation(temp);
		//System.out.println("\nThis is the Store");
		List<double []> Store = updateDeltas (temp, 1);
		List<double []> Deltas = new ArrayList<>();
		
		if(previousFront.size()>0) {
			Deltas = updateDeltas(temp, epsilon);
		}
		else{
			//System.out.println("Stop at here if(previousFront.size()<=0)");
			
			while(currentFront.size()>newSize){
				currentFront.remove(findMaxSolution (currentFront));
			}
			/*
			while (currentFront.size()>newSize) {
				NondominatedSorting nondominatedSorting = new NondominatedSorting(comparator);
				nondominatedSorting.updateCrowdingDistance(currentFront);
				currentFront.truncate(currentFront.size()-1, new CrowdingComparator());
				//System.out.println("The system test with CrowDistance algorithms");
			}
			*/	
			//System.out.println("no need to truncated-----------------------------------");
			return currentFront;
		}	
		//List<double []> Store = updateDeltas (temp, 1);
		//System.out.println("\nThis is the Deltas");
		//List<double []> Deltas = updateDeltas (temp, epsilon);
		
		while (resultFilter.size()<newSize) {
			//System.out.println("The newSize is"+newSize);
			List<Integer> storeIndex = new ArrayList<Integer>();
			resultFilter.clear();
			k++;
			if (k>k_Max) {
				while (currentFront.size() > newSize) {
					currentFront.remove(findMaxSolution(currentFront));
				}
				//System.out.println("The end of Filter");
				return currentFront;
			}
			//System.out.println("\nThis is the previousFront");
				
			//System.out.println("\nThis is the tempFront");
			//NSGAIV.utilsPopulation.printPopulation(temp);
			//for (Solution solution : currentFront)
			//temp.add(solution);
			// precompute the dominance relations
			int[][] dominanceChecks = new int[currentFront.size()][previousFront.size()];
			
			for (int i = 0; i < currentFront.size(); i++) {
				Solution si;

				if(k==1) {
					int index = findMinSolutionIndex(currentFront);
					Solution currentMin = findMinSolution(currentFront);
					Solution previousMax = findMinSolution(previousFront);
					List<double[]> BigDeltas = initDelta(currentMin,index,previousMax,Deltas);
					si = updateObjecitvesCurrent(temp.get(i),BigDeltas.get(i));
				} else{
					si = updateObjecitvesCurrent(temp.get(i),Deltas.get(i));
				}
				for (int j = 0; j < previousFront.size(); j++) {
						Solution sj = previousFront.get(j);
						dominanceChecks[i][j] = compare(si, sj);//compareSolutionAproximate(si, sj, k, epsilon);//.compare(si, sj);
						//System.out.println("\nThis is the newest dominanceChecks[i][j]:"+dominanceChecks[i][j]);
						//dominanceChecks[j][i] = -dominanceChecks[i][j];
				}
			}
					
			for (int i = 0; i < currentFront.size(); i++) {
				List<Integer> dominates = new ArrayList<Integer>();
				int dominatedCount = 0;	
				//int dominatesCount = 0;	
				for (int j = 0; j < previousFront.size(); j++) {
					if (dominanceChecks[i][j] >= 0) {
						dominatedCount += 1;
					}
				}
				
				if (dominatedCount == 0) {
					storeIndex.add(i);
					//Solution solution = currentFront.get(i);
					//resultFilter.add(solution);
				}		
				
			}
			if (storeIndex.size()>=newSize) {
				for (int i = 0; i<currentFront.size();i++){
					for (int j = 0; j<currentFront.get(i).getNumberOfObjectives();j++) {
						temp.get(i).setObjective(j, Store.get(i)[j]);//;setObjectives(Store.get(m));
					}
				}
				//Back up the resultFilter with StoreIndex
				for(int i=0; i<storeIndex.size();i++){
					resultFilter.add(currentFront.get(storeIndex.get(i)));
				}
				//if (storeIndex.size()==newSize){System.out.println("After reduce Deltas at k = "+k+"and Size:="+resultFilter.size()+"and newSize is:="+newSize);}
				if (storeIndex.size()>newSize){ //System.out.println("Stop at here if (storeIndex.size()>newSize)");
					
					while(resultFilter.size()>newSize){
						resultFilter.remove(findMaxSolution (resultFilter));
					}
					
					return resultFilter;
					/*
					while (resultFilter.size()>newSize) {
						NondominatedSorting nondominatedSorting = new NondominatedSorting(comparator);
						nondominatedSorting.updateCrowdingDistance(resultFilter);
						resultFilter.truncate(resultFilter.size()-1, new CrowdingComparator());
						//System.out.println("The system test with CrowDistance algorithms with resultFilter.size():"+ resultFilter.size() + ">newSize:"+newSize);
					}
					*/
					//System.out.println("After reduce Deltas at k = "+k+"and Size:="+resultFilter.size()+"and newSize is:="+newSize);
//					currentFront.clear();
//					currentFront.addAll(resultFilter);
//					return resultFilter;
				}
				
			}//else {System.out.println("The size of result is:storeIndex.size()<newSize");
			//S = resultFilter.size();
				//updateIncreaseDeltas (Deltas);
			//}		
			//System.out.println("The size of result is:="+S);
			//NSGAIV.utilsPopulation.printPopulation(resultFilter);
		}
		//System.out.println("After truncated at k = "+k+"and Size:="+resultFilter.size()+"and newSize is:="+newSize);
		return resultFilter;
	}
	/**
	 * Truncates the population to the specified size using the reference-point
	 * based nondominated sorting method.
	 */
	@Override
	public void truncate(int size, Comparator<? super Solution> comparator) {
		if (size() > size) {
			// remove all solutions past the last front
			sort(new RankComparator());

			int maxRank = (Integer)super.get(size-1).getAttribute(RANK_ATTRIBUTE);
			Population front = new Population();
			Population previousFront = new Population();// Dung edit
			Population currentFront = new Population();// Dung edit
			
			for (int i = 0; i < size(); i++) {
				int rank = (Integer)get(i).getAttribute(RANK_ATTRIBUTE);
				
				if (rank > maxRank) {
					front.add(get(i));
				}
				else {
						if (rank==maxRank) currentFront.add(get(i));
						if (rank==maxRank-1) previousFront.add(get(i));
				}
			}
			/*
			System.out.println("Here is the previous Front");
			NSGAIV.utilsPopulation.printPopulation(previousFront);
			System.out.println("Here is the current Front");
			NSGAIV.utilsPopulation.printPopulation(currentFront);
			*/
			removeAll(front);
			
			// update the ideal point
			//updateIdealPoint();

			// translate objectives so the ideal point is at the origin
			//translateByIdealPoint();

			// calculate the extreme points, calculate the hyperplane defined
			// by the extreme points, and compute the intercepts
			//normalizeByIntercepts(calculateIntercepts());

			// get the solutions in the last front
			front = new Population();

			for (int i = 0; i < size(); i++) {
				int rank = (Integer)get(i).getAttribute(RANK_ATTRIBUTE);

				if (rank == maxRank) {
					front.add(get(i));
				}
			}

			removeAll(front);
			
			int N = size - size();// size = 100; size() is the members of previous front
			//System.out.println("The solution more is:="+size+"-"+size()+"="+N);
			//System.out.println("The original previous Population is:*************************************");
			//NSGAIV.utilsPopulation.printPopulation(previousFront);
			Population resultFilter = filter(previousFront, currentFront, N, numberOfObjectives);//,comparator);
			if(resultFilter.size()+size()==size){
				for (Solution solution : resultFilter){
					add(solution);
				}
				//System.out.println("no need to go to while size():"+size()+" < size"+size);
			}
			
				
			//System.out.println("The solution more is:="+size+"-"+size()+"="+N);
			/*
			System.out.println("\nThe previous Population is:*************************************");
			NSGAIV.utilsPopulation.printPopulation(previousFront);
			System.out.println("\nThe current Population is:*************************************");
			NSGAIV.utilsPopulation.printPopulation(currentFront);
			System.out.println("\nThe new Population is:*************************************");
			NSGAIV.utilsPopulation.printPopulation(this);
			System.out.println("\nThe end of *************************************");
			*/
			//System.out.println("The result more is:="+resultFilter.size());
			// associate each solution to a reference point
			//List<List<Solution>> members = associateToReferencePoint(this);
			//List<List<Solution>> potentialMembers = associateToReferencePoint(front);
			//List<List<Solution>> potentialMembers = associateToReferencePoint(resultFilter);
			//Set<Integer> excluded = new HashSet<Integer>();
			
			// loop over niche-preservation operation until population is full
			/*while (size() < size) {
				System.out.println("inside while size():"+size()+" < size"+size);
				// identify reference point with the fewest associated members
				List<Integer> minIndices = new ArrayList<Integer>();
				int minCount = Integer.MAX_VALUE;

				for (int i = 0; i < members.size(); i++) {
					if (!excluded.contains(i) && (members.get(i).size() <= minCount)) {
						if (members.get(i).size() < minCount) {
							minIndices.clear();
							minCount = members.get(i).size();
						}
						
						minIndices.add(i);
					}
				}
				
				int minIndex = PRNG.nextItem(minIndices);

				// add associated solution
				if (minCount == 0) {
					if (potentialMembers.get(minIndex).isEmpty()) {
						excluded.add(minIndex);
					} else {
						Solution minSolution = findSolutionWithMinimumDistance(potentialMembers.get(minIndex), weights.get(minIndex));
						add(minSolution);
						members.get(minIndex).add(minSolution);
						potentialMembers.get(minIndex).remove(minSolution);
					}
				} else {
					if (potentialMembers.get(minIndex).isEmpty()) {
						excluded.add(minIndex);
					} else {
						Solution randSolution = PRNG.nextItem(potentialMembers.get(minIndex));
						add(randSolution);
						members.get(minIndex).add(randSolution);
						potentialMembers.get(minIndex).remove(randSolution);
					}
				}
			}*/
			
		}
		//System.out.println("outside size():"+size()+" == size"+size);
	}

	/**
	 * Truncates the population to the specified size using the reference-point
	 * based nondominated sorting method.
	 */
	@Override
	public void truncate(int size) {
		truncate(size, new RankComparator());
	}
	public static int compare(Solution solution1, Solution solution2) {
		boolean dominate1 = false;
		boolean dominate2 = false;

		for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
			if (solution1.getObjective(i) < solution2.getObjective(i)) {
				dominate1 = true;

				if (dominate2) {
					return 0;
				}
			} else if (solution1.getObjective(i) > solution2.getObjective(i)) {
				dominate2 = true;

				if (dominate1) {
					return 0;
				}
			}
		}

		if (dominate1 == dominate2) {
			return 0;
		} else if (dominate1) {
			return -1;
		} else {
			return 1;
		}
	}
	public static int compareAproximate(Solution solution1, Solution solution2, int k, double beta) {
		boolean dominate1 = false;
		boolean dominate2 = false;
		double Delta = beta;//beta;
		for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
			if (solution1.getObjective(i) < solution2.getObjective(i)+k*Delta) {
				double si = solution1.getObjective(i);
				double sj = solution2.getObjective(i)+k*Delta;
				System.out.println("\n k:================="+k);
				System.out.println("\n Comparing solution1.getObjective(i)"+si);
				System.out.println("\n Comparing solution2.getObjective(i)"+sj);
				System.out.println("\n Comparing solution1.getObjective(i)"+solution1.getObjective(i)+"with solution2.getObjective(i)+beta"+solution2.getObjective(i)+"and"+beta);
				dominate1 = true;

				if (dominate2) {
					return 0;
				}
			} else if (solution1.getObjective(i) > solution2.getObjective(i)+k*Delta) {
				double si = solution1.getObjective(i);
				double sj = solution2.getObjective(i)+k*Delta;
				System.out.println("\n Wrong------------------------------");
				System.out.println("\n Comparing solution1.getObjective(i)"+si);
				System.out.println("\n Comparing solution2.getObjective(i)"+sj);
				dominate2 = true;

				if (dominate1) {
					return 0;
				}
			}
		}

		if (dominate1 == dominate2) {
			return 0;
		} else if (dominate1) {
			System.out.println("\n True");
			return -1;
		} else {
			return 1;
		}
	}
	
}

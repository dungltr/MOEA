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
package NSGAIV;
import NSGAIV.matrixPrint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.FastNondominatedSorting;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedPopulation.DuplicateMode;
import org.moeaframework.core.NondominatedSorting;
import org.moeaframework.core.Population;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ObjectiveComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.util.Vector;

import NSGAIV.writeMatrix2CSV;

/**
 * Non-dominated sorting algorithm for dominance depth ranking. Assigns the
 * {@code rank} and {@code crowdingDistance} attributes to solutions. Solutions
 * of rank 0 belong to the Pareto non-dominated front.
 * <p>
 * Despite its name, this naive non-dominated sort implementation tends to be
 * faster than the "fast non-dominated sort" implementation from [1].  This
 * is primarily due to the fact that for the average case, the "fast" version
 * always requires O(MN^2) comparisons while this naive implementations requires
 * only (K-1)/2 * M * (N-1)*N/2, assuming there are K equally sized fronts.
 * <p>
 * References:
 * <ol>
 * <li>Deb et al (2002). "A Fast and Elitist Multiobjective Genetic Algorithm:
 * NSGA-II." IEEE Transactions on Evolutionary Computation. 6(2):182-197.
 * </ol>
 */
public class NSGAIVNondominatedSorting extends NondominatedSorting {

	/**
	 * Attribute key for the rank of a solution.
	 */
	public static final String RANK_ATTRIBUTE = "rank";

	/**
	 * Attribute key for the crowding distance of a solution.
	 */
	public static final String CROWDING_ATTRIBUTE = "crowdingDistance";

	/**
	 * The dominance comparator.
	 */
	protected final DominanceComparator comparator;

	/**
	 * Constructs a fast non-dominated sorting operator using Pareto dominance.
	 */
	public NSGAIVNondominatedSorting() {
		this(new ParetoDominanceComparator());
	}

	/**
	 * Constructs a non-dominated sorting operator using the specified
	 * dominance comparator.
	 * 
	 * @param comparator the dominance comparator
	 */
	public NSGAIVNondominatedSorting(DominanceComparator comparator) {
		super();
		this.comparator = comparator;
	}
	
	/**
	 * Returns the dominance comparator used by this non-dominated sorting
	 * routine.
	 * 
	 * @return the dominance comparator used by this non-dominated sorting
	 *         routine
	 */
	public DominanceComparator getComparator() {
		return comparator;
	}

	/**
	 * Performs non-dominated sorting on the specified population,
	 * assigning the {@code rank} and {@code crowdingDistance} attributes to
	 * solutions.
	 * 
	 * @param population the population whose solutions are to be evaluated
	 */
	public void evaluate(Population population) {
	/*	
		//////Fast Nondominated Sorting ////////////
		int N = population.size();
		
		// precompute the dominance relations
		int[][] dominanceChecks = new int[N][N];
		
		for (int i = 0; i < N; i++) {
			Solution si = population.get(i);
			
			for (int j = i+1; j < N; j++) {
				if (i != j) {
					Solution sj = population.get(j);
					
					dominanceChecks[i][j] = comparator.compare(si, sj);
					dominanceChecks[j][i] = -dominanceChecks[i][j];
				}
			}
		}
		
		// compute for each solution s_i the solutions s_j that it dominates
		// and the number of times it is dominated
		int[] dominatedCounts = new int[N];
		int[] dominatesCounts = new int[N];
		int[] equivalentCounts = new int[N];
		List<List<Integer>> dominatesList = new ArrayList<List<Integer>>();
		List<List<Integer>> dominatedList = new ArrayList<List<Integer>>();
		List<List<Integer>> equivalentList = new ArrayList<List<Integer>>();
		List<Integer> currentFront = new ArrayList<Integer>();
		List<Integer> dominatedCountList = new ArrayList<Integer>();
		List<Integer> dominatesCountList = new ArrayList<Integer>();
		List<Integer> equivalentCountList = new ArrayList<Integer>();
		List<Integer> AllFront = new ArrayList<Integer>();
		for (int i = 0; i < N; i++)
			AllFront.add(i);
		
		for (int i = 0; i < N; i++) {
			List<Integer> dominates = new ArrayList<Integer>();
			List<Integer> dominated = new ArrayList<Integer>();
			List<Integer> equivalent = new ArrayList<Integer>();
			int dominatedCount = 0;
			int dominatesCount = 0;
			int equivalentCount = 0;
			
			for (int j = 0; j < N; j++) {
				if (i != j) {
					if (dominanceChecks[i][j] < 0) {
						dominatesCount +=1;
						dominates.add(j);
					} 	else { 
							if (dominanceChecks[j][i] < 0) {
								dominatedCount += 1;
								dominated.add(j);
							}
							else {
								equivalentCount +=1;
								equivalent.add(j);
							}
						}
				}
			}
			dominatedCountList.add(dominatedCount);
			dominatesCountList.add(dominatesCount);
			equivalentCountList.add(equivalentCount);
			
			if (dominatedCount == 0) {
				currentFront.add(i);
			}
			
			dominatesList.add(dominates);
			dominatedCounts[i] = dominatedCount;
			
			dominatedList.add(dominated);
			dominatesCounts[i] = dominatesCount;
			
			equivalentList.add(equivalent);
			equivalentCounts[i] = equivalentCount;
			System.out.println("element: "+i);
			System.out.println("dominatesList: "+dominatesList.toString());
			System.out.println("dominatedList: "+dominatedList.toString());
			System.out.println("equivalentList: "+equivalentList.toString());
		}
		Solution lala;
		// assign ranks
		int rank = 0;
		////////////////////////////////////////
		for (int m = 0; m < population.size(); m++) {
            Solution solution = population.get(m);
            int[] x = EncodingUtils.getInt(solution);
            double[] objectives = solution.getObjectives();

            //Negate objectives to return them to their maximized form.
            objectives = Vector.negate(objectives);//.negate(objectives);

            //2.2.5 Print results

            
            System.out.println("\n    Solution " + (m) + ":");
            for (int i=0; i < objectives.length; i++)
                System.out.print("      Obj "+i+": " + -objectives[i]);
            System.out.println("    Con 1: " + solution.getConstraint(0));

            for(int j=0;j<x.length;j++){
                System.out.print("      Var " + (j+1) + ":" + x[j]+"\n");
            }
        }
		///////////////////////////////////////////////
		System.out.println("FirstFront: "+currentFront.toString());
		System.out.println("Equivalent List: "); 
		NSGAIV.matrixPrint.printArray(equivalentCounts);
		for (int i=0; i<currentFront.size();i++) {
			System.out.println("element:"+currentFront.get(i) +"has equivalent number:"+equivalentCounts[currentFront.get(i)]);
		}
			
		System.out.println("AllFront: "+AllFront.toString());
		for (int i=0; i< currentFront.size(); i++) {
			
			AllFront.remove(currentFront.get(i));
		}		
		System.out.println("NextFront: "+AllFront.toString());
		
		int nextFrontCount = equivalentCounts[currentFront.get(0)];
		System.out.println("nextFrontCount------------------------------------------------"+nextFrontCount);
		System.out.println("AllFrontCount------------------------------------------------"+AllFront.size());
		System.out.println("currentFrontCount------------------------------------------------"+currentFront.size());
		while (!AllFront.isEmpty()) {
			
			List<Integer> nextFront = new ArrayList<Integer>();
			List<Integer> currentFrontInternal = new ArrayList<Integer>();
			Population solutionsInFront = new Population();
			
			for (int i = 0; i < currentFront.size(); i++) {
				Solution solution = population.get(currentFront.get(i));
				solution.setAttribute(RANK_ATTRIBUTE, rank);
				solutionsInFront.add(solution);
			}
			System.out.println("-------------------------");
			updateCrowdingDistance(solutionsInFront);			
			for (int i = 0; i < AllFront.size(); i++) {
				if (nextFrontCount == dominatedCounts[AllFront.get(i)]) {
					currentFrontInternal.add(AllFront.get(i));
					//AllFront.remove(All.get(i));
				}
				else {
					nextFront.add(AllFront.get(i));
				}
			}
			nextFrontCount = nextFrontCount + equivalentCounts[currentFrontInternal.get(0)];
			rank +=1;
			
			currentFront = currentFrontInternal;
			AllFront = nextFront;
			//////////////////////////////////////
			double[] MaxRank = new double [1];
			MaxRank[0] = (double) rank; 
			try {
				writeMatrix2CSV.addArray2Csv("/Users/letrungdung/maxRank.csv", MaxRank);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//////////////////////////////////////
			////////Fast Nondominated Sorting ended//////////////////////////////
		//////////////////
		*/	
		//////////////////Original Fast Non Dominated
		/*
		int N = population.size();
		
		// precompute the dominance relations
		int[][] dominanceChecks = new int[N][N];
		
		for (int i = 0; i < N; i++) {
			Solution si = population.get(i);
			
			for (int j = i+1; j < N; j++) {
				if (i != j) {
					Solution sj = population.get(j);
					
					dominanceChecks[i][j] = comparator.compare(si, sj);
					dominanceChecks[j][i] = -dominanceChecks[i][j];
				}
			}
		}
		
		// compute for each solution s_i the solutions s_j that it dominates
		// and the number of times it is dominated
		int[] dominatedCounts = new int[N];
		List<List<Integer>> dominatesList = new ArrayList<List<Integer>>();
		List<Integer> currentFront = new ArrayList<Integer>();
		
		
		for (int i = 0; i < N; i++) {
			List<Integer> dominates = new ArrayList<Integer>();
			int dominatedCount = 0;
			
			for (int j = 0; j < N; j++) {
				if (i != j) {
					if (dominanceChecks[i][j] < 0) {
						dominates.add(j);
					} else if (dominanceChecks[j][i] < 0) {
						dominatedCount += 1;
					}
				}
			}
			
			if (dominatedCount == 0) {
				currentFront.add(i);
			}
			
			dominatesList.add(dominates);
			dominatedCounts[i] = dominatedCount;
		}
		// assign ranks
		int rank = 0;
		
		while (!currentFront.isEmpty()) {
			List<Integer> nextFront = new ArrayList<Integer>();
			Population solutionsInFront = new Population();
			
			for (int i = 0; i < currentFront.size(); i++) {
				Solution solution = population.get(currentFront.get(i));
				solution.setAttribute(RANK_ATTRIBUTE, rank);
				
				// update the dominated counts as compute next front
				for (Integer j : dominatesList.get(currentFront.get(i))) {
					dominatedCounts[j] -= 1;
					
					if (dominatedCounts[j] == 0) {
						nextFront.add(j);
					}
				}		
				solutionsInFront.add(solution);
			}			
			updateCrowdingDistance(solutionsInFront);		
			rank += 1;
			currentFront = nextFront;
			*/
		///////////////////
		List<Solution> remaining = new ArrayList<Solution>();

		for (Solution solution : population) {
			remaining.add(solution);
		}

		int rank = 0;

		while (!remaining.isEmpty()) {
			NondominatedPopulation front = new NondominatedPopulation(
					comparator, DuplicateMode.ALLOW_DUPLICATES);
			for (Solution solution : remaining) {
				front.add(solution);
			}
			for (Solution solution : front) {
				remaining.remove(solution);
				solution.setAttribute(RANK_ATTRIBUTE, rank);
			}
			updateCrowdingDistance(front);
			rank++;	
		}
	}
	
	/**
	 * Computes and assigns the {@code crowdingDistance} attribute to solutions.
	 * The specified population should consist of solutions within the same
	 * front/rank.
	 * 
	 * @param front the population whose solutions are to be evaluated
	 */
	public void updateCrowdingDistance(Population front) {
		// initially assign all crowding distances of 0.0
		for (Solution solution : front) {
			solution.setAttribute(CROWDING_ATTRIBUTE, 0.0);
		}
		
		// remove any duplicate solutions, the duplicate solutions will retain
		// the crowding distance of 0.0
		Population uniqueFront = new Population();
		
		for (Solution s1 : front) {
			boolean isDuplicate = false;
			
			for (Solution s2 : uniqueFront) {
				if (NondominatedPopulation.distance(s1, s2) < Settings.EPS) {
					isDuplicate = true;
					break;
				}
			}
			
			if (!isDuplicate) {
				uniqueFront.add(s1);
			}
		}
		
		front = uniqueFront;

		// then compute the crowding distance for the unique solutions
		int n = front.size();
		
		if (n < 3) {
			for (Solution solution : front) {
				solution.setAttribute(CROWDING_ATTRIBUTE,
						Double.POSITIVE_INFINITY);
			}
		} else {
			int numberOfObjectives = front.get(0).getNumberOfObjectives();

			for (int i = 0; i < numberOfObjectives; i++) {
				front.sort(new ObjectiveComparator(i));

				double minObjective = front.get(0).getObjective(i);
				double maxObjective = front.get(n - 1).getObjective(i);

				front.get(0).setAttribute(CROWDING_ATTRIBUTE,
						Double.POSITIVE_INFINITY);
				front.get(n - 1).setAttribute(CROWDING_ATTRIBUTE,
						Double.POSITIVE_INFINITY);

				for (int j = 1; j < n - 1; j++) {
					double distance = (Double)front.get(j).getAttribute(
							CROWDING_ATTRIBUTE);
					distance += (front.get(j + 1).getObjective(i) - 
							front.get(j - 1).getObjective(i))
							/ (maxObjective - minObjective);
					front.get(j).setAttribute(CROWDING_ATTRIBUTE, distance);
				}
			}
		}
	}

}

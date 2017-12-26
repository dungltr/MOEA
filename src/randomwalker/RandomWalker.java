package randomwalker;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

public class RandomWalker extends AbstractEvolutionaryAlgorithm {
	
	private final Variation variation;

	public RandomWalker(Problem problem, Initialization initialization,
			Variation variation) {
		super(problem, new NondominatedSortingPopulation(), null, initialization);
		this.variation = variation;
	}

	@Override
	protected void iterate() {
		// get the current population
		NondominatedSortingPopulation population = (NondominatedSortingPopulation)getPopulation();
		
		// randomly select a solution from the population
		int index = PRNG.nextInt(population.size());
		Solution parent = population.get(index);
		
		// mutate the selected solution
		Solution offspring = variation.evolve(new Solution[] { parent })[0];
		
		// evaluate the objectives/constraints
		evaluate(offspring);
		
		// add the offspring to the population
		population.add(offspring);
		
		// use non-dominated sorting to remove the worst solution
		population.truncate(population.size()-1);
	}

}
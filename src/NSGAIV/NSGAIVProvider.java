package NSGAIV;

import java.util.Properties;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;


import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.FrameworkException;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;

import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;

import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;

import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

public class NSGAIVProvider extends AlgorithmProvider {

	@Override
	public Algorithm getAlgorithm(String name, Properties properties, Problem problem) {
		if (name.equalsIgnoreCase("NSGAIV")) {
			// if the user requested the RandomWalker algorithm
			TypedProperties typedProperties = new TypedProperties(properties);
			
			// allow the user to customize the population size (default to 100)
			int populationSize = typedProperties.getInt("populationSize", 100);
			
			// initialize the algorithm with randomly-generated solutions
			Initialization initialization = new RandomInitialization(problem, populationSize);
			
			// use the operator factory to create a polynomial mutation operator
			Variation variation = OperatorFactory.getInstance().getVariation("pm", properties, problem);
			
			// construct and return the RandomWalker algorithm
			return newNSGAIV(typedProperties, problem);
		} else {
			// return null if the user requested a different algorithm
			return null;
		}
	}
	private Algorithm newNSGAIV(TypedProperties properties, Problem problem) {
		int populationSize = (int)properties.getDouble("populationSize", 100);

		Initialization initialization = new RandomInitialization(problem,
				populationSize);

		NSGAIVNondominatedSortingPopulation population = 
				new NSGAIVNondominatedSortingPopulation();

		TournamentSelection selection = null;
		
		if (properties.getBoolean("withReplacement", true)) {
			selection = new TournamentSelection(2, new ChainedComparator(
					new ParetoDominanceComparator(),
					new CrowdingComparator()));
		}

		Variation variation = OperatorFactory.getInstance().getVariation(null, 
				properties, problem);

		return new NSGAIV(problem, population, null, selection, variation,
				initialization);
	}

}
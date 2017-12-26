package randomwalker;

import java.util.Properties;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

public class RandomWalkerProvider extends AlgorithmProvider {

	@Override
	public Algorithm getAlgorithm(String name, Properties properties, Problem problem) {
		if (name.equalsIgnoreCase("RandomWalker")) {
			// if the user requested the RandomWalker algorithm
			TypedProperties typedProperties = new TypedProperties(properties);
			
			// allow the user to customize the population size (default to 100)
			int populationSize = typedProperties.getInt("populationSize", 100);
			
			// initialize the algorithm with randomly-generated solutions
			Initialization initialization = new RandomInitialization(problem, populationSize);
			
			// use the operator factory to create a polynomial mutation operator
			Variation variation = OperatorFactory.getInstance().getVariation("pm", properties, problem);
			
			// construct and return the RandomWalker algorithm
			return new RandomWalker(problem, initialization, variation);
		} else {
			// return null if the user requested a different algorithm
			return null;
		}
	}

}
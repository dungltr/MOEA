package NSGAIV;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.util.Vector;

public class utilsPopulation {
	public static void printPopulation(Population result) {
		System.out.println("Num of Solutions: "+ result.size());
        //double[][] matrixResult = new double [result.size()][result.get(0).getNumberOfObjectives()];
        // 2.2.4 Read solutions
        for (int m = 0; m < result.size(); m++) {
            Solution solution = result.get(m);
            int[] x = EncodingUtils.getInt(solution);
            double[] objectives = solution.getObjectives();

            //Negate objectives to return them to their maximized form.
            objectives = Vector.negate(objectives);//.negate(objectives);
            //2.2.5 Print results       
            System.out.println("\n    Solution " + (m + 1) + ":");
            for (int i=0; i < objectives.length; i++)
                System.out.print("      Obj "+i+": " + -objectives[i]);
            //System.out.println("    Con 1: " + solution.getConstraint(0));

/*            for(int j=0;j<x.length;j++){
                System.out.print("      Var " + (j+1) + ":" + x[j]+"\n");
            }
*/            
            //for (int j=0; j < objectives.length ;j++)
              //  matrixResult[m][j] = -objectives[j];
        }
	}
	public static void printSolution(Solution solution) {
		System.out.println("Solutions: ");
 //       double[][] matrixResult = new double [result.size()][result.get(0).getNumberOfObjectives()];
            int[] x = EncodingUtils.getInt(solution);
            double[] objectives = solution.getObjectives();

            //Negate objectives to return them to their maximized form.
            objectives = Vector.negate(objectives);//.negate(objectives);
            //2.2.5 Print results       
//            System.out.println("\n    Solution " + (m + 1) + ":");
            for (int i=0; i < objectives.length; i++)
                System.out.print("      Obj "+i+": " + -objectives[i]);
            System.out.println("    Con 1: " + solution.getConstraint(0));

            for(int j=0;j<x.length;j++){
                System.out.print("      Var " + (j+1) + ":" + x[j]+"\n");
            }
            
//            for (int j=0; j < objectives.length ;j++)
//                matrixResult[m][j] = -objectives[j];
	}

}

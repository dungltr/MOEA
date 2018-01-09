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
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.Vector;
import NSGAIV.ReadFile;
import NSGAIV.writeMatrix2CSV;
import static NSGAIV.ReadMatrixCSV.readMatrix;
import static java.lang.Math.pow;

import java.io.IOException;

/**
 * Demonstrates using an Executor to solve the UF1 test problem with NSGA-II,
 * one of the most widely-used multiobjective evolutionary algorithms.
 */
public class Example0 {
	public static class MO extends AbstractProblem {
	    private int numberOfVariables;
	    private int numberOfObjectives;
	    private int numberOfConstraints;
	    //static String MOEA_HOME = ReadFile.readhome("MOEA_HOME");
	    static String MOEA_HOME = ReadFile.readhome("MOEA_Framework");
	    static String matrix = "pf/problem";//"plan";
	    String matrixFile = MOEA_HOME + "/"+ matrix +".csv";//ReadFile.readhome(matrix)+".csv";
	    int Max = ReadFile.count(matrixFile);
	    double[][] matrixMetrics = readMatrix(matrixFile, Max);

	    public MO() throws IOException {
	        super(1, 2, 1); // old is 2,3,1       
	        numberOfVariables = 1;//matrixMetrics[0].length;
	        numberOfObjectives = 2;//matrixMetrics.length;// old is 3
	        numberOfConstraints = 1;
	    }

	    @Override
	    public Solution newSolution() {
	        Solution solution = new Solution(getNumberOfVariables(),getNumberOfObjectives(), getNumberOfConstraints());
	        for (int i = 0; i < getNumberOfVariables(); i++) {
	            solution.setVariable(i, new RealVariable(0.0, Max));
	        }

	        return solution;
	    }

	    @Override
	    public void evaluate(Solution solution) {
	        int[] x = EncodingUtils.getInt(solution);
	        
	        double[] f = new double[numberOfObjectives];
	        double[] g = new double[numberOfConstraints];

	        double[][] b = new double[numberOfObjectives][numberOfVariables];
	        for (int i = 0; i < numberOfObjectives; i++){
	            for (int j = 0; j < numberOfVariables; j++)
	                b[i][j] = matrixMetrics[x[0]][i+1];
	        }
	      
	        //Objectives.
	        for (int i = 0; i < numberOfObjectives; i++) {
	            f[i] = 0;
	            for (int j = 0; j < numberOfVariables; j++) {
	                f[i] -= b[i][j];
	            }
	        }      
	        //Constraints:
	        //constraints that are satisfied have a value of zero; violated constraints have
	        //non-zero values (both positive and negative).
	        
	        for (int i = 0; i < numberOfConstraints; i++) {
	            double sum = 0.0;
	            for (int j = 0; j < numberOfVariables; j++) {
	                sum += x[j];
	            }
	            if (sum <= Max) {
	                g[i] = 0.0;
	            }   else {
	                g[i] = sum - Max;
	                }
	        }        
	        //Negate the objectives since Knapsack is maximization.
	        solution.setObjectives(Vector.negate(f));
	        //solution.setObjectives(Vector.normalize(f));
	        solution.setConstraints(g);
	    }

	    @Override
	    public int getNumberOfConstraints() {
	        return numberOfConstraints;
	    }

	    @Override
	    public int getNumberOfObjectives() {
	        return numberOfObjectives;
	    }

	    @Override
	    public int getNumberOfVariables() {
	        return numberOfVariables;
	    }
	}

	public static void main(String[] args) throws IOException {
        // 2.2.1 Create Excel workbook
        //XSSFWorkbook workBook = new XSSFWorkbook();
        // 2.2.2 Create Excel sheets by different iterations
        //XSSFSheet sheet1 = workBook.createSheet("Iteration");    
        for (int k = 0; k < 2; k++) {
            int iteration = (int) pow(10, k + 1);
            System.out.println("Iteration: " + iteration);
            NondominatedPopulation result = new Executor()
                    .withProblemClass(MO.class)
                    .withAlgorithm("NSGAV")
                    .withMaxEvaluations(iteration)
                    .withProperty("populationSize", 2)
                    //.withProperty("org.moeaframework.core.fast_nondominated_sorting",false)
                    .run();
            System.out.println("Num of Solutions: "+ result.size());
            double[][] matrixResult = new double [result.size()][result.get(0).getNumberOfObjectives()];
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
                System.out.println("    Con 1: " + solution.getConstraint(0));

                for(int j=0;j<x.length;j++){
                    System.out.print("      Var " + (j+1) + ":" + x[j]+"\n");
                }
                
                for (int j=0; j < objectives.length ;j++)
                    matrixResult[m][j] = -objectives[j];
            }
        System.out.println("Matrix of result-------------------------");    
            for (int i = 0; i < matrixResult.length; i++){
                
                for (int j = 0; j< matrixResult[0].length; j++){
                    System.out.print(" " + matrixResult[i][j]);
                } 
                System.out.println("\n");
            }
        writeMatrix2CSV.addMatrix2Csv(ReadFile.readhome("MOEA_Framework") + "/pf/problem" +"_result.csv", matrixResult);
        System.out.println("-----------------------------------------");    
        }
    }
}


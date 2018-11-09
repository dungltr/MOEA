package org.moeaframework.problem.Hybrid;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HybridProblem extends AbstractProblem {
    /*
    double[][] AUM = {
            {0, 1, 1, 1, 1, 1},
            {0, 0, 1, 1, 1, 1},
            {0, 0, 0, 1, 1, 1},
            {1, 1, 0, 0, 0, 0},
            {1, 1, 1, 0, 0, 0},
            {0, 1, 1, 0, 0, 0}
    };
    String[][] T = {
            {"0", "1", "1", "1", "1", null},
            {"0", "0", "1", "1", null, "1"},
            {"0", "0", "0", "1", null, null},
            {"0", "0", "0", "1", null, null},
            {"0", "0", "0", "1", null, null},
            {"0", "0", "0", "1", null, null},
            {"0", "0", "0", "1", "1", null},
            {"1", "1", "0", "0", "0", "0"},
            {"1", "1", "1", null, null, null},
            {"0", "1", "1", null, null, null}
    };
    double[] frequencies = {600, 500, 700, 1000, 200, 400};
    */
    /**
     * The number of variables defined by this problem.
     */
    public static int columnAUM;
    public static int rowAUM;
    public static int rowT;

    public static double[][] AUM = new double[rowAUM][columnAUM];
    public static double[] frequencies = new double[rowAUM];
    public static  String[][] T = new String[rowT][columnAUM];

    public HybridProblem(double[][] AUM, String[][] T, double[] frequencies) {
        super(4, 4, 1);
        this.columnAUM = AUM[0].length;
        this.rowAUM = AUM.length;
        this.rowT = T.length;

        this.AUM = AUM;
        this.frequencies = frequencies;
        this.T = new String[rowT][columnAUM];

        for (int i = 0; i < AUM.length; i++){
            for (int j = 0; j < AUM[0].length; j++){
                this.AUM[i][j] = AUM[i][j];
                this.frequencies[i] = frequencies[i];
            }
        }

        for (int i = 0; i < rowT; i++){
            for (int j = 0; j < columnAUM; j++){
                this.T[i][j] = T[i][j];
            }
        }

        System.out.println("The size of AUM are: "+ this.AUM.length + " and " + this.AUM[0].length);
        System.out.println("The size of T are: "+ this.T.length + " and " + this.T[0].length);
        System.out.println("The size of frequencies are: "+ this.frequencies.length);

    }
    @Override
    public String getName() {
        return "HybridProblem";
    }

    @Override
    public void evaluate(Solution solution) {
        double alpha = ((RealVariable)solution.getVariable(0)).getValue();
        double beta = ((RealVariable)solution.getVariable(1)).getValue();
        double lamda = ((RealVariable)solution.getVariable(2)).getValue();
        double theta = ((RealVariable)solution.getVariable(3)).getValue();
        List<Integer> layout = new ArrayList<>();
        layout.add(0); //row store
        layout.add(1); //column store
        Map<List<Integer>, Integer> configuration = StorageConfiguration.StorageConfiguration(AUM, frequencies, T, layout, alpha, beta, lamda, theta);
        List<Double> result = Statistic.statistic(configuration,AUM, frequencies, T);

        double[] functions = new double[result.size()];
        double[] constraints = new double[result.size()];
        for (int i = 0; i < functions.length; i++){
            functions[i] = result.get(i);
            solution.setObjective(i, functions[i]);
            constraints[i] = functions[i];
            //solution.setConstraint(0, constraints[i] <= 0.0 ? 0.0 : constraints[i]);// if constraint <= 0 then = 0 else = constrain;
        }
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(4, 4, 2);
        solution.setVariable(0, new RealVariable(0, 1));
        solution.setVariable(1, new RealVariable(0, 1));
        solution.setVariable(2, new RealVariable(0, 1));
        solution.setVariable(3, new RealVariable(0, 1));
        //solution.setVariable(3, new BinaryIntegerVariable(0,100));
        return solution;
    }

}
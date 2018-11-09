package org.moeaframework.problem.Hybrid;

//import Algorithms.testScilab;

public class HSM {
    public static void main(String[] args) throws Exception {
        double[][] AASM = {
                {1.00, 0.55, 0.07, 0.00, 0.00, 0.00},
                {0.55, 1.00, 0.44, 0.18, 0.18, 0.18},
                {0.07, 0.44, 1.00, 0.46, 0.46, 0.46},
                {0.00, 0.18, 0.46, 1.00, 1.00, 1.00},
                {0.00, 0.18, 0.46, 1.00, 1.00, 1.00},
                {0.00, 0.18, 0.46, 1.00, 1.00, 1.00}
        };
        double[][] ADSM = {
                {1.00, 1.00, 1.00, 0.83, 0.50, 0.33},
                {1.00, 1.00, 1.00, 0.83, 0.50, 0.33},
                {1.00, 1.00, 1.00, 0.83, 0.50, 0.33},
                {0.83, 0.83, 0.83, 1.00, 0.60, 0.40},
                {0.50, 0.50, 0.50, 0.60, 1.00, 0.25},
                {0.33, 0.33, 0.33, 0.40, 0.25, 1.00}
        };
        double[] frequencies = {600, 500, 700, 1000, 200, 400};
        double alpha = 0.3;
        double[][] HSM = ContrucHybridSimilarityMatrix(AASM, ADSM, alpha);
        //testScilab.printMatrix(HSM);
    }
    public static double[][] ContrucHybridSimilarityMatrix (double[][] AASM, double[][] ADSM, double alpha){
        double[][] HSM = new double[AASM.length][AASM[0].length];
        for (int i = 0; i < AASM.length; i++){
            for (int j = 0; j < AASM[0].length; j++){
                HSM[i][j] = alpha*AASM[i][j] + (1-alpha)*ADSM[i][j];
            }
        }
        return HSM;
    }
}

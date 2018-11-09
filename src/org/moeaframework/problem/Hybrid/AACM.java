package org.moeaframework.problem.Hybrid;

//import Algorithms.testScilab;

public class AACM {
    public static void main(String[] args) throws Exception {
        double[][] AUM = {
                {0, 1, 1, 1, 1, 1},
                {0, 0, 1, 1, 1, 1},
                {0, 0, 0, 1, 1, 1},
                {1, 1, 0, 0, 0, 0},
                {1, 1, 1, 0, 0, 0},
                {0, 1, 1, 0, 0, 0}
        };
        double[] frequencies = {600, 500, 700, 1000, 200, 400};
        double[][] AACM = ContructAttributeAccessCorrelationMatrix(AUM, frequencies);
        //testScilab.printMatrix(AACM);
    }
    public static double[][] ContructAttributeAccessCorrelationMatrix (double[][] AUM, double[] frequencies){
        double[][] AACM = new double[AUM[0].length][AUM[0].length];
        for (int i = 0; i < AUM[0].length; i++){
            for (int j = 0; j < AUM[0].length; j++){
                AACM[i][j] = 0;
            }
        }

        for (int i = 0; i < AUM.length; i++){
            double[] row = new double[AUM[0].length];
            for (int j = 0; j < AUM[0].length; j++){
                if (AUM[i][j]==1) row[j] = 1;
                else row[j] = 0;
            }
            for (int k = 0; k < AUM[0].length; k++){
                if (row[k] == 1) {
                    for (int l = k; l < AUM[0].length; l ++){
                        AACM[k][l] = AACM[k][l] + row[l]*frequencies[i];
                    }
                }
            }
        }
        return AACM;
    }
}

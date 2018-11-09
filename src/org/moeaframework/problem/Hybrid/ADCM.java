package org.moeaframework.problem.Hybrid;

//import Algorithms.testScilab;

public class ADCM {
    public static void main(String[] args) throws Exception {
        String[][] T = {
                {"0", "1", "1", "1", "1", null},
                {"0", "0", "1", "1", null, "1"},
                //{"0", "0", "0", "1", null, null},
                //{"0", "0", "0", "1", null, null},
                //{"0", "0", "0", "1", null, null},
                {"0", "0", "0", "1", null, null},
                {"0", "0", "0", "1", "1", null},
                {"1", "1", "0", "0", "0", "0"},
                //{"1", "1", "1", null, null, null},
                {"0", "1", "1", null, null, null}
        };
        double[] frequencies = {600, 500, 700, 1000, 200, 400};
        double[][] ADCM = ContructAttributeDensityCorrelationMatrix(T);
        //testScilab.printMatrix(ADCM);
    }
    public static double[][] ContructAttributeDensityCorrelationMatrix (String[][] T){
        double[][] ADCM = new double[T[0].length][T[0].length];
        for (int i = 0; i < T[0].length; i++){
            for (int j = 0; j < T[0].length; j++){
                ADCM[i][j] = 0;
            }
        }

        for (int i = 0; i < T.length; i++){
            double[] row = new double[T[0].length];
            for (int j = 0; j < T[0].length; j++){
                if (T[i][j]!=null) row[j] = 1;
                else row[j] = 0;
            }
            for (int k = 0; k < T[0].length; k++){
                if (row[k] == 1) {
                    for (int l = k; l < T[0].length; l ++){
                        ADCM[k][l] = ADCM[k][l] + row[l];
                    }
                }
            }
        }
        return ADCM;
    }
}

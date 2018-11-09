package org.moeaframework.problem.Hybrid;

//import Algorithms.testScilab;

public class ADSM {
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
        System.out.println("There is the table of null");
        //testScilab.printMatrix(convertIsNotNull2double(isNotNull(T)));
        double[][] ADSM = ContructAttributeDensitySimilarityMatrix(T);
        System.out.println("There is table of ADSM");
        //testScilab.printMatrix(ADSM);
    }
    public static double[][] ContructAttributeDensitySimilarityMatrix (String[][] T){
        boolean[][] isNotNull = isNotNull(T);
        double[][] ADSM = new double[T[0].length][T[0].length];
        for (int i = 0; i < T[0].length; i++){
            boolean[] Ai = AASM.takeColumnBitMatrix(isNotNull,i);
            for (int j = 0; j < T[0].length; j++){
                boolean[] Aj = AASM.takeColumnBitMatrix(isNotNull,j);
                boolean[] and = AASM.andLogic(Ai,Aj);
                boolean[] or = AASM.orLogic(Ai,Aj);
                int andLogic = AASM.checkSumVector(and);
                //int orLogic = AASM.checkSumVector(or);
                ADSM[i][j] = (double) andLogic/(AASM.checkSumVector(Ai)+AASM.checkSumVector(Aj)-andLogic);
            }
        }
        return ADSM;
    }
    public static boolean[][] isNotNull (String [][] T){
        boolean[][] notNull = new boolean[T.length][T[0].length];
        for (int i = 0; i < T.length; i++){
            for (int j = 0; j < T[0].length; j++){
                if (T[i][j] != null) notNull[i][j] = true;
                else notNull[i][j] = false;
            }
        }
        return notNull;
    }
    public static double[][] convertIsNotNull2double (boolean[][] isnotNull){
        double[][] matrix = new double[isnotNull.length][isnotNull[0].length];
        for (int i = 0; i < isnotNull.length; i++){
            for (int j = 0; j < isnotNull[0].length; j++){
                if (isnotNull[i][j]) matrix[i][j] = 1;
                else matrix[i][j] = 0;
            }
        }
        return matrix;
    }
}

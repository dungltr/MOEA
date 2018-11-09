package org.moeaframework.problem.Hybrid;

//import Algorithms.testScilab;

public class AASM {
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
        double[][] AASM = ContructAttributeAccessSimilarityMatrix(AUM, frequencies);
        //testScilab.printMatrix(AASM);
    }
    public static double[][] ContructAttributeAccessSimilarityMatrix (double[][] AUM){
        boolean[][] AUMbit = new boolean[AUM.length][AUM[0].length];
        for (int i = 0; i < AUM.length; i++){
            for (int j = 0; j < AUM[0].length; j++){
                if (AUM[i][j]==1) AUMbit[i][j] = true;
                else AUMbit[i][j] = false;
            }
        }
        double[][] AASM = new double[AUM[0].length][AUM[0].length];
        for (int i = 0; i < AUM[0].length; i++){
            boolean[] Ai = takeColumnBitMatrix(AUMbit,i);
            for (int j = 0; j < AUM[0].length; j++){
                boolean[] Aj = takeColumnBitMatrix(AUMbit,j);
                boolean[] and = andLogic(Ai,Aj);
                boolean[] or = orLogic(Ai,Aj);
                int andLogic = checkSumVector(and);
                int orLogic = checkSumVector(or);
                AASM[i][j] = (double) andLogic/orLogic;
            }
        }
        return AASM;
    }
    public static double[][] ContructAttributeAccessSimilarityMatrix (double[][] AUM, double[] frequencies){
        boolean[][] AUMbit = new boolean[AUM.length][AUM[0].length];
        for (int i = 0; i < AUM.length; i++){
            for (int j = 0; j < AUM[0].length; j++){
                if (AUM[i][j]==1) AUMbit[i][j] = true;
                else AUMbit[i][j] = false;
            }
        }
        double[][] AASM = new double[AUM[0].length][AUM[0].length];
        for (int i = 0; i < AUM[0].length; i++){
            boolean[] Ai = takeColumnBitMatrix(AUMbit,i);
            for (int j = 0; j < AUM[0].length; j++){
                boolean[] Aj = takeColumnBitMatrix(AUMbit,j);
                boolean[] and = andLogic(Ai,Aj);
                boolean[] or = orLogic(Ai,Aj);
                int andLogic = checkSumVector(and);
                int orLogic = checkSumVector(or);
                double sumAndLogicFrequencies = sumFrequencies(and, frequencies);
                AASM[i][j] = sumAndLogicFrequencies/(sumFrequencies(Ai,frequencies)+sumFrequencies(Aj,frequencies)-sumAndLogicFrequencies);
            }
        }
        return AASM;
    }
    public static double sumFrequencies (boolean[] andLogic, double[] frequencies){
        double sum = 0;
        for (int i = 0; i < andLogic.length; i++){
            if (andLogic[i]) sum = sum + frequencies[i];
        }
        return sum;
    }
    public static boolean[] takeColumnBitMatrix (boolean[][] matrix, int column){
        boolean[] vectorBit = new boolean[matrix.length];
        for (int i = 0; i < matrix.length; i++){
            vectorBit[i] = matrix[i][column];
        }
        return vectorBit;
    }
    public static boolean[] andLogic(boolean[] Ai,boolean[] Aj){
        boolean[] vector = new boolean[Ai.length];
        for (int i = 0; i < Ai.length; i++){
            vector[i] = Ai[i] & Aj[i];
        }
        return vector;
    }
    public static boolean[] orLogic(boolean[] Ai,boolean[] Aj){
        boolean[] vector = new boolean[Ai.length];
        for (int i = 0; i < Ai.length; i++){
            vector[i] = Ai[i] | Aj[i];
        }
        return vector;
    }
    public static int checkSumVector(boolean[] A){
        int count = 0;
        for (int i = 0; i < A.length; i++){
            if (A[i]) count ++;
        }
        return count;
    }
}

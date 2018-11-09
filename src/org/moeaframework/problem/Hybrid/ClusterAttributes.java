package org.moeaframework.problem.Hybrid;

//import Algorithms.testScilab;

import java.util.ArrayList;
import java.util.List;

public class ClusterAttributes {
    public static void main(String[] args) throws Exception {
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
                //{"0", "0", "0", "1", null, null},
                //{"0", "0", "0", "1", null, null},
                //{"0", "0", "0", "1", null, null},
                {"0", "0", "0", "1", null, null},
                {"0", "0", "0", "1", "1", null},
                {"1", "1", "0", "0", "0", "0"},
                //{"1", "1", "1", null, null, null},
                {"0", "1", "1", null, null, null}
        };
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
        double[][] AACM = org.moeaframework.problem.Hybrid.AACM.ContructAttributeAccessCorrelationMatrix(AUM,frequencies);
        double alpha = 0.3;
        double beta = 0.5;
        double[][] ADCM = org.moeaframework.problem.Hybrid.ADCM.ContructAttributeDensityCorrelationMatrix(T);
        double[][] HSM = org.moeaframework.problem.Hybrid.HSM.ContrucHybridSimilarityMatrix(AASM, ADSM, alpha);
        //testScilab.printMatrix(HSM);
        List<List<Integer>> Cluster = ClusterAttributes(AACM,ADCM,HSM,alpha,beta);
        System.out.println(Cluster.toString());
    }
    public static List<List<Integer>> ClusterAttributes (double[][] AACM, double[][] ADCM, double [][] HSM, double alpha, double beta){
        double[][] PriM = new double [AACM.length][AACM[0].length];
        if (alpha >= 0.5) {
            for (int i = 0; i < PriM.length; i++){
                for (int j = 0; j < PriM[0].length; j++){
                    PriM[i][j] = AACM[i][j];
                }
            }
        }
        else {
            for (int i = 0; i < PriM.length; i++){
                for (int j = 0; j < PriM[0].length; j++){
                    PriM[i][j] = ADCM[i][j];
                }
            }
        }
        List<List<Integer>> setClusterAttribute = new ArrayList<List<Integer>>();
        List<Integer> attribute = resetAttribute(PriM);

        while (attribute.size()>0){
            ////////////////////////////////////
            List<Integer> cluster = new ArrayList<>();
            double PriMmax = 0;
            int Max = 0;
            for (int index:attribute){
                if (PriM[index][index] > PriMmax) {
                    PriMmax = PriM[index][index];
                    Max = index;
                }
            }
            cluster.add(Max);
            int indexMax = 0;
            for (int i = 0; i< attribute.size(); i++){
                if (attribute.get(i) == Max) indexMax = i;
            }
            attribute.remove(indexMax);
            List<Integer> temp = new ArrayList<>();
            /////////////////////////////// Looking for the attribute which is similar to all solution in cluster
            for (int attibuteLeft: attribute) {

                boolean similar = true;
                for (int currentAttribute:cluster) {
                    if(((currentAttribute < attibuteLeft)&&(HSM[currentAttribute][attibuteLeft] < beta))
                        ||((currentAttribute > attibuteLeft)&&(HSM[currentAttribute][attibuteLeft] > beta))){
                        similar = false;
                        break;
                    }

                }
                if (similar == true){
                    cluster.add(attibuteLeft);
                    temp.add(attibuteLeft);
                }

            }
            /////////////remove the solution is added to cluster///////////////////////////
            for (int remove:temp){
                for (int i = 0; i < attribute.size(); i++){
                    if (attribute.get(i) == remove) attribute.remove(i);
                }
                //attribute.remove(remove);
            }
            //////////////////////////////////////
            setClusterAttribute.add(cluster);
        }
        return setClusterAttribute;
    }
    public static List<Integer> resetAttribute (double[][] AASM){
        List<Integer> attribute = new ArrayList<>();
        for (int i = 0; i < AASM[0].length; i++){
            attribute.add(i);
        }
        return attribute;
    }
}

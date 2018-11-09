package org.moeaframework.problem.Hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StorageConfiguration {
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
        double alpha = 0.5;
        double beta = 0.4;
        double lada = 0.6;
        double theta = 0.5;
        List<Integer> layout = new ArrayList<>();
        layout.add(0); //row store
        layout.add(1); //column store
        Map<List<Integer>, Integer> configuration = StorageConfiguration(AUM, frequencies, T, layout, alpha, beta, lada, theta);
        System.out.println(configuration.toString());
    }
    public static Map<List<Integer>, Integer> StorageConfiguration(double[][] AUM,
                                                                   double[] frequencies,
                                                                   String[][] T,
                                                                   List<Integer> layout,
                                                                   double alpha,
                                                                   double beta,
                                                                   double lamda,
                                                                   double theta){
        double [][] AACM = org.moeaframework.problem.Hybrid.AACM.ContructAttributeAccessCorrelationMatrix(AUM, frequencies);
        double [][] ADCM = org.moeaframework.problem.Hybrid.ADCM.ContructAttributeDensityCorrelationMatrix(T);
        double [][] AASM = org.moeaframework.problem.Hybrid.AASM.ContructAttributeAccessSimilarityMatrix(AUM,frequencies);
        double [][] ADSM = org.moeaframework.problem.Hybrid.ADSM.ContructAttributeDensitySimilarityMatrix(T);
        double [][] HSM = org.moeaframework.problem.Hybrid.HSM.ContrucHybridSimilarityMatrix(AASM, ADSM, alpha);
        List<List<Integer>> Cluster = ClusterAttributes.ClusterAttributes(AACM,ADCM,HSM,alpha,beta);
        //System.out.println(Cluster.toString());
        Map<List<Integer>, Integer> configuration = org.moeaframework.problem.Hybrid.MergeAndSelectStores.mergeAndSelectStore(AASM, Cluster, layout, lamda, theta);
        return configuration;
    }

}

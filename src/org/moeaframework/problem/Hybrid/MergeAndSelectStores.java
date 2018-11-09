package org.moeaframework.problem.Hybrid;

//import Algorithms.testScilab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergeAndSelectStores {
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
        double lamda = 0;
        double theta = 0;
        double[][] ADCM = org.moeaframework.problem.Hybrid.ADCM.ContructAttributeDensityCorrelationMatrix(T);
        double[][] HSM = org.moeaframework.problem.Hybrid.HSM.ContrucHybridSimilarityMatrix(AASM, ADSM, alpha);
        //testScilab.printMatrix(HSM);
        List<List<Integer>> Cluster = ClusterAttributes.ClusterAttributes(AACM,ADCM,HSM,alpha,beta);
        double interSim = interClusterAccessSimilar(Cluster.get(0),Cluster.get(1),AASM);
        double intraSim = intraClusterAccessSimmilar(Cluster.get(0),AASM);
        System.out.println(Cluster.toString());
        System.out.println(interSim);
        System.out.println(intraSim);

        List<Integer> merge = merge(Cluster.get(0),Cluster.get(1));
        List<Integer> layout = new ArrayList<>();
        layout.add(0); //row store
        layout.add(1); //column store
        System.out.println(merge.toString());
        Map<List<Integer>, Integer> configuration = mergeAndSelectStore(AASM, Cluster, layout, lamda, theta);
        System.out.println(configuration.toString());
    }

    public static double interClusterAccessSimilar (List<Integer> Cu, List<Integer> Cv, double[][] AASM){
        double sum = 0;
        for (int u:Cu) {
            for(int v:Cv){
                sum = sum + AASM[u][v];
            }
        }
        return sum/(Cu.size()*Cv.size());

    }

    public static double intraClusterAccessSimmilar (List<Integer> Cu, double[][] AASM){
        if (Cu.size()==1) return 1;
        else {
            double sum = 0;
            for (int u:Cu) {
                for(int v:Cu){
                    if (u!=v) sum = sum + AASM[u][v];
                }
            }
            return sum/(Cu.size()*(Cu.size()-1));
        }


    }

    public static List<Integer> merge(List<Integer> U, List<Integer> V){
        List<Integer> merge = U;
        for(Integer v:V){
            merge.add(v);
        }
        return merge;
    }

    public static Map<List<Integer>, Integer> mergeAndSelectStore (double[][] AASM,
                                                           List<List<Integer>> cluster,
                                                           List<Integer> layout,
                                                           double lamda, double theta){
        List<List<Integer>> originalCluster = cluster;
        Map<List<Integer>, Integer> configuration = new HashMap<List<Integer>, Integer>();

        boolean found;
        do {
            double MaxSim = 0;
            found = false;
            int indexU = 0;
            int indexV = 0;
            for (int i = 0; i < originalCluster.size()-1; i++) {
                for (int j = i+ 1; j < originalCluster.size(); j++){
                    double currentSim = interClusterAccessSimilar(originalCluster.get(i),originalCluster.get(j),AASM);
                    if ((currentSim >= theta) && (currentSim > MaxSim)){
                        MaxSim = currentSim;
                        found = true;
                        indexU = i;
                        indexV = j;
                    }
                }
            }
            if (found){
                List<Integer> U = originalCluster.get(indexU);
                List<Integer> V = originalCluster.get(indexV);
                List<Integer> merge = merge(U,V);
                originalCluster.add(merge);
                originalCluster.remove(U);
                originalCluster.remove(V);
            }

        }
        while (found);

        List<Integer> store = new ArrayList<>();
        for (int i = 0; i < originalCluster.size(); i++){
            if (intraClusterAccessSimmilar(originalCluster.get(i),AASM) >= lamda){
                store.add(layout.get(0));// row store is 0
            }
            else store.add(layout.get(1));// column store is 1;
        }
        for (int i = 0; i < originalCluster.size(); i++){
            configuration.put(originalCluster.get(i),store.get(i));
        }
        return configuration;
    }
}

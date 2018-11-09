package org.moeaframework.problem.Hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Statistic {
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
        // configuration 1: 0, 0, 0, 0
        // configuration 2: 0, 0.4, 0.6, 0.5: 1800 87000 vs 1,800 71,600
        // configuration 3: 0.2, 0.8, 0.8, 0.8: 1100 108,600 vs 1,100 107,600
        double alpha = 0.2;
        double beta  = 0.8;
        double lamda = 0.8;
        double theta = 0.8;
        List<Integer> layout = new ArrayList<>();
        layout.add(0); //row store
        layout.add(1); //column store
        Map<List<Integer>, Integer> configuration = StorageConfiguration.StorageConfiguration(AUM, frequencies, T, layout, alpha, beta, lamda, theta);
        System.out.println(configuration.toString());
        List<Double> result = statistic(configuration,AUM, frequencies, T);
        System.out.println(result.toString());//testScilab.printMatrix(checkFrequenciesAUM(AUM, frequencies, column));
    }
    public static double[][] resetAUM (double[][] AUM){
        double [][] resetMatrix = new double[AUM.length][AUM[0].length];;
        for (int i = 0; i < AUM.length; i++){
            for (int j = 0; j < AUM[0].length; j++){
                resetMatrix[i][j] = 0;
            }
        }
        return resetMatrix;
    }
    public static double[] resetArrayAUM(double[][] AUM){
        double[] resetMatrix = new double[AUM[0].length];
        for (int i = 0; i < AUM.length; i++){
                resetMatrix[i] = 0;
        }
        return resetMatrix;
    }
    public static double[][] checkFrequenciesAUM(double[][] AUM, double [] countArray, double[] frequencies, List<Integer> column){
        double [][] checkFrequencies = new double[AUM.length][AUM[0].length];
        for (int i = 0; i < AUM.length; i++){
            //int k = 0;
            for (int j: column){
                if (AUM[i][j] > 0){
                    checkFrequencies[i][j] = frequencies[i]*AUM[i][j]*countArray[j];
                }
            //    k++;
            }
        }
        return checkFrequencies;
    }
    public static List<Double> statistic(Map<List<Integer>, Integer> configuration, double[][] AUM, double[] frequencies, String[][] T){
        double[][] countAUM = resetAUM(AUM);
        double[] countArray = resetArrayAUM(AUM);
        List<Double> result = new ArrayList<>();
        long countSpace = 0;
        long countNull = 0;
        long numberOfJoins = 0;
        long numberOfScanCell = 0;
        List<List<Integer>> setColumn = new ArrayList<>();
        List<Integer> setStore = new ArrayList<>();
        List<Long> sizeColumn = new ArrayList<>();
        for(Map.Entry<List<Integer>, Integer> entry: configuration.entrySet()) {
            List<Integer> columns = entry.getKey();
            setColumn.add(columns);
            setStore.add(entry.getValue());
            long countSpaceColumn = 0;
            long countNullColumn = 0;

            for (int i = 0; i < T.length; i++){
                long countNullInRow = 0;
                for (int column: columns) {
                    if (T[i][column]==null) countNullInRow++;
                    else countArray[column] = countArray[column] + 1;
                }
                if (countNullInRow != columns.size()) {
                    countNullColumn = countNullColumn + countNullInRow;
                    countSpaceColumn = countSpaceColumn + columns.size();
                }
            }
            countSpaceColumn = countSpaceColumn + countSpaceColumn/columns.size();
            sizeColumn.add(countSpaceColumn);
            countSpace = countSpace + countSpaceColumn;
            countNull = countNull + countNullColumn;
            //System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        result.add((double)countSpace);
        result.add((100*(double)countNull/(double)countSpace));
        /////////////////////////////////////////////////////////////////////////////////////////::
        //System.out.println("The space is : "+countSpace);
        //System.out.println("The space of null is : "+ countNull);
        //System.out.println("The set of column : "+ setColumn.toString());
        ///////////////////////////////////////
        List<Integer> column = new ArrayList<>();
        for (int i = 0; i < AUM[0].length; i++) column.add(i);
        //testScilab.printArray(countArray);
        double [][] statisticColum = checkFrequenciesAUM(AUM, countArray, frequencies, column);
        //testScilab.printMatrix(statisticColum);
        //////////////Finding the join/////////////////////////
        if (setColumn.size()>1){
            for (int k = 0; k < AUM.length; k++){// for row of AUM
                for(int i = 0; i < setColumn.size(); i++){ // for each set of column
                    if (i<setColumn.size()-1){
                        boolean found = false;
                        for (int j = i + 1; j < setColumn.size(); j++){
                            for (int l = 0; l < setColumn.get(i).size(); l++)
                                if(AUM[k][setColumn.get(i).get(l)]==1) {
                                    for(int m = 0; m < setColumn.get(j).size(); m++){
                                        if(AUM[k][setColumn.get(j).get(m)]==1)
                                            found = true;
                                    }
                                }
                        }
                        if (found) {
                            numberOfJoins = numberOfJoins + (long)frequencies[k];
                        }
                        ////////////////////////////////////////////////////////////////////////////
                    }
                    /*
                    int Full = AUM.length * setColumn.get(i).size();
                    boolean foundScan = false;
                    int store = setStore.get(i);
                    for (int m = 0; m < setColumn.get(i).size(); m++){
                        if(AUM[k][setColumn.get(i).get(m)]==1){
                            foundScan = true;
                        }
                        if (store == 1){
                            //numberOfScanCell = numberOfScanCell + (int) statisticColum[k][setColumn.get(i).get(m)];
                        }
                    }
                    if (store == 0){
                        if (!foundScan) {
                            Full = Full - setColumn.get(i).size();
                        }
                        //numberOfScanCell = numberOfScanCell + (int) frequencies[k]*sizeColumn.get(i).;
                        System.out.println("There is an update of numberOfScanCell" + numberOfScanCell);

                    }
                    countFullNull = countFullNull * setColumn.get(i).size();
                   */

                }

            }
        }
        //////Findding the numberofScanCell/////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (setColumn.size()>1){
            for(int i = 0; i < setColumn.size(); i++){ //for each set
                if(setStore.get(i)==0){
                    int full = T.length*setColumn.get(i).size();
                    for (int k = 0; k < T.length; k++){
                        boolean checkNull = false;
                        for (int j = 0; j <setColumn.get(i).size(); j++){
                            if (T[k][setColumn.get(i).get(j)]!=null) checkNull = true;
                        }
                        if (!checkNull) full = full - setColumn.get(i).size();
                    }

                    for (int k = 0; k < AUM.length; k++){
                        long countRow = 0;
                        boolean checkNull = false;
                        for (int j = 0; j < setColumn.get(i).size(); j ++){
                            if (AUM[k][setColumn.get(i).get(j)]==1) {
                                countRow++;
                                checkNull = true;
                            }

                        }
                        if (checkNull)
                            numberOfScanCell = numberOfScanCell + (long) frequencies[k] * (full/setColumn.get(i).size()) * (setColumn.get(i).size()+1);
                    }
                }
                else {
                    for (int j = 0; j < setColumn.get(i).size(); j ++){
                        for (int  k = 0; k < AUM.length; k ++){
                            numberOfScanCell = numberOfScanCell + (long) statisticColum[k][setColumn.get(i).get(j)];
                        }
                    }
                }
            }
        }
        else{////////// there is only one table
            if(setStore.get(0) == 0){
                for (int k = 0; k < AUM.length; k++){
                    boolean foundScan = false;
                    for (int j = 0; j < setColumn.get(0).size(); j++){
                        if(AUM[k][setColumn.get(0).get(j)]==1){
                            foundScan = true;
                        }
                    }
                    if (foundScan) numberOfScanCell = numberOfScanCell + (long) frequencies[k]*sizeColumn.get(0);
                }
            }
            else {
                for (int k = 0; k < AUM.length; k++){
                    boolean foundScan = false;
                    for (int j = 0; j < setColumn.get(0).size(); j++){
                        if(AUM[k][setColumn.get(0).get(j)]==1){
                            foundScan = true;
                        }
                    }
                    if (foundScan) numberOfScanCell = numberOfScanCell + (long) frequencies[k]*sizeColumn.get(0);
                }
            }

        }
        result.add((double)numberOfJoins);
        result.add((double)numberOfScanCell);
        //System.out.println("The number of joins : "+ numberOfJoins);
        //System.out.println("The number of scan cell : "+ numberOfScanCell);
        return result;
    }
}

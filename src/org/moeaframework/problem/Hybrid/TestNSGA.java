package org.moeaframework.problem.Hybrid;
//import Algorithms.Writematrix2CSV;
import NSGAIV.GeneratorLatexTable;
import NSGAIV.ReadFile;
import NSGAIV.writeMatrix2CSV;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import static org.apache.commons.math.util.MathUtils.round;

public class TestNSGA {
    private static int populationSize = 10;
    //private static int MaxEvaluations = populationSize*10;
    static int  maxEvaluate = populationSize;// = Ask("maxEvaluate",population*10);
    static int  runSeed = 100;// = Ask("RunSeed", 20);
    //private static int numberSeeds = runSeed;
    private static double sbx = 30.0;
    private static double pm = 20.0;

    public static void main(String[] args) throws IOException {
        List<String> name = new ArrayList<>();
        String nameFile1 = "generalinfotable";
        String nameFile2 = "sequenceattributes";
        String nameFile3 = "patientall";
        String nameFile4 = "studyall";
        //name.add(nameFile1);
        //name.add(nameFile2);
        name.add(nameFile3);
        name.add(nameFile4);
        for (int i = 0; i < 1; i ++){
            for(String nameFile:name){
                experimentQuality(nameFile);
            }
        }
        //testConfiguration(nameFile1);
    }
    public static double[][] createAUM(String name){
        switch (name){
            case "generalinfotable": {
                double[][] AUM = {
                        {1, 1, 1, 1},
                        {1, 0, 0, 1},
                        {0, 0, 1, 0},
                        {0, 1, 0, 0}
                };
                return AUM;
            }
            case "sequenceattributes": {
                double[][] AUM = {
                        {1, 1, 1, 1},
                        {1, 1, 1, 0}
                };
                return AUM;
            }
            case "patientall": {
                double[][] AUM = {
                        {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0},
                        {1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
                        {1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                        {1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
                        {1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0},
                        {1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0}
                };
                return AUM;
            }
            case "studyall": {
                double[][] AUM = {
                        {1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                        {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0},
                        {1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0},
                        {1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0},
                        {1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0}
                };
                return AUM;
            }
            default:
                break;
        }
        double[][] AUM = {
                {1, 1, 1, 1},
                {1, 0, 0, 1},
                {0, 0, 1, 0},
                {0, 1, 0, 0}
        };
        return AUM;
    }
    public static double[] createFrequencies(String name){
        switch (name){
            case "generalinfotable": {
                double[] frequencies = {100, 100, 100, 100};
                return frequencies;
            }
            case "sequenceattributes": {
                double[] frequencies = {100, 100};
                return frequencies;
            }
            case "patientall": {
                double[] frequencies = {300, 100, 100, 100, 100, 100};
                return frequencies;
            }
            case "studyall": {
                double[] frequencies = {300, 100, 100, 100, 100, 100};
                return frequencies;
            }
            default:
                break;
        }
        double[] frequencies = {100, 100, 100, 100};
        return frequencies;
    }
    public static String[][] fixMatrix(double [][] AUM, String [][] T){
        String [][] fix = new String[T.length][AUM[0].length];
        for (int i = 0; i < fix.length; i++){
            for (int j = 0; j < fix[0].length; j++){
                fix[i][j] = T[i][j];
            }
        }
        return fix;
    }
    public static void testConfiguration(String nameFile) throws IOException {
        String file = "/home/ubuntu/dicom/"+nameFile+".txt";
        double [][] AUM = createAUM(nameFile);
        double[] frequencies = createFrequencies(nameFile);
        String[][] originalT = ReadBigFile.readAllFile(file);
        String[][] T = fixMatrix(AUM,originalT);

        double alpha = 0;
        double beta = 0;
        double lambda = 0;
        double theta = 0;
        List<Integer> layout = new ArrayList<>();
        layout.add(0); //row store
        layout.add(1); //column store
        Map<List<Integer>, Integer> configuration = StorageConfiguration.StorageConfiguration(AUM, frequencies, T, layout, alpha, beta, lambda, theta);
        System.out.println(configuration.toString());

    }
    public static double testBigTable(String nameFile, String algorithm) throws IOException {
        String file = "/home/ubuntu/dicom/"+nameFile+".txt";
        int populationSize = 100;
        double [][] AUM = createAUM(nameFile);
        double[] frequencies = createFrequencies(nameFile);

        long start = System.currentTimeMillis();
        String[][] originalT = ReadBigFile.readAllFile(file);
        String[][] T = fixMatrix(AUM,originalT);
        long stop = System.currentTimeMillis();
        System.out.println("The system read bigtale in "+ (stop-start)/1000);

        HybridProblem problem = new HybridProblem(AUM,T,frequencies);
        long stopHybrid = System.currentTimeMillis();
        System.out.println("The system Hybrid runs in "+ (stopHybrid-stop)/1000);
        NondominatedPopulation result = new Executor()
                .withProperty("populationSize", populationSize)
                .withAlgorithm(algorithm)
                .withProblem(problem)
                .withMaxEvaluations(100)
                .run();
        Analyzer analyzer = new Analyzer()
                .withProblem(problem);

        String Caption = "Generational Distance";
        switch(Caption) {
            case "Generational Distance" :
                analyzer.includeGenerationalDistance();// Statements
                break; // optional
            case "Hyper volume" :
                analyzer.includeHypervolume();// Statements
                break; // optional
            case "Inverted Generational Distance" :
                analyzer.includeInvertedGenerationalDistance();// Statements
                break; // optional
            case "Maximum Pareto Front Error" :
                analyzer.includeMaximumParetoFrontError();// Statements
                break; // optional
            // You can have any number of case statements.
            default : // Optional
                analyzer.includeGenerationalDistance();// Statements
        }
        analyzer.showStatisticalSignificance();

        String store = "";
        String title = "";
        for (int i = 0; i < result.get(0).getNumberOfVariables(); i++){
            title = title + "V" + i + "\t"+ "\t";
        }
        for (int i = 0; i < result.get(0).getNumberOfObjectives(); i++){
            title = title + "Obj" + i + "\t"+ "\t";
        }
        store = store + title + "\n";
        System.out.println(title);

        for (Solution solution : result) {
            String value = "";
            for (int i = 0; i < result.get(0).getNumberOfVariables(); i++){
                double temp = ((RealVariable)solution.getVariable(i)).getValue();
                value = value + temp  + "\t"+ "\t";
            }
            for (int i = 0; i < result.get(0).getNumberOfObjectives(); i++){
                //double temp = ((RealVariable)solution.getVariable(i)).getValue();
                value = value + (double)solution.getObjective(i) + "\t"+ "\t";
            }
            store = store + value;
            System.out.println(value);
            List<Integer> layout = new ArrayList<>();
            layout.add(0); //row store
            layout.add(1); //column store

            double alpha = ((RealVariable)solution.getVariable(0)).getValue();
            double beta = ((RealVariable)solution.getVariable(1)).getValue();
            double lamda = ((RealVariable)solution.getVariable(2)).getValue();
            double theta = ((RealVariable)solution.getVariable(3)).getValue();

            Map<List<Integer>, Integer> configuration = StorageConfiguration.StorageConfiguration(AUM,frequencies,T,layout,alpha,beta,lamda,theta);
            //System.out.println(configuration.toString());
            store = store + "\t" + "\t" + configuration.toString() + "\n";

            //if (!solution.violatesConstraints()) {
            //    System.out.format("%10.3f   %10.3f  %10.3f  %10.3f%n",
            //            solution.getObjective(0),
            //            solution.getObjective(1),
            //            solution.getObjective(2),
            //            solution.getObjective(3));
            //}
        }
        long stopNSGA = System.currentTimeMillis();
        double NSGATime = (double) (stopNSGA-stopHybrid)/1000;
        System.out.println("The system runs NSGA in "+ NSGATime);
        System.out.println("The store result is :\n" + store);
        System.out.println("The total of good configuration is:" + result.size());
        //PrintWriter out = new PrintWriter("filename.txt");

        try (PrintWriter out = new PrintWriter("/home/ubuntu/dicom/result_"+nameFile+".txt")) {
            out.println(store.replaceAll("\t\t","\t"));
        }
        return NSGATime;
    }
    public static void testSmallTable(){
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
        HybridProblem problem = new HybridProblem(AUM,T,frequencies);
        NondominatedPopulation result = new Executor()
                .withAlgorithm("NSGAII")
                .withProblem(problem)
                .withMaxEvaluations(100)
                .run();
        String title = "";
        for (int i = 0; i < result.get(0).getNumberOfVariables(); i++){
            title = title + "V" + i + "\t"+ "\t";
        }
        for (int i = 0; i < result.get(0).getNumberOfObjectives(); i++){
            title = title + "Obj" + i + "\t"+ "\t";
        }
        System.out.println(title);

        for (Solution solution : result) {
            String value = "";
            for (int i = 0; i < result.get(0).getNumberOfVariables(); i++){
                double temp = ((RealVariable)solution.getVariable(i)).getValue();
                value = value + temp  + "\t"+ "\t";
            }
            for (int i = 0; i < result.get(0).getNumberOfObjectives(); i++){
                //double temp = ((RealVariable)solution.getVariable(i)).getValue();
                value = value + (double)solution.getObjective(i) + "\t"+ "\t";
            }
            System.out.println(value);


            //if (!solution.violatesConstraints()) {
            //    System.out.format("%10.3f   %10.3f  %10.3f  %10.3f%n",
            //            solution.getObjective(0),
            //            solution.getObjective(1),
            //            solution.getObjective(2),
            //            solution.getObjective(3));
            //}
        }
    }
    public static void experiment(String fileName) throws IOException {
        List<String> algorithms = new ArrayList<>();
        algorithms.add("NSGAII");
        algorithms.add("NSGAIII");
        algorithms.add("NSGAV");
        algorithms.add("MOEAD");
        algorithms.add("eMOEA");
        double [] executionTime = new double[algorithms.size()];
        for (int i = 0; i < algorithms.size(); i++) {
            System.out.println("Let begin -------------------------------------------"+algorithms.get(i));
            //long startNSGA = System.currentTimeMillis();
            //testBigTable(fileName,algorithms.get(i));
            //long stopNSGA = System.currentTimeMillis();
            executionTime[i] = testBigTable(fileName,algorithms.get(i));
            System.out.println("The system runs " + algorithms.get(i) + " in "+ executionTime[i]);

        }
        //Writematrix2CSV.addArray2Csv(fileName+"ExecutionTime.csv",executionTime);
        String fileLink = "/home/ubuntu/dicom/"+fileName+"ExecutionTime.csv";
        //Writematrix2CSV.addArray2Csv(fileLink,executionTime);
    }
    public static void experimentQuality(String nameFile) throws IOException {
        String file = "/home/ubuntu/dicom/"+nameFile+".txt";
        List<String> Algorithms = new ArrayList<>();
        Algorithms.add("NSGAII");
        Algorithms.add("NSGAIII");
        Algorithms.add("NSGAV");
        Algorithms.add("MOEAD");
        Algorithms.add("eMOEA");

        String[] algorithms = new String [Algorithms.size()];// Algorithms.toArray();
        for (int i = 0; i<algorithms.length;i++){
            algorithms[i] = Algorithms.get(i);
        }
        List<String> Standards = new ArrayList<>();
        Standards.add("Generational Distance");
        //Standards.add("Hyper volume");
        Standards.add("Inverted Generational Distance");
        Standards.add("Maximum Pareto Front Error");
        String Caption = "";

        double [][] AUM = createAUM(nameFile);
        double[] frequencies = createFrequencies(nameFile);

        long start = System.currentTimeMillis();
        String[][] originalT = ReadBigFile.readAllFile(file);
        String[][] T = fixMatrix(AUM,originalT);
        long stop = System.currentTimeMillis();
        System.out.println("The system read bigtale in "+ (stop-start)/1000);
        List<String> Problems = new ArrayList<>();
        HybridProblem problem = new HybridProblem(AUM,T,frequencies);

        Problems.add(finalName(problem.getName(),nameFile));
        String homeFile = ReadFile.readhome("dicomHome")
                +"/"+ "MOEA_"//+Problems.get(0)+"_"+Problems.get(Problems.size()-1)
                //+"_"
                +populationSize+"P"+maxEvaluate+"M"+runSeed+"N"
                +algorithms.length+"A";;

        for (int i=0; i<Standards.size(); i++){
            Caption = Standards.get(i);
            StandardDistance(homeFile, problem, nameFile, algorithms, Caption);
            GeneratorLatexTable.GeneratorComputeTimeToLatex(homeFile, Caption, Problems, algorithms);
        }
    }
    public static String finalName(String problemName, String fileName){
        return fileName;
    }

    public static void StandardDistance(String homeFile, HybridProblem Problems, String nameFile, String[] algorithms, String Caption)throws IOException {
        String directory = Caption.replace(" ", "");
        String File = homeFile + "/"+ directory;//
        java.io.File fileDir = new File(File);
        if (!fileDir.exists()) fileDir.mkdirs();
        String texFile = File + ".tex";
        File fileTex = new File(texFile);
        if (fileTex.exists()){
            writeMatrix2CSV.addHline2tex(Caption, texFile, algorithms);
        }

        //for (int i = 0; i<Problems.size(); i++){
        //    System.out.println("Testing algorithms on: "+Problems.get(i));
            testStandardDistance(File, Problems, nameFile, algorithms, Caption);
        //}
        writeMatrix2CSV.addBottom2tex(texFile,algorithms);
    }
    public static void testStandardDistance(String File, HybridProblem problem, String nameFile, String[] algorithms, String Caption) throws IOException{
        //String directory = Caption.replace(" ", "");
        //setup the experiment

        Executor executor = new Executor()
                .withProblem(problem)
                .withProperty("sbx.distributionIndex", sbx)
                .withProperty("pm.distributionIndex", pm)
                .withMaxEvaluations(maxEvaluate);
        System.out.println("The sbx:= "+sbx+ " and pm:= " +pm);
        Analyzer analyzer = new Analyzer()
                .withProblem(problem);
        switch(Caption) {
            case "Generational Distance" :
                analyzer.includeGenerationalDistance();// Statements
                break; // optional
            case "Hyper volume" :
                analyzer.includeHypervolume();// Statements
                break; // optional
            case "Inverted Generational Distance" :
                analyzer.includeInvertedGenerationalDistance();// Statements
                break; // optional
            case "Maximum Pareto Front Error" :
                analyzer.includeMaximumParetoFrontError();// Statements
                break; // optional
            // You can have any number of case statements.
            default : // Optional
                analyzer.includeGenerationalDistance();// Statements
        }
        analyzer.showStatisticalSignificance();
        System.out.println("the name is "+problem.getName());
        //run each algorithm for 50 seeds
        for (String algorithm : algorithms) {
            analyzer.addAll(algorithm,
                    executor.withAlgorithm(algorithm).withProblem(problem).runSeedsProblemName(File,runSeed,finalName(problem.getName(),nameFile)));
        }
        //print the results
        //analyzer.showAggregate();
        analyzer.printAnalysis();
        GeneratorLatexTable.storeData(analyzer, File, finalName(problem.getName(),nameFile), algorithms, Caption);
        analyzer.showStatisticalSignificance();
    }
}
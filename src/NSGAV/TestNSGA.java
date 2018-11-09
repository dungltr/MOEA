package NSGAV;

import NSGAIV.GeneratorLatexTable;
import NSGAIV.ReadFile;
import NSGAIV.writeMatrix2CSV;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestNSGA {
    static int population = 100;// = Ask("Population",100);
    static int  maxEvaluate = population * 50;// = Ask("maxEvaluate",population*10);
    static int  runSeed = 50;// = Ask("RunSeed", 20);
    static int  sbx = 30;// = Ask("sbx",30);
    static int pm = 20;// = Ask("pm", 20);

    public static int Ask(String parameter, int defaultValue){
        Scanner in = new Scanner(System.in);
        System.out.printf(parameter + "(default = " + defaultValue +"):  ");
        int value = 100;
        try {
            value = in.nextInt();
            if (value==0) value = defaultValue;
        }
        catch (java.util.InputMismatchException e) {
            System.out.println("Invalid Input");
            return value;
        }
        return value;
    }

    public static List<String> AskProblem(){
        int minObjectives = Ask("MinObjecitves",4);
        int maxObjecitves = Ask("maxObjectives", 8);
        int numberProblem = Ask("numberProblems",2);
        int [] problems = new int[numberProblem];
        for (int i = 0; i < problems.length; i++){
            problems[i] = Ask("Problem "+i,i);
        }
        List<String> Problems = new ArrayList<>();
        for (int i = minObjectives; i <= maxObjecitves; i++){
            for (int j = 0; j < problems.length; j++){
                Problems.add("DTLZ"+problems[j]+"_"+i);
            }
        }
        return Problems;
    }
    public static int checkRange(int code, String [] algorithms){
        if (code < 0) return defaultCode(algorithms);
        else {
            if (code > defaultCode(algorithms)) return defaultCode(algorithms);
            else return code;
        }
    }
    public static int defaultCode (String[] algorithms){
        int code = 0;
        for (int i = 0; i< algorithms.length; i++){
            code = code + (int)(Math.pow(10,i));
        }
        return code;
    }
    public static String replace (String code){
        String codeString = code.replace("0", "0 ")
                .replace("1", "1 ")
                .replace("2", "1 ")
                .replace("3", "1 ")
                .replace("4", "1 ")
                .replace("5", "1 ")
                .replace("6", "1 ")
                .replace("7", "1 ")
                .replace("8", "1 ")
                .replace("9", "1 ");
        return codeString;
    }
    public static List<String> AskAlgorithms(){
        String[] algorithms = {"MOEAD","GDE3", "eMOEA", "NSGAII","NSGAIII", "NSGAV"};
        System.out.println("There are some algorithms: ");
        printArray(algorithms);
        System.out.println(" ");
        int code = Ask("Code Algorithms",defaultCode(algorithms));
        code = checkRange(code,algorithms);
        String[] codeString = replace(Integer.toString(code)).split(" ");
        List<String> Algorithms = new ArrayList<>();
        for (int i=codeString.length-1; i >=0; i --){
            if ((i < codeString.length)&&(codeString[i].equals("1")))
                //Algorithms.add(algorithms[i]);
                Algorithms.add(algorithms[codeString.length-1-i]);
        }
        return Algorithms;
    }
    public static void printList (List<String> string){
        for (int i = string.size()-1; i >= 0; i--){
            System.out.println(string.get(i));
        }
    }
    public static void printArray (String[] string){
        for (int i = string.length - 1; i>=0; i--){
            System.out.print(string[i]+", ");
        }
    }
    public static void testNSGA() throws IOException {
        int useDefault = Ask("Do you want to use a default setting",0);
        List<String> Problems = new ArrayList<>();
        List<String> Algorithms = new ArrayList<>();
        if (useDefault == 0){
            Problems = AskProblem();
            printList(Problems);
            Algorithms = AskAlgorithms();
            System.out.println("All algorithms is: ");
            printList(Algorithms);
            population = Ask("Population",100);
            maxEvaluate = Ask("maxEvaluate",population*10);
            runSeed = Ask("RunSeed", 50);
            sbx = Ask("sbx",30);
            pm = Ask("pm", 20);
        }
        else {
            for(int i = 2; i < 5; i++){
                for(int j = 1; j <= 4; j++){
                    Problems.add("DTLZ"+j+"_"+2*i);
                }
            }
            System.out.println("All problems are: ");
            printList(Problems);
            Algorithms.add("NSGAII");
            Algorithms.add("NSGAIII");
            Algorithms.add("NSGAV");
            Algorithms.add("MOEAD");
            Algorithms.add("eMOEA");
            System.out.println("All algorithms are: ");
            printList(Algorithms);
        }

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
        String homeFile =ReadFile.readhome("HOME_jMetalData")
                +"/"+ "MOEA_"+Problems.get(0)+"_"+Problems.get(Problems.size()-1)
                +population+"P"+maxEvaluate+"M"+runSeed+"N"
                +algorithms.length+"A";;

        for (int i=0; i<Standards.size(); i++){
            Caption = Standards.get(i);
            StandardDistance(homeFile, Problems, algorithms, Caption);
            GeneratorLatexTable.GeneratorComputeTimeToLatex(homeFile, Caption, Problems, algorithms);
        }
    }
    public static void StandardDistance(String homeFile, List<String> Problems, String[] algorithms, String Caption)throws IOException {
        String directory = Caption.replace(" ", "");
        String File = homeFile + "/"+ directory;//
        java.io.File fileDir = new File(File);
        if (!fileDir.exists()) fileDir.mkdirs();
        String texFile = File + ".tex";
        File fileTex = new File(texFile);
        if (fileTex.exists()){
            writeMatrix2CSV.addHline2tex(Caption, texFile, algorithms);
        }
        for (int i = 0; i<Problems.size(); i++){
            System.out.println("Testing algorithms on: "+Problems.get(i));
            testStandardDistance(File, Problems.get(i),algorithms, Caption);
        }
        writeMatrix2CSV.addBottom2tex(texFile,algorithms);
    }
    public static void testStandardDistance(String File, String problem, String[] algorithms, String Caption) throws IOException{
        String directory = Caption.replace(" ", "");
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
        //run each algorithm for 50 seeds
        for (String algorithm : algorithms) {
            analyzer.addAll(algorithm,
                    executor.withAlgorithm(algorithm).runSeeds(File,runSeed));
        }
        //print the results
        //analyzer.showAggregate();
        analyzer.printAnalysis();
        GeneratorLatexTable.storeData(analyzer, File, problem, algorithms, Caption);
        analyzer.showStatisticalSignificance();
    }
    public static void main(String args[]) throws IOException {
        testNSGA();
    }
}

package org.moeaframework.problem.Hybrid;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ReadBigFile {

    public static void main(String[] args) {
        try {
            test();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void test() throws IOException {
        String filePath = "/home/ubuntu/dicom/studyall.txt";
        String line = readLineFromFile(filePath,0);
        /*try {
            // file content is "ABCDEFGH"

            System.out.println(new String(readCharsFromFile(filePath, 0, 5)));

            //writeData(filePath, "Data", 5);
            //now file content is "ABCDEData"

            //appendData(filePath, "pankaj");
            //now file content is "ABCDEDatapankaj"
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        String [][] T =readAllFile(filePath);
        //printStringArray(T[0]);
        printStringMatrix(T);
        System.out.println(line);
    }
    public static void appendData(String filePath, String data) throws IOException {
        RandomAccessFile raFile = new RandomAccessFile(filePath, "rw");
        raFile.seek(raFile.length());
        System.out.println("current pointer = "+raFile.getFilePointer());
        raFile.write(data.getBytes());
        raFile.close();

    }

    public static void writeData(String filePath, String data, int seek) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        file.seek(seek);
        file.write(data.getBytes());
        file.close();
    }

    public static byte[] readCharsFromFile(String filePath, int seek, int chars) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filePath, "r");
        file.seek(seek);
        byte[] bytes = new byte[chars];
        file.read(bytes);
        file.close();
        return bytes;
    }
    public static String readLineFromFile(String filePath, int seek) throws IOException{
        RandomAccessFile file = new RandomAccessFile(filePath, "r");
        String line = "";
        file.seek(seek);
        String next = file.readLine();
        System.out.println(next);
        file.close();
        return next;
    }
    public static String[][] readAllFile(String filePath) throws IOException{
        RandomAccessFile file = new RandomAccessFile(filePath, "r");
        List<List<String>> listMatrix = new ArrayList<>();
        while (file.readLine()!=null) {
            List<String> listLine = new ArrayList<>();
            String line = file.readLine();
            //System.out.println(line);
            if((line=="null\n")||(line=="\n")||(line=="null")||(line==null)) line = "";
            else {
                if (line.contains("\n"))
                    line = line.replace("\n","");
                String[] column = line.split(",");
                for (int i = 0; i < column.length; i++){
                    if (column[i].contains("null")) listLine.add(null);
                    else listLine.add("1");
                }
                listMatrix.add(listLine);
            }
        }
        return convertListToMatrix(listMatrix);
    }

    public static String[][] convertListToMatrix(List<List<String>> listMatrix){
        int Max = 0;
        for (int i = 0; i < listMatrix.size(); i++){
            if(listMatrix.get(i).size()>Max) Max = listMatrix.get(i).size();
        }
        String[][] T = new String [listMatrix.size()][Max];
        for (int i = 0; i < listMatrix.size(); i++){
            //System.out.println(listMatrix.get(i).size());
            for (int j = 0; j < Max; j++){
                if ( j >= listMatrix.get(i).size())
                    T[i][j] = null;
                else
                    T[i][j] = listMatrix.get(i).get(j);
            }
        }
        return T;
    }
    public static void printStringMatrix(String[][] T){
        for (int i = 0; i < T.length; i++){
            for (int j = 0; j < T[0].length; j++){
                System.out.print(T[i][j]+"\t");
            }
            System.out.println();
        }
    }
    public static void printStringArray(String[] T){
        for (int i = 0; i < T.length; i++){
            System.out.print(T[i]+"\t");
            System.out.println();
        }
    }
}
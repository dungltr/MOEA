/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NSGAIV;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author letrungdung
 */
public class ReadFile {
    public static String readhome(String filename) {
       String sCurrentLine = "nothing";
       try (BufferedReader br = new BufferedReader(new FileReader(System.getenv().get("HOME")+"/"+filename+".txt"))) {
			while ((sCurrentLine = br.readLine()) != null) {
                                return sCurrentLine;
			}

		} catch (IOException e) {
			e.printStackTrace();                       
		}
       return sCurrentLine;
    }
    public static int count(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
        byte[] c = new byte[1024];
        int count = 0;
        int readChars = 0;
        boolean empty = true;
        while ((readChars = is.read(c)) != -1) {
            empty = false;
            for (int i = 0; i < readChars; ++i) {
                if (c[i] == '\n') {
                    ++count;
                }
            }
        }
        return (count == 0 && !empty) ? 1 : count;
        } finally {
        is.close();
       }
    }
}

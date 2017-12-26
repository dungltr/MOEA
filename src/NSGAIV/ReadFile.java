/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NSGAIV;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
}

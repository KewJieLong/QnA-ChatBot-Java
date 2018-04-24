/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kewjielong
 */
public class Utilities {
    public Utilities(){}
    
    public static String printArray(String [] list){
        String arrayString = "[";
        for(String element: list){
            arrayString = arrayString + element + ",";            
        }
        
        return arrayString.substring(0, arrayString.length() - 1) + "]";
        
    }
    
    public static void printArray(boolean [] list){
        for(boolean element: list){
            System.out.print(element + ",");
        }
    }
            
    public static void printArray(int [] list){
        for(int i: list){
            System.out.print(i);
            System.out.print(" ");
        }
    }           
}

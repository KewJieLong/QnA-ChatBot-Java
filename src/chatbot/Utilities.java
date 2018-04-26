/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kewjielong
 */
public class Utilities {
    public static ArrayList<String>verbTags = new ArrayList<String>(){{
        add("VB");
        add("VBD");
        add("VBG");
        add("VBN");
        add("VBP");
        add("VBZ");
    }};

    public static ArrayList<String>nounTags = new ArrayList<String>(){{
        add("NN");
        add("NNS");
        add("NNP");
        add("NNPS");
    }};

    public static ArrayList<String> properNountTags = new ArrayList<String> () {{
        add("NNPS");
        add("NNP");
    }};

    public static ArrayList<String> firstPersonVerbTags = new ArrayList<String> () {{
        add("VB");
        add("VBD");
        add("VBG");
    }};

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

    public static String [] tokenize(String sentence){
        return sentence.split(" ");
    }

    public static ArrayList<Integer> findTagIndex(String [] tags, ArrayList<String>targetTags){
        ArrayList <Integer> tagIndex = new ArrayList<>();

        int index = 0;
        for(String t: tags){
            if (targetTags.contains(t)){
                tagIndex.add(index);
            }

            index ++;
        }

        return tagIndex;
    }

    public static boolean containsCaseInsensitive(String s, ArrayList<String> l){
        for (String string : l){
            if (string.equalsIgnoreCase(s)){
                return true;
            }
        }
        return false;
    }

    public static String getTokenTags(String [] tags, String [] tokens, String term){
        int index = 0;

        for(int i = 0; i < tokens.length; i ++){
            if(tokens[i].equals(term)){
                index = i;
                break;
            }
        }

        if(verbTags.contains(tags[index])){
            return "VERB";
        } else if (nounTags.contains(tags[index])){
            return "NOUN";
        }

        return "";
    }
}

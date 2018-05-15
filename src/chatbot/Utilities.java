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

    public static ArrayList<String> properNounTags = new ArrayList<String> () {{
        add("NNPS");
        add("NNP");
    }};

    public static ArrayList<String> firstPersonVerbTags = new ArrayList<String> () {{
        add("VB");
        add("VBD");
        add("VBG");
    }};

    public static ArrayList<String> adjectiveTags = new ArrayList<String> () {{
        add("JJ");
        add("JJR");
        add("JJS");
    }};

    public static ArrayList<String> prepositionTags = new ArrayList<String> () {{
        add("IN");
    }};

    public static ArrayList<String> objectTags = new ArrayList<String> () {{
        add("NN");
        add("NNS");
        add("NNP");
        add("NNPS");
        add("PRP");
    }};

    public static ArrayList<String> locationWord = new ArrayList<String> () {{
       add("in");
       add("at");
       add("to");
       add("from");
    }};

    public static ArrayList<String> timeWord = new ArrayList<String> () {{
       add("morning");
       add("afternoon");
       add("evening");
       add("night");
    }};

    public static ArrayList <String> commomWord = new ArrayList<String>(){{
        add("a");
        add("is");
        add("are");
        add("was");
        add("were");
        add("when");
        add("what");
        add("who");
        add("why");
        add("where");
        add("how");
    }};

    public static ArrayList<String> mathOperator = new ArrayList<String>(){{
        add("+");
        add("-");
        add("*");
        add("/");
        add("%");
    }};

    public static ArrayList <String> questionWords = new ArrayList<String>(){{
        add("when");
        add("what");
        add("who");
        add("why");
        add("where");
        add("how");
        add("do");
        add("did");
        add("is");
        add("was");
        add("were");
        add("are");
    }};

    public static ArrayList <String> reasonWords = new ArrayList<String>() {{
        add("because");
        add("due");
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

    public static boolean isCommonWord(String w){
        return commomWord.contains(w);
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

    public static ArrayList <Integer> findObjectIndex(String [] tags, String [] tokens, String action){
        ArrayList<Integer>verbIndex = findTagIndex(tags, firstPersonVerbTags);
        ArrayList<Integer>objectIndex = new ArrayList<>();
        for(int i = 0; i < verbIndex.size(); i ++){
            System.out.println("verbindex = " + verbIndex.get(i));
            if(action.equals(tokens[verbIndex.get(i)])){
                for(int j = verbIndex.get(i); j >= 0; j --){
                    System.out.println("tags = " + tags[j]);
                    if(nounTags.contains(tags[j])){
                        System.out.println("MATCH");
                        objectIndex.add(j);
                        break;
                    }
                }
            }
        }

        return objectIndex;
    }

    public static String findObjectIndex(String [] tokens, String [] tags){
        String obj = null;
        // attempt one - the noun before verb as object
        for(int i = 0; i < tokens.length; i ++){
            if(commomWord.contains(tokens[i])){ continue; } // skip common word
            if(verbTags.contains(tags[i])){
                for(int j = i; j > 0; j -- ){
                    if(nounTags.contains(tags[j])){
                        obj =  tokens[j];
                    }
                }
            }
        }

        // attempt two - grab the noun as object
        if(obj == null){
            for(int i = 0; i < tokens.length; i ++){
                if(nounTags.contains(tags[i])){
                    if(i - 1 >= 0 && !locationWord.contains(tokens[i - 1])){
                        obj = tokens[i];
                    }
                    break;
                }
            }
        }

        return obj;
    }

    public static ArrayList <Integer> findActionIndex(String [] tags, String [] tokens, String object){
        System.out.println(Arrays.toString(tags));
        ArrayList<Integer>nounIndex = findTagIndex(tags, nounTags);
        ArrayList<Integer>actionIndex = new ArrayList<>();
        for(int i = 0; i < nounIndex.size(); i ++){
            System.out.println("action object :" + tokens[nounIndex.get(i)]);
            if(object.equals(tokens[nounIndex.get(i)])){
                System.out.println("FK");
                for(int j = nounIndex.get(i) + 1; j < tags.length; j ++){
                    if(firstPersonVerbTags.contains(tags[j])){
                        actionIndex.add(j);
                        break;
                    }
                }
            }
        }

        return actionIndex;
    }

    public static ArrayList<Integer> findTargetIndex(String [] tags, String [] tokens, String action){
        ArrayList<Integer>verbIndex = findTagIndex(tags, firstPersonVerbTags);
        ArrayList<Integer>targetIndex = new ArrayList<>();
        for(int i = 0; i < verbIndex.size(); i ++){
            if(action.equals(tokens[verbIndex.get(i)])){
                for(int j = verbIndex.get(i); j < tags.length; j ++){
                    System.out.println("tags = " + tags[j]);
                    if(nounTags.contains(tags[j])){
                        System.out.println("MATCH");
                        targetIndex.add(j);
                        break;
                    }
                }
            }
        }

        return targetIndex ;
    }

    public static String [] cropArray(int startIndex, int endIndex, String[]arr){
        int length = endIndex - startIndex;
        String [] newArr = new String[length];
        if(endIndex > startIndex){
            int index = 0;
            for(int i = startIndex; i < endIndex; i++){
                newArr[index] = arr[i];
                index ++;
            }

            return newArr;
        }
        return newArr;
    }

    public static String arrayToSring(String [] arr){
        String re = "";
        for(String e: arr){
            re += e + " ";
        }

        return re;
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

    public static double calCosineSimilarity(double[]docTFIDF, double[]queryTFIDF, double vectorLengthDoc,
                                             double vectorLengthQuery){
        double value = 0;
        for(int i = 0; i < docTFIDF.length; i ++){
            value += docTFIDF[i] * queryTFIDF[i];
        }

        value = value / (vectorLengthDoc * vectorLengthQuery);

        return value;
    }

    public static String [] subArray(String [] arr, int start, int end){
        String [] newArr = new String[end - start];
        int index = 0;
        for(int i = start; i < end; i++){
            newArr[index] = arr[i];
            index ++;
        }

        return newArr;
    }

    public static boolean isNumeric(String str){
        try{
            double d = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean isVerb(String [] tokens, String [] tags, int verbIndex){
        // first word, is not a verb
        if(verbIndex == 0)
            return false;
        // next word is still a verb, not a verb
        if(verbIndex + 1 < tokens.length){
            if(verbTags.contains(tags[verbIndex + 1])){
                return false;
            }
        }
        return true;
    }

    public static boolean isNegative(String doc){
        if(doc.toLowerCase().contains("no") || doc.toLowerCase().contains("not")
                || doc.toLowerCase().contains("never")){
            return true;
        }
        return false;
    }
}

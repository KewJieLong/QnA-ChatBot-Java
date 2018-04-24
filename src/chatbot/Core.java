/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author kewjielong
 */
public class Core {
    private static NN_PosTagger posTagger = new NN_PosTagger();
    private static TFIDF tfidf = new TFIDF();
    private static Utilities ult = new Utilities();
    private static Random rand = new Random();
    private static String [] acceptedReply = new String[] {
            "I got it",
            "I see",
            "OK"
    };
    
    public Core(){}
   
    public static String reply(String sentence){
        String [] tags = posTagger.tag(sentence);
        String [] token = tokenize(sentence);

        for(String t: tags){
            System.out.print(t + " ");
        }

        System.out.println();
        for(String t: token){
            System.out.print(t + " ");
        }

        System.out.println("Most relevent text: " + tfidf.getMostReleventDoc(token));

        // do not save question to docs
        if(isSave(token, tags)){
            tfidf.addDoc(sentence, true, true);
        }

        if(isAsking(token)){
            return tfidf.getMostReleventDoc(token);
        }else{
            return acceptedReply[rand.nextInt(acceptedReply.length)];
        }

    }



    public static ArrayList<Integer> nounIndex(String [] tags){
        ArrayList <Integer> tagIndex = new ArrayList<>();
        ArrayList<String>nounTags = new ArrayList<String>(){{
            add("NN");
            add("NNS");
            add("NNP");
            add("NNPS");
        }};

        int index = 0;
        for(String t: tags){
            if (nounTags.contains(t)){
                tagIndex.add(index);
            }

            index ++;
        }

        return tagIndex;
    }



    public static ArrayList<Integer> verbIndex(String [] tags){
        ArrayList <Integer> tagIndex = new ArrayList<>();
        ArrayList<String>nounTags = new ArrayList<String>(){{
            add("VB");
            add("VBD");
            add("VBG");
            add("VBN");
            add("VBP");
            add("VBZ");
        }};
        
        int index = 0;
        for(String t: tags){
            if (nounTags.contains(t)){
                tagIndex.add(index);
            }
            
            index ++;
        }
        
        return tagIndex;
    }

    public static String [] tokenize(String sentence){
        return sentence.split(" ");
    }
    
    public static boolean isCompleteSentence(String[] token, String[] tags){
        if(token.length < 2){
            return false;
        }
        
//        for(String t: tags){
//            
//        }
        
        return true;
    }
    
//    public static boolean isCorrectGrammer(String[] tags){
//        
//        
//        int index = 0;
//        for(String t: tags){
//            
//        }
//        
//        return true;
//    }
    
    public static boolean isAsking(String [] token){
        ArrayList <String> questionWords = new ArrayList<String>(){{
            add("when");
            add("what");
            add("who");
            add("why");
            add("where");
            add("how");
        }};

        for(String t: token){
            if(questionWords.contains(t.toLowerCase())){
                return true;
            }
        }
        return false;
    }
    
    public static boolean isSave(String [] token, String[] tags){
        if(isAsking(token)){return false;}        
        if(!isCompleteSentence(token, tags)){return false;}
        return true;
    }
    
    public static int mostFrequent(int []  ary) {
        Map<Integer, Integer> m = new HashMap<Integer, Integer>();
        for (int a : ary) {
            Integer freq = m.get(a);
            m.put(a, (freq == null) ? 1 : freq + 1);
        }

        int max = -1;
        int mostFrequent = -1;
        for (Map.Entry<Integer, Integer> e : m.entrySet()) {
            if (e.getValue() > max) {
                mostFrequent = e.getKey();
                max = e.getValue();
            }
        }

        return mostFrequent;
    }
    
    public static  int [] arrayListToArray(ArrayList<Integer>arylist){        
        int [] array = new int[arylist.size()];
        int index = 0;
        for(int e: arylist){
            array[index] = e;
            index ++;                    
        }
        
       return array;
    }
           
}

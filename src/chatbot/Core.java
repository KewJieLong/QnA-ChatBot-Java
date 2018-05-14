/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;

import javax.script.ScriptException;
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
   
    public static String reply(String sentence) throws ScriptException {
        String [] tags = posTagger.tag(sentence);
        String [] token = ult.tokenize(sentence);
        for(String t: tags){
            System.out.print(t + " ");
        }

        System.out.println();
        for(String t: token){
            System.out.print(t + " ");
        }

        int questionT = -1;
        Question question = null;
        if((questionT = isAsking(token)) >= 0){
            question = new Question(tags, token, tfidf, ult, posTagger, questionT);
            String answer = question.answer();
            return answer;
        } else if((questionT = isMathQues(sentence)) >= 0) {
            System.out.println("Math question");
            question = new Question(tags, token, tfidf, ult, posTagger, questionT);
            String answer = question.ansMath();
            return answer;
        }

        // do not save question to docs
        if(isSave(token, tags)){
            System.out.println("Saving");
            tfidf.addDoc(sentence, true, true);
            return acceptedReply[rand.nextInt(acceptedReply.length)];
        } else {
            return "I don't understand what you are saying";
        }
    }



    
    public static boolean isCompleteSentence(String[] token, String[] tags){
        if(token.length < 2){
            return false;
        }

        boolean hvObj = false;
        boolean hvAction = false;
        for(String t: tags){
            if(ult.objectTags.contains(t)){
                hvObj = true;
            }

            if(ult.verbTags.contains(t)){
                hvAction = true;
            }
        }

        if(hvObj && hvAction) { return true; }
        return false;
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
    
    public static int isAsking(String [] token){
        for(String t: token){
            if(ult.questionWords.contains(t.toLowerCase())){
                System.out.printf("Asking word:");
                System.out.println(t);
                return ult.questionWords.indexOf(t.toLowerCase());
            }
        }
        return -1;
    }

    public static int isMathQues(String sentence){
        for(char s: sentence.toCharArray()){
            if(ult.mathOperator.contains(String.valueOf(s))){
                return ult.mathOperator.indexOf(String.valueOf(s));
            }
        }
        return -1;
    }
    
    public static boolean isSave(String [] token, String[] tags){
        if(isAsking(token) >= 0){return false;}
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

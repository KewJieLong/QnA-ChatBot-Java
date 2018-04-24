/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;
import java.util.Arrays;
/**
 *
 * @author kewjielong
 */
public class PosTagger {  
    private final String[] TAGS = {"NOUN", "ADJECTIVES", "PRONOUNS", "INTERJECTIONS"
            , "CONJUNCTIONS", "PREPOSITIONS", "ADVERBS", "VERBS"};
    
    private static final String[] PRONOUNS = {"I", "YOU", "HE", "SHE", "IT", "WE", "THEY"};
    private static final String [] AUXILIARY_VERBS = {"AM", "IS", "ARE", "WAS", "WERE", "BE", "BEING", "BEEN", "M"};
    private static final String[] ADVERBS = {"THE", "HOW", "WHAT", "WHY", "WHERE", 
                                        "WHO", "WHEN"};
    private static final String[] PREPOSITION = {"TO"};
    
    
    
    public PosTagger(){        
    }
    
    public static String [] tokenize(String sentence){
        String [] token = sentence.replaceAll("[^A-Za-z]+", " ").split(" ");        
        Utilities.printArray(token);
        return token;
    }
    
    public static String[] tag(String[] token){
        String [] tagging = new String[token.length];
        
        for(int i=0; i < token.length; i++){        
            // check pronouns 
            if(Arrays.asList(PRONOUNS).contains(token[i].toUpperCase())){
                System.out.println(token[i]);
                tagging[i] = "PRONOUNS";
            }
            
            // check auxiliary verbs
            if(Arrays.asList(AUXILIARY_VERBS).contains(token[i].toUpperCase())){
                tagging[i] = "VERB";
            }
            
            // check PREPOSITION
            if(Arrays.asList(PREPOSITION).contains(token[i].toUpperCase())){
                tagging[i] = "PREPOSITION";
                
                if(i + 1 < token.length){
                    if(!Arrays.asList(ADVERBS).contains(token[i + 1])){
                        tagging[i + 1] = "VERB";
                    }
                }
            }
            
             // check adverbs
             if(Arrays.asList(ADVERBS).contains(token[i].toUpperCase())){
                 tagging[i] = "ADVERBS";
                 if(i + 1 < token.length){
                    tagging[i + 1] = "NOUN";
                 }                 
             }
            
            // check verb 
            if(token[i].contains("ing") || token[i].contains("ed")){                
                tagging[i] = "VERB";
            }                                                         
        }
        
        // double checking 
        for(int i=0; i<tagging.length; i++){
            if(tagging[i] == "VERB"){
                if(i + 1 < tagging.length){
                    
                }
            }
        }
        
        return tagging;                      
    }
    
    public static boolean [] findVerb(String[] token){                        
        boolean [] verbPosition = new boolean[token.length];
        Utilities.printArray(verbPosition);        
        for(int i=0; i < token.length; i++){        
            if(Arrays.asList(PRONOUNS).contains(token[i].toUpperCase())){
                System.out.println(token[i]);
            }
            
            if(token[i].contains("ing") || token[i].contains("ed")){                
                verbPosition[i] = true;
            }            
            Utilities.printArray(verbPosition);                        
        }
        
        return verbPosition;
    }
    
    public static void findNoun(String[] token){
        
    }
    
    public static void findPronoun(String[] token){
        
    }
    
    public static void main(String[]args){
        String sentence = "DO you want to eat something?";
        String[] token = tokenize(sentence);    
        String[] tagging = tag(token);
        System.out.println(Utilities.printArray(token)); 
        System.out.println(Utilities.printArray(tagging));
        
    }
}

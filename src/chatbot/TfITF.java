/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author kewjielong
 */
public class TfITF {
    private static final String FILENAME = "raven.txt";
    public static void main(String[]args){
        BufferedReader br = null;
        FileReader fr = null;
        ArrayList <String>docs = new ArrayList();                        
        ArrayList <HashMap<String, Double>>collectionTFDoc = new ArrayList();
        
        HashMap<String, Double> wordIDF = new HashMap<>();             
        HashMap<String, Integer> wordFeq = new HashMap<>();
        
        int line = 0;
        
        try{
            fr = new FileReader(FILENAME);
            br = new BufferedReader(fr);
            String sCurrentLine;
            
            while ((sCurrentLine = br.readLine()) != null) {
                line ++;
                sCurrentLine = sCurrentLine.replaceAll("[^A-Za-z]+", " ");
                String [] c = sCurrentLine.split(" ");
                docs.add(sCurrentLine);
                
                HashMap<String, Integer> wordFeqDoc = new HashMap<>();
                HashMap<String, Double> TFDoc = new HashMap<>();
                for(String s: c){
                    String w = s.toLowerCase();
                    
                    // WordFeq for ALL Document
                    if(wordFeq.containsKey(w)){                        
                        wordFeq.put(w, wordFeq.get(w) + 1);                                                
                    }else{
                        wordFeq.put(w, 1);
                    }
                    
                    // WordFeq for CURRENT Document
                    if(wordFeqDoc.containsKey(w)){
                        wordFeqDoc.put(w, wordFeqDoc.get(w) + 1);
                    }else{
                        wordFeqDoc.put(w, 1);
                    }
                    
//                    if (word.indexOf(s) >= 0){
//                        int index = word.indexOf(s);
//                        int new_count = (int)countInDoc.get(index) + 1;
//                        countInDoc.set(index, new_count);
//                    }else{
//                        countInDoc.add(1);
//                        word.add(s);
//                    }
                }  
                
                
//                System.out.println("Word Feq doc");
//                System.out.println(wordFeqDoc);
                for(String key: wordFeqDoc.keySet()){                    
                    TFDoc.put(key, (wordFeqDoc.get(key) / (double)c.length));                                        
                }
                
                collectionTFDoc.add(TFDoc);
                
//                System.out.println(TFDoc);
                
            }
                
        } catch(IOException e){
            e.printStackTrace();
        }finally {
            try {

                    if (br != null)
                            br.close();

                    if (fr != null)
                            fr.close();

            } catch (IOException ex) {

                    ex.printStackTrace();

            }
        }
        
        System.out.println("word Feq: ");
        System.out.println(wordFeq);   
        System.out.println("total document");
        System.out.println(line);
        
        // calculate all the IDF for each word
        for(String key: wordFeq.keySet()){
            wordIDF.put(key, Math.log((double)line / (double)wordFeq.get(key)));
        }
        
        System.out.println(wordIDF.get("raven"));
        
        
        String term = "raven";                
        Double maxTfIdf = 0.0;
        int maxIndex = 0;
        int index = 0;
        for(HashMap hm: collectionTFDoc){                        
            if(hm.containsKey(term)){
                double TfIdf = (double)hm.get(term) * wordIDF.get(term);
                if(maxTfIdf < TfIdf){
                    maxTfIdf = TfIdf;
                    maxIndex = index;
                }
            }         
            index ++;
        }
        
        System.out.println(maxIndex);
        System.out.println(docs.get(maxIndex));
        System.out.println(maxTfIdf);
        
        
        
    }
}
